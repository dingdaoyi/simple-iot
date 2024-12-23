package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.IotRule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.model.PageResult;
import com.github.dingdaoyi.model.query.RuleAddQuery;
import com.github.dingdaoyi.model.query.RulePageQuery;
import com.github.dingdaoyi.model.vo.RulePageVo;

/**
 * @author dingyunwei
 */
public interface IotRuleService extends IService<IotRule>{


    PageResult<RulePageVo> pageByQuery(RulePageQuery pageQuery);
    /**
     * 添加
     * @param addQuery
     * @return
     */
    boolean save(RuleAddQuery addQuery);
}
