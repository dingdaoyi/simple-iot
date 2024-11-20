package com.github.dingdaoyi.proto.model;

import lombok.Getter;

@Getter
public class ProtocolException extends Exception {
    private final String deviceId;

    public ProtocolException(String deviceId, String message) {
        super(message);
        this.deviceId = deviceId;
    }
}
