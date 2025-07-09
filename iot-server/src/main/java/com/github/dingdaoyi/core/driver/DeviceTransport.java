package com.github.dingdaoyi.core.driver;

/**
 * 设备驱动传输接口，统一驱动生命周期管理
 * @author dingyunwei
 */
public interface DeviceTransport {
    /**
     * 启动驱动
     */
    void start();

    /**
     * 停止驱动
     */
    void stop();

    /**
     * 重载驱动（如配置变更时）
     */
    default void reload() {
        stop();
        start();
    }

    /**
     * 是否运行中
     */
    boolean isRunning();

    /**
     * 获取驱动类型
     */
    String getType();

    /**
     * 获取驱动名称
     */
    String getName();

    /**
     * 动态注册新驱动（可选）
     */
    default void addDriver(Object driverConfig) {}

    /**
     * 动态注销驱动（可选）
     */
    default void removeDriver(String driverName) {}
} 