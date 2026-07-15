package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.DeviceGroup;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.mapper.DeviceGroupMapper;
import com.github.dingdaoyi.mapper.DeviceGroupRelationMapper;
import com.github.dingdaoyi.mapper.DeviceMapper;
import com.github.dingdaoyi.entity.DeviceGroupRelation;
import com.github.dingdaoyi.service.DeviceGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceGroupServiceImpl extends ServiceImpl<DeviceGroupMapper, DeviceGroup>
        implements DeviceGroupService {

    private final DeviceGroupRelationMapper relationMapper;
    private final DeviceMapper deviceMapper;

    public DeviceGroupServiceImpl(DeviceGroupRelationMapper relationMapper, DeviceMapper deviceMapper) {
        this.relationMapper = relationMapper;
        this.deviceMapper = deviceMapper;
    }

    @Override
    public List<DeviceGroup> tree() {
        return list();
    }

    @Override
    public List<DeviceGroup> children(Integer parentId) {
        return list(new LambdaQueryWrapper<DeviceGroup>()
                .eq(DeviceGroup::getParentId, parentId));
    }

    @Override
    @Transactional
    public void assignDevices(Integer groupId, List<Integer> deviceIds) {
        for (Integer deviceId : deviceIds) {
            Long exists = relationMapper.selectCount(
                    new LambdaQueryWrapper<DeviceGroupRelation>()
                            .eq(DeviceGroupRelation::getGroupId, groupId)
                            .eq(DeviceGroupRelation::getDeviceId, deviceId));
            if (exists == 0) {
                DeviceGroupRelation rel = new DeviceGroupRelation();
                rel.setGroupId(groupId);
                rel.setDeviceId(deviceId);
                relationMapper.insert(rel);
            }
        }
    }

    @Override
    @Transactional
    public void removeDevices(Integer groupId, List<Integer> deviceIds) {
        relationMapper.delete(new LambdaQueryWrapper<DeviceGroupRelation>()
                .eq(DeviceGroupRelation::getGroupId, groupId)
                .in(DeviceGroupRelation::getDeviceId, deviceIds));
    }

    @Override
    public List<Integer> listDeviceIds(Integer groupId) {
        return relationMapper.selectList(
                        new LambdaQueryWrapper<DeviceGroupRelation>()
                                .eq(DeviceGroupRelation::getGroupId, groupId))
                .stream()
                .map(DeviceGroupRelation::getDeviceId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Device> listDevices(Integer groupId) {
        List<Integer> deviceIds = listDeviceIds(groupId);
        if (deviceIds.isEmpty()) {
            return Collections.emptyList();
        }
        return deviceMapper.selectBatchIds(deviceIds);
    }
}
