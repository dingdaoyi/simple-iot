package com.github.dingdaoyi.model.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 短信模板类型
 *
 * @author dingyunwei
 */
@Getter
public enum SmsTemplateType {

    ALARM_NOTIFY("alarm_notify", "告警通知", "${deviceName}设备发生${message}报警,请速确认!"),
    VERIFY_CODE("verify_code", "验证码", "您的验证码是${code}，5分钟内有效"),
    ;
    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    private final String template;

    SmsTemplateType(String code, String desc, String template) {
        this.code = code;
        this.desc = desc;
        this.template = template;

    }
}