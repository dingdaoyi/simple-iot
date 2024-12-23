package com.github.dingdaoyi.iot.rule;

/**
 * 规则处理
 * @author dingyunwei
 */
public interface RuleProcessor {

    Object parse(Object value,String script);
}
