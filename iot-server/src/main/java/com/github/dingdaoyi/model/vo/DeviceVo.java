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
public class DeviceVo extends Device {

    @Schema(description = "产品")
    private Product product;

    @Schema(description = "产品类型")
    private ProductType productType;

    public void setProductVo(ProductVo productVo) {
        this.product = productVo;
        this.productType = productVo.getProductType();
    }
    public static DeviceVo build(Device device) {
        DeviceVo deviceVo = new DeviceVo();
        $.copy(device, deviceVo);
        return deviceVo;
    }
}
