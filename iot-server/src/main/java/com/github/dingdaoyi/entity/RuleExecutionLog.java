package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "tb_rule_execution_log", autoResultMap = true)
@Schema(description = "规则链执行日志")
public class RuleExecutionLog {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("rule_chain_id")
    @Schema(description = "规则链ID")
    private Integer ruleChainId;

    @TableField("device_key")
    @Schema(description = "设备Key")
    private String deviceKey;

    @TableField("device_id")
    @Schema(description = "设备ID")
    private Integer deviceId;

    @TableField("device_name")
    @Schema(description = "设备名称")
    private String deviceName;

    @TableField("message_type")
    @Schema(description = "消息类型")
    private String messageType;

    @TableField("event_time")
    @Schema(description = "事件时间")
    private LocalDateTime eventTime;

    @Schema(description = "是否成功")
    private Boolean success;

    @TableField("duration_ms")
    @Schema(description = "耗时(ms)")
    private Long durationMs;

    @TableField("trace_count")
    @Schema(description = "轨迹数")
    private Integer traceCount;

    @TableField(value = "traces", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "执行轨迹")
    private List<Map<String, Object>> traces;

    @TableField(value = "input_snapshot", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "输入快照")
    private Map<String, Object> inputSnapshot;

    @TableField(value = "create_time", fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
