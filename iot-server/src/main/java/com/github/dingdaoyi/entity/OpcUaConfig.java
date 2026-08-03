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
 * OPC UA 设备配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_opcua_config")
@Schema(description = "OPC UA设备配置")
public class OpcUaConfig extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("device_id")
    private Integer deviceId;

    @Schema(description = "OPC UA 端点 URL, 如 opc.tcp://192.168.1.100:4840")
    @TableField("endpoint_url")
    private String endpointUrl;

    @Schema(description = "安全模式: NONE / SIGN / SIGN_ENCRYPT")
    @TableField("security_mode")
    private String securityMode;

    @Schema(description = "用户名(可选)")
    @TableField("username")
    private String username;

    @Schema(description = "密码(可选)")
    @TableField("password")
    private String password;

    @Schema(description = "轮询间隔(ms)")
    @TableField("interval_ms")
    private Integer intervalMs;

    @Schema(description = "节点映射 JSON: [{identifier, nodeId, dataType, scale}]")
    @TableField("node_map")
    private String nodeMap;

    @TableField("enabled")
    private Boolean enabled;
}
