package com.github.dingdaoyi.iot.impl;

import com.github.dingdaoyi.iot.DataProcessingService;
import com.github.dingdaoyi.iot.IoTDataProcessor;
import com.github.dingdaoyi.iot.proto.ProtocolFactory;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.ProtocolException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * @author dingyunwei
 */
@Service
@Slf4j
public class IoTDataProcessorImpl implements IoTDataProcessor {
    @Resource
    private ExecutorService executorService;
    @Resource
    private List<DataProcessingService> dataProcessingServices;

    @Override
    public void messageUp(DeviceRequest request) {
        Optional<ProtocolDecoder> optional = ProtocolFactory.getDecoder(request.getProtoKey());
        if (optional.isEmpty()) {
            log.warn("协议解析器不存在,请检查产品信息:{}", request.getProtoKey());
            return;
        }
        ProtocolDecoder decoder = optional.get();
        try {
            DecodeResult result = decoder.decode(request);
            for (DataProcessingService processingService : dataProcessingServices) {
                executorService.submit(() -> processingService.process(result));
            }
        } catch (ProtocolException e) {
            log.error("协议解析失败,原因:{}", e.getMessage());
        }
    }
}
