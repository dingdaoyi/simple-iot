package com.github.dingdaoyi.iot;


import com.github.dingdaoyi.proto.model.DeviceRequest;

/**
 * @author dingyunwei
 */
public interface IoTDataProcessor {

    /**
     * 数据接收
     * @param request
     */
    void messageUp(DeviceRequest request);
}
