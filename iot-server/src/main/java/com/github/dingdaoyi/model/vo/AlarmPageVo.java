package com.github.dingdaoyi.model.vo;

import com.github.dingdaoyi.entity.enu.AlarmSeverity;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 告警分页VO
 * @author dingyunwei
 */
@Data
@Schema(description = "告警分页VO")
public class AlarmPageVo {

    @Schema(description = "告警ID")
    private Integer id;

    @Schema(description = "告警类型")
    private String alarmType;

    @Schema(description = "告警名称")
    private String alarmName;

    @Schema(description = "严重程度")
    private AlarmSeverity severity;

    @Schema(description = "状态")
    private AlarmStatus status;

    @Schema(description = "告警消息")
    private String message;

    @Schema(description = "设备ID")
    private Integer deviceId;

    @Schema(description = "设备Key")
    private String deviceKey;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "告警详情")
    private Map<String, Object> details;

    @Schema(description = "告警开始时间")
    private LocalDateTime startTs;

    @Schema(description = "告警结束时间")
    private LocalDateTime endTs;

    @Schema(description = "告警清除时间")
    private LocalDateTime clearTs;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
}
