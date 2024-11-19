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
public class ProductVo extends Product {
    @Schema(description = "产品类型")
    private ProductType productType;

    public ProductVo(Product product) {
        $.copy(product, this);
    }

}
