package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.FilterEventTypeConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventTypeFilterNodeTest {

    private EventTypeFilterNode node;

    @BeforeEach
    void setUp() {
        node = new EventTypeFilterNode();
    }

    @Test
    void getNodeTypeReturnsFilterEventType() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.FILTER_EVENT_TYPE);
    }

    @Test
    void executeReturnsFalseWhenNoEventData() {
        RuleContext context = new RuleContext();
        FilterEventTypeConfig config = new FilterEventTypeConfig();
        config.setIdentifier("alarm");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("False");
        assertThat(result.message()).contains("没有事件数据");
    }

    @Test
    void executeReturnsTrueWhenNoIdentifierConfigured() {
        RuleContext context = contextWithEvent("alarm", List.of());
        FilterEventTypeConfig config = new FilterEventTypeConfig();
        // no identifier set

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("True");
        assertThat(result.message()).contains("未配置事件过滤");
    }

    @Test
    void executeReturnsTrueWhenEventIdentifierMatches() {
        RuleContext context = contextWithEvent("alarm", List.of());
        FilterEventTypeConfig config = new FilterEventTypeConfig();
        config.setIdentifier("alarm");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("True");
        assertThat(result.message()).contains("事件匹配: alarm");
    }

    @Test
    void executeReturnsFalseWhenEventIdentifierDoesNotMatch() {
        RuleContext context = contextWithEvent("temperature_warning", List.of());
        FilterEventTypeConfig config = new FilterEventTypeConfig();
        config.setIdentifier("alarm");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("False");
        assertThat(result.message()).contains("事件不匹配");
    }

    @Test
    void executeReturnsFalseWhenParamNotFound() {
        RuleContext context = contextWithEvent("alarm", List.of());
        FilterEventTypeConfig config = new FilterEventTypeConfig();
        config.setIdentifier("alarm");
        config.setParamIdentifier("severity");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("False");
        assertThat(result.message()).contains("事件参数为空");
    }

    @ParameterizedTest
    @CsvSource({
        "GT, 80, 70, true",
        "GT, 60, 70, false",
        "GE, 70, 70, true",
        "GE, 60, 70, false",
        "EQ, 70, 70, true",
        "EQ, 60, 70, false",
        "LT, 60, 70, true",
        "LT, 80, 70, false",
        "LE, 70, 70, true",
        "LE, 80, 70, false",
        "NE, 60, 70, true",
        "NE, 70, 70, false"
    })
    void executeEvaluatesNumericParamConditionsCorrectly(String operator, int actualValue, int thresholdValue, boolean expected) {
        DeviceData param = new DeviceData();
        param.setIdentifier("severity");
        param.setValue(actualValue);
        
        RuleContext context = contextWithEvent("alarm", List.of(param));
        FilterEventTypeConfig config = new FilterEventTypeConfig();
        config.setIdentifier("alarm");
        config.setParamIdentifier("severity");
        config.setOperator(operator);
        config.setValue(String.valueOf(thresholdValue));

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo(expected ? "True" : "False");
    }

    @Test
    void executeEvaluatesStringParamConditions() {
        DeviceData param = new DeviceData();
        param.setIdentifier("level");
        param.setValue("critical");
        
        RuleContext context = contextWithEvent("alarm", List.of(param));
        FilterEventTypeConfig config = new FilterEventTypeConfig();
        config.setIdentifier("alarm");
        config.setParamIdentifier("level");
        config.setOperator("EQ");
        config.setValue("critical");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("True");
    }

    @Test
    void executeReturnsFalseWhenParamIdentifierNotFound() {
        DeviceData param = new DeviceData();
        param.setIdentifier("other_param");
        param.setValue(100);
        
        RuleContext context = contextWithEvent("alarm", List.of(param));
        FilterEventTypeConfig config = new FilterEventTypeConfig();
        config.setIdentifier("alarm");
        config.setParamIdentifier("missing_param");

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("False");
        assertThat(result.message()).contains("事件参数不存在");
    }

    private RuleContext contextWithEvent(String identifier, List<DeviceData> params) {
        RuleContext context = new RuleContext();
        context.setMessageType(RuleContext.MessageType.EVENT);
        
        DeviceEventData eventData = new DeviceEventData(identifier, com.github.dingdaoyi.proto.model.tsl.EventTypeEnum.INFO);
        eventData.setParams(params);
        
        com.github.dingdaoyi.proto.model.DecodeResult decodeResult = new com.github.dingdaoyi.proto.model.DecodeResult();
        decodeResult.setEventData(eventData);
        context.setDecodeResult(decodeResult);
        
        return context;
    }
}
