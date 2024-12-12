package com.github.dingdaoyi.iot.impl;

import com.github.dingdaoyi.iot.DeviceChannelManager;
import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.model.ExceptionType;
import com.github.dingdaoyi.proto.model.ProtocolException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于分区的设备通道管理实现
 * @author dingyunwei
 */
public class PartitionedDeviceChannelManager implements DeviceChannelManager {

    private static final int PARTITION_COUNT = 16;
    private final ConcurrentHashMap<String, DeviceConnection>[] partitions;

    public PartitionedDeviceChannelManager() {
        partitions = new ConcurrentHashMap[PARTITION_COUNT];
        for (int i = 0; i < PARTITION_COUNT; i++) {
            partitions[i] = new ConcurrentHashMap<>();
        }
    }

    private int getPartitionIndex(String deviceKey) {
        return Math.abs(deviceKey.hashCode() % PARTITION_COUNT);
    }

    @Override
    public void addConnection(String deviceKey, DeviceConnection connection) {
        int partitionIndex = getPartitionIndex(deviceKey);
        partitions[partitionIndex].put(deviceKey, connection);
    }

    @Override
    public DeviceConnection getConnection(String deviceKey) {
        int partitionIndex = getPartitionIndex(deviceKey);
        return partitions[partitionIndex].get(deviceKey);
    }

    @Override
    public void removeConnection(String deviceKey) {
        int partitionIndex = getPartitionIndex(deviceKey);
        DeviceConnection connection = partitions[partitionIndex].remove(deviceKey);
        if (connection != null) {
            connection.disconnect();
        }
    }

    @Override
    public void close() {
        for (ConcurrentHashMap<String, DeviceConnection> partition : partitions) {
            partition.forEach((deviceKey, connection) -> {
                if (!connection.isConnected()) {
                    partition.remove(deviceKey);
                }
            });
        }
    }

    @Override
    public void sendMessage(String deviceKey, Map<String, Object> metadata, byte[] message) throws ProtocolException {
        int partitionIndex = getPartitionIndex(deviceKey);
        DeviceConnection connection = partitions[partitionIndex].get(deviceKey);
        if (connection == null || !connection.isConnected()) {
            throw new ProtocolException(deviceKey, ExceptionType.DEVICE_NOT_CONNECTED);
        }
        connection.sendMessage(metadata, message);
    }
}