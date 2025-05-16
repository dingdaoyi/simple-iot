package com.github.dingdaoyi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.model.enu.SystemCode;
import com.github.dingdaoyi.model.exception.ServiceException;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.model.ToEntity;
import com.github.dingdaoyi.model.query.ModelPropertyUpdateQuery;
import com.github.dingdaoyi.model.query.ProductPropertyAddQuery;
import com.github.dingdaoyi.model.query.StandardPropertyAddQuery;
import com.github.dingdaoyi.service.CacheService;
import com.github.dingdaoyi.service.ProductService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.mapper.ModelPropertyMapper;
import com.github.dingdaoyi.entity.ModelProperty;
import com.github.dingdaoyi.service.ModelPropertyService;
import org.springframework.transaction.annotation.Transactional;

import static com.github.dingdaoyi.service.CacheService.TSL_MODEL_CACHE;

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
    public List<ModelProperty> listByProductType(Integer productTypeId, Integer productId, Integer paramType, String search, Boolean all) {
        LambdaQueryWrapper<ModelProperty> queryWrapper = Wrappers
                .<ModelProperty>lambdaQuery()
                .eq(ModelProperty::getProductTypeId, productTypeId)
                //标准物模型不查自定义部分,自定义物模型不查询custom字段
                .eq(ObjectUtils.isNull(productId), ModelProperty::getCustom, false)
                .eq(ObjectUtils.isNotNull(productId), ModelProperty::getProductId, productId)
                .eq(ObjectUtils.isNotNull(paramType), ModelProperty::getParamType, paramType)
                .and(StringUtils.isNotBlank(search), w -> w
                        .like(ModelProperty::getName, search)
                        .or()
                        .eq(ModelProperty::getIdentifier, search)
                );
        LambdaQueryWrapper<ModelProperty> allQueryWrapper = Wrappers
                .<ModelProperty>lambdaQuery()
                .and(w -> w.and(wr ->
                                wr.eq(ModelProperty::getProductTypeId, productTypeId)
                                        .eq(ModelProperty::getCustom, false))
                        .or()
                        .eq(ObjectUtils.isNotNull(productId), ModelProperty::getProductId, productId))
                .eq(ObjectUtils.isNotNull(paramType), ModelProperty::getParamType, paramType)
                .and(StringUtils.isNotBlank(search), w -> w
                        .like(ModelProperty::getName, search)
                        .or()
                        .eq(ModelProperty::getIdentifier, search)
                );
        return list(all ? allQueryWrapper : queryWrapper);
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

        if (CollectionUtil.isNotEmpty(children)) {
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
                throw new ServiceException(SystemCode.BAD_REQUEST, "子属性添加失败");
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
                .eq(ObjectUtils.isNotNull(productId), ModelProperty::getProductId, productId));
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
            throw new ServiceException(SystemCode.BAD_REQUEST,"属性不存在,无法修改!");
        }
        boolean result = baseMapper.updateById(property.toEntity()) > 0;
        if (result) {
            cacheService.clearCache(TSL_MODEL_CACHE);
        }
        //TODO 暂时未对于结构体字段的增加删除兼容
        return result;
    }

    @Override
    public List<ModelProperty> listByProduct(Integer productId, Integer productTypeId) {
        LambdaQueryWrapper<ModelProperty> allQueryWrapper = Wrappers
                .<ModelProperty>lambdaQuery()
                .and(w -> w.and(wr ->
                                wr.eq(ModelProperty::getProductTypeId, productTypeId)
                                        .eq(ModelProperty::getCustom, false))
                        .or()
                        .eq(ObjectUtils.isNotNull(productId), ModelProperty::getProductId, productId));
        return list(allQueryWrapper);
    }

    @Override
    public boolean existsByProduct(Integer productId) {
        return exists(Wrappers.<ModelProperty>lambdaQuery()
                .eq(ModelProperty::getProductId, productId));
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
