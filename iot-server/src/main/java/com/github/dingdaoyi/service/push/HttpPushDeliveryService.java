package com.github.dingdaoyi.service.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.entity.PushLog;
import com.github.dingdaoyi.mapper.PushLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.util.Map;

/**
 * Sends HTTP webhook pushes with optional Simple IoT HMAC signature headers.
 */
@Slf4j
@Service
public class HttpPushDeliveryService {

    public static final String HEADER_TIMESTAMP = "X-Simple-IoT-Timestamp";
    public static final String HEADER_SIGNATURE = "X-Simple-IoT-Signature";
    public static final String HEADER_SIGNATURE_METHOD = "X-Simple-IoT-Signature-Method";
    public static final String HEADER_EVENT = "X-Simple-IoT-Event";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PushWebhookSigner signer;
    private final PushEndpointGuard endpointGuard;
    private final PushLogMapper pushLogMapper;
    private final Clock clock;

    @Autowired
    public HttpPushDeliveryService(RestTemplate restTemplate,
                                   ObjectMapper objectMapper,
                                   PushWebhookSigner signer,
                                   PushEndpointGuard endpointGuard,
                                   PushLogMapper pushLogMapper) {
        this(restTemplate, objectMapper, signer, endpointGuard, pushLogMapper, Clock.systemUTC());
    }

    HttpPushDeliveryService(RestTemplate restTemplate,
                            ObjectMapper objectMapper,
                            PushWebhookSigner signer,
                            PushEndpointGuard endpointGuard,
                            PushLogMapper pushLogMapper,
                            Clock clock) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.signer = signer;
        this.endpointGuard = endpointGuard;
        this.pushLogMapper = pushLogMapper;
        this.clock = clock;
    }

    public PushDeliveryResult deliverHttp(PushConfig config, Object payload, String eventType) {
        String body = stringifyPayload(payload);
        return deliverHttp(config, body, eventType);
    }

    public PushDeliveryResult deliverHttp(PushConfig config, String body, String eventType) {
        String method = config.getHttpMethod() != null ? config.getHttpMethod() : "POST";
        String url = config.getHttpUrl();
        long start = System.currentTimeMillis();
        try {
            endpointGuard.validate(url);
            HttpHeaders headers = buildHeaders(config, body, eventType);
            HttpEntity<String> entity = new HttpEntity<>(body == null ? "" : body, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.valueOf(method.toUpperCase()), entity, String.class);
            String message = String.format("HTTP推送成功: %s %s -> %s", method.toUpperCase(), url, response.getStatusCode());
            PushDeliveryResult result = PushDeliveryResult.success(response.getStatusCode().value(), message, response.getBody());
            saveLog(config, eventType, result, start);
            return result;
        } catch (Exception e) {
            log.error("HTTP推送失败: {} {}", method, url, e);
            PushDeliveryResult result = PushDeliveryResult.failure("HTTP推送失败: " + e.getMessage());
            saveLog(config, eventType, result, start);
            return result;
        }
    }

    // ponytail: one-liner log save, fire-and-forget via try-catch so logging never fails the push
    private void saveLog(PushConfig config, String eventType, PushDeliveryResult result, long start) {
        try {
            PushLog logEntry = new PushLog();
            logEntry.setPushConfigId(config.getId());
            logEntry.setPushConfigName(config.getName());
            logEntry.setEventType(eventType);
            logEntry.setSuccess(result.isSuccess());
            logEntry.setStatusCode(result.getStatusCode());
            logEntry.setMessage(result.getMessage());
            logEntry.setResponseBody(result.getResponseBody() != null && result.getResponseBody().length() > 2000
                    ? result.getResponseBody().substring(0, 2000) : result.getResponseBody());
            logEntry.setDurationMs(System.currentTimeMillis() - start);
            pushLogMapper.insert(logEntry);
        } catch (Exception e) {
            log.warn("推送日志保存失败", e);
        }
    }

    private HttpHeaders buildHeaders(PushConfig config, String body, String eventType) {
        HttpHeaders headers = new HttpHeaders();
        if (config.getHttpHeaders() != null) {
            config.getHttpHeaders().forEach(kv -> {
                if (kv.getKey() != null && !kv.getKey().isBlank()) {
                    headers.set(kv.getKey(), kv.getValue() == null ? "" : kv.getValue());
                }
            });
        }
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        if (eventType != null && !eventType.isBlank()) {
            headers.set(HEADER_EVENT, eventType);
        }
        if (Boolean.TRUE.equals(config.getHttpSignEnabled()) && config.getHttpSignSecret() != null && !config.getHttpSignSecret().isBlank()) {
            String timestamp = String.valueOf(clock.millis());
            headers.set(HEADER_TIMESTAMP, timestamp);
            headers.set(HEADER_SIGNATURE, signer.sign(config.getHttpSignSecret(), timestamp, body));
            headers.set(HEADER_SIGNATURE_METHOD, "HMAC-SHA256");
        }
        return headers;
    }

    private String stringifyPayload(Object payload) {
        if (payload == null) {
            return "";
        }
        if (payload instanceof String text) {
            return text;
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            if (payload instanceof Map<?, ?>) {
                return "{}";
            }
            return String.valueOf(payload);
        }
    }
}
