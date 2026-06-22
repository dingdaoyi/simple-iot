package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.model.vo.RuleChainValidationResultVo;
import com.github.dingdaoyi.rule.config.InputPropertyConfig;
import com.github.dingdaoyi.rule.config.OutputMessageConfig;
import com.github.dingdaoyi.service.impl.RuleChainServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RuleChainValidationServiceTest {

    private final RuleChainServiceImpl service = new RuleChainServiceImpl();

    @Test
    void validateReportsInvalidConnectionAndUnreachableNode() {
        RuleChain ruleChain = ruleChain(
            List.of(
                node("input", RuleNodeType.INPUT_PROPERTY, "属性输入", new InputPropertyConfig()),
                node("output", RuleNodeType.OUTPUT_MESSAGE, "消息输出", new OutputMessageConfig()),
                node("orphan", RuleNodeType.OUTPUT_MESSAGE, "孤立输出", new OutputMessageConfig())
            ),
            List.of(
                connection("output", "input", "Success")
            )
        );

        RuleChainValidationResultVo result = service.validateDraft(ruleChain);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anySatisfy(error -> {
            assertThat(error.getCode()).isEqualTo("INVALID_CONNECTION");
            assertThat(error.getMessage()).contains("输出节点是终端节点");
        });
        assertThat(result.getWarnings()).anySatisfy(warning -> {
            assertThat(warning.getCode()).isEqualTo("UNREACHABLE_NODE");
            assertThat(warning.getNodeId()).isEqualTo("orphan");
        });
        assertThat(result.getReachableNodeIds()).containsExactly("input");
    }

    @Test
    void validatePassesSimpleInputToOutputChain() {
        RuleChain ruleChain = ruleChain(
            List.of(
                node("input", RuleNodeType.INPUT_PROPERTY, "属性输入", new InputPropertyConfig()),
                node("output", RuleNodeType.OUTPUT_MESSAGE, "消息输出", new OutputMessageConfig())
            ),
            List.of(connection("input", "output", "Success"))
        );

        RuleChainValidationResultVo result = service.validateDraft(ruleChain);

        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getReachableNodeIds()).containsExactlyInAnyOrder("input", "output");
    }

    private static RuleChain ruleChain(List<RuleChain.RuleNode> nodes, List<RuleChain.RuleConnection> connections) {
        RuleChain ruleChain = new RuleChain();
        ruleChain.setName("草稿规则链");
        ruleChain.setIsEnabled(true);
        RuleChain.RuleChainConfiguration configuration = new RuleChain.RuleChainConfiguration();
        configuration.setNodes(nodes);
        configuration.setConnections(connections);
        ruleChain.setConfiguration(configuration);
        return ruleChain;
    }

    private static RuleChain.RuleNode node(String id, RuleNodeType type, String name, com.github.dingdaoyi.rule.config.NodeConfig config) {
        RuleChain.RuleNode node = new RuleChain.RuleNode();
        node.setId(id);
        node.setType(type.name());
        node.setName(name);
        node.setConfig(config);
        return node;
    }

    private static RuleChain.RuleConnection connection(String source, String target, String type) {
        RuleChain.RuleConnection connection = new RuleChain.RuleConnection();
        connection.setSource(source);
        connection.setTarget(target);
        connection.setType(type);
        return connection;
    }
}
