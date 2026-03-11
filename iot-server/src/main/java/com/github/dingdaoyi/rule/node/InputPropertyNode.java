package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.InputPropertyConfig;
import com.github.dingdaoyi.rule.config.NodeConfig;
import org.springframework.stereotype.Component;

/**
 * 属性上报输入节点
 * 只有属性上报消息才会通过此节点
 * @author dingyunwei
 */
@Component
public class InputPropertyNode implements RuleNodeExecutor {

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.INPUT_PROPERTY;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        // 检查是否是属性上报消息
        if (!context.isPropertyMessage()) {
            return NodeResult.of(false, null, "非属性上报消息");
        }

        InputPropertyConfig cfg = (InputPropertyConfig) config;
        int propCount = context.getAllProperties().size();
        String detail = "属性上报: " + propCount + " 个属性";
        return NodeResult.ok(detail);
    }
}
