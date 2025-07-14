package com.github.dingdaoyi.core.service;

import com.github.dingdaoyi.core.model.DeviceBase;

import java.util.Optional;

/**
 * @author dingyunwei
 */
public interface DeviceProvider {

    /**
     *
     * @param threadDeviceId 第三方id
     */
    Optional<DeviceBase> getByThreadDeviceId(String threadDeviceId);
}
