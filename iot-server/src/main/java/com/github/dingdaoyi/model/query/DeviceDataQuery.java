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
}
