package com.github.dingdaoyi.iot.proto.defaul.model;

import lombok.Data;

/**
 * @author dingyunwei
 */
@Data
public class MqttHeader {

    /**
     * 消息id
     */
    private Integer msgId;

    /**
     * 标识符
     */
    private String identifier;
}
