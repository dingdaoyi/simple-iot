package com.github.dingdaoyi.rule.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.NodeConfig;
import com.github.dingdaoyi.rule.config.OutputHttpConfig;
import com.github.dingdaoyi.service.PushConfigService;
import com.github.dingdaoyi.service.push.HttpPushDeliveryService;
import com.github.dingdaoyi.service.push.PushDeliveryResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP推送节点 - 引用推送配置
 * @author dingyunwei
 */
@Slf4j
@Component
public class HttpOutputNode implements RuleNodeExecutor {

    @Resource
    private PushConfigService pushConfigService;

    @Resource
    private HttpPushDeliveryService httpPushDeliveryService;

    @Resource
    private ObjectMapper objectMapper;

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{(\\w+)}");

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.OUTPUT_HTTP;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        OutputHttpConfig cfg = (OutputHttpConfig) config;

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

        // 3. 构建模板化推送配置
        PushConfig runtimeConfig = buildRuntimeConfig(pushConfig, variables);

        // 4. 构建请求体
        String body;
        String customBody = cfg.getCustomBody();
        if (customBody != null) {
            body = replaceTemplate(customBody, variables);
        } else {
            // 默认发送完整上下文
            body = buildDefaultBody(context, variables);
        }

        // 5. 发送请求
        PushDeliveryResult result = httpPushDeliveryService.deliverHttp(runtimeConfig, body, "rule.output.http");
        if (result.isSuccess()) {
            String detail = String.format("HTTP推送 -> %s %s (状态: %s)",
                    runtimeConfig.getHttpMethod(), runtimeConfig.getHttpUrl(), result.getStatusCode());
            return NodeResult.ok(detail);
        }
        return NodeResult.fail(result.getMessage());
    }

    private PushConfig buildRuntimeConfig(PushConfig source, Map<String, Object> variables) {
        PushConfig runtime = new PushConfig();
        runtime.setId(source.getId());
        runtime.setName(source.getName());
        runtime.setType(source.getType());
        runtime.setIsEnabled(source.getIsEnabled());
        runtime.setHttpUrl(replaceTemplate(source.getHttpUrl(), variables));
        runtime.setHttpMethod(source.getHttpMethod() != null ? source.getHttpMethod() : "POST");
        runtime.setHttpTimeout(source.getHttpTimeout());
        runtime.setHttpSignEnabled(source.getHttpSignEnabled());
        runtime.setHttpSignSecret(source.getHttpSignSecret());
        if (source.getHttpHeaders() != null) {
            runtime.setHttpHeaders(source.getHttpHeaders().stream().map(kv -> {
                PushConfig.KeyValue copied = new PushConfig.KeyValue();
                copied.setKey(kv.getKey());
                copied.setValue(replaceTemplate(kv.getValue(), variables));
                return copied;
            }).toList());
        }
        return runtime;
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
     * 构建默认请求体
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
    private String buildDefaultBody(RuleContext context, Map<String, Object> variables) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("deviceKey", context.getDeviceKey());
        body.put("deviceName", context.getDeviceName());
        body.put("deviceId", context.getDeviceId());
        body.put("eventTime", context.getEventTime());
        body.put("messageType", context.getMessageType() != null ? context.getMessageType().name() : null);

        // 属性数据
        if (context.getDecodeResult() != null && context.getDecodeResult().getDataList() != null) {
            Map<String, Object> properties = new LinkedHashMap<>();
            context.getDecodeResult().getDataList().forEach(d ->
                properties.put(d.getIdentifier(), d.getValue())
            );
            body.put("properties", properties);
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
            body.put("event", event);
        });

        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            log.warn("构建HTTP默认请求体失败", e);
            return "{}";
        }
    }
}
