package com.github.dingdaoyi.proto.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dingyunwei
 */
@Data
public class DecodeResult {

    /**
     * 消息id, 如果是指令响应时, 判断
     */
    private Integer messageId;
    /**
     * 属性数据
     */
    private List<DeviceData> dataList = new ArrayList<>();

    /**
     * 事件类型
     */
    private DeviceEventData eventData;

    /**
     * 事件响应
     */
    private DeviceServiceResData serviceResData;
    /**
     * 原始数据
     */
    private String rowData;

}
