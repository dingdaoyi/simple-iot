package com.github.dingdaoyi.service;

import com.github.dingdaoyi.controller.iot.dto.RuleChainDebugRequest;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.model.vo.RuleChainDebugResultVo;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.rule.RuleChainEngine;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.FilterPropertyConfig;
import com.github.dingdaoyi.rule.config.InputPropertyConfig;
import com.github.dingdaoyi.service.impl.RuleChainServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RuleChainDebugServiceTest {

    private RuleChainServiceImpl service;

    @BeforeEach
    void setUp() {
        RuleChainEngine engine = new RuleChainEngine();
        ReflectionTestUtils.setField(engine, "executors", List.of(
            new com.github.dingdaoyi.rule.node.InputPropertyNode(),
            new com.github.dingdaoyi.rule.node.PropertyFilterNode()
        ));
        engine.init();

        service = new RuleChainServiceImpl();
        ReflectionTestUtils.setField(service, "ruleChainEngine", engine);
    }

    @Test
    void debugExecutesDraftRuleChainAndReturnsRichTrace() {
        RuleChainDebugRequest request = new RuleChainDebugRequest();
        request.setRuleChain(ruleChain(
            List.of(
                inputNode("input", "属性输入"),
                propertyFilterNode("filter", "温度过滤", "temperature", "GT", 30)
            ),
            List.of(connection("input", "filter", "Success"))
        ));
        request.setMessageType(RuleContext.MessageType.PROPERTY);
        request.setDeviceKey("device-001");
        request.setDeviceName("温控器");
        request.setProperties(Map.of("temperature", 36.5));
        request.setEnrichedData(Map.of("scenario", "uat"));

        RuleChainDebugResultVo result = service.debug(request);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getRuleChainName()).isEqualTo("草稿规则链");
        assertThat(result.getExecutedNodeCount()).isEqualTo(2);
        assertThat(result.getDurationMs()).isGreaterThanOrEqualTo(0);
        assertThat(result.getTraces()).hasSize(2);

        RuleContext.ExecutionTrace inputTrace = result.getTraces().get(0);
        assertThat(inputTrace.getNodeId()).isEqualTo("input");
        assertThat(inputTrace.getNodeType()).isEqualTo("INPUT_PROPERTY");
        assertThat(inputTrace.getStatus()).isEqualTo("SUCCESS");
        assertThat(inputTrace.getInput()).containsEntry("messageType", "PROPERTY");
        assertThat(inputTrace.getOutput()).containsEntry("connectionType", "Success");
        assertThat(inputTrace.getDurationMs()).isGreaterThanOrEqualTo(0);

        RuleContext.ExecutionTrace filterTrace = result.getTraces().get(1);
        assertThat(filterTrace.getNodeId()).isEqualTo("filter");
        assertThat(filterTrace.getNodeType()).isEqualTo("FILTER_PROPERTY");
        assertThat(filterTrace.getConnectionType()).isEqualTo("True");
        assertThat(filterTrace.getStatus()).isEqualTo("SUCCESS");
        assertThat(filterTrace.getDetail()).contains("temperature=36.5");
        assertThat(filterTrace.getOutput()).containsEntry("success", true);
    }

    @Test
    void debugMarksFailureWhenNodeExecutorThrows() {
        RuleChainEngine engine = new RuleChainEngine();
        ReflectionTestUtils.setField(engine, "executors", List.of(new ThrowingExecutor()));
        engine.init();
        ReflectionTestUtils.setField(service, "ruleChainEngine", engine);

        RuleChainDebugRequest request = new RuleChainDebugRequest();
        request.setRuleChain(ruleChain(List.of(inputNode("input", "属性输入")), List.of()));
        request.setMessageType(RuleContext.MessageType.PROPERTY);
        request.setProperties(Map.of("temperature", 36.5));

        RuleChainDebugResultVo result = service.debug(request);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getTraces()).hasSize(1);
        assertThat(result.getTraces().getFirst().getStatus()).isEqualTo("ERROR");
        assertThat(result.getTraces().getFirst().getError()).isEqualTo("boom");
    }

    private static RuleChain ruleChain(List<RuleChain.RuleNode> nodes, List<RuleChain.RuleConnection> connections) {
        RuleChain ruleChain = new RuleChain();
        ruleChain.setId(100);
        ruleChain.setName("草稿规则链");
        ruleChain.setIsEnabled(true);
        RuleChain.RuleChainConfiguration configuration = new RuleChain.RuleChainConfiguration();
        configuration.setNodes(nodes);
        configuration.setConnections(connections);
        ruleChain.setConfiguration(configuration);
        return ruleChain;
    }

    private static RuleChain.RuleNode inputNode(String id, String name) {
        RuleChain.RuleNode node = node(id, RuleNodeType.INPUT_PROPERTY, name);
        node.setConfig(new InputPropertyConfig());
        return node;
    }

    private static RuleChain.RuleNode propertyFilterNode(String id, String name, String identifier, String operator, Object value) {
        FilterPropertyConfig config = new FilterPropertyConfig();
        config.setIdentifier(identifier);
        config.setOperator(operator);
        config.setValue(value);
        RuleChain.RuleNode node = node(id, RuleNodeType.FILTER_PROPERTY, name);
        node.setConfig(config);
        return node;
    }

    private static RuleChain.RuleNode node(String id, RuleNodeType type, String name) {
        RuleChain.RuleNode node = new RuleChain.RuleNode();
        node.setId(id);
        node.setType(type.name());
        node.setName(name);
        return node;
    }

    private static RuleChain.RuleConnection connection(String source, String target, String type) {
        RuleChain.RuleConnection connection = new RuleChain.RuleConnection();
        connection.setSource(source);
        connection.setTarget(target);
        connection.setType(type);
        return connection;
    }

    private static class ThrowingExecutor implements RuleNodeExecutor {
        @Override
        public RuleNodeType getNodeType() {
            return RuleNodeType.INPUT_PROPERTY;
        }

        @Override
        public NodeResult execute(RuleContext context, com.github.dingdaoyi.rule.config.NodeConfig config) {
            throw new IllegalStateException("boom");
        }
    }
}
