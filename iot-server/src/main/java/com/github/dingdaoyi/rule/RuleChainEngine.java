package com.github.dingdaoyi.rule;

import cn.hutool.core.collection.CollectionUtil;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.config.SubFlowConfig;
import com.github.dingdaoyi.service.RuleChainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 规则链执行引擎
 * @author dingdaoyi
 */
@Slf4j
@Service
public class RuleChainEngine {

    @Resource
    private List<RuleNodeExecutor> executors;

    @Resource
    private RuleChainService ruleChainService;

    private Map<RuleNodeType, RuleNodeExecutor> executorMap;

    @jakarta.annotation.PostConstruct
    public void init() {
        this.executorMap = executors.stream()
            .collect(Collectors.toMap(RuleNodeExecutor::getNodeType, e -> e));
    }

    /**
     * 执行规则链（顶层入口）
     */
    public void execute(RuleChain ruleChain, RuleContext context) {
        executeInternal(ruleChain, context, new HashSet<>());
    }

    /**
     * 执行规则链（内部，带环检测）
     * @param callStack 当前调用栈中的规则链ID，防止子流循环引用
     */
    private void executeInternal(RuleChain ruleChain, RuleContext context, Set<Integer> callStack) {
        if (!Boolean.TRUE.equals(ruleChain.getIsEnabled())) {
            return;
        }

        RuleChain.RuleChainConfiguration config = ruleChain.getConfiguration();
        if (config == null || CollectionUtil.isEmpty(config.getNodes())) {
            return;
        }

        context.setRuleChainId(ruleChain.getId());

        Map<String, RuleChain.RuleNode> nodeMap = config.getNodes().stream()
            .collect(Collectors.toMap(RuleChain.RuleNode::getId, n -> n));

        Map<String, List<Connection>> connectionMap = buildConnectionMap(config.getConnections());

        // 找到输入节点开始执行
        config.getNodes().stream()
            .filter(n -> {
                RuleNodeType type = RuleNodeType.valueOf(n.getType());
                return type.getCategory() == RuleNodeType.NodeCategory.INPUT;
            })
            .forEach(inputNode -> executeNode(inputNode, nodeMap, connectionMap, context, callStack));
    }

    private void executeNode(RuleChain.RuleNode node,
                             Map<String, RuleChain.RuleNode> nodeMap,
                             Map<String, List<Connection>> connectionMap,
                             RuleContext context,
                             Set<Integer> callStack) {
        RuleNodeType nodeType = RuleNodeType.valueOf(node.getType());

        // 子流节点特殊处理
        if (nodeType == RuleNodeType.SUB_FLOW) {
            executeSubFlowNode(node, context, callStack);
            return;
        }

        RuleNodeExecutor executor = executorMap.get(nodeType);

        if (executor == null) {
            log.warn("未找到节点执行器: {}", nodeType);
            context.addTrace(RuleContext.ExecutionTrace.builder()
                .nodeId(node.getId())
                .nodeName(node.getName())
                .nodeType(nodeType.name())
                .connectionType("Failure")
                .status("SKIPPED")
                .detail("未找到节点执行器: " + nodeType)
                .timestamp(LocalDateTime.now())
                .input(snapshotContext(context))
                .output(Map.of("connectionType", "Failure"))
                .build());
            return;
        }

        long start = System.nanoTime();
        Map<String, Object> inputSnapshot = snapshotContext(context);
        try {
            RuleNodeExecutor.NodeResult result = executor.execute(context, node.getConfig());
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            String connectionType = result.connectionType();
            String message = result.message();
            context.addTrace(RuleContext.ExecutionTrace.builder()
                .nodeId(node.getId())
                .nodeName(node.getName())
                .nodeType(nodeType.name())
                .connectionType(connectionType)
                .status(result.success() ? "SUCCESS" : "FAILED")
                .detail(message)
                .durationMs(durationMs)
                .timestamp(LocalDateTime.now())
                .input(inputSnapshot)
                .output(snapshotResult(result))
                .build());

            List<Connection> connections = connectionMap.getOrDefault(node.getId(), Collections.emptyList());
            connections.stream()
                .filter(c -> matchesConnectionType(c.type(), result.connectionType()))
                .forEach(c -> {
                    RuleChain.RuleNode nextNode = nodeMap.get(c.target());
                    if (nextNode != null) {
                        executeNode(nextNode, nodeMap, connectionMap, context, callStack);
                    }
                });

        } catch (Exception e) {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            log.error("节点执行失败: {} - {}", node.getName(), e.getMessage(), e);
            context.addTrace(RuleContext.ExecutionTrace.builder()
                .nodeId(node.getId())
                .nodeName(node.getName())
                .nodeType(nodeType.name())
                .connectionType("Failure")
                .status("ERROR")
                .detail(e.getMessage())
                .error(e.getMessage())
                .durationMs(durationMs)
                .timestamp(LocalDateTime.now())
                .input(inputSnapshot)
                .output(Map.of("connectionType", "Failure"))
                .build());
        }
    }

    /**
     * 执行子流节点：加载目标规则链并递归执行
     */
    private void executeSubFlowNode(RuleChain.RuleNode node, RuleContext context, Set<Integer> callStack) {
        SubFlowConfig config = node.getConfig() instanceof SubFlowConfig
            ? (SubFlowConfig) node.getConfig()
            : null;

        long start = System.nanoTime();
        Map<String, Object> inputSnapshot = snapshotContext(context);

        if (config == null || config.getTargetRuleChainId() == null) {
            context.addTrace(RuleContext.ExecutionTrace.builder()
                .nodeId(node.getId())
                .nodeName(node.getName())
                .nodeType(RuleNodeType.SUB_FLOW.name())
                .connectionType("Failure")
                .status("FAILED")
                .detail("子流配置缺失: 未指定目标规则链ID")
                .timestamp(LocalDateTime.now())
                .input(inputSnapshot)
                .output(Map.of("connectionType", "Failure"))
                .build());
            return;
        }

        Integer targetId = config.getTargetRuleChainId();

        // 环检测
        if (callStack.contains(targetId)) {
            log.warn("子流循环引用被阻止: 规则链 {} -> {}", context.getRuleChainId(), targetId);
            context.addTrace(RuleContext.ExecutionTrace.builder()
                .nodeId(node.getId())
                .nodeName(node.getName())
                .nodeType(RuleNodeType.SUB_FLOW.name())
                .connectionType("Failure")
                .status("FAILED")
                .detail("子流循环引用被阻止: " + targetId)
                .error("CIRCULAR_REFERENCE")
                .timestamp(LocalDateTime.now())
                .input(inputSnapshot)
                .output(Map.of("connectionType", "Failure"))
                .build());
            return;
        }

        RuleChain targetChain = ruleChainService.getById(targetId);
        if (targetChain == null || !Boolean.TRUE.equals(targetChain.getIsEnabled())) {
            context.addTrace(RuleContext.ExecutionTrace.builder()
                .nodeId(node.getId())
                .nodeName(node.getName())
                .nodeType(RuleNodeType.SUB_FLOW.name())
                .connectionType("Failure")
                .status("FAILED")
                .detail("目标规则链不存在或未启用: " + targetId)
                .timestamp(LocalDateTime.now())
                .input(inputSnapshot)
                .output(Map.of("connectionType", "Failure"))
                .build());
            return;
        }

        // 记录子流调用前的轨迹数
        int traceCountBefore = context.getTraces().size();

        // 递归执行子链
        Set<Integer> newStack = new HashSet<>(callStack);
        newStack.add(targetId);
        executeInternal(targetChain, context, newStack);

        long durationMs = (System.nanoTime() - start) / 1_000_000;
        int subTraceCount = context.getTraces().size() - traceCountBefore;

        context.addTrace(RuleContext.ExecutionTrace.builder()
            .nodeId(node.getId())
            .nodeName(node.getName())
            .nodeType(RuleNodeType.SUB_FLOW.name())
            .connectionType("Success")
            .status("SUCCESS")
            .detail("子流执行完成: " + targetChain.getName() + " (" + subTraceCount + " 节点)")
            .durationMs(durationMs)
            .timestamp(LocalDateTime.now())
            .input(inputSnapshot)
            .output(Map.of(
                "connectionType", "Success",
                "targetRuleChainId", targetId,
                "targetRuleChainName", targetChain.getName(),
                "subTraceCount", subTraceCount
            ))
            .build());
    }

    private Map<String, Object> snapshotContext(RuleContext context) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("deviceKey", context.getDeviceKey());
        snapshot.put("deviceId", context.getDeviceId());
        snapshot.put("deviceName", context.getDeviceName());
        snapshot.put("messageType", context.getMessageType() != null ? context.getMessageType().name() : null);
        snapshot.put("eventTime", context.getEventTime());
        snapshot.put("properties", context.getAllProperties().stream()
            .collect(Collectors.toMap(
                data -> data.getIdentifier(),
                data -> data.getValue(),
                (left, right) -> right,
                LinkedHashMap::new
            )));
        context.getEventData().ifPresent(eventData -> {
            snapshot.put("eventIdentifier", eventData.getIdentifier());
            snapshot.put("eventParams", eventData.getParams().stream()
                .collect(Collectors.toMap(
                    data -> data.getIdentifier(),
                    data -> data.getValue(),
                    (left, right) -> right,
                    LinkedHashMap::new
                )));
        });
        snapshot.put("enrichedData", new LinkedHashMap<>(context.getEnrichedData()));
        return snapshot;
    }

    private Map<String, Object> snapshotResult(RuleNodeExecutor.NodeResult result) {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("success", result.success());
        output.put("connectionType", result.connectionType());
        output.put("message", result.message());
        output.put("data", result.data());
        return output;
    }

    private Map<String, List<Connection>> buildConnectionMap(List<RuleChain.RuleConnection> connections) {
        if (CollectionUtil.isEmpty(connections)) {
            return Collections.emptyMap();
        }
        return connections.stream()
            .collect(Collectors.groupingBy(
                RuleChain.RuleConnection::getSource,
                Collectors.mapping(c -> new Connection(c.getType(), c.getTarget()), Collectors.toList())
            ));
    }

    public record Connection(String type, String target) {}

    /**
     * 判断连接类型是否匹配。null type 视为通用连接（Success）。
     */
    private static boolean matchesConnectionType(String connType, String resultConnType) {
        if (connType == null) {
            return "Success".equals(resultConnType);
        }
        return connType.equals(resultConnType);
    }
}
