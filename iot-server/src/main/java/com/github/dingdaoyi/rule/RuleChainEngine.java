package com.github.dingdaoyi.rule;

import cn.hutool.core.collection.CollectionUtil;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 规则链执行引擎
 * @author dingyunwei
 */
@Slf4j
@Service
public class RuleChainEngine {

    @Resource
    private List<RuleNodeExecutor> executors;

    private Map<RuleNodeType, RuleNodeExecutor> executorMap;

    @jakarta.annotation.PostConstruct
    public void init() {
        this.executorMap = executors.stream()
            .collect(Collectors.toMap(RuleNodeExecutor::getNodeType, e -> e));
    }

    /**
     * 执行规则链
     */
    public void execute(RuleChain ruleChain, RuleContext context) {
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
            .forEach(inputNode -> executeNode(inputNode, nodeMap, connectionMap, context));
    }

    private void executeNode(RuleChain.RuleNode node,
                             Map<String, RuleChain.RuleNode> nodeMap,
                             Map<String, List<Connection>> connectionMap,
                             RuleContext context) {
        RuleNodeType nodeType = RuleNodeType.valueOf(node.getType());
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
                .filter(c -> c.type().equals(result.connectionType()))
                .forEach(c -> {
                    RuleChain.RuleNode nextNode = nodeMap.get(c.target());
                    if (nextNode != null) {
                        executeNode(nextNode, nodeMap, connectionMap, context);
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
}
