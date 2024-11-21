package com.github.dingdaoyi.proto.inter;

import java.io.IOException;
import java.util.Map;

/**
 * 设备连接
 * @author dingyunwei
 */
public interface DeviceConnection {
    /**
     * 是否已经连接
     * @return
     */
    boolean isConnected();

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 发送数据
     * @param metadata 消息信息,按照每个协议自行定义
     * @param message 消息数据
     */
    void sendMessage(Map<String,Object> metadata, byte[] message) throws IOException;
}
