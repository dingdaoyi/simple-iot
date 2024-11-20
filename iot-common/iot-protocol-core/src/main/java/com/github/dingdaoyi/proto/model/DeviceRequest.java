package com.github.dingdaoyi.proto.model;

import com.github.dingdaoyi.proto.inter.DeviceConnection;
import lombok.Data;

/**
 * 设备数据请求
 * @author dingyunwei
 */
@Data
public class DeviceRequest {

    /*
    设备编号
     */
    private Integer deviceId;

    /**
     * 通道连接
     */
    private DeviceConnection connection;
    /**
     * 上报数据类型
     */
    private ProtoMessageType messageType;

    /**
     * 协议key
     */
    private String protoKey;

    /**
     * 设备数据
     */
    private byte[] data;
}
