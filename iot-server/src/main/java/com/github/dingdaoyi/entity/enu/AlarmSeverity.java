package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 告警严重程度
 * @author dingyunwei
 */
@Getter
public enum AlarmSeverity {
    CRITICAL("严重", "#dc2626", 1),
    MAJOR("主要", "#ea580c", 2),
    MINOR("次要", "#f59e0b", 3),
    WARNING("警告", "#3b82f6", 4);

    @EnumValue
    @JsonValue
    private final String value;
    private final String name;
    private final String color;
    private final int level;

    AlarmSeverity(String name, String color, int level) {
        this.value = this.name();
        this.name = name;
        this.color = color;
        this.level = level;
    }
}
