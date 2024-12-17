package com.github.dingdaoyi.iot;


import com.github.dingdaoyi.proto.model.DecodeResult;

/**
 * 数据处理器
 * @author dingyunwei
 */
public interface DataProcessor {

    void process(DecodeResult message, String deviceKey);
}
