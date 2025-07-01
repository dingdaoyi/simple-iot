package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dingdaoyi.core.base.BaseEntity;
import com.github.dingdaoyi.model.enu.SmsSupplier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 短信配置实体
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "sms_config",autoResultMap = true)
@Schema(description = "短信配置")
public class SmsConfig extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    @Schema(description = "配置名称")
    private String name;
    
    @Schema(description = "短信供应商")
    private SmsSupplier supplier;
    
    @Schema(description = "访问密钥")
    private String accessKey;
    
    @Schema(description = "密钥")
    private String secretKey;
    
    @Schema(description = "短信签名")
    private String signName;
    
    @Schema(description = "模板ID")
    private String templateId;
    
    @Schema(description = "接入点")
    private String endpoint;
    
    @Schema(description = "区域")
    private String region;
    
    @Schema(description = "其他配置JSON")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String,String> configJson;
    
    @Schema(description = "是否默认配置")
    private Boolean isDefault;
    
    @Schema(description = "状态 1启用 2禁用")
    private Integer status;
}