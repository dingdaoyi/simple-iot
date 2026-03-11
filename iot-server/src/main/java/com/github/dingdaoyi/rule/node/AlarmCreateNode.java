package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.enu.AlarmSeverity;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.AlarmCreateConfig;
import com.github.dingdaoyi.rule.config.NodeConfig;
import com.github.dingdaoyi.service.AlarmService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 创建告警节点
 * @author dingyunwei
 */
@Component
public class AlarmCreateNode implements RuleNodeExecutor {

    @Resource
    private AlarmService alarmService;

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{(\\w+)}");

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.ALARM_CREATE;
    }

    @Override
    public RuleNodeExecutor.NodeResult execute(RuleContext context, NodeConfig config) {
        AlarmCreateConfig cfg = (AlarmCreateConfig) config;

        String alarmType = cfg.getAlarmType();
        if (alarmType == null) {
            return RuleNodeExecutor.NodeResult.fail("告警类型不能为空");
        }

        // 检查是否已有相同类型的活动告警
        if (alarmService.findActiveAlarm(context.getDeviceKey(), alarmType).isPresent()) {
            return RuleNodeExecutor.NodeResult.success("已存在相同类型的活动告警");
        }

        // 构建变量替换上下文
        Map<String, Object> variables = context.buildVariables();

        // 解析严重程度
        AlarmSeverity severity = AlarmSeverity.WARNING;
        if (cfg.getSeverity() != null) {
            try {
                severity = AlarmSeverity.valueOf(cfg.getSeverity());
            } catch (IllegalArgumentException ignored) {}
        }

        // 创建告警
        Alarm alarm = new Alarm();
        alarm.setAlarmType(alarmType);
        alarm.setAlarmName(replaceTemplate(cfg.getAlarmName() != null ? cfg.getAlarmName() : alarmType, variables));
        alarm.setSeverity(severity);
        alarm.setStatus(AlarmStatus.ACTIVE);
        alarm.setMessage(replaceTemplate(cfg.getMessage() != null ? cfg.getMessage() : "", variables));
        alarm.setDeviceId(context.getDeviceId());
        alarm.setDeviceKey(context.getDeviceKey());
        alarm.setDeviceName(context.getDeviceName());
        alarm.setRuleChainId(context.getRuleChainId());
        alarm.setStartTs(LocalDateTime.now());

        // 告警详情
        Map<String, Object> details = new HashMap<>();
        details.put("deviceName", context.getDeviceName());
        details.put("eventTime", context.getEventTime());
        DecodeResult decodeResult = context.getDecodeResult();
        if (decodeResult != null && decodeResult.getDataList() != null) {
            for (DeviceData d : decodeResult.getDataList()) {
                details.put(d.getIdentifier(), d.getValue());
            }
        }
        alarm.setDetails(details);

        alarmService.save(alarm);

        String detail = String.format("创建告警: %s [%s]", alarm.getAlarmName(), severity.getName());
        return RuleNodeExecutor.NodeResult.success(detail);
    }

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
}
