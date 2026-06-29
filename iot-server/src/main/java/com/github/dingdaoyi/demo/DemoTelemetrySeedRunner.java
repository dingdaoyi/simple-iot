package com.github.dingdaoyi.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Optional startup hook for local/demo environments.
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "simple.iot.demo", name = "seed-enabled", havingValue = "true")
public class DemoTelemetrySeedRunner implements ApplicationRunner {

    private final DemoTelemetrySeeder demoTelemetrySeeder;

    @Override
    public void run(ApplicationArguments args) {
        demoTelemetrySeeder.seed();
        log.info("Demo telemetry seed is ready: productKey={}, deviceKey={}, protoKey={}",
                DemoTelemetryConstants.PRODUCT_KEY,
                DemoTelemetryConstants.DEVICE_KEY,
                DemoTelemetryConstants.PROTO_KEY);
    }
}
