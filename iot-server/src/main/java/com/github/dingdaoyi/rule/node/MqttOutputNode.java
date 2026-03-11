package com.github.dingdaoyi.rule.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.NodeConfig;
import com.github.dingdaoyi.rule.config.OutputMqttConfig;
import com.github.dingdaoyi.service.PushConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.spring.server.MqttServerTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MQTT推送节点 - 引用推送配置
 * @author dingyunwei
 */
@Slf4j
@Component
public class MqttOutputNode implements RuleNodeExecutor {

    @Resource
    private PushConfigService pushConfigService;

    @Resource
    @Lazy
    private MqttServerTemplate mqttServerTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{(\\w+)}");
    private static final Pattern TOPIC_VAR_PATTERN = Pattern.compile("\\{(\\w+)}");

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.OUTPUT_MQTT;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        OutputMqttConfig cfg = (OutputMqttConfig) config;

        Integer pushConfigId = cfg.getPushConfigId();

        if (pushConfigId == null) {
            return NodeResult.fail("推送配置ID不能为空");
        }

        // 1. 获取推送配置
        PushConfig pushConfig = pushConfigService.getById(pushConfigId);
        if (pushConfig == null) {
            return NodeResult.fail("推送配置不存在: " + pushConfigId);
        }

        if (!Boolean.TRUE.equals(pushConfig.getIsEnabled())) {
            return NodeResult.fail("推送配置已禁用: " + pushConfig.getName());
        }

        // 2. 构建模板变量
        Map<String, Object> variables = context.buildVariables();

        // 3. 处理Topic (支持 {deviceKey} 等变量)
        String topic = replaceTopicVariables(pushConfig.getMqttTopic(), variables);

        // 4. 构建Payload
        String payload;
        String customPayload = cfg.getCustomPayload();
        if (customPayload != null) {
            payload = replaceTemplate(customPayload, variables);
        } else {
            payload = buildDefaultPayload(context, variables);
        }

        // 5. 获取QoS和Retain
        int qos = pushConfig.getMqttQos() != null ? pushConfig.getMqttQos() : 0;
        boolean retain = Boolean.TRUE.equals(pushConfig.getMqttRetain());

        // 6. 发布消息
        try {
            MqttQoS mqttQoS = MqttQoS.valueOf(qos);
            byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);

            // 使用 publishAll 广播给所有订阅该 topic 的客户端
            mqttServerTemplate.publishAll(topic, payloadBytes, mqttQoS, retain);

            String detail = String.format("MQTT推送 -> %s (QoS=%d, retain=%b)",
                    topic, qos, retain);
            return NodeResult.ok(detail);

        } catch (Exception e) {
            log.error("MQTT推送失败: topic={}", topic, e);
            return NodeResult.fail("MQTT推送失败: " + e.getMessage());
        }
    }

    /**
     * 替换Topic中的变量 {var}
     */
    private String replaceTopicVariables(String topic, Map<String, Object> variables) {
        if (topic == null || topic.isEmpty()) {
            return "";
        }
        Matcher matcher = TOPIC_VAR_PATTERN.matcher(topic);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = variables.get(key);
            String replacement = value != null ? value.toString() : "";
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 替换模板变量 ${var}
     */
    private String replaceTemplate(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return "";
        }
        Matcher matcher = TEMPLATE_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = variables.get(key);
            String replacement = value != null ? value.toString() : "";
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 构建默认Payload
     * 默认格式:
     * {
     *   "deviceKey": "xxx",
     *   "deviceName": "xxx",
     *   "deviceId": 123,
     *   "eventTime": "2024-03-11 16:30:00",
     *   "messageType": "PROPERTY",
     *   "properties": {"temperature": 25.5, "humidity": 60}
     * }
     */
    private String buildDefaultPayload(RuleContext context, Map<String, Object> variables) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("deviceKey", context.getDeviceKey());
        payload.put("deviceName", context.getDeviceName());
        payload.put("deviceId", context.getDeviceId());
        payload.put("eventTime", context.getEventTime());
        payload.put("messageType", context.getMessageType() != null ? context.getMessageType().name() : null);

        // 属性数据
        if (context.getDecodeResult() != null && context.getDecodeResult().getDataList() != null) {
            Map<String, Object> properties = new LinkedHashMap<>();
            context.getDecodeResult().getDataList().forEach(d ->
                properties.put(d.getIdentifier(), d.getValue())
            );
            payload.put("properties", properties);
        }

        // 事件数据
        context.getEventData().ifPresent(eventData -> {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("identifier", eventData.getIdentifier() != null ? eventData.getIdentifier() : "");
            // 事件参数
            if (eventData.getParams() != null && !eventData.getParams().isEmpty()) {
                Map<String, Object> eventParams = new LinkedHashMap<>();
                eventData.getParams().forEach(p -> eventParams.put(p.getIdentifier(), p.getValue()));
                event.put("params", eventParams);
            }
            payload.put("event", event);
        });

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return "{}";
        }
    }
}
