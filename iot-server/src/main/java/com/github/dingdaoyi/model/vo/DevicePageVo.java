package com.github.dingdaoyi.model.vo;

import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dreamlu.mica.core.utils.$;

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
