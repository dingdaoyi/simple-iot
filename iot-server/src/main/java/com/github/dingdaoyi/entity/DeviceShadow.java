package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备影子
 * @author dingyunwei
 */
@Schema
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_device_shadow")
public class DeviceShadow extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "设备id")
    private Integer deviceId;

    @Schema(description = "期望状态 JSON")
    private String desiredState;

    @Schema(description = "上报状态 JSON")
    private String reportedState;

    @Schema(description = "版本号")
    private Integer version;
}
