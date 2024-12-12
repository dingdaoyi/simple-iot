package com.github.dingdaoyi.iot.proto.defaul.model;

import com.github.dingdaoyi.utils.MessageIdGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingyunwei
 */
@Data
@NoArgsConstructor
public class MqttHeader {

    /**
     * 消息id
     */
    private Integer msgId;

    /**
     * 标识符
     */
    private String identifier;

    public MqttHeader(String identifier) {
        this.msgId = MessageIdGenerator.nextId();
        this.identifier = identifier;
    }
}
