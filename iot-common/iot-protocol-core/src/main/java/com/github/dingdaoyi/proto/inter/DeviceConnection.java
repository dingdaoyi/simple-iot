package com.github.dingdaoyi.proto.inter;

import java.io.IOException;

/**
 * 设备连接
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
     * @param identifier 标识符
     * @param message 消息数据
     */
    void sendMessage(String identifier,byte[] message) throws IOException;
}
