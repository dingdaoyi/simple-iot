package com.github.dingdaoyi.iot.proto.defaul.strategy;

import cn.hutool.core.lang.TypeReference;
import com.github.dingdaoyi.iot.proto.defaul.model.MqttMessage;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import com.github.dingdaoyi.proto.model.ProtocolException;
import com.github.dingdaoyi.proto.model.tsl.TslModel;

/**
 * 消息解码策略接口
 * @param <T> 消息体类型
 * @author dingyunwei
 */
public interface MessageDecodeStrategy<T> {

    /**
     * 支持的消息类型
     */
    ProtoMessageType messageType();

    /**
     * JSON 类型引用
     */
    TypeReference<MqttMessage<T>> typeReference();

    /**
     * 解码消息
     * @param message MQTT 消息
     * @param tslModel 物模型
     * @param deviceKey 设备Key
     * @return 解码结果
     */
    DecodeResult decode(MqttMessage<T> message, TslModel tslModel, String deviceKey) throws ProtocolException;
}
