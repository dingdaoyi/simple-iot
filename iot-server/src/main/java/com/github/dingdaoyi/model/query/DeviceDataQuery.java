package com.github.dingdaoyi.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceDataQuery extends TimeQuery{

    @Schema(description = "设备编号")
    private String deviceKey;


    @Schema(description = "标识符")
    private String identifier;

    @Schema(description = "当前页码，从1开始", defaultValue = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", defaultValue = "20")
    private Integer size = 20;
}
