package com.github.dingdaoyi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.controller.iot.dto.RuleChainDebugRequest;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.entity.enu.RuleSourceType;
import com.github.dingdaoyi.mapper.RuleChainMapper;
import com.github.dingdaoyi.model.query.RuleChainAddQuery;
import com.github.dingdaoyi.model.query.RuleChainPageQuery;
import com.github.dingdaoyi.model.query.RuleChainUpdateQuery;
import com.github.dingdaoyi.model.vo.RuleChainDebugResultVo;
import com.github.dingdaoyi.model.vo.RuleChainDetailVo;
import com.github.dingdaoyi.model.vo.RuleChainPageVo;
import com.github.dingdaoyi.model.vo.RuleChainValidationResultVo;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.rule.RuleChainEngine;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.RuleChainService;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 规则链服务实现
 * @author dingyunwei
 */
@Slf4j
@Service
public class RuleChainServiceImpl extends ServiceImpl<RuleChainMapper, RuleChain> implements RuleChainService {

    @Resource
    private DeviceService deviceService;

    @Resource
    private ProductService productService;

    @Resource
    private RuleChainEngine ruleChainEngine;

    @Resource
    @org.springframework.context.annotation.Lazy
    private com.github.dingdaoyi.rule.ScheduleTriggerService scheduleTriggerService;

    @Override
    public PageResult<RuleChainPageVo> pageByQuery(RuleChainPageQuery query) {
        Page<RuleChainPageVo> page = PageHelper.page(query);
        Page<RuleChainPageVo> result = baseMapper.pageByQuery(page, query);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Optional<RuleChainDetailVo> details(Integer id) {
        RuleChain ruleChain = baseMapper.selectById(id);
        if (ruleChain == null) {
            return Optional.empty();
        }
        RuleChainDetailVo vo = new RuleChainDetailVo();
        BeanUtil.copyProperties(ruleChain, vo);
        vo.setSourceType(ruleChain.getSourceType().name());

        // 填充数据源名称
        fillSourceName(vo, ruleChain);

        return Optional.of(vo);
    }

    @Override
    public boolean add(RuleChainAddQuery query) {
        RuleChain entity = query.toEntity();
        boolean ok = save(entity);
        if (ok) scheduleTriggerService.registerAll();
        return ok;
    }

    @Override
    public boolean updateByQuery(RuleChainUpdateQuery query) {
        RuleChain entity = query.toEntity();
        boolean ok = updateById(entity);
        if (ok) scheduleTriggerService.registerAll();
        return ok;
    }

    @Override
    public List<RuleChain> getByDeviceKey(String deviceKey) {
        // 1. 获取设备信息
        Device device = deviceService.getByDeviceKey(deviceKey).orElse(null);
        if (device == null) {
            return new ArrayList<>();
        }

        List<RuleChain> result = new ArrayList<>();

        // 2. 获取按产品配置的规则链
        List<RuleChain> productChains = list(Wrappers.<RuleChain>lambdaQuery()
            .eq(RuleChain::getIsEnabled, true)
            .eq(RuleChain::getSourceType, RuleSourceType.PRODUCT)
            .eq(RuleChain::getSourceId, device.getProductId()));
        result.addAll(productChains);

        // 3. 获取按设备配置的规则链
        List<RuleChain> deviceChains = list(Wrappers.<RuleChain>lambdaQuery()
            .eq(RuleChain::getIsEnabled, true)
            .eq(RuleChain::getSourceType, RuleSourceType.DEVICE)
            .eq(RuleChain::getSourceId, device.getId()));
        result.addAll(deviceChains);

        return result;
    }

    @Override
    public List<RuleChain> getByProductId(Integer productId) {
        return list(Wrappers.<RuleChain>lambdaQuery()
            .eq(RuleChain::getIsEnabled, true)
            .eq(RuleChain::getSourceType, RuleSourceType.PRODUCT)
            .eq(RuleChain::getSourceId, productId));
    }

    @Override
    public List<RuleChain> listEnabled() {
        return list(Wrappers.<RuleChain>lambdaQuery()
            .eq(RuleChain::getIsEnabled, true));
    }

    @Override
    public RuleChainValidationResultVo validateDraft(RuleChain ruleChain) {
        RuleChainValidationResultVo result = new RuleChainValidationResultVo();
        RuleChain.RuleChainConfiguration config = ruleChain == null ? null : ruleChain.getConfiguration();
        if (config == null || CollectionUtil.isEmpty(config.getNodes())) {
            result.addError("EMPTY_CHAIN", null, null, "规则链至少需要一个输入节点");
            return result;
        }

        List<RuleChain.RuleNode> nodes = config.getNodes();
        List<RuleChain.RuleConnection> connections = Optional.ofNullable(config.getConnections()).orElseGet(List::of);
        Map<String, RuleChain.RuleNode> nodeMap = nodes.stream()
            .filter(node -> node.getId() != null)
            .collect(Collectors.toMap(RuleChain.RuleNode::getId, Function.identity(), (left, right) -> left, LinkedHashMap::new));

        Set<String> inputNodeIds = new LinkedHashSet<>();
        for (RuleChain.RuleNode node : nodes) {
            RuleNodeType nodeType = parseNodeType(node.getType());
            if (node.getId() == null) {
                result.addError("NODE_ID_EMPTY", null, null, "节点ID不能为空");
                continue;
            }
            if (nodeType == null) {
                result.addError("UNKNOWN_NODE_TYPE", node.getId(), null, "未知节点类型: " + node.getType());
                continue;
            }
            if (nodeType.getCategory() == RuleNodeType.NodeCategory.INPUT) {
                inputNodeIds.add(node.getId());
            }
        }
        if (inputNodeIds.isEmpty()) {
            result.addError("NO_INPUT_NODE", null, null, "规则链至少需要一个输入节点");
        }

        Map<String, List<RuleChain.RuleConnection>> outgoing = new LinkedHashMap<>();
        for (RuleChain.RuleConnection connection : connections) {
            String connectionId = connection.getSource() + "->" + connection.getTarget() + ":" + connection.getType();
            RuleChain.RuleNode source = nodeMap.get(connection.getSource());
            RuleChain.RuleNode target = nodeMap.get(connection.getTarget());
            if (source == null || target == null) {
                result.addError("MISSING_NODE", null, connectionId, "连接引用的源节点或目标节点不存在");
                continue;
            }
            if (!isValidConnection(source.getType(), target.getType())) {
                result.addError("INVALID_CONNECTION", source.getId(), connectionId, getConnectionErrorMessage(source.getType(), target.getType()));
            }
            outgoing.computeIfAbsent(connection.getSource(), key -> new ArrayList<>()).add(connection);
        }

        Set<String> reachable = collectReachableNodeIds(inputNodeIds, outgoing);
        result.setReachableNodeIds(new ArrayList<>(reachable));
        for (RuleChain.RuleNode node : nodes) {
            if (!reachable.contains(node.getId())) {
                result.addWarning("UNREACHABLE_NODE", node.getId(), null, "节点未从任何输入节点连通，调试和运行时不会执行: " + Optional.ofNullable(node.getName()).orElse(node.getId()));
            }
        }
        return result;
    }

    @Override
    public RuleChainDebugResultVo debug(RuleChainDebugRequest request) {
        RuleChain ruleChain = request.getRuleChain();
        if (ruleChain.getIsEnabled() == null) {
            ruleChain.setIsEnabled(true);
        }

        RuleContext context = buildDebugContext(request);
        long start = System.nanoTime();
        ruleChainEngine.execute(ruleChain, context);
        long durationMs = (System.nanoTime() - start) / 1_000_000;

        RuleChainDebugResultVo result = new RuleChainDebugResultVo();
        RuleChainValidationResultVo validation = validateDraft(ruleChain);
        result.setValidation(validation);
        result.setRuleChainId(ruleChain.getId());
        result.setRuleChainName(ruleChain.getName());
        result.setDurationMs(durationMs);
        result.setTraces(context.getTraces());
        result.setExecutedNodeCount(context.getTraces().size());
        result.setExecutedConnections(buildExecutedConnections(ruleChain, context.getTraces()));
        result.setSuccess(validation.isValid() && context.getTraces().stream()
            .noneMatch(trace -> "FAILED".equals(trace.getStatus()) || "ERROR".equals(trace.getStatus())));
        return result;
    }

    private List<Map<String, String>> buildExecutedConnections(RuleChain ruleChain, List<RuleContext.ExecutionTrace> traces) {
        RuleChain.RuleChainConfiguration config = ruleChain.getConfiguration();
        if (config == null || CollectionUtil.isEmpty(config.getConnections()) || CollectionUtil.isEmpty(traces)) {
            return List.of();
        }
        Map<String, RuleContext.ExecutionTrace> traceMap = traces.stream()
            .collect(Collectors.toMap(RuleContext.ExecutionTrace::getNodeId, Function.identity(), (left, right) -> left, LinkedHashMap::new));
        List<Map<String, String>> result = new ArrayList<>();
        for (RuleChain.RuleConnection connection : config.getConnections()) {
            RuleContext.ExecutionTrace sourceTrace = traceMap.get(connection.getSource());
            if (sourceTrace != null && matchesDebugType(connection.getType(), sourceTrace.getConnectionType()) && traceMap.containsKey(connection.getTarget())) {
                Map<String, String> edge = new LinkedHashMap<>();
                edge.put("source", connection.getSource());
                edge.put("target", connection.getTarget());
                edge.put("type", connection.getType() != null ? connection.getType() : "Success");
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * 判断连接类型是否匹配。null type 视为通用 Success 连接。
     */
    private static boolean matchesDebugType(String connType, String traceConnType) {
        if (connType == null) {
            return "Success".equals(traceConnType);
        }
        return connType.equals(traceConnType);
    }

    private RuleContext buildDebugContext(RuleChainDebugRequest request) {
        RuleContext context = new RuleContext();
        context.setDeviceKey(request.getDeviceKey());
        context.setDeviceId(request.getDeviceId());
        context.setDeviceName(request.getDeviceName());
        context.setMessageType(request.getMessageType());
        if (request.getEnrichedData() != null) {
            context.getEnrichedData().putAll(request.getEnrichedData());
        }

        DecodeResult decodeResult = new DecodeResult();
        if (request.getProperties() != null) {
            request.getProperties().forEach((identifier, value) ->
                decodeResult.getDataList().add(new DeviceData(identifier, inferDataType(value), value))
            );
        }
        if (request.getEventIdentifier() != null) {
            DeviceEventData eventData = new DeviceEventData(request.getEventIdentifier(), null);
            if (request.getEventParams() != null) {
                request.getEventParams().forEach((identifier, value) ->
                    eventData.getParams().add(new DeviceData(identifier, inferDataType(value), value))
                );
            }
            decodeResult.setEventData(eventData);
        }
        context.setDecodeResult(decodeResult);
        return context;
    }

    private Set<String> collectReachableNodeIds(Set<String> inputNodeIds, Map<String, List<RuleChain.RuleConnection>> outgoing) {
        Set<String> visited = new LinkedHashSet<>();
        Deque<String> queue = new ArrayDeque<>(inputNodeIds);
        while (!queue.isEmpty()) {
            String nodeId = queue.removeFirst();
            if (!visited.add(nodeId)) {
                continue;
            }
            for (RuleChain.RuleConnection connection : outgoing.getOrDefault(nodeId, List.of())) {
                if (!visited.contains(connection.getTarget())) {
                    queue.addLast(connection.getTarget());
                }
            }
        }
        return visited;
    }

    private RuleNodeType parseNodeType(String type) {
        try {
            return type == null ? null : RuleNodeType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private boolean isValidConnection(String sourceType, String targetType) {
        RuleNodeType source = parseNodeType(sourceType);
        RuleNodeType target = parseNodeType(targetType);
        if (source == null || target == null) {
            return false;
        }
        RuleNodeType.NodeCategory sourceCategory = source.getCategory();
        RuleNodeType.NodeCategory targetCategory = target.getCategory();
        if (targetCategory == RuleNodeType.NodeCategory.INPUT) {
            return false;
        }
        if (sourceCategory == RuleNodeType.NodeCategory.OUTPUT) {
            return false;
        }
        if (sourceCategory == RuleNodeType.NodeCategory.INPUT) {
            if (source == RuleNodeType.INPUT_PROPERTY && target == RuleNodeType.FILTER_EVENT_TYPE) {
                return false;
            }
            if (source == RuleNodeType.INPUT_EVENT && target == RuleNodeType.FILTER_PROPERTY) {
                return false;
            }
            if (source == RuleNodeType.INPUT_ONLINE && targetCategory == RuleNodeType.NodeCategory.FILTER) {
                return false;
            }
        }
        if (sourceCategory == RuleNodeType.NodeCategory.FILTER) {
            return targetCategory == RuleNodeType.NodeCategory.OUTPUT || targetCategory == RuleNodeType.NodeCategory.ALARM;
        }
        if (sourceCategory == RuleNodeType.NodeCategory.ALARM) {
            return targetCategory == RuleNodeType.NodeCategory.OUTPUT;
        }
        return true;
    }

    private String getConnectionErrorMessage(String sourceType, String targetType) {
        RuleNodeType source = parseNodeType(sourceType);
        RuleNodeType target = parseNodeType(targetType);
        if (source == null || target == null) {
            return "连接包含未知节点类型";
        }
        RuleNodeType.NodeCategory sourceCategory = source.getCategory();
        RuleNodeType.NodeCategory targetCategory = target.getCategory();
        if (sourceCategory == RuleNodeType.NodeCategory.OUTPUT) {
            return "输出节点是终端节点，不能继续连接";
        }
        if (targetCategory == RuleNodeType.NodeCategory.INPUT) {
            return "输入节点不能作为连接目标";
        }
        if (source == RuleNodeType.INPUT_PROPERTY && target == RuleNodeType.FILTER_EVENT_TYPE) {
            return "属性上报不能连接到事件类型过滤，请使用属性条件过滤";
        }
        if (source == RuleNodeType.INPUT_EVENT && target == RuleNodeType.FILTER_PROPERTY) {
            return "事件上报不能连接到属性条件过滤，请使用事件类型过滤";
        }
        if (source == RuleNodeType.INPUT_ONLINE && targetCategory == RuleNodeType.NodeCategory.FILTER) {
            return "设备上下线事件不能使用过滤节点，可直接连接告警或输出节点";
        }
        if (sourceCategory == RuleNodeType.NodeCategory.FILTER) {
            return "过滤节点只能连接到输出或告警节点";
        }
        if (sourceCategory == RuleNodeType.NodeCategory.ALARM) {
            return "告警节点只能连接到输出节点进行自定义推送";
        }
        return "此连接不被允许";
    }

    private DataTypeEnum inferDataType(Object value) {
        if (value instanceof Boolean) {
            return DataTypeEnum.BOOL;
        }
        if (value instanceof Float || value instanceof Double || value instanceof java.math.BigDecimal) {
            return DataTypeEnum.DOUBLE;
        }
        if (value instanceof Number) {
            return DataTypeEnum.INT;
        }
        if (value instanceof Map<?, ?>) {
            return DataTypeEnum.STRUCT;
        }
        return DataTypeEnum.TEXT;
    }

    private void fillSourceName(RuleChainDetailVo vo, RuleChain ruleChain) {
        if (ruleChain.getSourceId() == null) {
            return;
        }
        switch (ruleChain.getSourceType()) {
            case PRODUCT -> {
                Product product = productService.getById(ruleChain.getSourceId());
                if (product != null) {
                    vo.setSourceName(product.getModel());
                }
            }
            case DEVICE -> {
                Device device = deviceService.getById(ruleChain.getSourceId());
                if (device != null) {
                    vo.setSourceName(device.getDeviceName());
                }
            }
            default -> {}
        }
    }
}
