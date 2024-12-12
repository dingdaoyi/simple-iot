package com.github.dingdaoyi.proto.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dingyunwei
 */
@Data
public class DeviceServiceResData {

    /**
     * 标识符
     */
    private String identifier;

    /**
     * 请求参数
     */
    private List<DeviceData> resultData = new ArrayList<>();

    public DeviceServiceResData(String identifier) {
        this.identifier = identifier;
    }

    public Map<String, Object> toMap() {
        return resultData.stream()
                .collect(Collectors.toMap(DeviceData::identifier, DeviceData::value));
    }
}
