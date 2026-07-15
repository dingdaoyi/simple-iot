package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.DeviceTag;

import java.util.List;

public interface DeviceTagService extends IService<DeviceTag> {

    /**
     * 获取所有标签
     */
    List<DeviceTag> all();

    /**
     * 批量给设备打标签
     */
    void tagDevices(Integer tagId, List<Integer> deviceIds);

    /**
     * 移除设备标签
     */
    void untagDevices(Integer tagId, List<Integer> deviceIds);

    /**
     * 获取设备的标签列表
     */
    List<DeviceTag> listByDeviceId(Integer deviceId);

    /**
     * 获取标签下的设备列表
     */
    List<Device> listDevicesByTagId(Integer tagId);
}
