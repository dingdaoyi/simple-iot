package com.github.dingdaoyi.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 短信发送请求
 * @author dingyunwei
 */
@Data
@Schema(description = "短信发送请求")
public class SmsSendQuery {
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;
    
    @NotBlank(message = "短信内容不能为空")
    @Schema(description = "短信内容")
    private String message;
}