package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author dingyunwei
 */

public enum RuleInputType {
    /**
     * 属性
     */
    PROP_DATA(1),

    /**
     * 事件
     */
    EVENT(2),

    /**
     * 设备在离线
     */
    DEVICE_STATUS(3),
    ;
    @JsonValue
    @EnumValue
    public final int value;

    RuleInputType(int value) {
        this.value = value;
    }
}
