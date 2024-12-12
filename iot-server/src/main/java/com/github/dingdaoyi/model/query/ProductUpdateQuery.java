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
     * 描述
     */
    @Schema(description = "描述")
    private String remark;


    @Override
    public Product toEntity() {
        Product product = new Product();
        product.setRemark(remark);
        product.setId(id);
        return product;
    }
}
