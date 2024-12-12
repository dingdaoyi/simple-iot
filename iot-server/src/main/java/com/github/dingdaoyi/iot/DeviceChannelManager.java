package com.github.dingdaoyi.iot;

import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.model.ProtocolException;

import java.io.IOException;
import java.util.Map;

/**
 * 设备通道管理接口
 * @author dingyunwei
 */
public interface DeviceChannelManager {

    /**
     * 添加设备连接
     * @param deviceKey 设备唯一标识
     * @param connection 设备连接对象
     */
    void addConnection(String deviceKey, DeviceConnection connection);

    /**
     * 根据 deviceKey 获取设备连接
     * @param deviceKey 设备唯一标识
     * @return 设备连接对象
     */
    DeviceConnection getConnection(String deviceKey);

    /**
     * 移除设备连接
     * @param deviceKey 设备唯一标识
     */
    void removeConnection(String deviceKey);

    /**
     * 清理已关闭的连接
     */
    void close();

    /**
     * 发送消息给指定设备
     * @param deviceKey 设备唯一标识
     * @param metadata 消息元数据
     * @param message 消息数据
     * @throws IOException 如果发送失败
     */
    void sendMessage(String deviceKey, Map<String, Object> metadata, byte[] message) throws ProtocolException;
}