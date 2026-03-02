package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author dingyunwei
 */
@Data
public class ProductUpdateQuery implements ToEntity<Product> {

    /**
     * 产品id
     */
    @Schema(description = "产品id")
    private Integer id;

    /**
     * 产品型号
     */
    @Schema(description = "产品型号")
    private String model;

    /**
     * 厂家
     */
    @Schema(description = "厂家")
    private String manufacturer;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String remark;

    /**
     * 产品图标
     */
    @Schema(description = "产品图标")
    private String icon;


    @Override
    public Product toEntity() {
        Product product = new Product();
        product.setId(id);
        product.setModel(model);
        product.setManufacturer(manufacturer);
        product.setRemark(remark);
        product.setIcon(icon);
        return product;
    }
}
