package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.entity.enu.RuleSourceType;
import com.github.dingdaoyi.model.PageResult;
import com.github.dingdaoyi.model.query.RuleAddQuery;
import com.github.dingdaoyi.model.query.RulePageQuery;
import com.github.dingdaoyi.model.vo.RulePageVo;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.IotRule;
import com.github.dingdaoyi.mapper.IotRuleMapper;
import com.github.dingdaoyi.service.IotRuleService;
/**
 * @author dingyunwei
 */
@Service
public class IotRuleServiceImpl extends ServiceImpl<IotRuleMapper, IotRule> implements IotRuleService{
    @Resource
    private ProductService productService;
    @Resource
    private DeviceService deviceService;

    @Override
    public PageResult<RulePageVo> pageByQuery(RulePageQuery pageQuery) {
        Page<RulePageVo> page = PageHelper.page(pageQuery);
        PageResult<RulePageVo> result = PageHelper.result(baseMapper.pageByQuery(page, pageQuery));
        for (RulePageVo rulePageVo : result) {
            switch (rulePageVo.getSourceType()) {
                case PRODUCT -> rulePageVo.setSourceName(productService.getTypeModel(rulePageVo.getSourceId()));
                case DEVICE -> rulePageVo.setSourceName(deviceService.getDeviceKey(rulePageVo.getSourceId()));
            }
        }
        return result;
    }

    @Override
    public boolean save(RuleAddQuery addQuery) {
        //TODO 数据校验转化
        return super.save(addQuery.toEntity());
    }
}
