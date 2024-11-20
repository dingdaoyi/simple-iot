package com.github.dingdaoyi.iot.impl;

import com.github.dingdaoyi.iot.DataProcessingService;
import com.github.dingdaoyi.proto.model.DecodeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author dingyunwei
 */
@Slf4j
@Service
public class DataProcessingServiceImpl implements DataProcessingService {
    @Override
    public void process(DecodeResult message) {
        log.info("process message: {}", message);
    }
}
