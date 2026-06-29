package com.github.dingdaoyi.demo;

/**
 * Stable identifiers used by the demo telemetry seed, simulator and smoke tests.
 */
public final class DemoTelemetryConstants {

    private DemoTelemetryConstants() {
    }

    public static final String PROTO_KEY = "demo-json-telemetry";
    public static final String PRODUCT_KEY = "demo-smart-sensor";
    public static final String DEVICE_KEY = "demo-sensor-001";
    public static final String DEVICE_SECRET = "demo-secret";
    public static final String PRODUCT_TYPE_CODE = "demo-sensor";
    public static final String HIGH_TEMP_EVENT_IDENTIFIER = "high_temperature";
    public static final String HIGH_TEMP_ALARM_TYPE = "demo_high_temperature";
    public static final double HIGH_TEMP_THRESHOLD = 60.0D;
}
