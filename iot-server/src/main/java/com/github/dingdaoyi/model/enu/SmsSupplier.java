package com.github.dingdaoyi.model.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 短信供应商
 * @author dingyunwei
 */
@Getter
public enum SmsSupplier {
    ALIBABA("alibaba", "阿里云"),
    TENCENT("tencent", "腾讯云"),
    HUAWEI("huawei", "华为云");

    @EnumValue
    @JsonValue
    private final String code;
    
    private final String name;

    SmsSupplier(String code, String name) {
        this.code = code;
        this.name = name;
    }
}