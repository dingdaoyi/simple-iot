package com.github.dingdaoyi.service.push;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of a push delivery attempt.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushDeliveryResult {

    private boolean success;
    private Integer statusCode;
    private String message;
    private String responseBody;

    public static PushDeliveryResult success(Integer statusCode, String message, String responseBody) {
        return new PushDeliveryResult(true, statusCode, message, responseBody);
    }

    public static PushDeliveryResult failure(String message) {
        return new PushDeliveryResult(false, null, message, null);
    }
}
