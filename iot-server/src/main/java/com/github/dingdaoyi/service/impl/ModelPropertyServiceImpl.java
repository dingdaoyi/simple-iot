package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.entity.ModelService;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.enu.DataTypeEnum;
import com.github.dingdaoyi.model.ToEntity;
import com.github.dingdaoyi.model.enu.SysCodeEnum;
import com.github.dingdaoyi.model.query.ModelPropertyUpdateQuery;
import com.github.dingdaoyi.model.query.ProductPropertyAddQuery;
import com.github.dingdaoyi.model.query.StandardPropertyAddQuery;
import com.github.dingdaoyi.service.CacheService;
import com.github.dingdaoyi.service.ProductService;
import jakarta.annotation.Resource;
import net.dreamlu.mica.core.exception.ServiceException;
import net.dreamlu.mica.core.result.SystemCode;
import net.dreamlu.mica.core.utils.$;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.mapper.ModelPropertyMapper;
import com.github.dingdaoyi.entity.ModelProperty;
import com.github.dingdaoyi.service.ModelPropertyService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dingyunwei
 */
@Service
public class ModelPropertyServiceImpl extends ServiceImpl<ModelPropertyMapper, ModelProperty> implements ModelPropertyService {

    @Resource
    private CacheService cacheService;
    @Resource
    private ProductService productService;

    @Override
    public List<ModelProperty> listByProductType(Integer productTypeId, Integer productId, Integer paramType) {
        return list(Wrappers
                .<ModelProperty>lambdaQuery()
                .eq(ModelProperty::getProductTypeId, productTypeId)
                //标准物模型不查自定义部分,自定义物模型不查询custom字段
                .eq($.isNull(productId), ModelProperty::getCustom, false)
                .eq($.isNotNull(productId), ModelProperty::getProductId, productId)
                .eq($.isNotNull(paramType), ModelProperty::getParamType, paramType));
    }

    @Override
    public List<ModelProperty> listByParentId(Integer parentId) {
        return list(Wrappers.<ModelProperty>lambdaQuery()
                .eq(ModelProperty::getParentId, parentId));
    }

    @Override
    public boolean updateById(ModelProperty entity) {
        return super.updateById(entity);
    }

    @Override
    public Boolean saveStandardProperty(StandardPropertyAddQuery property) {
        ModelProperty entity = property.toEntity();
        boolean result = baseMapper.insert(entity) > 0;
        if (entity.getDataType() == DataTypeEnum.STRUCT) {
            insertChildren(property.getChildren(), entity.getId());
        }

        return result;
    }

    private void insertChildren(List<? extends ToEntity<ModelProperty>> children, Integer parentId) {

        if ($.isNotEmpty(children)) {
            //保存子类模型
            List<ModelProperty> propertyList = children
                    .stream()
                    .map(item -> {
                        ModelProperty child = item.toEntity();
                        child.setParentId(parentId);
                        return child;
                    })
                    .toList();
            int result = baseMapper.insertList(propertyList);
            if (result != propertyList.size()) {
                throw new ServiceException(SysCodeEnum.BAD_REQUEST, "子属性添加失败");
            }
        }
    }

    @Override
    public boolean allExists(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return count(Wrappers.<ModelProperty>lambdaQuery().in(ModelProperty::getId, ids)) == ids.size();
    }

    @Override
    public boolean exists(String identifier, Integer productTypeId) {
        return exists(identifier, productTypeId, null);
    }

    @Override
    public boolean exists(String identifier, Integer productTypeId, Integer productId) {
        return exists(Wrappers.<ModelProperty>lambdaQuery()
                .eq(ModelProperty::getIdentifier, identifier)
                .eq(ModelProperty::getProductTypeId, productTypeId)
                .eq($.isNotNull(productId), ModelProperty::getProductId, productId));
    }

    @Override
    public Boolean saveProductProperty(ProductPropertyAddQuery property) {
        Product product = productService.getById(property.getProductId());
        if (product == null) {
            throw new ServiceException(SystemCode.PARAM_VALID_ERROR, "请选择正确的产品");
        }
        ModelProperty entity = property.toEntity();
        boolean result = baseMapper.insert(entity) > 0;
        if (entity.getDataType() == DataTypeEnum.STRUCT) {
            insertChildren(property.getChildren(), entity.getId());
        }
        cacheService.evictTslModel(product.getProductKey());
        return result;
    }

    @Override
    public Boolean update(ModelPropertyUpdateQuery property) {
        ModelProperty modelProperty = getById(property.getId());
        if (modelProperty == null) {
            throw new ServiceException("属性不存在,无法修改!");
        }
        boolean result = baseMapper.updateById(property.toEntity()) > 0;
        if (result && modelProperty.getProductId() != null) {
            Product product = productService.getById(modelProperty.getProductId());
            cacheService.evictTslModel(product.getProductKey());
        }
        //TODO 暂时未对于结构体字段的增加删除兼容
        return result;
    }

    @Override
    public List<ModelProperty> listByProduct(Integer productId, Integer productTypeId) {
        LambdaQueryWrapper<ModelProperty> wrapper = Wrappers
                .<ModelProperty>lambdaQuery()
                .eq(ModelProperty::getProductTypeId, productTypeId)
                .and(w -> w
                        .eq(ModelProperty::getCustom, false)
                        .or()
                        .eq(ModelProperty::getCustom, true)
                        .eq(ModelProperty::getProductId, productId)
                )
                .orderByAsc(ModelProperty::getId);
        return list(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        //先删除结构体的子类
        remove(Wrappers
                .<ModelProperty>lambdaQuery()
                .eq(ModelProperty::getParentId, id)
                .eq(ModelProperty::getDataType, DataTypeEnum.STRUCT));
        return super.removeById(id);
    }
}
