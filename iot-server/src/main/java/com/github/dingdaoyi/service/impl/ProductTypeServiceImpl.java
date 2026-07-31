package com.github.dingdaoyi.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.model.PageQuery;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.model.vo.ProductTypeVo;
import com.github.dingdaoyi.utils.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    private static final String CACHE_NAME = "productTypeTree";

    @Cacheable(value = CACHE_NAME, key = "'tree'", condition = "#parentId == -1 and #withChildren == true and (#name == null or #name.isEmpty())")
    @Override
    public List<ProductTypeVo> listByParentId(Integer parentId, Boolean withChildren, String name) {
        List<ProductTypeVo> productTypeVoList = baseMapper.listByParentId(parentId, name);
        if (parentId==-1 && BooleanUtil.isTrue(withChildren) && StringUtils.isBlank(name)) {
            for (ProductTypeVo productTypeVo : productTypeVoList) {
                productTypeVo.setChildren(baseMapper.listByParentId(productTypeVo.getId(), null));
            }
        }
        return productTypeVoList;
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @Override
    public boolean add(ProductType entity) {
        return save(entity);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @Override
    public Boolean updateStatusById(Integer status, Integer id) {
        return baseMapper.updateStatusById(status, id) > 0;
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @Override
    public boolean updateById(ProductType entity) {
        return super.updateById(entity);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @Override
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
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
                .like(StringUtils.isNotBlank(query.getName()), ProductType::getName, query.getName()));
        return PageHelper.result(result);
    }

    @Override
    public boolean existsByParentId(Integer parentId) {
        return exists(Wrappers
        .<ProductType>lambdaQuery()
                .eq(ProductType::getParentId, parentId));
    }
}
