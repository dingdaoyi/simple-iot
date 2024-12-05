package com.github.dingdaoyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.model.query.ProductPageQuery;
import com.github.dingdaoyi.model.vo.ProductPageVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author dingyunwei
 */
public interface ProductMapper extends BaseMapper<Product> {

    Page<ProductPageVo> pageByQuery(Page<ProductPageVo> page, @Param("pageQuery") ProductPageQuery pageQuery);
}