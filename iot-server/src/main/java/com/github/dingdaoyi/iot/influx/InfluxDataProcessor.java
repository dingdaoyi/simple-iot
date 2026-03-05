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
        String productKey = tslModel.getProductKey();
        if (CollectionUtil.isNotEmpty(message.getDataList())) {
            this.saveProperties(message.getDataList(), deviceKey, productKey);
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
     * <p>
     * 按产品区分存储，使用 identifier 作为字段名，避免 InfluxDB 同名字段类型不一致的问题
     * measurement 命名: {propDatabase}_{productKey}
     *
     * @param dataList   属性列表
     * @param deviceKey  设备编号
     * @param productKey 产品Key
     */
    private void saveProperties(List<DeviceData> dataList, String deviceKey, String productKey) {
        if (CollectionUtil.isEmpty(dataList)) {
            return;
        }

        // 构建按产品区分的 measurement
        String measurement = properties.getPropDatabase() + "_" + productKey.toLowerCase(Locale.ROOT);

        // 将一次上报的所有属性合并到一个 Point 中，使用 identifier 作为字段名
        Point point = Point.measurement(measurement)
                .setTag("deviceKey", deviceKey)
                .setTimestamp(Instant.now());

        for (DeviceData deviceData : dataList) {
            String fieldName = deviceData.getIdentifier();
            Object value = deviceData.getValue();
            if (value == null) {
                continue;
            }
            switch (deviceData.getDataType()) {
                case DATE, STRUCT, ENUM, TEXT -> point.setField(fieldName, value.toString());
                case INT -> point.setField(fieldName, Integer.parseInt(value.toString()));
                case FLOAT -> point.setField(fieldName, Float.parseFloat(value.toString()));
                case DOUBLE -> point.setField(fieldName, Double.parseDouble(value.toString()));
                case BOOL -> point.setField(fieldName, Boolean.parseBoolean(value.toString()));
            }
        }
        log.info("保存属性信息;deviceKey={}|productKey={}|data={}", deviceKey, productKey, JSONUtil.toJsonStr(dataList));
        influxDBClient.writePoint(point);
    }

    @Override
    public List<KeyValue<String, Object>> getLatestData(String deviceKey) {
        Optional<DeviceDTO> dtoOptional = deviceService.getByDeviceKey(deviceKey);
        if (dtoOptional.isEmpty()) {
            return new ArrayList<>();
        }
        DeviceDTO deviceDTO = dtoOptional.get();
        String productKey = deviceDTO.getProductKey();

        Optional<TslModel> optional = tslModelService.findByProductKey(productKey);
        if (optional.isEmpty()) {
            return new ArrayList<>();
        }
        TslModel tslModel = optional.get();
        List<TslProperty> propertyList = tslModel.getProperties().stream()
                .filter(TslProperty::isProperty).toList();

        List<KeyValue<String, Object>> dataList = new ArrayList<>();
        QueryOptions queryOptions = new QueryOptions(properties.getDatabase(), QueryType.SQL);

        // 按产品区分的 measurement
        String measurement = properties.getPropDatabase() + "_" + productKey.toLowerCase(Locale.ROOT);

        // 为每个属性单独查询最新值
        for (TslProperty property : propertyList) {
            String identifier = property.getIdentifier();
            String sql = "select last_value(\"" + identifier + "\") as \"" + identifier + "\" from \"" + measurement + "\" where \"deviceKey\"=$deviceKey";
            try (Stream<PointValues> stream = influxDBClient.queryPoints(sql, Map.of("deviceKey", deviceKey), queryOptions)) {
                stream.findFirst().ifPresent(row -> {
                    Object value = row.getField(identifier);
                    if (value != null) {
                        dataList.add(new KeyValue<>(identifier, value));
                    }
                });
            } catch (Exception e) {
                log.debug("查询属性 {} 出错:{}", identifier, e.getMessage());
            }
        }
        return dataList;
    }

    /**
     * 查询单个属性的历史数据
     *
     * @param query 查询条件
     * @return 历史数据列表
     */
    @Override
    public List<KeyValue<String, Object>> metric(DeviceDataQuery query) {
        // 获取设备信息以确定产品
        Optional<DeviceDTO> deviceOptional = deviceService.getByDeviceKey(query.getDeviceKey());
        if (deviceOptional.isEmpty()) {
            return new ArrayList<>();
        }
        String productKey = deviceOptional.get().getProductKey();

        List<KeyValue<String, Object>> dataList = new ArrayList<>();
        QueryOptions queryOptions = new QueryOptions(properties.getDatabase(), QueryType.SQL);

        // 按产品区分的 measurement
        String measurement = properties.getPropDatabase() + "_" + productKey.toLowerCase(Locale.ROOT);
        String identifier = query.getIdentifier();

        // 查询指定 identifier 字段的历史数据
        String sqlParams = "select \"" + identifier + "\", time from \"" + measurement + "\" where \"deviceKey\"=$deviceKey" +
                " and time >= $beginTime and time <= $endTime order by time asc";

        try (Stream<PointValues> stream = influxDBClient.queryPoints(sqlParams, Map.of(
                "deviceKey", query.getDeviceKey(),
                "beginTime", DateUtil.formatLocalDateTime(query.getBeginTime()),
                "endTime", DateUtil.formatLocalDateTime(query.getEndTime())), queryOptions)) {

            stream.forEach(row -> dataList.add(new KeyValue<>(TimeUtils.toDateTimeStr(row.getTimestamp()), row.getField(identifier))));
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
