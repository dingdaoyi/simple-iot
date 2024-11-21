package com.github.dingdaoyi.proto.model;

/**
 * 协议解析完的原始数据
 */
public interface ProtoMessage {

    /**
     * 消息id
     * @return
     */
    int getMessageId();

    /**
     * 获取标识符
     * @return
     */
    String getIdentifier();
}
