package com.github.dingdaoyi.entity.enu;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.dingdaoyi.iot.rule.FunCover;
import com.github.dingdaoyi.iot.rule.RuleProcessor;

/**
 * @author dingyunwei
 */

public enum RuleType {
    /**
     * 过滤器
     */
    FILTER(1, new FunCover()),
    /**
     * 函数
     */
    FUNCTION(2, new FunCover()),
    ;
    @EnumValue
    @JsonValue
    public final int value;

    RuleType(int value, RuleProcessor ruleProcessor) {
        this.value = value;
    }
}
