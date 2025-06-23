package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.dingdaoyi.core.base.BaseEntity;
import com.github.dingdaoyi.entity.enu.CommandStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Schema
@Data
@TableName(value = "tb_device_command", autoResultMap = true)
public class DeviceCommand extends BaseEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Long id;

    /**
     * 标识符
     */
    @TableField(value = "identifier")
    @Schema(description = "标识符")
    private String identifier;

    @TableField(value = "product_key")
    @Schema(description = "产品key")
    private String productKey;

    /**
     * 协议key
     */
    @TableField(value = "proto_key")
    @Schema(description = "协议key")
    private String protoKey;

    /**
     * 设备编号
     */
    @TableField(value = "device_key")
    @Schema(description = "设备编号")
    private String deviceKey;

    /**
     * 请求参数
     */
    @TableField(value = "input_params", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "请求参数")
    private Map<String, Object> inputParams=new HashMap<>();

    /**
     * 指令结果
     */
    @TableField(value = "result_data", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "指令结果")
    private Map<String, Object> resultData=new HashMap<>();

    /**
     * 状态
     */
    private CommandStatus status;
}