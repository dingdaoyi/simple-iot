package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 1,产品;2,设备分组,3, 特定设备
 */
public enum RuleTargetType {
    HTTP(1),
    MQTT(2),
    MESSAGE(3),
    ;
    @JsonValue
    @EnumValue
    public final int value;

    RuleTargetType(int value) {
        this.value = value;
    }
}
