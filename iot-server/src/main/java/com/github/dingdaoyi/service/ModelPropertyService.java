package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.ModelProperty;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.model.query.ModelPropertyUpdateQuery;
import com.github.dingdaoyi.model.query.ProductPropertyAddQuery;
import com.github.dingdaoyi.model.query.StandardPropertyAddQuery;

import java.util.List;

public interface ModelPropertyService extends IService<ModelProperty> {


    List<ModelProperty> listByProductType(Integer productTypeId, Integer productId,Integer paramType);

    /**
     * 根据父级id查询
     * @param parentId
     * @return
     */
    List<ModelProperty> listByParentId(Integer parentId);

    Boolean saveStandardProperty(StandardPropertyAddQuery property);

    /**
     * 所有id是否存在
     *
     * @param ids
     * @return
     */
    boolean allExists(List<Integer> ids);

    /**
     * 存在判断
     * @param identifier
     * @param productTypeId
     * @return
     */
    boolean exists(String identifier,
                   Integer productTypeId);

    boolean exists(String identifier, Integer productTypeId,  Integer productId);

    Boolean saveProductProperty(ProductPropertyAddQuery property);

    Boolean update(ModelPropertyUpdateQuery property);
}
