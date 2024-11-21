package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.ProductType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.model.PageQuery;
import com.github.dingdaoyi.model.PageResult;

import java.util.List;

public interface ProductTypeService extends IService<ProductType>{


    /**
     * 通过父级查询子级
     * @param parentId
     * @return
     */
    List<ProductType> listByParentId(Integer parentId);

    boolean add(ProductType entity);

    Boolean updateStatusById(Integer status, Integer id);

    boolean existsById(Integer productTypeId);

    PageResult<ProductType> pageByQuery(PageQuery query);
}
