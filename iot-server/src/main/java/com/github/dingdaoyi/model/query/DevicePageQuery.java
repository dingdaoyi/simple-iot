package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.model.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备分页
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DevicePageQuery extends PageQuery {
    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceKey;

    /**
     * 产品类型id
     */
    @Schema(description = "产品类型id")
    private Integer productTypeId;

    /**
     * 产品id
     */
    @Schema(description = "产品id")
    private Integer productId;

    @Schema(description = "厂家")
    private String manufacturer;
    /**
     * 父级id
     */
    @Schema(description = "父级设备id")
    private Integer parentId = -1;

}
