package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.core.base.BaseEntity;
import com.github.dingdaoyi.model.enu.SmsTemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 短信模板实体
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sms_template")
@Schema(description = "短信模板")
public class SmsTemplate extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    @Schema(description = "配置ID")
    private Integer configId;
    
    @Schema(description = "模板类型")
    private SmsTemplateType templateType;
    
    @Schema(description = "模板ID")
    private String templateId;
    
    @Schema(description = "模板名称")
    private String templateName;
    
    @Schema(description = "模板内容")
    private String templateContent;
    
    @Schema(description = "状态 1启用 2禁用")
    private Integer status;
}