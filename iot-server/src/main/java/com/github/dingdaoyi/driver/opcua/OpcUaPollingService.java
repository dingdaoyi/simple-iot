package com.github.dingdaoyi.driver.opcua;

import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.OpcUaConfig;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.iot.IotDataProcessor;
import com.github.dingdaoyi.mapper.DeviceMapper;
import com.github.dingdaoyi.mapper.OpcUaConfigMapper;
import com.github.dingdaoyi.mapper.ProductMapper;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import com.github.dingdaoyi.service.TslModelService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * OPC UA 轮询调度器
 * ponytail: 每次 poll 创建新 client, NONE 安全模式最简, 不做连接池
 * @author dingyunwei
 */
@Slf4j
@Component
public class OpcUaPollingService {

    @Resource
    private OpcUaConfigMapper configMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private IotDataProcessor dataProcessor;
    @Resource
    private TslModelService tslModelService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4, r -> {
        Thread t = new Thread(r, "opcua-poll");
        t.setDaemon(true);
        return t;
    });
    private final Map<Integer, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void start() {
        List<OpcUaConfig> configs = configMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OpcUaConfig>()
                .eq("enabled", true));
        for (OpcUaConfig cfg : configs) {
            schedulePoll(cfg);
        }
        log.info("OPC UA polling started for {} devices", configs.size());
    }

    @PreDestroy
    public void stop() {
        tasks.values().forEach(f -> f.cancel(false));
        scheduler.shutdownNow();
        log.info("OPC UA polling stopped");
    }

    public void reload(int configId) {
        OpcUaConfig cfg = configMapper.selectById(configId);
        if (cfg == null) return;
        var existing = tasks.remove(configId);
        if (existing != null) existing.cancel(false);
        if (Boolean.TRUE.equals(cfg.getEnabled())) schedulePoll(cfg);
    }

    public void reloadAll() {
        stop();
        tasks.clear();
        start();
    }

    private void schedulePoll(OpcUaConfig cfg) {
        int interval = cfg.getIntervalMs() != null ? cfg.getIntervalMs() : 5000;
        var future = scheduler.scheduleAtFixedRate(() -> poll(cfg), 0, interval, TimeUnit.MILLISECONDS);
        tasks.put(cfg.getId(), future);
    }

    private void poll(OpcUaConfig cfg) {
        Device device = deviceMapper.selectById(cfg.getDeviceId());
        if (device == null) return;
        Product product = productMapper.selectById(device.getProductId());
        if (product == null) return;

        List<NodeMapping> mappings = parseMap(cfg.getNodeMap());
        if (mappings.isEmpty()) return;

        OpcUaClient client = null;
        try {
            client = createClient(cfg);
            client.connect().get(10, TimeUnit.SECONDS);

            List<DeviceData> dataList = new ArrayList<>();
            for (NodeMapping m : mappings) {
                readNode(client, m, dataList);
            }

            if (!dataList.isEmpty()) {
                sendDeviceData(dataList, device, product);
            }
            markOnline(device);
        } catch (Exception e) {
            log.warn("OPC UA poll failed: device={}, endpoint={}", device.getDeviceKey(), cfg.getEndpointUrl(), e);
        } finally {
            if (client != null) {
                try { client.disconnect().get(5, TimeUnit.SECONDS); } catch (Exception ignored) {}
            }
        }
    }

    /** 读单个节点, 成功则加入 dataList */
    private void readNode(OpcUaClient client, NodeMapping m, List<DeviceData> dataList) {
        try {
            NodeId nodeId = NodeId.parse(m.nodeId());
            UaVariableNode node = client.getAddressSpace().getVariableNode(nodeId);
            DataValue dv = node.readValue();
            Variant variant = dv.getValue();
            Object raw = variant.getValue();
            Object scaled = applyScale(raw, m.scale());
            dataList.add(new DeviceData(m.identifier(), m.toDataType(), scaled));
        } catch (Exception e) {
            log.warn("OPC UA read failed: nodeId={}, mapping={}", m.nodeId(), m.identifier(), e);
        }
    }

    /** 构建 DeviceRequest 并上报 */
    private void sendDeviceData(List<DeviceData> dataList, Device device, Product product) {
        DeviceRequest request = new DeviceRequest();
        request.setDeviceKey(device.getDeviceKey());
        request.setProductKey(product.getProductKey());
        request.setProtoKey("opcua");
        request.setMessageType(ProtoMessageType.PROPERTY);
        request.setData(JSONUtil.toJsonStr(dataList).getBytes());
        try {
            dataProcessor.messageUp(request);
        } catch (Exception e) {
            log.warn("OPC UA messageUp failed: device={}", device.getDeviceKey(), e);
        }
    }

    /** 离线设备标记上线 */
    private void markOnline(Device device) {
        if (!Boolean.TRUE.equals(device.getOnline())) {
            device.setOnline(true);
            deviceMapper.updateById(device);
        }
    }

    /**
     * 创建 OPC UA 客户端
     * ponytail: 用三参数 create(), endpointSelector 选 NONE 端点, configBuilder 设 identity
     */
    private OpcUaClient createClient(OpcUaConfig cfg) throws Exception {
        String securityMode = cfg.getSecurityMode() != null ? cfg.getSecurityMode().toUpperCase() : "NONE";

        return OpcUaClient.create(
            cfg.getEndpointUrl(),
            endpoints -> {
                // 选匹配安全模式的端点
                return endpoints.stream()
                        .filter(e -> e.getSecurityMode().name().contains(securityMode))
                        .findFirst()
                        .or(() -> endpoints.stream().findFirst());
            },
            builder -> {
                builder.setApplicationUri("urn:simple-iot:opcua:client");
                if (cfg.getUsername() != null && !cfg.getUsername().isBlank()) {
                    builder.setIdentityProvider(new UsernameProvider(
                        cfg.getUsername(),
                        cfg.getPassword() != null ? cfg.getPassword() : ""));
                }
                return builder.build();
            }
        );
    }

    private Object applyScale(Object raw, double scale) {
        if (scale == 1.0 || raw == null) return raw;
        if (raw instanceof Number n) return n.doubleValue() * scale;
        return raw;
    }

    private List<NodeMapping> parseMap(String json) {
        if (json == null || json.isBlank()) return List.of();
        return JSONUtil.toList(json, NodeMapping.class);
    }
}
