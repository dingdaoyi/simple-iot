package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.entity.ProductType;
import com.github.dingdaoyi.model.PageResult;
import com.github.dingdaoyi.model.enu.SysCodeEnum;
import com.github.dingdaoyi.model.query.ProductPageQuery;
import com.github.dingdaoyi.model.vo.ProductPageVo;
import com.github.dingdaoyi.model.vo.ProductVo;
import com.github.dingdaoyi.service.ModelPropertyService;
import com.github.dingdaoyi.service.ModelServiceService;
import com.github.dingdaoyi.service.ProductTypeService;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import net.dreamlu.mica.core.exception.ServiceException;
import net.dreamlu.mica.core.utils.$;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.mapper.ProductMapper;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.service.ProductService;

/**
 * @author dingyunwei
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Resource
    private ProductTypeService productTypeService;

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

    @Override
    public Optional<ProductVo> details(Integer productId) {
        Product product = getById(productId);
        if (product == null) {
            return Optional.empty();
        }
        ProductVo productVo = new ProductVo(product);
        ProductType productType = productTypeService.getById(product.getProductTypeId());
        productVo.setProductType(productType);
        return Optional.of(productVo);
    }

    @Override
    public boolean existsById(Integer productId) {
        return exists(Wrappers
                .<Product>lambdaQuery()
                .eq(Product::getId, productId));
    }

    @Override
    public Optional<Product> getByProductKey(String productKey) {
        return Optional.ofNullable(getOne(Wrappers.<Product>lambdaQuery()
                .eq(Product::getProductKey, productKey)));
    }

    @Override
    public PageResult<ProductPageVo> pageByQuery(ProductPageQuery pageQuery) {
        Page<ProductPageVo> page = PageHelper.page(pageQuery);
        Page<ProductPageVo> data = baseMapper.pageByQuery(page, pageQuery);
        return PageHelper.result(data);
    }

    @Override
    public boolean existsByProtocol(Integer protocolId) {
        return exists(Wrappers.<Product>lambdaQuery()
                .eq(Product::getProtocolId, protocolId));
    }

    @Override
    public String getTypeModel(Integer id) {
        return baseMapper.getTypeModel(id);
    }
    @Override
    public boolean removeById(Serializable id) {

        return super.removeById(id);
    }
}
