package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum EventTypeEnum {
    INFO(1),WARN(2),FAULT(3);

    @EnumValue
    @JsonValue
    private final int value;

    EventTypeEnum(int value) {
        this.value = value;
    }
}
