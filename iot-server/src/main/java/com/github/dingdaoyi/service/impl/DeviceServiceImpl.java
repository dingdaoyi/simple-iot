package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.core.model.DeviceBase;
import com.github.dingdaoyi.core.service.DeviceProvider;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.model.query.DevicePageQuery;
import com.github.dingdaoyi.model.vo.DevicePageVo;
import com.github.dingdaoyi.model.vo.DeviceVo;
import com.github.dingdaoyi.model.vo.ProductVo;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.entity.DeviceGroupRelation;
import com.github.dingdaoyi.entity.DeviceShadow;
import com.github.dingdaoyi.mapper.DeviceGroupRelationMapper;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.service.DeviceShadowService;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.TslModelService;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.mapper.DeviceMapper;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.service.DeviceService;

/**
 * @author dingyunwei
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService, DeviceProvider {
    @Resource
    private ProductService productService;

    @Resource
    private TslModelService tslModelService;

    @Resource
    private DeviceShadowService shadowService;

    @Resource
    private DeviceGroupRelationMapper groupRelationMapper;

    @Override
    public Optional<DeviceVo> details(Integer id) {
        Device device = baseMapper.selectById(id);
        if (device == null) {
            return Optional.empty();
        }
        return getDeviceVo(device);
    }

    private Optional<DeviceVo> getDeviceVo(Device device) {
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
        Page<DevicePageVo> result = baseMapper.pageByQuery(page, query);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public void updateOlinStatus(String deviceKey, boolean online) {
        baseMapper
                .update(Wrappers
                        .<Device>lambdaUpdate()
                        .eq(Device::getDeviceKey, deviceKey)
                        .set(Device::getOnline, online)
                        .set(online, Device::getActiveStatus, true));
    }

    @Override
    public String getDeviceKey(Integer id) {
        return baseMapper.findDeviceKeyById(id);
    }

    @Override
    public List<Device> list(Integer productTypeId, Integer productId, String deviceKey) {
        return baseMapper.listByProductAndDeviceKey(productTypeId, productId, deviceKey);
    }

    @Override
    public Optional<DeviceDTO> getByThirdDeviceId(String thirdDeviceId) {
        return baseMapper.selectByThirdDeviceId(thirdDeviceId);
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        Integer deviceId = (Integer) id;
        // 子设备存在则拒绝删除
        long childCount = count(Wrappers.<Device>lambdaQuery().eq(Device::getParentId, deviceId));
        if (childCount > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "存在子设备，无法删除");
        }
        boolean deleted = super.removeById(id);
        if (deleted) {
            // 级联清理：设备影子
            shadowService.remove(Wrappers.<DeviceShadow>lambdaQuery().eq(DeviceShadow::getDeviceId, deviceId));
            // 级联清理：设备分组关联
            groupRelationMapper.delete(Wrappers.<DeviceGroupRelation>lambdaQuery().eq(DeviceGroupRelation::getDeviceId, deviceId));
        }
        return deleted;
    }

    @Override
    public Optional<DeviceBase> getByThreadDeviceId(String threadDeviceId) {
        return getOneOpt(Wrappers
                .<Device>lambdaQuery()
                .eq(Device::getThirdDeviceId, threadDeviceId))
                .map(item -> {
                    DeviceBase deviceBase = new DeviceBase();
                    deviceBase.setThreadDeviceId(item.getThirdDeviceId());
                    deviceBase.setDeviceKey(item.getDeviceKey());
                    return deviceBase;
                });
    }
}
