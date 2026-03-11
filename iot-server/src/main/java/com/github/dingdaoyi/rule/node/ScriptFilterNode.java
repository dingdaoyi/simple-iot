package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.FilterScriptConfig;
import com.github.dingdaoyi.rule.config.NodeConfig;
import org.springframework.stereotype.Component;

/**
 * 脚本过滤节点
 * 使用简单表达式进行过滤
 * @author dingyunwei
 */
@Component
public class ScriptFilterNode implements RuleNodeExecutor {

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.FILTER_SCRIPT;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        FilterScriptConfig cfg = (FilterScriptConfig) config;

        if (cfg == null || cfg.getScript() == null) {
            return NodeResult.of(false, null, "未配置脚本");
        }

        String script = cfg.getScript();

        // TODO: 实现脚本执行引擎
        // 目前简单返回 true，后续可集成 Groovy/JavaScript 引擎
        // Map<String, Object> variables = context.buildVariables();
        // boolean result = scriptEngine.evaluate(script, variables);

        return NodeResult.of(true, "脚本通过: " + script, "脚本不通过");
    }
}
