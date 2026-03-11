package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 规则链数据源类型
 * @author dingyunwei
 */
@Getter
public enum RuleSourceType {
    PRODUCT("产品", "按产品配置规则链,该产品下所有设备触发"),
    DEVICE_GROUP("设备分组", "按设备分组配置规则链"),
    DEVICE("特定设备", "为特定设备配置规则链");

    @EnumValue
    @JsonValue
    private final String value;
    private final String name;
    private final String description;

    RuleSourceType(String name, String description) {
        this.value = this.name();
        this.name = name;
        this.description = description;
    }
}
