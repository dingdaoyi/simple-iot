package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author dingyunwei
 */

@Getter
public enum ParamType {
    PROPERTY(1),PARAM(2),
    ;
    @JsonValue
    @EnumValue
    private  final int value;
    ParamType(int value) {
        this.value = value;
    }
}
