package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.FilterScriptConfig;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptFilterNodeTest {

    private final ScriptFilterNode node = new ScriptFilterNode();

    @Test
    void javascriptFilterReturnsTrueWhenScriptExpressionMatchesContext() {
        FilterScriptConfig config = config("return msg.temperature > 30 && device.deviceKey === 'device-001' && enriched.tenant === 'demo';");
        RuleContext context = context(Map.of("temperature", 36.5), Map.of("tenant", "demo"));

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("True");
        assertThat(result.message()).contains("脚本返回 true");
        assertThat(result.data()).containsEntry("result", true);
    }

    @Test
    void javascriptFilterReturnsFalseWhenScriptExpressionDoesNotMatchContext() {
        FilterScriptConfig config = config("return msg.temperature > 50;");
        RuleContext context = context(Map.of("temperature", 36.5), Map.of());

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.connectionType()).isEqualTo("False");
        assertThat(result.message()).contains("脚本返回 false");
        assertThat(result.data()).containsEntry("result", false);
    }

    @Test
    void javascriptFilterReportsFailureWhenScriptThrows() {
        FilterScriptConfig config = config("throw new Error('bad script');");
        RuleContext context = context(Map.of("temperature", 36.5), Map.of());

        RuleNodeExecutor.NodeResult result = node.execute(context, config);

        assertThat(result.success()).isFalse();
        assertThat(result.connectionType()).isEqualTo("Failure");
        assertThat(result.message()).contains("脚本执行失败").contains("bad script");
    }

    private static FilterScriptConfig config(String script) {
        FilterScriptConfig config = new FilterScriptConfig();
        config.setLanguage("javascript");
        config.setScript(script);
        return config;
    }

    private static RuleContext context(Map<String, Object> properties, Map<String, Object> enrichedData) {
        RuleContext context = new RuleContext();
        context.setDeviceKey("device-001");
        context.setDeviceId(1001);
        context.setDeviceName("温控器");
        context.setMessageType(RuleContext.MessageType.PROPERTY);
        context.getEnrichedData().putAll(enrichedData);

        DecodeResult decodeResult = new DecodeResult();
        properties.forEach((identifier, value) -> decodeResult.getDataList().add(new DeviceData(identifier, DataTypeEnum.DOUBLE, value)));
        context.setDecodeResult(decodeResult);
        return context;
    }
}
