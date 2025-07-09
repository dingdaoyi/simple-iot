package com.github.dingdaoyi.core.driver;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author dingyunwei
 */

@Getter
public enum DriverTypeEnum {
    MQTT("MQTT", "MQTT驱动"),
    TCP("TCP", "TCP驱动"),
    UDP("UDP", "UDP驱动"),
    HTTP("HTTP", "HTTP驱动"),
    CUSTOM("CUSTOM", "自定义驱动");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    DriverTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
} 