package com.github.dingdaoyi.model.query;

import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.iot.influx.TimeUtils;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.proto.model.tsl.EventTypeEnum;
import com.influxdb.v3.client.PointValues;
import lombok.*;
import org.tio.utils.json.JsonUtil;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dingyunwei
 */
@Getter
@Setter
public class DeviceEventDataVo extends DeviceEventData {

    /**
     * 原始数据
     */
    private String rowValue;

    /**
     * 事件
     */
    private LocalDateTime time;

    public DeviceEventDataVo(String identifier, EventTypeEnum eventType, String rowValue) {
        super(identifier, eventType);
        this.rowValue = rowValue;
    }
    public static DeviceEventDataVo fromPointValues(PointValues pointValues) {
        String identifier = pointValues.getTag("identifier");
        String eventType = pointValues.getTag("eventType");
        String rowValue = pointValues.getStringField("rowData");
        DeviceEventDataVo eventDataVo = new DeviceEventDataVo(identifier, EventTypeEnum.of(Integer.parseInt(eventType)), rowValue);
        String value = pointValues.getStringField("value");
        List<DeviceData> deviceData = JSONUtil.toList(value, DeviceData.class);
        eventDataVo.setParams(deviceData);
        eventDataVo.setTime(TimeUtils.toLocalDateTime(pointValues.getTimestamp()));
        return eventDataVo;
    }
}
