package com.github.dingdaoyi.iot.proto.defaul.model;

import com.github.dingdaoyi.proto.model.ProtocolException;
import lombok.Data;

@Data
public class MqttErrors {
    private int code;
    private String msg;
    public MqttErrors(ProtocolException exception) {
        this.code = exception.getType().code;
        this.msg = exception.getMessage();
    }
}
