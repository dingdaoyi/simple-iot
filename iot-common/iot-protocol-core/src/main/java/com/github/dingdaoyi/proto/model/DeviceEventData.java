package com.github.dingdaoyi.proto.model;

import com.github.dingdaoyi.proto.model.tsl.EventTypeEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dingyunwei
 */
@Data
public class DeviceEventData {

    /**
     * 标识符
     */
    private String identifier;

    /**
     * 事件类型
     */
    private EventTypeEnum eventType;

    /**
     * 请求参数
     */
    private List<DeviceData> params=new ArrayList<>();

    public DeviceEventData(String identifier, EventTypeEnum eventType) {
        this.identifier = identifier;
        this.eventType = eventType;
    }
}
