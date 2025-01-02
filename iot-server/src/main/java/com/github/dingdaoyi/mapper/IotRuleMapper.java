package com.github.dingdaoyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.entity.IotRule;
import com.github.dingdaoyi.model.query.RulePageQuery;
import com.github.dingdaoyi.model.vo.RuleDetailVo;
import com.github.dingdaoyi.model.vo.RulePageVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingyunwei
 */
public interface IotRuleMapper extends BaseMapper<IotRule> {

    Page<RulePageVo> pageByQuery(Page<RulePageVo> page, @Param("pageQuery") RulePageQuery pageQuery);

    List<IotRule> listByDeviceKey(@Param("deviceKey") String deviceKey);
}