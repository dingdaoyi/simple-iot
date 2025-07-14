package com.github.dingdaoyi.core.model;

import lombok.Data;

/**
 * @author dingyunwei
 */
@Data
public class DeviceBase {
    /**
     * 设备编号
     */
    private String deviceKey;

    /**
     * 第三方设备编号
     */
    private String threadDeviceId;
}
