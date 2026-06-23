package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.InputEventConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InputEventNodeTest {

    private InputEventNode node;

    @BeforeEach
    void setUp() {
        node = new InputEventNode();
    }

    @Test
    void getNodeTypeReturnsInputEvent() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.INPUT_EVENT);
    }

    @Test
    void executeFailsWhenMessageTypeIsNotEvent() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.PROPERTY);
        InputEventConfig config = new InputEventConfig();

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isFalse();
        assertThat(result.connectionType()).isEqualTo("Failure");
        assertThat(result.message()).contains("非事件上报消息");
    }

    @Test
    void executeFailsWhenEventDataIsNull() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.EVENT);
        // no event data
        InputEventConfig config = new InputEventConfig();

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("无事件数据");
    }

    @Test
    void executeSucceedsWhenNoIdentifierFilterConfigured() {
        RuleContext context = contextWithEvent("alarm");
        InputEventConfig config = new InputEventConfig();
        // no identifiers filter

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("Success");
        assertThat(result.message()).contains("事件上报: alarm");
    }

    @Test
    void executeSucceedsWhenEventIdentifierIsInFilterList() {
        RuleContext context = contextWithEvent("temperature_alarm");
        InputEventConfig config = new InputEventConfig();
        config.setIdentifiers(List.of("temperature_alarm", "humidity_alarm", "power_alarm"));

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("Success");
        assertThat(result.message()).contains("事件上报: temperature_alarm");
    }

    @Test
    void executeFailsWhenEventIdentifierIsNotInFilterList() {
        RuleContext context = contextWithEvent("door_open");
        InputEventConfig config = new InputEventConfig();
        config.setIdentifiers(List.of("temperature_alarm", "humidity_alarm", "power_alarm"));

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isFalse();
        assertThat(result.connectionType()).isEqualTo("Failure");
        assertThat(result.message()).contains("事件不在过滤列表中");
    }

    @Test
    void executeSucceedsWithEmptyIdentifiersList() {
        RuleContext context = contextWithEvent("any_event");
        InputEventConfig config = new InputEventConfig();
        config.setIdentifiers(List.of());

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("事件上报: any_event");
    }

    private RuleContext contextWithEvent(String identifier) {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.EVENT);
        
        DeviceEventData eventData = new DeviceEventData(identifier, com.github.dingdaoyi.proto.model.tsl.EventTypeEnum.INFO);
        
        DecodeResult decodeResult = new DecodeResult();
        decodeResult.setEventData(eventData);
        context.setDecodeResult(decodeResult);
        
        return context;
    }
}
