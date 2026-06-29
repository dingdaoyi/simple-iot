package com.github.dingdaoyi.demo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Small CLI helper that prints a ready-to-publish demo telemetry payload.
 */
public final class DemoTelemetrySimulator {

    private static final String DEFAULT_TOPIC_PREFIX = "simpleiot";
    private static final String DEFAULT_MQTT_CLIENT_ID_PREFIX = "simple_";
    private static final String PROPERTY_TOPIC_SEGMENT = "pro";
    private static final String EVENT_TOPIC_SEGMENT = "ev";

    private DemoTelemetrySimulator() {
    }

    public static void main(String[] args) {
        Payload payload = payload(parseArgs(args));
        System.out.println("Demo telemetry payload");
        System.out.println("productKey=" + payload.productKey());
        System.out.println("deviceKey=" + payload.deviceKey());
        System.out.println("protoKey=" + payload.protoKey());
        System.out.println("mqttHost=localhost");
        System.out.println("mqttPort=1883");
        System.out.println("clientId=" + payload.clientId());
        System.out.println("username=" + payload.username());
        System.out.println("password=" + payload.password());
        System.out.println("topic=" + payload.topic());
        System.out.println(payload.json());
    }

    static Payload defaultPayload() {
        return payload(Map.of());
    }

    static Payload payload(Map<String, String> overrides) {
        String productKey = overrides.getOrDefault("productKey", DemoTelemetryConstants.PRODUCT_KEY);
        String deviceKey = overrides.getOrDefault("deviceKey", DemoTelemetryConstants.DEVICE_KEY);
        String protoKey = overrides.getOrDefault("protoKey", DemoTelemetryConstants.PROTO_KEY);
        double temperature = Double.parseDouble(overrides.getOrDefault("temperature", "72.5"));
        long humidity = Long.parseLong(overrides.getOrDefault("humidity", "43"));
        double voltage = Double.parseDouble(overrides.getOrDefault("voltage", "220.8"));
        boolean online = Boolean.parseBoolean(overrides.getOrDefault("online", "true"));
        String mode = overrides.getOrDefault("mode", "auto");
        String password = overrides.getOrDefault("password", DemoTelemetryConstants.DEVICE_SECRET);
        String topicPrefix = overrides.getOrDefault("topicPrefix", DEFAULT_TOPIC_PREFIX);
        String clientIdPrefix = overrides.getOrDefault("clientIdPrefix", DEFAULT_MQTT_CLIENT_ID_PREFIX);
        boolean eventMessage = Boolean.parseBoolean(overrides.getOrDefault("event", "false"));

        StringJoiner jsonJoiner = new StringJoiner(",", "{", "}");
        if (eventMessage) {
            jsonJoiner.add("\"eventIdentifier\":\""
                    + escapeJson(overrides.getOrDefault("eventIdentifier", DemoTelemetryConstants.HIGH_TEMP_EVENT_IDENTIFIER))
                    + "\"");
            jsonJoiner.add("\"eventType\":\"" + escapeJson(overrides.getOrDefault("eventType", "WARN")) + "\"");
        }
        String json = jsonJoiner
                .add("\"temperature\":" + temperature)
                .add("\"humidity\":" + humidity)
                .add("\"voltage\":" + voltage)
                .add("\"online\":" + online)
                .add("\"mode\":\"" + escapeJson(mode) + "\"")
                .toString();
        String topic = joinTopic(topicPrefix, eventMessage ? EVENT_TOPIC_SEGMENT : PROPERTY_TOPIC_SEGMENT, productKey);
        String clientId = clientIdPrefix + deviceKey;
        String username = deviceKey;
        return new Payload(productKey, deviceKey, protoKey, topic, clientId, username, password, json);
    }

    private static String joinTopic(String topicPrefix, String messageType, String productKey) {
        if (topicPrefix == null || topicPrefix.isBlank()) {
            return messageType + "/" + productKey;
        }
        return topicPrefix.replaceAll("/+$", "") + "/" + messageType + "/" + productKey;
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String arg : args) {
            int split = arg.indexOf('=');
            if (split > 0) {
                values.put(arg.substring(0, split), arg.substring(split + 1));
            }
        }
        return values;
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    record Payload(String productKey, String deviceKey, String protoKey, String topic, String clientId, String username,
                   String password, String json) {
    }
}
