package com.github.dingdaoyi.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.ProductType;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceVo extends Device {

    @Schema(description = "产品")
    private Product product;

    @Schema(description = "产品类型")
    private ProductType productType;

    @Schema(description = "物模型")
    private TslModel tslModel;

    public void setProductVo(ProductVo productVo) {
        this.productType = productVo.getProductType();
        productVo.setProductType(null);
        this.product = productVo;
    }
    public static DeviceVo build(Device device) {
        DeviceVo deviceVo = new DeviceVo();
        BeanUtil.copyProperties(device, deviceVo);
        return deviceVo;
    }
}
