package com.github.dingdaoyi.core.driver;

import java.util.List;

public interface DeviceKeyParser {

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
    String deviceKey(byte[] data);
}
