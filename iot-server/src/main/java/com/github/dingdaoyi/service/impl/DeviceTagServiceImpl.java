package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.DeviceTag;
import com.github.dingdaoyi.entity.DeviceTagRelation;
import com.github.dingdaoyi.mapper.DeviceTagMapper;
import com.github.dingdaoyi.mapper.DeviceTagRelationMapper;
import com.github.dingdaoyi.service.DeviceTagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceTagServiceImpl extends ServiceImpl<DeviceTagMapper, DeviceTag>
        implements DeviceTagService {

    private final DeviceTagRelationMapper relationMapper;

    public DeviceTagServiceImpl(DeviceTagRelationMapper relationMapper) {
        this.relationMapper = relationMapper;
    }

    @Override
    public List<DeviceTag> all() {
        return list();
    }

    @Override
    @Transactional
    public void tagDevices(Integer tagId, List<Integer> deviceIds) {
        for (Integer deviceId : deviceIds) {
            Long exists = relationMapper.selectCount(
                    new LambdaQueryWrapper<DeviceTagRelation>()
                            .eq(DeviceTagRelation::getTagId, tagId)
                            .eq(DeviceTagRelation::getDeviceId, deviceId));
            if (exists == 0) {
                DeviceTagRelation rel = new DeviceTagRelation();
                rel.setTagId(tagId);
                rel.setDeviceId(deviceId);
                relationMapper.insert(rel);
            }
        }
    }

    @Override
    @Transactional
    public void untagDevices(Integer tagId, List<Integer> deviceIds) {
        relationMapper.delete(new LambdaQueryWrapper<DeviceTagRelation>()
                .eq(DeviceTagRelation::getTagId, tagId)
                .in(DeviceTagRelation::getDeviceId, deviceIds));
    }

    @Override
    public List<DeviceTag> listByDeviceId(Integer deviceId) {
        List<Integer> tagIds = relationMapper.selectList(
                        new LambdaQueryWrapper<DeviceTagRelation>()
                                .eq(DeviceTagRelation::getDeviceId, deviceId))
                .stream()
                .map(DeviceTagRelation::getTagId)
                .collect(Collectors.toList());
        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        return listByIds(tagIds);
    }
}
