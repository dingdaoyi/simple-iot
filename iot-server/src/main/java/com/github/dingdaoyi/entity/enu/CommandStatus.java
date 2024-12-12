package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CommandStatus {
    SAVED(0),DELIVERED(1), DONE(2),
    ;

    @JsonValue
    @EnumValue
    public final int value;

    CommandStatus(int value) {
        this.value = value;
    }
}
