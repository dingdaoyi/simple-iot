package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.entity.DeviceGroup;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.DeviceTag;

import java.util.List;

public interface DeviceGroupService extends IService<DeviceGroup> {

    /**
     * 获取分组树（全量）
     */
    List<DeviceGroup> tree();

    /**
     * 获取分组的子分组
     */
    List<DeviceGroup> children(Integer parentId);

    /**
     * 批量分配设备到分组
     */
    void assignDevices(Integer groupId, List<Integer> deviceIds);

    /**
     * 移除设备分组关联
     */
    void removeDevices(Integer groupId, List<Integer> deviceIds);

    /**
     * 获取分组下的设备ID列表
     */
    List<Integer> listDeviceIds(Integer groupId);

    /**
     * 获取分组下的设备列表（含设备详情）
     */
    List<Device> listDevices(Integer groupId);
}
