package com.github.dingdaoyi.iot.rule;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author dingyunwei
 */
public class FunCover implements RuleProcessor {
    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public Object parse(Object value, String script) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("value", value);
        return parser.parseExpression(script).getValue(context);
    }

}
