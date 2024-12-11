package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author dingyunwei
 */
@Schema
@Data
@TableName(value = "tb_device")
public class Device {
    /**
     * 设备编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "设备编号")
    private Integer id;

    /**
     * 产品id
     */
    @TableField(value = "product_id")
    @Schema(description = "产品id")
    private Integer productId;

    /**
     * 设备名称
     */
    @TableField(value = "device_name")
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备key
     */
    @TableField(value = "device_key")
    @Schema(description = "设备key")
    private String deviceKey;

    /**
     * 设备密钥
     */
    @TableField(value = "device_secret")
    @Schema(description = "设备密钥")
    private String deviceSecret;

    /**
     * 在线状态
     */
    @TableField(value = "online")
    @Schema(description = "在线状态")
    private Boolean online;


    /**
     * 激活状态
     */
    @TableField(value = "active_status")
    @Schema(description = "激活状态")
    private Boolean activeStatus;
}