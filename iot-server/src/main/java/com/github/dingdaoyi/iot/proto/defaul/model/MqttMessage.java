package com.github.dingdaoyi.iot.proto.defaul.model;

import lombok.Data;

/**
 * mqtt 消息
 * @author dingyunwei
 */
@Data
public class MqttMessage<T> {

    /**
     * 头信息
     */
    private MqttHeader header;

    /**
     * 消息体
     */
    private T body;

    public Integer messageId() {
        if (header == null) {
            return 0;
        }
        return header.getMsgId();
    }

    /**
     * 获取标识符
     * @return
     */
    public String identifier() {
        if (header == null) {
            return "";
        }
        return header.getIdentifier();
    }
}
