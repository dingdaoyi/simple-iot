package com.github.dingdaoyi.proto.model;

import lombok.Getter;

/**
 * @author dingyunwei
 */
@Getter
public class ProtocolException extends Exception {
    private final String deviceKey;
    private final ExceptionType type;
    private final Integer messageId;
    private final String detailMessage;

    public ProtocolException(String deviceKey, ExceptionType type, Integer messageId) {
        super(type.msg);
        this.deviceKey = deviceKey;
        this.type = type;
        this.messageId = messageId;
        this.detailMessage = null;
    }

    public ProtocolException(String deviceKey, ExceptionType type, Integer messageId, String message) {
        super(message);
        this.deviceKey = deviceKey;
        this.type = type;
        this.messageId = messageId;
        this.detailMessage = message;
    }
    public ProtocolException(String deviceKey, ExceptionType type) {
        this(deviceKey, type, null);
    }

    @Override
    public String getMessage() {
        if (detailMessage != null && !detailMessage.isBlank()) {
            return detailMessage;
        }
        if (messageId != null) {
            return type.msg + deviceKey + ":" + messageId;
        }
        return type.msg + ":" + deviceKey;
    }
}
