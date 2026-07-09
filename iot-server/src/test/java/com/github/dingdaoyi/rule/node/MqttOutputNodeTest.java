package com.github.dingdaoyi.rule.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.entity.enu.PushConfigType;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.config.OutputMqttConfig;
import com.github.dingdaoyi.service.PushConfigService;
import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.spring.server.MqttServerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MqttOutputNodeTest {

    @Mock
    private PushConfigService pushConfigService;
    @Mock
    private MqttServerTemplate mqttServerTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MqttOutputNode node;

    @BeforeEach
    void setUp() throws Exception {
        node = new MqttOutputNode();
        for (var field : MqttOutputNode.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType() == PushConfigService.class) field.set(node, pushConfigService);
            if (field.getType() == MqttServerTemplate.class) field.set(node, mqttServerTemplate);
            if (field.getType() == ObjectMapper.class) field.set(node, objectMapper);
        }
    }

    private RuleContext buildContext() {
        RuleContext ctx = new RuleContext();
        ctx.setDeviceKey("dev-1");
        ctx.setDeviceName("测试设备");
        ctx.setDeviceId(1);
        return ctx;
    }

    private PushConfig buildEnabledMqttConfig() {
        PushConfig cfg = new PushConfig();
        cfg.setId(200);
        cfg.setName("mqtt推送");
        cfg.setType(PushConfigType.MQTT);
        cfg.setIsEnabled(true);
        cfg.setMqttTopic("device/{deviceKey}");
        cfg.setMqttQos(1);
        cfg.setMqttRetain(false);
        return cfg;
    }

    @Test
    void getNodeTypeReturnsOutputMqtt() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.OUTPUT_MQTT);
    }

    @Test
    void failsWhenPushConfigIdIsNull() {
        OutputMqttConfig config = new OutputMqttConfig();

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("推送配置ID不能为空");
    }

    @Test
    void failsWhenPushConfigNotFound() {
        when(pushConfigService.getById(200)).thenReturn(null);
        OutputMqttConfig config = new OutputMqttConfig();
        config.setPushConfigId(200);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("推送配置不存在");
    }

    @Test
    void failsWhenPushConfigDisabled() {
        PushConfig disabled = buildEnabledMqttConfig();
        disabled.setIsEnabled(false);
        when(pushConfigService.getById(200)).thenReturn(disabled);

        OutputMqttConfig config = new OutputMqttConfig();
        config.setPushConfigId(200);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("已禁用");
    }

    @Test
    void publishesToMqttSuccessfully() {
        PushConfig cfg = buildEnabledMqttConfig();
        when(pushConfigService.getById(200)).thenReturn(cfg);

        OutputMqttConfig config = new OutputMqttConfig();
        config.setPushConfigId(200);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("MQTT推送");
        assertThat(result.message()).contains("device/dev-1");
        verify(mqttServerTemplate).publishAll(
                eq("device/dev-1"), any(byte[].class), eq(MqttQoS.valueOf(1)), eq(false));
    }

    @Test
    void usesCustomPayloadWhenProvided() {
        PushConfig cfg = buildEnabledMqttConfig();
        when(pushConfigService.getById(200)).thenReturn(cfg);

        OutputMqttConfig config = new OutputMqttConfig();
        config.setPushConfigId(200);
        config.setCustomPayload("{\"custom\":\"data\"}");

        node.execute(buildContext(), config);

        verify(mqttServerTemplate).publishAll(
                anyString(), argThat((byte[] bytes) -> new String(bytes).contains("\"custom\":\"data\"")),
                any(), anyBoolean());
    }

    @Test
    void defaultsToQosZeroWhenNotSet() {
        PushConfig cfg = buildEnabledMqttConfig();
        cfg.setMqttQos(null); // should default to 0
        when(pushConfigService.getById(200)).thenReturn(cfg);

        OutputMqttConfig config = new OutputMqttConfig();
        config.setPushConfigId(200);

        node.execute(buildContext(), config);

        verify(mqttServerTemplate).publishAll(anyString(), any(byte[].class), eq(MqttQoS.valueOf(0)), anyBoolean());
    }

    @Test
    void resolvesTopicVariables() {
        PushConfig cfg = buildEnabledMqttConfig();
        cfg.setMqttTopic("iot/{deviceKey}/data");
        when(pushConfigService.getById(200)).thenReturn(cfg);

        OutputMqttConfig config = new OutputMqttConfig();
        config.setPushConfigId(200);

        var result = node.execute(buildContext(), config);

        assertThat(result.message()).contains("iot/dev-1/data");
    }
}
