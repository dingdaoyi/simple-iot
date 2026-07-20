package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_webhook_ingress")
@Schema(description = "Webhook数据接入配置")
public class WebhookIngress extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    @Schema(description = "配置名称")
    private String name;

    @TableField("token")
    @Schema(description = "接入Token（URL路径用）")
    private String token;

    @TableField("secret")
    @Schema(description = "HMAC签名密钥")
    private String secret;

    @TableField("device_id")
    @Schema(description = "关联设备ID")
    private Integer deviceId;

    @TableField("enabled")
    @Schema(description = "是否启用")
    private Boolean enabled;

    @TableField("description")
    @Schema(description = "描述")
    private String description;
}
