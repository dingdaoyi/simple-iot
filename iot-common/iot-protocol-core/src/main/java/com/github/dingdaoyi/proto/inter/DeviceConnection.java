package com.github.dingdaoyi.proto.inter;

import java.io.IOException;
import java.nio.ByteBuffer;

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
     * @param message
     */
    void sendMessage(byte[] message) throws IOException;
}
