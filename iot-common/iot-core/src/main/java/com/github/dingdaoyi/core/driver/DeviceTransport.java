package com.github.dingdaoyi.core.driver;

/**
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
} 