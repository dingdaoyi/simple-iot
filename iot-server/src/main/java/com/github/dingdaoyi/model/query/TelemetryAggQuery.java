package com.github.dingdaoyi.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 时序聚合查询
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TelemetryAggQuery extends TimeQuery {

    @NotBlank(message = "设备编号不能为空")
    @Schema(description = "设备编号")
    private String deviceKey;

    @NotBlank(message = "属性标识符不能为空")
    @Schema(description = "属性标识符")
    private String identifier;

    @NotNull(message = "聚合间隔不能为空")
    @Schema(description = "聚合间隔: 1m, 5m, 1h, 1d")
    private String interval;

    @Schema(description = "聚合函数: avg, min, max, sum, count")
    private String function = "avg";
}
