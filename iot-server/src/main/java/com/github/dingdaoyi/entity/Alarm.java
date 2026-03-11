package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.dingdaoyi.core.base.BaseEntity;
import com.github.dingdaoyi.entity.enu.AlarmSeverity;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 告警实体
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_alarm", autoResultMap = true)
@Schema(description = "告警")
public class Alarm extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "告警ID")
    private Integer id;

    @TableField(value = "alarm_type")
    @Schema(description = "告警类型")
    private String alarmType;

    @TableField(value = "alarm_name")
    @Schema(description = "告警名称")
    private String alarmName;

    @TableField(value = "severity")
    @Schema(description = "严重程度")
    private AlarmSeverity severity;

    @TableField(value = "status")
    @Schema(description = "状态")
    private AlarmStatus status;

    @TableField(value = "message")
    @Schema(description = "告警消息")
    private String message;

    @TableField(value = "device_id")
    @Schema(description = "设备ID")
    private Integer deviceId;

    @TableField(value = "device_key")
    @Schema(description = "设备Key")
    private String deviceKey;

    @TableField(value = "device_name")
    @Schema(description = "设备名称")
    private String deviceName;

    @TableField(value = "rule_chain_id")
    @Schema(description = "触发规则链ID")
    private Integer ruleChainId;

    @TableField(value = "details", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "告警详情")
    private Map<String, Object> details;

    @TableField(value = "start_ts")
    @Schema(description = "告警开始时间")
    private LocalDateTime startTs;

    @TableField(value = "end_ts")
    @Schema(description = "告警结束时间")
    private LocalDateTime endTs;

    @TableField(value = "clear_ts")
    @Schema(description = "告警清除时间")
    private LocalDateTime clearTs;

    @TableField(value = "clear_by")
    @Schema(description = "清除人")
    private String clearBy;
}
