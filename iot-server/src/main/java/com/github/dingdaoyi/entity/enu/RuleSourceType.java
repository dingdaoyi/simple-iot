package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 1: http, 2,mqtt; 3,message
 * @author dingyunwei
 */
public enum RuleSourceType {
    PRODUCT(1),
    GROUP(2),
    DEVICE(3),
    ;
    @JsonValue
    @EnumValue
    public final int value;

    RuleSourceType(int value) {
        this.value = value;
    }
}
