package com.github.dingdaoyi.model.enu;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 通知类型
 * @author dingyunwei
 */
@Getter
public enum NotifyType {
    EMAIL(1),SMS(2),;
    @EnumValue
    @JsonValue
    private final int value;
    NotifyType(int value) {
        this.value = value;
    }
}
