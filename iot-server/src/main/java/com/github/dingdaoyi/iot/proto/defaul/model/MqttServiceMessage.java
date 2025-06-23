package com.github.dingdaoyi.iot.proto.defaul.model;

import lombok.Data;

import java.util.Map;

/**
 * 指令下发参数
 * @author dingyunwei
 */
@Data
public  class MqttServiceMessage {
    private Map<String, Object> data;

    public MqttServiceMessage( Map<String, Object> data) {
        this.data = data;
    }
}
