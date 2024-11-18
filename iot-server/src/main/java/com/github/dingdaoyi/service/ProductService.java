package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface ProductService extends IService<Product>{


    /**
     * 根据产品类型和厂家查询
     * @param productTypeId
     * @param manufacturer
     * @return
     */
    List<Product> listByType(Integer productTypeId, String manufacturer);

    Boolean add(Product entity);

    /**
     * 查询是否已经存在
     * @param model 产品型号
     * @param manufacturer 厂家
     * @param productTypeId 产品类型id
     * @return 是否存在
     */
    boolean existsUnique( String model,  String manufacturer, Integer productTypeId);
}
