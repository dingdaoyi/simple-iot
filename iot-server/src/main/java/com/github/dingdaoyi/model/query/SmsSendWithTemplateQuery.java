package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.model.enu.SmsTemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Map;

/**
 * 使用模板发送短信请求
 * @author dingyunwei
 */
@Data
@Schema(description = "使用模板发送短信请求")
public class SmsSendWithTemplateQuery {
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;
    
    @NotNull(message = "模板类型不能为空")
    @Schema(description = "模板类型")
    private SmsTemplateType templateType;
    
    @Schema(description = "模板参数")
    private Map<String, Object> templateParams;
    
    @Schema(description = "配置ID，不传则使用默认配置")
    private Integer configId;
}