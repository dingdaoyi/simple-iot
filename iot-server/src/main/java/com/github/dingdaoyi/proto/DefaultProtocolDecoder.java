package com.github.dingdaoyi.proto;

import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.ProtocolException;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.TslModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author dingyunwei
 */
@Service
@Slf4j
public class DefaultProtocolDecoder implements ProtocolDecoder {
    @Resource
    private DeviceService deviceService;
    @Resource
    private TslModelService tslModelService;
    @Override
    public String protocolKey() {
        return "system-default";
    }

    @Override
    public DecodeResult decode(DeviceRequest request) throws ProtocolException {
        byte[] data = request.getData();
        String dataStr = new String(data, StandardCharsets.UTF_8);
        log.info("收到解析数据:{}",dataStr);
        return new DecodeResult();
    }
}
