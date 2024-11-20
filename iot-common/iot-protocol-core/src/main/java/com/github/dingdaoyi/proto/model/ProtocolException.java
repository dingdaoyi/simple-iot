package com.github.dingdaoyi.proto.model;

import lombok.Getter;

@Getter
public class ProtocolException extends Exception {
    private final String deviceKey;

    public ProtocolException(String deviceKey, String message) {
        super(message);
        this.deviceKey = deviceKey;
    }
}
