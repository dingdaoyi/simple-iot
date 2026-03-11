package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.MessageReceive;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.NodeConfig;
import com.github.dingdaoyi.rule.config.OutputMessageConfig;
import com.github.dingdaoyi.service.MessageReceiveService;
import com.github.dingdaoyi.service.NotificationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 消息推送节点 - 复用现有 MessageReceive
 * @author dingyunwei
 */
@Component
public class MessageOutputNode implements RuleNodeExecutor {

    @Resource
    private MessageReceiveService messageReceiveService;

    @Resource
    private List<NotificationService> notificationServices;

    private Map<String, NotificationService> notificationServiceMap;

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{(\\w+)}");

    @jakarta.annotation.PostConstruct
    public void init() {
        this.notificationServiceMap = notificationServices.stream()
            .collect(Collectors.toMap(s -> s.getNotifyType().name(), s -> s));
    }

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.OUTPUT_MESSAGE;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        OutputMessageConfig cfg = (OutputMessageConfig) config;

        Integer messageReceiveId = cfg.getMessageReceiveId();

        if (messageReceiveId == null) {
            return NodeResult.fail("消息配置ID不能为空");
        }

        // 1. 获取消息接收配置
        MessageReceive messageReceive = messageReceiveService.getById(messageReceiveId);
        if (messageReceive == null) {
            return NodeResult.fail("消息配置不存在: " + messageReceiveId);
        }

        // 2. 构建模板变量
        Map<String, Object> variables = buildTemplateVariables(context);

        // 3. 构建消息参数
        Map<String, Object> params = new HashMap<>();

        // 处理标题
        String titleTemplate = cfg.getTitle();
        if (titleTemplate != null && !titleTemplate.isEmpty()) {
            params.put("title", replaceTemplate(titleTemplate, variables));
        } else {
            params.put("title", "设备告警通知 - " + context.getDeviceName());
        }

        // 处理内容
        String contentTemplate = cfg.getContent();
        if (contentTemplate != null && !contentTemplate.isEmpty()) {
            params.put("content", replaceTemplate(contentTemplate, variables));
        } else {
            // 默认内容
            params.put("content", String.format("设备 %s 于 %s 触发规则",
                context.getDeviceName(), context.getEventTime()));
        }

        // 添加设备信息
        params.put("deviceName", context.getDeviceName() != null ? context.getDeviceName() : "");
        params.put("deviceKey", context.getDeviceKey() != null ? context.getDeviceKey() : "");
        params.put("eventTime", context.getEventTime() != null ? context.getEventTime().toString() : "");

        // 添加事件类型名称
        context.getEventData().ifPresent(eventData -> {
            params.put("eventTypeName", eventData.getIdentifier());
        });

        // 添加事件内容 (用于模板渲染)
        Map<String, Object> eventContent = new HashMap<>();
        if (context.getDecodeResult() != null && context.getDecodeResult().getDataList() != null) {
            context.getDecodeResult().getDataList().forEach(d -> {
                params.put(d.getIdentifier(), d.getValue() != null ? d.getValue() : "");
                eventContent.put(d.getIdentifier(), d.getValue() != null ? d.getValue() : "");
            });
        }
        params.put("eventContent", eventContent);

        // 4. 发送通知
        NotificationService notificationService = notificationServiceMap.get(
            messageReceive.getNotifyType().name()
        );

        if (notificationService != null) {
            String templateId = getTemplateId(messageReceive.getNotifyType());
            notificationService.sendMessage(messageReceive.getReceiver(), templateId, params);

            String detail = String.format("发送到: %s (%s)",
                messageReceive.getName(), messageReceive.getNotifyType());
            return NodeResult.ok(detail);
        } else {
            return NodeResult.fail("不支持的通知类型: " + messageReceive.getNotifyType());
        }
    }

    private Map<String, Object> buildTemplateVariables(RuleContext context) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("deviceName", context.getDeviceName() != null ? context.getDeviceName() : "");
        vars.put("deviceKey", context.getDeviceKey() != null ? context.getDeviceKey() : "");
        vars.put("eventTime", context.getEventTime() != null ? context.getEventTime().toString() : "");

        if (context.getDecodeResult() != null && context.getDecodeResult().getDataList() != null) {
            context.getDecodeResult().getDataList().forEach(d ->
                vars.put(d.getIdentifier(), d.getValue() != null ? d.getValue() : "")
            );
        }

        return vars;
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

    private String getTemplateId(NotifyType notifyType) {
        return switch (notifyType) {
            case EMAIL -> "event_notification.ftl";
            case SMS -> "alarm_sms";
            default -> "";
        };
    }
}
