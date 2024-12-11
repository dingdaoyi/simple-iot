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
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

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
        productVo.ifPresent(deviceVo::setProductVo);
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

}
