package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.InputScheduleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputScheduleNodeTest {

    private InputScheduleNode node;

    @BeforeEach
    void setUp() {
        node = new InputScheduleNode();
    }

    @Test
    void getNodeTypeReturnsInputSchedule() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.INPUT_SCHEDULE);
    }

    @Test
    void executeReturnsOkWithCronExpression() {
        RuleContext context = new RuleContext();
        InputScheduleConfig config = new InputScheduleConfig();
        config.setCron("0 0 23 * * ?");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("0 0 23 * * ?");
    }

    @Test
    void executeUsesDefaultCronWhenConfigIsNull() {
        RuleContext context = new RuleContext();
        InputScheduleConfig config = new InputScheduleConfig();
        // cron is null

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("* * * * * ?");
    }
}
