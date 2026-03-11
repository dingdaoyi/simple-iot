package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.InputOnlineConfig;
import com.github.dingdaoyi.rule.config.NodeConfig;
import org.springframework.stereotype.Component;

/**
 * 设备上下线输入节点
 * 只有设备上下线消息才会通过此节点
 * @author dingyunwei
 */
@Component
public class InputOnlineNode implements RuleNodeExecutor {

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.INPUT_ONLINE;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        RuleContext.MessageType messageType = context.getMessageType();

        // 检查是否是上下线消息
        if (messageType == RuleContext.MessageType.ONLINE || messageType == RuleContext.MessageType.OFFLINE) {
            InputOnlineConfig cfg = (InputOnlineConfig) config;
            String status = cfg.getOnlineStatus() != null ? cfg.getOnlineStatus() : "all";

            if ("all".equals(status)) {
                String msg = messageType == RuleContext.MessageType.ONLINE ? "设备上线" : "设备下线";
                return NodeResult.success(msg);
            } else if ("online".equals(status) && messageType == RuleContext.MessageType.ONLINE) {
                return NodeResult.success("设备上线");
            } else if ("offline".equals(status) && messageType == RuleContext.MessageType.OFFLINE) {
                return NodeResult.success("设备下线");
            }
            return NodeResult.fail("上下线状态不匹配");
        }
        // 不是上下线消息，不通过
        return NodeResult.fail("非上下线消息");
    }
}
