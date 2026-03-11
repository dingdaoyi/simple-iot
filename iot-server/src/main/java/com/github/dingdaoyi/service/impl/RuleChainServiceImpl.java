package com.github.dingdaoyi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.RuleSourceType;
import com.github.dingdaoyi.mapper.RuleChainMapper;
import com.github.dingdaoyi.model.query.RuleChainAddQuery;
import com.github.dingdaoyi.model.query.RuleChainPageQuery;
import com.github.dingdaoyi.model.query.RuleChainUpdateQuery;
import com.github.dingdaoyi.model.vo.RuleChainDetailVo;
import com.github.dingdaoyi.model.vo.RuleChainPageVo;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.RuleChainService;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 规则链服务实现
 * @author dingyunwei
 */
@Slf4j
@Service
public class RuleChainServiceImpl extends ServiceImpl<RuleChainMapper, RuleChain> implements RuleChainService {

    @Resource
    private DeviceService deviceService;

    @Resource
    private ProductService productService;

    @Override
    public PageResult<RuleChainPageVo> pageByQuery(RuleChainPageQuery query) {
        Page<RuleChainPageVo> page = PageHelper.page(query);
        Page<RuleChainPageVo> result = baseMapper.pageByQuery(page, query);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Optional<RuleChainDetailVo> details(Integer id) {
        RuleChain ruleChain = baseMapper.selectById(id);
        if (ruleChain == null) {
            return Optional.empty();
        }
        RuleChainDetailVo vo = new RuleChainDetailVo();
        BeanUtil.copyProperties(ruleChain, vo);
        vo.setSourceType(ruleChain.getSourceType().name());

        // 填充数据源名称
        fillSourceName(vo, ruleChain);

        return Optional.of(vo);
    }

    @Override
    public boolean add(RuleChainAddQuery query) {
        RuleChain entity = query.toEntity();
        return save(entity);
    }

    @Override
    public boolean updateByQuery(RuleChainUpdateQuery query) {
        RuleChain entity = query.toEntity();
        return updateById(entity);
    }

    @Override
    public List<RuleChain> getByDeviceKey(String deviceKey) {
        // 1. 获取设备信息
        Device device = deviceService.getByDeviceKey(deviceKey).orElse(null);
        if (device == null) {
            return new ArrayList<>();
        }

        List<RuleChain> result = new ArrayList<>();

        // 2. 获取按产品配置的规则链
        List<RuleChain> productChains = list(Wrappers.<RuleChain>lambdaQuery()
            .eq(RuleChain::getIsEnabled, true)
            .eq(RuleChain::getSourceType, RuleSourceType.PRODUCT)
            .eq(RuleChain::getSourceId, device.getProductId()));
        result.addAll(productChains);

        // 3. 获取按设备配置的规则链
        List<RuleChain> deviceChains = list(Wrappers.<RuleChain>lambdaQuery()
            .eq(RuleChain::getIsEnabled, true)
            .eq(RuleChain::getSourceType, RuleSourceType.DEVICE)
            .eq(RuleChain::getSourceId, device.getId()));
        result.addAll(deviceChains);

        return result;
    }

    @Override
    public List<RuleChain> getByProductId(Integer productId) {
        return list(Wrappers.<RuleChain>lambdaQuery()
            .eq(RuleChain::getIsEnabled, true)
            .eq(RuleChain::getSourceType, RuleSourceType.PRODUCT)
            .eq(RuleChain::getSourceId, productId));
    }

    private void fillSourceName(RuleChainDetailVo vo, RuleChain ruleChain) {
        if (ruleChain.getSourceId() == null) {
            return;
        }
        switch (ruleChain.getSourceType()) {
            case PRODUCT -> {
                Product product = productService.getById(ruleChain.getSourceId());
                if (product != null) {
                    vo.setSourceName(product.getModel());
                }
            }
            case DEVICE -> {
                Device device = deviceService.getById(ruleChain.getSourceId());
                if (device != null) {
                    vo.setSourceName(device.getDeviceName());
                }
            }
            default -> {}
        }
    }
}
