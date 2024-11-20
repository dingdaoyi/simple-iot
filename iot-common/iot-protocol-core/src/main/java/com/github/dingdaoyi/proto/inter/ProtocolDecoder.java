package com.github.dingdaoyi.proto.inter;

import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.ProtocolException;

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
    DecodeResult decode(DeviceRequest request) throws ProtocolException;
}
