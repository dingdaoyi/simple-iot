package com.github.dingdaoyi.driver.http;

import com.github.dingdaoyi.core.driver.DeviceTransport;
import org.springframework.stereotype.Component;

/**
 * @author dingyunwei
 */
@Component
public class HttpDeviceTransport implements DeviceTransport {


    private volatile boolean running = false;

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public String getType() {
        return "HTTP";
    }

    @Override
    public String getName() {
        return "HTTP统一驱动";
    }
} 