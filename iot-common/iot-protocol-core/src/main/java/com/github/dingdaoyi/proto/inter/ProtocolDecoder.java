package com.github.dingdaoyi.proto.inter;

import com.github.dingdaoyi.proto.model.*;
import com.github.dingdaoyi.proto.model.tsl.TslModel;

/**
 * @author dingyunwei
 */
public interface ProtocolDecoder {

    /**
     * 获取协议的key值,协议唯一标识
     * @return
     */
    String protocolKey();
    /**
     * 协议解析
     * @param request
     * @return
     */
    DecodeResult decode(DeviceRequest request, TslModel tslModel) throws ProtocolException;


    /**
     * 协议解析
     * @param message 消息
     */
    EncoderResult encode(EncoderMessage message, TslModel tslModel) throws ProtocolException;


    /**
     * 回复错误信息
     * @param connection
     * @param e
     */
    void responseError(DeviceConnection connection, ProtocolException e);
}
