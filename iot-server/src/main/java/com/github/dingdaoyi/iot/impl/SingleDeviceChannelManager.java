package com.github.dingdaoyi.iot.impl;

import com.github.dingdaoyi.iot.DeviceChannelManager;
import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.model.ExceptionType;
import com.github.dingdaoyi.proto.model.ProtocolException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 非分区的设备通道管理实现
 */
public class SingleDeviceChannelManager implements DeviceChannelManager {

    private final ConcurrentHashMap<String, DeviceConnection> connections = new ConcurrentHashMap<>();

    @Override
    public void addConnection(String deviceKey, DeviceConnection connection) {
        connections.put(deviceKey, connection);
    }

    @Override
    public DeviceConnection getConnection(String deviceKey) {
        return connections.get(deviceKey);
    }

    @Override
    public void removeConnection(String deviceKey) {
        DeviceConnection connection = connections.remove(deviceKey);
        if (connection != null) {
            connection.disconnect();
        }
    }

    @Override
    public void close() {
        connections.forEach((deviceKey, connection) -> {
            if (!connection.isConnected()) {
                connections.remove(deviceKey);
            }
        });
    }

    @Override
    public void sendMessage(String deviceKey, Map<String, Object> metadata, byte[] message) throws ProtocolException {
        DeviceConnection connection = connections.get(deviceKey);
        if (connection == null || !connection.isConnected()) {
            throw new ProtocolException(deviceKey, ExceptionType.DEVICE_NOT_CONNECTED);
        }
        connection.sendMessage(metadata, message);
    }
}