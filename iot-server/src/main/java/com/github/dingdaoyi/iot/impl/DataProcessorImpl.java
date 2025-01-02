package com.github.dingdaoyi.iot.impl;

import com.github.dingdaoyi.iot.DataProcessor;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author dingyunwei
 */
@Slf4j
@Service
public class DataProcessorImpl implements DataProcessor {
    @Override
    public void process(DecodeResult message, String deviceKey, TslModel tslModel) {
        log.info("process message: {}", message);
    }
}
