package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 告警状态
 * @author dingyunwei
 */
@Getter
public enum AlarmStatus {
    ACTIVE("活动", "告警正在发生"),
    CLEARED("已清除", "告警已自动或手动清除"),
    ACKNOWLEDGED("已确认", "告警已被人员确认");

    @EnumValue
    @JsonValue
    private final String value;
    private final String name;
    private final String description;

    AlarmStatus(String name, String description) {
        this.value = this.name();
        this.name = name;
        this.description = description;
    }
}
