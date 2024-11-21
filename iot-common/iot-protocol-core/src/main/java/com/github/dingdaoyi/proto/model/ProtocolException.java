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

    public ProtocolException(String deviceKey, ExceptionType type, Integer messageId) {
        super(type.msg);
        this.deviceKey = deviceKey;
        this.type = type;
        this.messageId = messageId;
    }
    public ProtocolException(String deviceKey, ExceptionType type) {
        this(deviceKey, type, null);
    }
}
