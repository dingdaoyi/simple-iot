package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.InputOnlineConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputOnlineNodeTest {

    private InputOnlineNode node;

    @BeforeEach
    void setUp() {
        node = new InputOnlineNode();
    }

    @Test
    void getNodeTypeReturnsInputOnline() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.INPUT_ONLINE);
    }

    @Test
    void executeFailsWhenMessageTypeIsNotOnlineOrOffline() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.PROPERTY);
        InputOnlineConfig config = new InputOnlineConfig();

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("非上下线消息");
    }

    @Test
    void executeSucceedsForOnlineMessageWhenStatusIsAll() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.ONLINE);
        InputOnlineConfig config = new InputOnlineConfig();
        config.setOnlineStatus("all");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("设备上线");
    }

    @Test
    void executeSucceedsForOfflineMessageWhenStatusIsAll() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.OFFLINE);
        InputOnlineConfig config = new InputOnlineConfig();
        config.setOnlineStatus("all");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("设备下线");
    }

    @Test
    void executeSucceedsForOnlineMessageWhenStatusIsOnline() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.ONLINE);
        InputOnlineConfig config = new InputOnlineConfig();
        config.setOnlineStatus("online");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("设备上线");
    }

    @Test
    void executeFailsForOfflineMessageWhenStatusIsOnline() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.OFFLINE);
        InputOnlineConfig config = new InputOnlineConfig();
        config.setOnlineStatus("online");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("上下线状态不匹配");
    }

    @Test
    void executeSucceedsForOfflineMessageWhenStatusIsOffline() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.OFFLINE);
        InputOnlineConfig config = new InputOnlineConfig();
        config.setOnlineStatus("offline");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("设备下线");
    }

    @Test
    void executeFailsForOnlineMessageWhenStatusIsOffline() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.ONLINE);
        InputOnlineConfig config = new InputOnlineConfig();
        config.setOnlineStatus("offline");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("上下线状态不匹配");
    }

    @Test
    void executeDefaultsToAllWhenOnlineStatusIsNull() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.ONLINE);
        InputOnlineConfig config = new InputOnlineConfig();
        // onlineStatus is null, should default to "all"

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("设备上线");
    }
}
