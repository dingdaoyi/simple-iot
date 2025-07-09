package com.github.dingdaoyi.model.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author dingyunwei
 */

@Getter
public enum ConnectionTypeEnum {
    DEFAULT("DEFAULT", "平台内置"),
    CUSTOM("CUSTOM", "自定义上传");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    ConnectionTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
} 