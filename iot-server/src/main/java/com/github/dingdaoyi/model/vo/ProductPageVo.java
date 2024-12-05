package com.github.dingdaoyi.model.vo;

import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dreamlu.mica.core.utils.$;

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
