package com.github.dingdaoyi.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.dingdaoyi.entity.EmailConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮箱配置 VO (密码脱敏)
 * @author dingyunwei
 */
@Data
@Schema(description = "邮箱配置VO")
public class EmailConfigVo {

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

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 从 Entity 转换为 VO
     */
    public static EmailConfigVo build(EmailConfig entity) {
        if (entity == null) {
            return null;
        }
        EmailConfigVo vo = new EmailConfigVo();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }
}