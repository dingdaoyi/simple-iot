package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.SmsConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 短信配置添加请求
 * @author dingyunwei
 */
@Data
@Schema(description = "短信配置添加请求")
public class SmsConfigAddQuery {
    
    @NotBlank(message = "配置名称不能为空")
    @Schema(description = "配置名称")
    private String name;
    
    @NotBlank(message = "短信供应商不能为空")
    @Schema(description = "短信供应商")
    private String supplier;
    
    @NotBlank(message = "访问密钥不能为空")
    @Schema(description = "访问密钥")
    private String accessKey;
    
    @NotBlank(message = "密钥不能为空")
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
    private Map<String,String> configJson;
    
    public SmsConfig toEntity() {
        SmsConfig config = new SmsConfig();
        config.setName(this.name);
        config.setSupplier(this.supplier);
        config.setAccessKey(this.accessKey);
        config.setSecretKey(this.secretKey);
        config.setSignName(this.signName);
        config.setTemplateId(this.templateId);
        config.setEndpoint(this.endpoint);
        config.setRegion(this.region);
        config.setConfigJson(this.configJson);
        config.setIsDefault(false);
        config.setStatus(1);
        return config;
    }
}