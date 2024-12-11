package com.github.dingdaoyi.proto.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dingyunwei
 */
@Data
public class DecodeResult {

    /**
     * 属性数据
     */
    private List<DeviceData> dataList = new ArrayList<>();

    /**
     * 事件类型
     */
    private List<DeviceEventData> eventList = new ArrayList<>();

    /**
     * 事件数据
     */
    private List<DataDecodeError> errors=new ArrayList<>();

    /**
     * 解析的数据
     */
    private ProtoMessage protoMessage;

    /**
     * 原始数据
     */
    private String rowData;

}
