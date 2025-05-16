package com.github.dingdaoyi.model.vo;

import com.github.dingdaoyi.entity.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductPageVo extends Product {

    /**
     * 产品类型名称
     */
    private String productTypeName;
}
