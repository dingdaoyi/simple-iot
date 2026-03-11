package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.AlarmClearConfig;
import com.github.dingdaoyi.rule.config.NodeConfig;
import com.github.dingdaoyi.service.AlarmService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 清除告警节点
 * @author dingyunwei
 */
@Component
public class AlarmClearNode implements RuleNodeExecutor {

    @Resource
    private AlarmService alarmService;

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.ALARM_CLEAR;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        AlarmClearConfig cfg = (AlarmClearConfig) config;

        String alarmType = cfg != null ? cfg.getAlarmType() : null;

        if (alarmType != null) {
            // 清除指定类型的告警
            alarmService.clearByDeviceKeyAndType(context.getDeviceKey(), alarmType);
            return NodeResult.success("已清除告警: " + alarmType);
        } else {
            // 清除该设备的所有活动告警
            alarmService.clearAllByDeviceKey(context.getDeviceKey());
            return NodeResult.success("已清除所有告警");
        }
    }
}
