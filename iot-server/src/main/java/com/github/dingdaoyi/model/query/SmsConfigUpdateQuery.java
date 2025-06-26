package com.github.dingdaoyi.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 短信配置更新请求
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "短信配置更新请求")
public class SmsConfigUpdateQuery extends SmsConfigAddQuery {
    
    @NotNull(message = "ID不能为空")
    @Schema(description = "配置ID")
    private Integer id;
}