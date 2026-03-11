package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.model.PageQuery;
import com.github.dingdaoyi.entity.enu.AlarmSeverity;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 告警分页查询
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "告警分页查询")
public class AlarmPageQuery extends PageQuery {

    @Schema(description = "设备Key")
    private String deviceKey;

    @Schema(description = "告警类型")
    private String alarmType;

    @Schema(description = "严重程度")
    private AlarmSeverity severity;

    @Schema(description = "状态")
    private AlarmStatus status;
}
