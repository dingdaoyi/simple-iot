package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.model.PageQuery;
import com.github.dingdaoyi.model.PageResult;
import com.github.dingdaoyi.utils.PageHelper;
import net.dreamlu.mica.core.utils.$;
import org.springframework.stereotype.Service;

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

    @Override
    public PageResult<ProductType> pageByQuery(PageQuery query) {
        Page<ProductType> page = PageHelper.page(query);
        Page<ProductType> result = baseMapper.selectPage(page, Wrappers
                .<ProductType>lambdaQuery()
                .like($.isNotBlank(query.getName()), ProductType::getName, query.getName()));
        return PageHelper.result(result);
    }
}
