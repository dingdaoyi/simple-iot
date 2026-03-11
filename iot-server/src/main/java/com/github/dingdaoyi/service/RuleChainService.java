package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.model.query.RuleChainAddQuery;
import com.github.dingdaoyi.model.query.RuleChainPageQuery;
import com.github.dingdaoyi.model.query.RuleChainUpdateQuery;
import com.github.dingdaoyi.model.vo.RuleChainDetailVo;
import com.github.dingdaoyi.model.vo.RuleChainPageVo;

import java.util.List;
import java.util.Optional;

/**
 * 规则链服务接口
 * @author dingyunwei
 */
public interface RuleChainService extends IService<RuleChain> {

    /**
     * 分页查询
     */
    PageResult<RuleChainPageVo> pageByQuery(RuleChainPageQuery query);

    /**
     * 详情
     */
    Optional<RuleChainDetailVo> details(Integer id);

    /**
     * 添加
     */
    boolean add(RuleChainAddQuery query);


    boolean updateByQuery(RuleChainUpdateQuery query);

    /**
     * 添加 (兼容 Controller)
     */
    default boolean save(RuleChainAddQuery query) {
        return add(query);
    }


    /**
     * 根据设备Key获取关联的规则链
     */
    List<RuleChain> getByDeviceKey(String deviceKey);

    /**
     * 根据产品ID获取规则链
     */
    List<RuleChain> getByProductId(Integer productId);
}
