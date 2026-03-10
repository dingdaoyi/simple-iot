package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 邮箱配置实体
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "tb_email_config")
@Schema(description = "邮箱配置")
public class EmailConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;

    @Schema(description = "配置名称")
    private String name;

    @Schema(description = "SMTP服务器地址")
    private String host;

    @Schema(description = "SMTP端口")
    private Integer port;

    @Schema(description = "发件人邮箱")
    private String username;

    @Schema(description = "邮箱密码/授权码")
    private String password;

    @Schema(description = "协议 smtp")
    private String protocol;

    @Schema(description = "编码")
    private String encoding;

    @Schema(description = "是否启用SSL")
    private Boolean sslEnabled;

    @Schema(description = "是否默认配置")
    private Boolean isDefault;

    @Schema(description = "状态 1启用 2禁用")
    private Integer status;

    /**
     * 是否为启用状态
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}
