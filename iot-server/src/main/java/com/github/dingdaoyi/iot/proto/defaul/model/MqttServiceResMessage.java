package com.github.dingdaoyi.iot.proto.defaul.model;

import lombok.Data;

import java.util.Map;

/**
 * 服务响应消息体
 * @author dingyunwei
 */
@Data
public class MqttServiceResMessage {
    /**
     * 参数值
     * key 标识符,
     * value 值
     */
    private Map<String,Object> data;
}
