package com.github.dingdaoyi.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductVo extends Product {
    @Schema(description = "产品类型")
    private ProductType productType;

    public ProductVo(Product product) {
        BeanUtil.copyProperties(product, this);
    }

}
