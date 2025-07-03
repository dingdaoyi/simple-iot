package com.github.dingdaoyi.model.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 短信模板类型
 *
 * @author dingyunwei
 */
@Getter
public enum SmsTemplateType {

    ALARM_NOTIFY("alarm_notify", "告警通知", "设备${deviceName}（${deviceKey}）于${eventTime}发生${eventTypeName}事件，内容：${eventContent}，请及时处理。"),
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

    public static SmsTemplateType fromCode(String code) {
        return Arrays.stream(values()).filter(item -> StringUtils.equals(item.getCode(), code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知的短信模板类型: " + code));
    }
}