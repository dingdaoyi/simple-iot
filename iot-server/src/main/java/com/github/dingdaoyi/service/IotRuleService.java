package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.IotRule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.model.query.RuleAddQuery;
import com.github.dingdaoyi.model.query.RulePageQuery;
import com.github.dingdaoyi.model.query.RuleUpdateQuery;
import com.github.dingdaoyi.model.vo.RuleDetailVo;
import com.github.dingdaoyi.model.vo.RulePageVo;

import java.util.List;

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

    /**
     *
     * 修改
     * @param updateQuery 参数
     * @return
     */
    boolean update(RuleUpdateQuery updateQuery);

    RuleDetailVo details(Integer id);

    List<IotRule> queryByDeviceKey(String deviceKey);
}
