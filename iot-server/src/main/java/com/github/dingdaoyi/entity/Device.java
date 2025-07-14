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
 * @author dingyunwei
 */
@Schema
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_device")
public class Device extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "设备编号")
    private Integer id;

    @TableField(value = "product_id")
    @Schema(description = "产品id")
    private Integer productId;

    @TableField(value = "device_name")
    @Schema(description = "设备名称")
    private String deviceName;

    @TableField(value = "device_key")
    @Schema(description = "设备key")
    private String deviceKey;

    @TableField(value = "device_secret")
    @Schema(description = "设备密钥")
    private String deviceSecret;

    @TableField(value = "online")
    @Schema(description = "在线状态")
    private Boolean online;

    @TableField(value = "active_status")
    @Schema(description = "激活状态")
    private Boolean activeStatus;

    @TableField(value = "third_device_id")
    @Schema(description = "第三方设备id")
    private String thirdDeviceId;
}