package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.core.base.BaseEntity;
import com.github.dingdaoyi.model.enu.NotifyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * IM 推送配置 (钉钉/企微/飞书 webhook)
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_im_push_config")
@Schema(description = "IM推送配置")
public class ImPushConfig extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "ID")
    private Integer id;

    @TableField(value = "name")
    @Schema(description = "配置名称")
    private String name;

    @TableField(value = "platform")
    @Schema(description = "平台: DINGTALK/WECOM/FEISHU")
    private NotifyType platform;

    @TableField(value = "webhook_url")
    @Schema(description = "Webhook URL")
    private String webhookUrl;

    @TableField(value = "secret")
    @Schema(description = "签名密钥(钉钉)")
    private String secret;

    @TableField(value = "is_enabled")
    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @TableField(value = "description")
    @Schema(description = "描述")
    private String description;
}
