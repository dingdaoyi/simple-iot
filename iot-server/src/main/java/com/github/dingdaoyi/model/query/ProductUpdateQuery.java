package com.github.dingdaoyi.model.query;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String mark;


    @Override
    public Product toEntity() {
        Product product = new Product();
        product.setMark(mark);
        product.setId(id);
        return product;
    }
}
