package com.github.dingdaoyi.iot.proto.defaul.model;

import lombok.Data;

/**
 * @author dingyunwei
 */
@Data
public class MqttPopMessage implements MqttMessage{
    /**
     * 标识符
     */
    private String identifier;

    /**
     * 参数值
     */
    private Object value;

}
