package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Wrapper;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.ProductType;
import com.github.dingdaoyi.mapper.ProductTypeMapper;
import com.github.dingdaoyi.service.ProductTypeService;

/**
 * @author dingyunwei
 */
@Service
public class ProductTypeServiceImpl extends ServiceImpl<ProductTypeMapper, ProductType> implements ProductTypeService {

    @Override
    public List<ProductType> listByParentId(Integer parentId) {
        return baseMapper.selectList(Wrappers
                .<ProductType>lambdaQuery()
                .eq(ProductType::getParentId, parentId));
    }

    @Override
    public boolean add(ProductType entity) {
        return save(entity);
    }

    @Override
    public Boolean updateStatusById(Integer status, Integer id) {
        return baseMapper.updateStatusById(status, id) > 0;
    }

    @Override
    public boolean existsById(Integer productTypeId) {
        return exists(Wrappers.<ProductType>lambdaQuery().eq(ProductType::getId, productTypeId));
    }
}
