package com.github.dingdaoyi.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.model.query.RuleChainPageQuery;
import com.github.dingdaoyi.model.vo.RuleChainPageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 规则链 Mapper
 * @author dingyunwei
 */
@Mapper
public interface RuleChainMapper extends BaseMapper<RuleChain> {

    /**
     * 分页查询
     */
    Page<RuleChainPageVo> pageByQuery(Page<RuleChainPageVo> page, @Param("query") RuleChainPageQuery query);

    /**
     * 根据产品ID查询规则链
     */
    List<RuleChain> selectByProductId(@Param("productId") Integer productId);

    /**
     * 根据设备Key查询关联的规则链
     */
    List<RuleChain> selectByDeviceKey(@Param("deviceKey") String deviceKey);
}
