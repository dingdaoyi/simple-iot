package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author dingyunwei
 */

@Getter
public enum StatusEnum {
    ENABLED(1),
    DISABLED(0);

    @JsonValue
    @EnumValue
    private final int value;

    StatusEnum(int value) {
        this.value = value;
    }
    public static StatusEnum of(int value) {
        for (StatusEnum e : StatusEnum.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return DISABLED;
    }
}
