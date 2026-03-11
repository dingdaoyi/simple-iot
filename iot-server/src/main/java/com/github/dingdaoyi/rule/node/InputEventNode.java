package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.InputEventConfig;
import com.github.dingdaoyi.rule.config.NodeConfig;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 事件上报输入节点
 * 只有事件上报消息才会通过此节点
 * @author dingyunwei
 */
@Component
public class InputEventNode implements RuleNodeExecutor {

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.INPUT_EVENT;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        if (!context.isEventMessage()) {
            return NodeResult.fail("非事件上报消息");
        }

        DeviceEventData eventData = context.getEventData().orElse(null);
        if (eventData == null) {
            return NodeResult.fail("无事件数据");
        }

        InputEventConfig cfg = (InputEventConfig) config;
        List<String> identifiers = cfg.getIdentifiers();

        if (identifiers != null && !identifiers.isEmpty()) {
            if (identifiers.contains(eventData.getIdentifier())) {
                return NodeResult.success("事件上报: " + eventData.getIdentifier());
            }
            return NodeResult.fail("事件不在过滤列表中");
        }
        return NodeResult.success("事件上报: " + eventData.getIdentifier());
    }
}
