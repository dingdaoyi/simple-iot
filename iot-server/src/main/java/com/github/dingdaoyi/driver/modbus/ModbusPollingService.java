package com.github.dingdaoyi.driver.modbus;

import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.ModbusConfig;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.iot.IotDataProcessor;
import com.github.dingdaoyi.mapper.DeviceMapper;
import com.github.dingdaoyi.mapper.ModbusConfigMapper;
import com.github.dingdaoyi.mapper.ProductMapper;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import com.github.dingdaoyi.service.TslModelService;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Modbus TCP 轮询调度器
 * ponytail: 每个设备一个 ScheduledFuture，用 ScheduledExecutor 线程池
 */
@Slf4j
@Component
public class ModbusPollingService {

    @Resource
    private ModbusConfigMapper configMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private IotDataProcessor dataProcessor;
    @Resource
    private TslModelService tslModelService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4, r -> {
        Thread t = new Thread(r, "modbus-poll");
        t.setDaemon(true);
        return t;
    });
    private final Map<Integer, java.util.concurrent.ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void start() {
        List<ModbusConfig> configs = configMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ModbusConfig>()
                .eq("enabled", true));
        for (ModbusConfig cfg : configs) {
            schedulePoll(cfg);
        }
        log.info("Modbus polling started for {} devices", configs.size());
    }

    @PreDestroy
    public void stop() {
        tasks.values().forEach(f -> f.cancel(false));
        scheduler.shutdownNow();
        log.info("Modbus polling stopped");
    }

    public void reload(int configId) {
        ModbusConfig cfg = configMapper.selectById(configId);
        if (cfg == null) return;
        var existing = tasks.remove(configId);
        if (existing != null) existing.cancel(false);
        if (cfg.getEnabled()) schedulePoll(cfg);
    }

    public void reloadAll() {
        stop();
        tasks.clear();
        start();
    }

    private void schedulePoll(ModbusConfig cfg) {
        int interval = cfg.getIntervalMs() != null ? cfg.getIntervalMs() : 5000;
        var future = scheduler.scheduleAtFixedRate(() -> poll(cfg), 0, interval, TimeUnit.MILLISECONDS);
        tasks.put(cfg.getId(), future);
    }

    private void poll(ModbusConfig cfg) {
        Device device = deviceMapper.selectById(cfg.getDeviceId());
        if (device == null) return;
        Product product = productMapper.selectById(device.getProductId());
        if (product == null) return;

        List<RegisterMapping> mappings = parseMap(cfg.getRegisterMap());
        if (mappings.isEmpty()) return;

        // ponytail: 同步连接，每次轮询开关一次，不做连接池
        try (Socket socket = new Socket(cfg.getHost(), cfg.getPort())) {
            socket.setSoTimeout(3000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            List<DeviceData> dataList = new ArrayList<>();
            for (RegisterMapping m : mappings) {
                try {
                    byte[] req = ModbusFrame.readRequest(cfg.getUnitId(), m.function(), m.address(), m.count());
                    out.write(req);
                    out.flush();

                    // ponytail: 简单读取，假设响应一次到达
                    byte[] header = in.readNBytes(9);
                    if (header.length < 9) continue;
                    int byteCount = header[8] & 0xFF;
                    byte[] payload = in.readNBytes(byteCount);
                    byte[] full = new byte[9 + byteCount];
                    System.arraycopy(header, 0, full, 0, 9);
                    System.arraycopy(payload, 0, full, 9, byteCount);

                    int[] regs = ModbusFrame.parseResponse(full);
                    Object value = ModbusFrame.toValue(regs, 0, m.count(), m.dataType(), m.scale());
                    dataList.add(new DeviceData(m.identifier(), m.toDataType(), value));
                } catch (Exception e) {
                    log.warn("Modbus read failed: device={}, mapping={}", device.getDeviceKey(), m.identifier(), e);
                }
            }

            if (!dataList.isEmpty()) {
                DeviceRequest request = new DeviceRequest();
                request.setDeviceKey(device.getDeviceKey());
                request.setProductKey(product.getProductKey());
                request.setProtoKey("modbus-tcp");
                request.setMessageType(ProtoMessageType.PROPERTY);
                // ponytail: 用 JSON 编码，复用 DefaultProtocolDecoder 的 JSON 解析路径
                String json = encodeAsJson(dataList);
                request.setData(json.getBytes());
                try {
                    dataProcessor.messageUp(request);
                } catch (Exception e) {
                    log.warn("Modbus messageUp failed: device={}", device.getDeviceKey(), e);
                }
            }

            // 标记上线
            if (!Boolean.TRUE.equals(device.getOnline())) {
                device.setOnline(true);
                deviceMapper.updateById(device);
            }
        } catch (Exception e) {
            log.warn("Modbus poll failed: device={}, host={}:{}", device.getDeviceKey(), cfg.getHost(), cfg.getPort(), e);
        }
    }

    private List<RegisterMapping> parseMap(String json) {
        if (json == null || json.isBlank()) return List.of();
        return JSONUtil.toList(json, RegisterMapping.class);
    }

    /** Encode as JSON: {type:property, params:{id:{value:..,dataType:..}}} */
    private String encodeAsJson(List<DeviceData> dataList) {
        var obj = JSONUtil.createObj();
        var params = JSONUtil.createObj();
        for (DeviceData d : dataList) {
            var item = JSONUtil.createObj();
            item.set("value", d.getValue());
            item.set("dataType", d.getDataType().getValue());
            params.set(d.getIdentifier(), item);
        }
        obj.set("type", "property");
        obj.set("params", params);
        return obj.toString();
    }
}
