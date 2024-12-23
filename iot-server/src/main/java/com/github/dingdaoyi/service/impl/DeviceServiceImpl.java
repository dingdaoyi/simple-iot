package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.model.PageResult;
import com.github.dingdaoyi.model.query.DevicePageQuery;
import com.github.dingdaoyi.model.vo.DevicePageVo;
import com.github.dingdaoyi.model.vo.DeviceVo;
import com.github.dingdaoyi.model.vo.ProductVo;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.TslModelService;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Optional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.mapper.DeviceMapper;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.service.DeviceService;
/**
 * @author dingyunwei
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService{
    @Resource
    private ProductService productService;

    @Resource
    private TslModelService tslModelService;
    @Override
    public Optional<DeviceVo> details(Integer id) {
        Device device = baseMapper.selectById(id);
        if (device == null) {
         return Optional.empty();
        }
        return getDeviceVo(device);
    }

    private @NotNull Optional<DeviceVo> getDeviceVo(Device device) {
        DeviceVo deviceVo = DeviceVo.build(device);
        Optional<ProductVo> productVo = productService.details(deviceVo.getProductId());
        if (productVo.isPresent()) {
            ProductVo product = productVo.get();
            deviceVo.setProductVo(product);
            Optional<TslModel> tslModelOptional = tslModelService.findByProductKey(product.getProductKey());
            tslModelOptional.ifPresent(deviceVo::setTslModel);
        }
        return Optional.of(deviceVo);
    }

    @Override
    public Optional<DeviceDTO> getByDeviceKey(String deviceKey) {
        return baseMapper.findByDeviceKey(deviceKey);
    }

    @Override
    public PageResult<DevicePageVo> pageByQuery(DevicePageQuery query) {
        Page<DevicePageVo> page = PageHelper.page(query);
        return PageHelper.result(baseMapper.pageByQuery(page,query));
    }

    @Override
    public void updateOlinStatus(String deviceKey, boolean online) {
        baseMapper
                .update(Wrappers
                        .<Device>lambdaUpdate()
                        .eq(Device::getDeviceKey, deviceKey)
                        .set(Device::getOnline, online)
                        .set(online,Device::getActiveStatus,true));
    }

    @Override
    public String getDeviceKey(Integer id) {
        return baseMapper.findDeviceKeyById(id);
    }

    @Override
    public boolean removeById(Serializable id) {
        //TODO 判断子设备,是否可以删除
        return super.removeById(id);
    }
}
