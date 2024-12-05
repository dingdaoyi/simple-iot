package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductPageQuery  extends PageQuery {
    /**
     * 产品名称
     */
    private String model;

    /**
     * 厂家id
     */
    private Integer productTypeId;

    /**
     * 厂家名称
     */
    private String manufacturer;

}
