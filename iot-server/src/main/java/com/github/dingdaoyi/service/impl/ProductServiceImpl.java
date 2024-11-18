package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.dreamlu.mica.core.utils.$;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.mapper.ProductMapper;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.service.ProductService;

/**
 * @author dingyunwei
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public List<Product> listByType(Integer productTypeId, String manufacturer) {
        return list(Wrappers
                .<Product>lambdaQuery()
                .eq(Product::getProductTypeId, productTypeId)
                .like($.isNotBlank(manufacturer), Product::getManufacturer, manufacturer));
    }

    @Override
    public Boolean add(Product entity) {
        return save(entity);
    }

    @Override
    public boolean existsUnique(String model, String manufacturer, Integer productTypeId) {
        return exists(Wrappers.<Product>lambdaQuery()
                .eq(Product::getModel, model)
                .eq(Product::getManufacturer, manufacturer)
                .eq(Product::getProductTypeId, productTypeId));
    }
}
