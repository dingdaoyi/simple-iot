package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.entity.enu.DataTypeEnum;
import com.github.dingdaoyi.model.ToEntity;
import com.github.dingdaoyi.model.enu.SysCodeEnum;
import com.github.dingdaoyi.model.query.ModelPropertyUpdateQuery;
import com.github.dingdaoyi.model.query.ProductPropertyAddQuery;
import com.github.dingdaoyi.model.query.StandardPropertyAddQuery;
import net.dreamlu.mica.core.exception.ServiceException;
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
        ModelProperty entity = property.toEntity();
        boolean result = baseMapper.insert(entity) > 0;
        if (entity.getDataType() == DataTypeEnum.STRUCT) {
            insertChildren(property.getChildren(), entity.getId());
        }
        return result;
    }

    @Override
    public Boolean update(ModelPropertyUpdateQuery property) {
        //TODO 暂时未对于结构体字段的增加删除兼容
        return baseMapper.updateById(property.toEntity()) > 0;
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
