package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.FilterPropertyConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyFilterNodeTest {

    private PropertyFilterNode node;

    @BeforeEach
    void setUp() {
        node = new PropertyFilterNode();
    }

    @Test
    void getNodeTypeReturnsFilterProperty() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.FILTER_PROPERTY);
    }

    @Test
    void executeFailsWhenConfigIncomplete() {
        RuleContext context = new RuleContext();
        FilterPropertyConfig config = new FilterPropertyConfig();
        config.setIdentifier("temperature");
        config.setOperator("GT");
        // missing value

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("配置参数不完整");
    }

    @Test
    void executeReturnsFalseWhenPropertyNotFound() {
        RuleContext context = new RuleContext();
        FilterPropertyConfig config = new FilterPropertyConfig();
        config.setIdentifier("missing_property");
        config.setOperator("GT");
        config.setValue(30);

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("False");
        assertThat(result.message()).contains("属性不存在");
    }

    @ParameterizedTest
    @CsvSource({
        "EQ, 36.5, 36.5, true",
        "EQ, 36.5, 37.0, false",
        "NE, 36.5, 37.0, true",
        "NE, 36.5, 36.5, false",
        "GT, 37.0, 36.5, true",
        "GT, 36.0, 36.5, false",
        "GE, 36.5, 36.5, true",
        "GE, 37.0, 36.5, true",
        "GE, 36.0, 36.5, false",
        "LT, 36.0, 36.5, true",
        "LT, 37.0, 36.5, false",
        "LE, 36.5, 36.5, true",
        "LE, 36.0, 36.5, true",
        "LE, 37.0, 36.5, false"
    })
    void executeEvaluatesNumericComparisonsCorrectly(String operator, double actualValue, double thresholdValue, boolean expected) {
        RuleContext context = contextWithProperty("temperature", actualValue);
        FilterPropertyConfig config = new FilterPropertyConfig();
        config.setIdentifier("temperature");
        config.setOperator(operator);
        config.setValue(thresholdValue);

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo(expected ? "True" : "False");
        assertThat(result.message()).contains("temperature=" + actualValue);
    }

    @ParameterizedTest
    @CsvSource({
        "EQ, 'online', 'online', true",
        "EQ, 'online', 'offline', false",
        "NE, 'online', 'offline', true",
        "NE, 'online', 'online', false",
        "CONTAINS, 'device-001-sensor', 'sensor', true",
        "CONTAINS, 'device-001-actuator', 'sensor', false"
    })
    void executeEvaluatesStringComparisonsCorrectly(String operator, String actualValue, String thresholdValue, boolean expected) {
        RuleContext context = contextWithProperty("status", actualValue);
        FilterPropertyConfig config = new FilterPropertyConfig();
        config.setIdentifier("status");
        config.setOperator(operator);
        config.setValue(thresholdValue);

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo(expected ? "True" : "False");
    }

    @ParameterizedTest
    @CsvSource({
        "EQ, true, true, true",
        "EQ, true, false, false",
        "NE, true, false, true",
        "NE, true, true, false"
    })
    void executeEvaluatesBooleanComparisonsCorrectly(String operator, boolean actualValue, boolean thresholdValue, boolean expected) {
        RuleContext context = contextWithProperty("alarm_enabled", actualValue);
        FilterPropertyConfig config = new FilterPropertyConfig();
        config.setIdentifier("alarm_enabled");
        config.setOperator(operator);
        config.setValue(thresholdValue);

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo(expected ? "True" : "False");
    }

    @Test
    void executeIncludesUnitInDetailWhenAvailable() {
        RuleContext context = contextWithProperty("temperature", 36.5);
        TslProperty tslProperty = new TslProperty();
        tslProperty.setIdentifier("temperature");
        tslProperty.setUnit("℃");
        TslModel tslModel = new TslModel();
        tslModel.setProperties(List.of(tslProperty));
        context.setTslModel(tslModel);

        FilterPropertyConfig config = new FilterPropertyConfig();
        config.setIdentifier("temperature");
        config.setOperator("GT");
        config.setValue(30);

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.message()).contains("36.5℃");
    }

    @Test
    void executeReturnsFalseForInvalidOperator() {
        RuleContext context = contextWithProperty("temperature", 36.5);
        FilterPropertyConfig config = new FilterPropertyConfig();
        config.setIdentifier("temperature");
        config.setOperator("INVALID_OP");
        config.setValue(30);

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("False");
    }

    private RuleContext contextWithProperty(String identifier, Object value) {
        RuleContext context = new RuleContext();
        DeviceData data = new DeviceData();
        data.setIdentifier(identifier);
        data.setValue(value);
        
        DecodeResult decodeResult = new DecodeResult();
        decodeResult.setDataList(List.of(data));
        context.setDecodeResult(decodeResult);
        
        return context;
    }
}
