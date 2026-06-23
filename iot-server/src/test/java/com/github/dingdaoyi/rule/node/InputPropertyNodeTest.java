package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.InputPropertyConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InputPropertyNodeTest {

    private InputPropertyNode node;

    @BeforeEach
    void setUp() {
        node = new InputPropertyNode();
    }

    @Test
    void getNodeTypeReturnsInputProperty() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.INPUT_PROPERTY);
    }

    @Test
    void executeSucceedsWhenMessageTypeIsProperty() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.PROPERTY);
        
        DeviceData temp = new DeviceData();
        temp.setIdentifier("temperature");
        temp.setValue(36.5);
        
        DeviceData humidity = new DeviceData();
        humidity.setIdentifier("humidity");
        humidity.setValue(62);
        
        DecodeResult decodeResult = new DecodeResult();
        decodeResult.setDataList(List.of(temp, humidity));
        context.setDecodeResult(decodeResult);
        
        InputPropertyConfig config = new InputPropertyConfig();

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("Success");
        assertThat(result.message()).contains("属性上报: 2 个属性");
    }

    @Test
    void executeFailsWhenMessageTypeIsNotProperty() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.EVENT);
        InputPropertyConfig config = new InputPropertyConfig();

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("False");
        assertThat(result.message()).contains("非属性上报消息");
    }

    @Test
    void executeReportsZeroPropertiesWhenDecodeResultIsEmpty() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.PROPERTY);
        
        DecodeResult decodeResult = new DecodeResult();
        decodeResult.setDataList(List.of());
        context.setDecodeResult(decodeResult);
        
        InputPropertyConfig config = new InputPropertyConfig();

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("0 个属性");
    }

    @Test
    void executeHandlesNullDecodeResultGracefully() {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.PROPERTY);
        // decodeResult is null
        
        InputPropertyConfig config = new InputPropertyConfig();

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("0 个属性");
    }
}
