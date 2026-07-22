package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审计日志
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_audit_log")
@Schema(description = "审计日志")
public class AuditLog extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "操作: CREATE/UPDATE/DELETE/LOGIN等")
    private String action;

    @Schema(description = "资源类型: DEVICE/ALARM/RULE_CHAIN等")
    private String resource;

    @Schema(description = "资源ID")
    private String resourceId;

    @Schema(description = "操作详情")
    private String detail;

    @Schema(description = "客户端IP")
    private String ip;
}
