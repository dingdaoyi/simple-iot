package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 推送配置类型
 * @author dingyunwei
 */
@Getter
public enum PushConfigType {

    HTTP("HTTP", "HTTP回调"),
    MQTT("MQTT", "MQTT转发");

    @EnumValue
    @JsonValue
    private final String value;
    private final String name;

    PushConfigType(String value, String name) {
        this.value = value;
        this.name = name;
    }
}
