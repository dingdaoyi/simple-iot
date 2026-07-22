package com.github.dingdaoyi.demo;

import com.github.dingdaoyi.iot.IotDataProcessor;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Periodically publishes simulated demo telemetry through the full data pipeline
 * (protocol decode -> rule engine -> InfluxDB -> alarm), so the demo dashboard
 * has live, updating data immediately after deployment.
 *
 * <p>Enabled only when both the demo seed is active and the publisher is not
 * explicitly disabled. Controlled by {@code simple.iot.demo.publisher-enabled}
 * and {@code simple.iot.demo.publisher-interval-ms}.</p>
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "simple.iot.demo", name = "publisher-enabled", havingValue = "true")
public class DemoTelemetryPublisher {

    @Resource
    private IotDataProcessor dataProcessor;

    /**
     * Counter to rotate data patterns - every 10th message sends a high-temperature
     * event to demonstrate alarm generation.
     */
    private final AtomicInteger tickCount = new AtomicInteger(0);

    /**
     * Default 15-second interval. Override with
     * {@code simple.iot.demo.publisher-interval-ms}.
     */
    @Scheduled(fixedRateString = "${simple.iot.demo.publisher-interval-ms:15000}")
    public void publishTelemetry() {
        try {
            int tick = tickCount.getAndIncrement();
            boolean sendEvent = tick % 10 == 9;

            String json = buildPayload(sendEvent);
            DeviceRequest request = new DeviceRequest();
            request.setDeviceKey(DemoTelemetryConstants.DEVICE_KEY);
            request.setProductKey(DemoTelemetryConstants.PRODUCT_KEY);
            request.setProtoKey(DemoTelemetryConstants.PROTO_KEY);
            request.setMessageType(sendEvent ? ProtoMessageType.EVENT : ProtoMessageType.PROPERTY);
            request.setData(json.getBytes(StandardCharsets.UTF_8));

            dataProcessor.messageUp(request);

            if (tick % 4 == 0) {
                dataProcessor.oline(DemoTelemetryConstants.DEVICE_KEY);
            }

            log.debug("Demo telemetry published: tick={}, event={}, payload={}", tick, sendEvent, json);
        } catch (Exception e) {
            log.warn("Demo telemetry publish failed (non-fatal): {}", e.getMessage());
        }
    }

    /**
     * Builds a realistic telemetry payload with slight random variation.
     * Every ~10th tick sends temperature above 60°C to trigger the alarm rule.
     */
    private String buildPayload(boolean sendEvent) {
        double baseTemp = 25.0 + ThreadLocalRandom.current().nextDouble(-3.0, 3.0);
        double temperature = sendEvent ? 65.0 + ThreadLocalRandom.current().nextDouble(0, 5.0) : baseTemp;
        long humidity = 40 + ThreadLocalRandom.current().nextLong(0, 20);
        double voltage = 220.0 + ThreadLocalRandom.current().nextDouble(-5.0, 5.0);
        boolean online = true;
        String mode = ThreadLocalRandom.current().nextBoolean() ? "auto" : "manual";

        StringBuilder sb = new StringBuilder("{");
        if (sendEvent) {
            sb.append("\"eventIdentifier\":\"")
              .append(DemoTelemetryConstants.HIGH_TEMP_EVENT_IDENTIFIER)
              .append("\",\"eventType\":\"WARN\",");
        }
        sb.append("\"temperature\":").append(temperature)
          .append(",\"humidity\":").append(humidity)
          .append(",\"voltage\":").append(voltage)
          .append(",\"online\":").append(online)
          .append(",\"mode\":\"").append(mode).append("\"")
          .append("}");
        return sb.toString();
    }
}
