package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.enu.RuleSourceType;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.model.query.RuleAddQuery;
import com.github.dingdaoyi.model.query.RulePageQuery;
import com.github.dingdaoyi.model.query.RuleUpdateQuery;
import com.github.dingdaoyi.model.vo.RuleDetailVo;
import com.github.dingdaoyi.model.vo.RulePageVo;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.MessageReceiveService;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.IotRule;
import com.github.dingdaoyi.mapper.IotRuleMapper;
import com.github.dingdaoyi.service.IotRuleService;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author dingyunwei
 */
@Service
@Slf4j
public class IotRuleServiceImpl extends ServiceImpl<IotRuleMapper, IotRule> implements IotRuleService {
    @Resource
    private ProductService productService;
    @Resource
    private DeviceService deviceService;
    @Resource
    private MessageReceiveService messageReceiveService;

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

    @Override
    public boolean update(RuleUpdateQuery updateQuery) {
        return super.updateById(updateQuery.toEntity());
    }

    @Override
    public RuleDetailVo details(Integer id) {
        IotRule iotRule = getById(id);
        RuleDetailVo vo = new RuleDetailVo(iotRule);
        switch (vo.getSourceType()) {
            case PRODUCT -> productService.details(vo.getSourceId())
                    .ifPresent(product -> {
                        vo.setSourceName(product.getModel() + "(" + product.getManufacturer() + ")");
                        vo.setProductId(product.getId());
                        vo.setProductName(product.getModel() + "(" + product.getManufacturer() + ")");
                        vo.setProductTypeId(product.getProductTypeId());
                        vo.setProductTypeName(product.getProductType().getName());
                    });
            case DEVICE -> deviceService.details(vo.getSourceId())
                    .ifPresent(device -> {
                        vo.setSourceName(device.getDeviceKey());
                        vo.setProductId(device.getProductId());
                        Product product = device.getProduct();
                        vo.setProductName(product.getModel() + "(" + product.getManufacturer() + ")");
                        vo.setProductTypeId(product.getProductTypeId());
                        vo.setProductTypeName(device.getProductType().getName());
                    });
        }
        switch (vo.getTargetType()) {
            case MESSAGE -> vo.setTargetName(messageReceiveService.getNameById(vo.getTargetId()));
        }

        return vo;
    }

    @Override
    public List<IotRule> queryByDeviceKey(String deviceKey) {
        return baseMapper.listByDeviceKey(deviceKey);
    }
    @Override
    public boolean removeById(Serializable id) {
        log.info("删除规则引擎:{}", id);
        return super.removeById(id);
    }
}
