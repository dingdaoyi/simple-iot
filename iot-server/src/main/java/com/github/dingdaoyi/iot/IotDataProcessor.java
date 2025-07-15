package com.github.dingdaoyi.iot;


import com.github.dingdaoyi.proto.model.DeviceRequest;

/**
 * @author dingyunwei
 */
public interface IotDataProcessor {

    /**
     * 数据接收
     * @param request
     */
    void messageUp(DeviceRequest request);

    /**
     * 设备上线
     * @param deviceKey 设备编号
     */
    void oline(String deviceKey);

    /**
     * 设备离线
     * @param deviceKey 设备编号
     */
    void offline(String deviceKey);
}
