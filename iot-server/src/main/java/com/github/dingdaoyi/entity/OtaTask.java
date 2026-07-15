package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.dingdaoyi.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_ota_task", autoResultMap = true)
@Schema(description = "OTA升级任务")
public class OtaTask extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("firmware_id")
    @Schema(description = "固件ID")
    private Integer firmwareId;

    @TableField("group_id")
    @Schema(description = "设备分组ID(可选)")
    private Integer groupId;

    @TableField(value = "device_ids", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "目标设备ID列表")
    private java.util.List<Integer> deviceIds;

    @Schema(description = "状态: PENDING/RUNNING/COMPLETED/FAILED")
    private String status;

    @Schema(description = "总数")
    private Integer total;

    @Schema(description = "成功数")
    private Integer success;

    @Schema(description = "失败数")
    private Integer fail;

    @TableField(value = "progress", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "各设备升级进度")
    private Map<String, String> progress;
}
