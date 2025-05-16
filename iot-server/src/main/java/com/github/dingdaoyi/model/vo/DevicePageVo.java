package com.github.dingdaoyi.model.vo;

import com.github.dingdaoyi.entity.Device;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DevicePageVo extends Device {
    /**
     * 产品类型名称
     */
    private String productTypeName;

    /**
     * 产品类型id
     */
    private Integer productTypeId;

    /**
     * 产品型号
     */
    private String productModel;

    /**
     * 厂家
     */
    private String manufacturer;


}
