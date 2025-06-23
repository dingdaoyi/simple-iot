package com.github.dingdaoyi.iot.influx;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.config.base.IotConfigProperties;
import com.github.dingdaoyi.iot.DataProcessor;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.model.query.DeviceDataQuery;
import com.github.dingdaoyi.model.query.DeviceEventDataVo;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.proto.model.KeyValue;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import com.github.dingdaoyi.service.DeviceDataService;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.TslModelService;
import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import com.influxdb.v3.client.PointValues;
import com.influxdb.v3.client.query.QueryOptions;
import com.influxdb.v3.client.query.QueryType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author dingyunwei
 */
@Service
@Slf4j
public class InfluxDataProcessor implements DataProcessor, DeviceDataService {

    @Resource
    private InfluxDBClient influxDBClient;

    private IotConfigProperties.InfluxDbProperties properties;

    @Resource
    private TslModelService tslModelService;

    @Resource
    private DeviceService deviceService;

    @Resource
    public void setProperties(IotConfigProperties properties) {
        this.properties = properties.getInfluxdb();
    }

    @Override
    public void process(DecodeResult message, String deviceKey, TslModel tslModel) {
        if (CollectionUtil.isNotEmpty(message.getDataList())) {
            this.saveProperties(message.getDataList(), deviceKey);
        }
        if (message.getEventData() != null) {
            this.saveEvent(message.getEventData(), message.getRowData(), deviceKey);
        }
    }

    private void saveEvent(DeviceEventData eventData, String rowData, String deviceKey) {
        Point point = Point.measurement(properties.getEventDatabase())
                .setTag("deviceKey", deviceKey)
                .setTag("identifier", eventData.getIdentifier())
                .setTag("eventType", eventData.getEventType().getValue() + "")
                .setField("value", JSONUtil.toJsonStr(eventData.getParams()))
                .setField("rowData", rowData)
                .setTimestamp(Instant.now());
        log.info("保存事件信息;{}|{}", deviceKey, JSONUtil.toJsonStr(eventData));
        influxDBClient.writePoint(point);
    }

    /**
     * 保存属性
     *
     * @param dataList  属性列表
     * @param deviceKey 设备编号
     */
    //TODO 当前未区分产品和产品类型,后面看怎么处理
    private void saveProperties(List<DeviceData> dataList, String deviceKey) {
        List<Point> points = new ArrayList<>();
        for (DeviceData deviceData : dataList) {
            Point point = Point.measurement(properties.getPropDatabase() + "_" + deviceData.getIdentifier())
                    .setTag("deviceKey", deviceKey)
                    .setTag("dataType", deviceData.getDataType().name())
                    .setTimestamp(Instant.now());
            switch (deviceData.getDataType()) {
                case DATE, STRUCT, ENUM, TEXT -> point.setField("value", deviceData.getValue().toString());
                case INT -> point.setField("value", (int) deviceData.getValue());
                case FLOAT -> point.setField("value", (float) deviceData.getValue());
                case DOUBLE -> point.setField("value", (double) deviceData.getValue());
                case BOOL -> point.setField("value", (boolean) deviceData.getValue());
            }
            points.add(point);
        }
        log.info("保存属性信息;{}|{}", deviceKey, JSONUtil.toJsonStr(dataList));
        influxDBClient.writePoints(points);
    }

    @Override
    public List<KeyValue<String, Object>> getLatestData(String deviceKey) {
        Optional<DeviceDTO> dtoOptional = deviceService.getByDeviceKey(deviceKey);
        if (dtoOptional.isEmpty()) {
            return new ArrayList<>();
        }
        DeviceDTO deviceDTO = dtoOptional.get();

        Optional<TslModel> optional = tslModelService.findByProductKey(deviceDTO.getProductKey());
        if (optional.isEmpty()) {
            return new ArrayList<>();
        }
        TslModel tslModel = optional.get();
        List<TslProperty> propertyList = tslModel.getProperties().stream()
                .filter(TslProperty::isProperty).toList();
        List<KeyValue<String, Object>> dataList = new ArrayList<>();
        QueryOptions queryOptions = new QueryOptions(properties.getDatabase(), QueryType.SQL);
        for (TslProperty property : propertyList) {
            String sqlParams = "select value from " + properties.getPropDatabase() + "_" + property.getIdentifier() + " where \"deviceKey\"=$deviceKey order by time desc limit 1";
            try (Stream<PointValues> stream = influxDBClient.queryPoints(sqlParams, Map.of("deviceKey", deviceKey), queryOptions)) {
                stream.forEach(row -> dataList.add(new KeyValue<>(property.getIdentifier(), row.getField("value"))));
            } catch (Exception e) {
                log.debug("查询出错:{}", e.getMessage());
            }
        }
        return dataList;
    }

    /**
     * 查询条件可能有点问题
     *
     * @param query
     * @return
     */
    @Override
    public List<KeyValue<String, Object>> metric(DeviceDataQuery query) {

        List<KeyValue<String, Object>> dataList = new ArrayList<>();
        QueryOptions queryOptions = new QueryOptions(properties.getDatabase(), QueryType.SQL);
        String sqlParams = "select value,time from " + properties.getPropDatabase() + "_" + query.getIdentifier() + " where \"deviceKey\"=$deviceKey" +
                           " and time> $beginTime and time<= $endTime order by time asc";
        try (Stream<PointValues> stream = influxDBClient.queryPoints(sqlParams, Map.of("deviceKey", query.getDeviceKey(),
                "beginTime", DateUtil.formatLocalDateTime(query.getBeginTime()), "endTime", DateUtil.formatLocalDateTime(query.getEndTime())), queryOptions)) {

            stream.forEach(row -> dataList.add(new KeyValue<>(TimeUtils.toDateTimeStr(row.getTimestamp()), row.getField("value"))));
        } catch (Exception e) {
            log.debug("查询出错:{}", e.getMessage());
        }
        return dataList;
    }

    @Override
    public List<DeviceEventDataVo> eventLogs(DeviceDataQuery query) {
        QueryOptions queryOptions = new QueryOptions(properties.getDatabase(), QueryType.SQL);
        //TODO 需要解决分页等问题
        String sqlParams = "select * from " + properties.getEventDatabase() + " where \"deviceKey\"=$deviceKey" +
                           (StringUtils.isNotBlank(query.getIdentifier()) ? " and \"identifier\"=$identifier" : "") +
                           " and time> $beginTime and time<= $endTime order by time desc limit 100";
        Map<String, Object> params = new HashMap<>();
        params.put("deviceKey", query.getDeviceKey());
        params.put("beginTime", DateUtil.formatLocalDateTime(query.getBeginTime()));
        params.put("endTime", DateUtil.formatLocalDateTime(query.getEndTime()));
        if (StringUtils.isNotBlank(query.getIdentifier())) {
            params.put("identifier", query.getIdentifier());
        }
        try (Stream<PointValues> stream = influxDBClient.queryPoints(sqlParams, params, queryOptions)) {
            return stream.map(DeviceEventDataVo::fromPointValues).toList();
        } catch (Exception e) {
            log.error("查询出错:{}", e.getMessage());
            throw new BusinessException(ResultCode.BAD_REQUEST, "请求参数错误!");
        }
    }
}
