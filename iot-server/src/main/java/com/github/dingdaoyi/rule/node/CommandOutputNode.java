package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.model.vo.DeviceVo;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.NodeConfig;
import com.github.dingdaoyi.rule.config.OutputCommandConfig;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.ServiceHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 设备指令节点 - 支持设备联动
 * @author dingyunwei
 */
@Component
@Slf4j
public class CommandOutputNode implements RuleNodeExecutor {

    @Resource
    private DeviceService deviceService;

    @Resource
    @Lazy
    private ServiceHandler serviceHandler;

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{(\\w+)}");

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.OUTPUT_COMMAND;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        OutputCommandConfig cfg = (OutputCommandConfig) config;

        String targetMode = cfg.getTargetMode() != null ? cfg.getTargetMode() : "self";
        String identifier = cfg.getIdentifier();
        Map<String, Object> paramsFromConfig = cfg.getParams();

        if (identifier == null || identifier.isEmpty()) {
            return NodeResult.fail("指令标识符不能为空");
        }

        // 1. 获取目标设备
        String targetDeviceKey;
        String targetDeviceName;

        if ("other".equals(targetMode)) {
            Integer targetDeviceId = cfg.getTargetDeviceId();
            if (targetDeviceId == null) {
                return NodeResult.fail("目标设备ID不能为空");
            }
            Optional<DeviceVo> targetDevice = deviceService.details(targetDeviceId);
            if (targetDevice.isEmpty()) {
                return NodeResult.fail("目标设备不存在: " + targetDeviceId);
            }
            targetDeviceKey = targetDevice.get().getDeviceKey();
            targetDeviceName = targetDevice.get().getDeviceName();
        } else {
            // 当前设备
            targetDeviceKey = context.getDeviceKey();
            targetDeviceName = context.getDeviceName();
        }

        // 2. 构建模板变量
        Map<String, Object> variables = buildTemplateVariables(context);

        // 3. 解析参数并替换模板变量
        Map<String, Object> params = replaceParams(paramsFromConfig, variables);

        // 4. 发送指令
        try {
            Map<String, Object> result = serviceHandler.sendMessage(targetDeviceKey, identifier, params);

            String detail = String.format("指令已发送到设备 %s: %s", targetDeviceName, identifier);
            log.info("设备联动指令发送成功: device={}, identifier={}, params={}",
                targetDeviceKey, identifier, params);

            return NodeResult.ok(detail);
        } catch (Exception e) {
            log.error("设备联动指令发送失败: device={}, identifier={}, error={}",
                targetDeviceKey, identifier, e.getMessage());
            return NodeResult.fail("指令发送失败: " + e.getMessage());
        }
    }

    /**
     * 构建模板变量
     */
    private Map<String, Object> buildTemplateVariables(RuleContext context) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("deviceName", context.getDeviceName() != null ? context.getDeviceName() : "");
        vars.put("deviceKey", context.getDeviceKey() != null ? context.getDeviceKey() : "");
        vars.put("deviceId", context.getDeviceId());
        vars.put("eventTime", context.getEventTime() != null ? context.getEventTime().toString() : "");

        // 添加所有属性值
        if (context.getDecodeResult() != null && context.getDecodeResult().getDataList() != null) {
            context.getDecodeResult().getDataList().forEach(d ->
                vars.put(d.getIdentifier(), d.getValue() != null ? d.getValue() : "")
            );
        }

        return vars;
    }

    /**
     * 替换参数中的模板变量
     */
    private Map<String, Object> replaceParams(Map<String, Object> params, Map<String, Object> variables) {
        if (params == null || params.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String strValue) {
                result.put(entry.getKey(), replaceTemplate(strValue, variables));
            } else {
                result.put(entry.getKey(), value);
            }
        }
        return result;
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
}
