package com.github.dingdaoyi.iot.influx;

import com.github.dingdaoyi.config.base.IotConfigProperties;
import com.github.dingdaoyi.iot.DataProcessor;
import com.github.dingdaoyi.model.query.DeviceDataQuery;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.proto.model.KeyValue;
import com.github.dingdaoyi.service.DeviceDataService;
import com.influxdb.client.BucketsApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.$;
import net.dreamlu.mica.core.utils.DatePattern;
import net.dreamlu.mica.core.utils.DateUtil;
import net.dreamlu.mica.core.utils.JsonUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public void setProperties(IotConfigProperties properties) {
        this.properties = properties.getInfluxdb();
        BucketsApi bucketsApi = influxDBClient.getBucketsApi();
    }

    @Override
    public void process(DecodeResult message, String deviceKey) {
        if ($.isNotEmpty(message.getDataList())) {
            this.saveProperties(message.getDataList(), deviceKey);
        }
        if (message.getEventData() != null) {
            this.saveEvent(message.getEventData(), message.getRowData(), deviceKey);
        }
    }

    private void saveEvent(DeviceEventData eventData,String rowData, String deviceKey) {
        Point point = Point.measurement(properties.getEventDatabase())
                .addTag("identifier", eventData.getIdentifier())
                .addTag("deviceKey", deviceKey)
                .addTag("eventType", eventData.getEventType().getValue() + "")
                .addField("value", JsonUtil.toJson(eventData.getParams()))
                .addField("rowData", rowData)
                .time(Instant.now(), WritePrecision.S);
        log.info("保存事件信息;{}|{}", deviceKey, JsonUtil.toJson(point));
        influxDBClient.getWriteApiBlocking().writePoint(point);
    }

    /**
     * 保存属性
     *
     * @param dataList  属性列表
     * @param deviceKey 设备编号
     */
    private void saveProperties(List<DeviceData> dataList, String deviceKey) {
        List<Point> points = new ArrayList<>();
        for (DeviceData deviceData : dataList) {
            Point point = Point.measurement(properties.getPropDatabase())
                    .addTag("identifier", deviceData.identifier())
                    .addTag("deviceKey", deviceKey)
                    .addTag("dataType", deviceData.dataType().name())
                    .time(Instant.now(), WritePrecision.S);
            switch (deviceData.dataType()) {
                case DATE, STRUCT, ENUM, TEXT -> point.addField("value", deviceData.value().toString());
                case INT -> point.addField("value", (int) deviceData.value());
                case FLOAT -> point.addField("value", (float) deviceData.value());
                case DOUBLE -> point.addField("value", (double) deviceData.value());
                case BOOL -> point.addField("value", (boolean) deviceData.value());
            }
            points.add(point);
        }
        log.info("保存属性信息;{}|{}", deviceKey, JsonUtil.toJson(dataList));
        influxDBClient.getWriteApiBlocking().writePoints(points);
    }

    @Override
    public List<KeyValue<String, Object>> getLatestData(String deviceKey) {

        String fluxQuery = String.format("""
                from(bucket: "%s")
                  |> range(start: -30d)
                  |> filter(fn: (r) => r["_measurement"] == "%s" and r["deviceKey"] == "%s")
                  |> map(fn: (r) => ({ r with _value: string(v: r._value) }))
                  |> last()
                  |> keep(columns: ["identifier", "_value"])""",properties.getBucket(),properties.getPropDatabase(), deviceKey);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<KeyValue<String, Object>> result = new ArrayList<>();
        // Execute query
        List<FluxTable> tableList = queryApi.query(fluxQuery,properties.getOrg(), Map.of());
        for (FluxTable fluxTable : tableList) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord record : records) {
                String identifier = record.getValueByKey("identifier").toString();
                Object value = record.getValueByKey("_value");
                result.add(new KeyValue<>(identifier, value));
            }
        }
        return result;
    }

    /**
     * 查询条件可能有点问题
     * @param query
     * @return
     */
    @Override
    public List<KeyValue<String, Object>> metric(DeviceDataQuery query) {

        String fluxQuery = String.format("""
                from(bucket: "%s")
                  |> range(start: time(v: "%s"),stop: time(v: "%s") )
                  |> filter(fn: (r) => r["_measurement"] == "%s" and r["deviceKey"] == "%s" and r["identifier"] == "%s")
                  |> aggregateWindow(every: %s, fn: mean)
                  |> fill(column: "_value", usePrevious: true)
                  |> keep(columns: ["_value","_time"])""",properties.getBucket(),
                DateUtil.format(query.getBeginTime(), DatePattern.UTC_PATTERN),DateUtil.format(query.getEndTime(), DatePattern.UTC_PATTERN),
                properties.getPropDatabase(),query.getDeviceKey(),query.getIdentifier(),
                SamplingIntervalCalculator.calculateSamplingInterval(query.getBeginTime(),query.getEndTime(),20)
                );
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<KeyValue<String, Object>> result = new ArrayList<>();
        // Execute query
        List<FluxTable> tableList = queryApi.query(fluxQuery,properties.getOrg());
        for (FluxTable fluxTable : tableList) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord record : records) {
                Object value = record.getValue();
                String date = DateUtil.format(record.getTime(), DatePattern.NORM_DATETIME_PATTERN);
                result.add(new KeyValue<>(date, value));
            }
        }
        return result;
    }
}
