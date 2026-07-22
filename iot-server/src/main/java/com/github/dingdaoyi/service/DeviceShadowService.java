package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.entity.DeviceShadow;
import cn.hutool.json.JSONObject;

import java.util.Optional;

/**
 * 设备影子服务
 * @author dingyunwei
 */
public interface DeviceShadowService extends IService<DeviceShadow> {

    /**
     * 获取设备影子
     */
    Optional<DeviceShadow> getByDeviceId(Integer deviceId);

    /**
     * 更新期望状态（合并）
     */
    DeviceShadow updateDesired(Integer deviceId, JSONObject desired);

    /**
     * 更新上报状态（合并）
     */
    DeviceShadow updateReported(Integer deviceId, JSONObject reported);

    /**
     * 清除期望状态
     */
    void clearDesired(Integer deviceId);
}
