package com.github.dingdaoyi.rule;

import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.config.NodeConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RuleChainEngineTest {

    private RuleChainEngine engine;
    private RecordingExecutor inputExecutor;
    private RecordingExecutor trueExecutor;
    private RecordingExecutor falseExecutor;
    private RecordingExecutor outputExecutor;

    @BeforeEach
    void setUp() {
        inputExecutor = new RecordingExecutor(RuleNodeType.INPUT_PROPERTY, RuleNodeExecutor.NodeResult.ok());
        trueExecutor = new RecordingExecutor(RuleNodeType.FILTER_PROPERTY, RuleNodeExecutor.NodeResult.of(true, "matched", "unmatched"));
        falseExecutor = new RecordingExecutor(RuleNodeType.FILTER_EVENT_TYPE, RuleNodeExecutor.NodeResult.of(false));
        outputExecutor = new RecordingExecutor(RuleNodeType.OUTPUT_MESSAGE, RuleNodeExecutor.NodeResult.success("sent"));

        engine = new RuleChainEngine();
        ReflectionTestUtils.setField(engine, "executors", List.of(inputExecutor, trueExecutor, falseExecutor, outputExecutor));
        engine.init();
    }

    @Test
    void executeStartsFromInputNodesAndFollowsMatchingConnectionTypes() {
        RuleChain ruleChain = enabledChain(
            List.of(
                node("input", RuleNodeType.INPUT_PROPERTY, "Input"),
                node("filterTrue", RuleNodeType.FILTER_PROPERTY, "True Filter"),
                node("filterFalse", RuleNodeType.FILTER_EVENT_TYPE, "False Filter"),
                node("output", RuleNodeType.OUTPUT_MESSAGE, "Output")
            ),
            List.of(
                connection("input", "filterTrue", "Success"),
                connection("input", "filterFalse", "Failure"),
                connection("filterTrue", "output", "True"),
                connection("filterFalse", "output", "False")
            )
        );
        RuleContext context = new RuleContext();

        engine.execute(ruleChain, context);

        assertThat(context.getRuleChainId()).isEqualTo(100);
        assertThat(inputExecutor.executedNodeConfigs).hasSize(1);
        assertThat(trueExecutor.executedNodeConfigs).hasSize(1);
        assertThat(falseExecutor.executedNodeConfigs).isEmpty();
        assertThat(outputExecutor.executedNodeConfigs).hasSize(1);
        assertThat(context.getTraces())
            .extracting(RuleContext.ExecutionTrace::getNodeId)
            .containsExactly("input", "filterTrue", "output");
        assertThat(context.getTraces())
            .extracting(RuleContext.ExecutionTrace::getConnectionType)
            .containsExactly("Success", "True", "Success");
    }

    @Test
    void executeSkipsDisabledRuleChain() {
        RuleChain ruleChain = enabledChain(List.of(node("input", RuleNodeType.INPUT_PROPERTY, "Input")), List.of());
        ruleChain.setIsEnabled(false);

        engine.execute(ruleChain, new RuleContext());

        assertThat(inputExecutor.executedNodeConfigs).isEmpty();
    }

    @Test
    void executeAddsFailureTraceWhenNodeExecutorThrows() {
        RecordingExecutor throwingInput = new RecordingExecutor(RuleNodeType.INPUT_PROPERTY, RuleNodeExecutor.NodeResult.ok());
        throwingInput.exception = new IllegalStateException("boom");
        engine = new RuleChainEngine();
        ReflectionTestUtils.setField(engine, "executors", List.of(throwingInput));
        engine.init();
        RuleChain ruleChain = enabledChain(List.of(node("input", RuleNodeType.INPUT_PROPERTY, "Input")), List.of());
        RuleContext context = new RuleContext();

        engine.execute(ruleChain, context);

        assertThat(context.getTraces()).hasSize(1);
        assertThat(context.getTraces().getFirst().getNodeId()).isEqualTo("input");
        assertThat(context.getTraces().getFirst().getConnectionType()).isEqualTo("Failure");
        assertThat(context.getTraces().getFirst().getDetail()).isEqualTo("boom");
    }

    private static RuleChain enabledChain(List<RuleChain.RuleNode> nodes, List<RuleChain.RuleConnection> connections) {
        RuleChain ruleChain = new RuleChain();
        ruleChain.setId(100);
        ruleChain.setIsEnabled(true);
        RuleChain.RuleChainConfiguration configuration = new RuleChain.RuleChainConfiguration();
        configuration.setNodes(nodes);
        configuration.setConnections(connections);
        ruleChain.setConfiguration(configuration);
        return ruleChain;
    }

    private static RuleChain.RuleNode node(String id, RuleNodeType type, String name) {
        RuleChain.RuleNode node = new RuleChain.RuleNode();
        node.setId(id);
        node.setType(type.name());
        node.setName(name);
        node.setConfig(new NodeConfig() {});
        return node;
    }

    private static RuleChain.RuleConnection connection(String source, String target, String type) {
        RuleChain.RuleConnection connection = new RuleChain.RuleConnection();
        connection.setSource(source);
        connection.setTarget(target);
        connection.setType(type);
        return connection;
    }

    private static class RecordingExecutor implements RuleNodeExecutor {
        private final RuleNodeType nodeType;
        private final NodeResult result;
        private final List<NodeConfig> executedNodeConfigs = new ArrayList<>();
        private RuntimeException exception;

        private RecordingExecutor(RuleNodeType nodeType, NodeResult result) {
            this.nodeType = nodeType;
            this.result = result;
        }

        @Override
        public RuleNodeType getNodeType() {
            return nodeType;
        }

        @Override
        public NodeResult execute(RuleContext context, NodeConfig config) {
            executedNodeConfigs.add(config);
            if (exception != null) {
                throw exception;
            }
            return result;
        }
    }
}
