package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author dingyunwei
 */

@Getter
public enum DataTypeEnum {
    INT(1),
    FLOAT(2),
    DOUBLE(3),
    ENUM(4),
    TEXT(5),
    BOOL(6),
    DATE(7),
    STRUCT(8),
    ;
    @EnumValue
//    @JsonValue
    private final int value;

    DataTypeEnum(int value) {
        this.value = value;
    }
}
