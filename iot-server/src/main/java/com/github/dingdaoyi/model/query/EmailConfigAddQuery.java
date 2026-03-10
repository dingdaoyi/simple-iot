package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.EmailConfig;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 邮箱配置新增参数
 * @author dingyunwei
 */
@Data
@Schema(description = "邮箱配置新增参数")
public class EmailConfigAddQuery implements ToEntity<EmailConfig> {

    @Schema(description = "配置名称")
    @NotBlank(message = "配置名称不能为空")
    private String name;

    @Schema(description = "SMTP服务器地址")
    @NotBlank(message = "SMTP服务器地址不能为空")
    private String host;

    @Schema(description = "SMTP端口")
    @NotNull(message = "SMTP端口不能为空")
    private Integer port;

    @Schema(description = "发件人邮箱")
    @NotBlank(message = "发件人邮箱不能为空")
    private String username;

    @Schema(description = "邮箱密码/授权码")
    @NotBlank(message = "邮箱密码不能为空")
    private String password;

    @Schema(description = "协议 smtp")
    private String protocol = "smtp";

    @Schema(description = "编码")
    private String encoding = "UTF-8";

    @Schema(description = "是否SSL")
    private Boolean sslEnabled = true;

    @Schema(description = "状态 1启用 2禁用")
    private Integer status = 1;

    @Override
    public EmailConfig toEntity() {
        EmailConfig config = new EmailConfig();
        config.setName(name);
        config.setHost(host);
        config.setPort(port);
        config.setUsername(username);
        config.setPassword(password);
        config.setProtocol(protocol);
        config.setEncoding(encoding);
        config.setSslEnabled(sslEnabled);
        config.setStatus(status);
        config.setIsDefault(false);
        return config;
    }
}
