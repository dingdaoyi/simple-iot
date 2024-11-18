package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author dingyunwei
 */

@Getter
public enum PropertyAccessMode {
    READE("r"),
    READE_WROTE("rw"),
    ;
    @JsonValue
    @EnumValue
    private final String vale;

    PropertyAccessMode(String vale) {
        this.vale = vale;
    }
}
