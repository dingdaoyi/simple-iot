package com.github.dingdaoyi.proto.model.tsl;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author dingyunwei
 */

@Getter
public enum EventTypeEnum {
    INFO(1),WARN(2),FAULT(3);

    @EnumValue
    @JsonValue
    private final int value;

    EventTypeEnum(int value) {
        this.value = value;
    }
    public static EventTypeEnum of(int value) {
        for (EventTypeEnum e : EventTypeEnum.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return INFO;
    }
}
