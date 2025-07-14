package com.github.dingdaoyi.core.driver;

import com.github.dingdaoyi.core.service.DeviceProvider;

import java.util.List;
import java.util.Optional;

public interface DeviceKeyParser {

    void setDeviceProvider(DeviceProvider deviceProvider);
    /**
     * 可以启动的类型
     * @return
     */
    List<DriverTypeEnum> driverTypes();
    /**
     * 解析驱动的名称
     * @return
     */
    String driverName();
    /**
     * 是否包含设备编号
     * @return
     */
    boolean hasDeviceKey(byte[] data);

    /**
     * 设备编号
     * @return
     */
    Optional<String> deviceKey(byte[] data);
}
