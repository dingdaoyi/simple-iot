package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.InputScheduleConfig;
import com.github.dingdaoyi.rule.config.NodeConfig;
import org.springframework.stereotype.Component;

/**
 * 定时触发输入节点
 * 被 ScheduleTriggerService 调用时, messageType 设为 PROPERTY
 * @author dingyunwei
 */
@Component
public class InputScheduleNode implements RuleNodeExecutor {

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.INPUT_SCHEDULE;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        InputScheduleConfig cfg = (InputScheduleConfig) config;
        String cron = cfg.getCron() != null ? cfg.getCron() : "* * * * * ?";
        return NodeResult.ok("定时触发: " + cron);
    }
}
