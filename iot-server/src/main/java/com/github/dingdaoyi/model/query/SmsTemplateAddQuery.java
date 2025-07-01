package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.SmsTemplate;
import com.github.dingdaoyi.model.enu.SmsTemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 短信模板添加请求
 * @author dingyunwei
 */
@Data
@Schema(description = "短信模板添加请求")
public class SmsTemplateAddQuery {
    
    @NotNull(message = "配置ID不能为空")
    @Schema(description = "配置ID")
    private Integer configId;
    
    @NotNull(message = "模板类型不能为空")
    @Schema(description = "模板类型")
    private SmsTemplateType templateType;
    
    @NotBlank(message = "模板ID不能为空")
    @Schema(description = "模板ID")
    private String templateId;
    
    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称")
    private String templateName;
    
    @Schema(description = "模板内容")
    private String templateContent;
    
    public SmsTemplate toEntity() {
        SmsTemplate template = new SmsTemplate();
        template.setConfigId(this.configId);
        template.setTemplateType(this.templateType);
        template.setTemplateId(this.templateId);
        template.setTemplateName(this.templateName);
        template.setTemplateContent(this.templateContent);
        template.setStatus(1);
        return template;
    }
}