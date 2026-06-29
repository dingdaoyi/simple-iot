package com.github.dingdaoyi.iot.influx;

import com.github.dingdaoyi.config.base.IotConfigProperties;
import com.github.dingdaoyi.model.query.DeviceDataQuery;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.proto.model.tsl.EventTypeEnum;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import com.influxdb.v3.client.query.QueryOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfluxDataProcessorTest {

    @Mock
    private InfluxDBClient influxDBClient;

    private InfluxDataProcessor processor;

    @BeforeEach
    void setUp() {
        IotConfigProperties properties = new IotConfigProperties();
        IotConfigProperties.InfluxDbProperties influx = new IotConfigProperties.InfluxDbProperties();
        influx.setDatabase("iot_bucket");
        influx.setPropDatabase("properties");
        influx.setEventDatabase("events");
        properties.setInfluxdb(influx);

        processor = new InfluxDataProcessor();
        ReflectionTestUtils.setField(processor, "influxDBClient", influxDBClient);
        processor.setProperties(properties);
    }

    @Test
    void processWritesPropertiesToProductScopedMeasurementWithTypedFields() {
        DecodeResult result = new DecodeResult();
        result.setDataList(List.of(
            new DeviceData("temperature", DataTypeEnum.DOUBLE, "21.5"),
            new DeviceData("humidity", DataTypeEnum.INT, "60"),
            new DeviceData("online", DataTypeEnum.BOOL, "true"),
            new DeviceData("status", DataTypeEnum.TEXT, "ok"),
            new DeviceData("ignored", DataTypeEnum.TEXT, null)
        ));
        TslModel tslModel = new TslModel();
        tslModel.setProductKey(" PK001 ");

        processor.process(result, "device-1", tslModel);

        ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
        verify(influxDBClient).writePoint(pointCaptor.capture());
        Point point = pointCaptor.getValue();
        assertThat(point.getMeasurement()).isEqualTo("properties_pk001");
        assertThat(point.getTag("deviceKey")).isEqualTo("device-1");
        assertThat(point.getField("temperature")).isEqualTo(21.5D);
        assertThat(point.getField("humidity")).isEqualTo(60L);
        assertThat(point.getField("online")).isEqualTo(true);
        assertThat(point.getField("status")).isEqualTo("ok");
        assertThat(point.getField("ignored")).isNull();
    }

    @Test
    void processWritesEventEvenWhenPropertyWriteFails() {
        DecodeResult result = new DecodeResult();
        result.setDataList(List.of(new DeviceData("temperature", DataTypeEnum.DOUBLE, "bad-double")));
        result.setEventData(new DeviceEventData("high_temperature", EventTypeEnum.WARN));
        TslModel tslModel = new TslModel();
        tslModel.setProductKey("PK001");

        processor.process(result, "device-1", tslModel);

        ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
        verify(influxDBClient).writePoint(pointCaptor.capture());
        Point point = pointCaptor.getValue();
        assertThat(point.getMeasurement()).isEqualTo("events");
        assertThat(point.getTag("deviceKey")).isEqualTo("device-1");
        assertThat(point.getTag("identifier")).isEqualTo("high_temperature");
    }

    @Test
    void processDoesNotWritePointWhenPropertyListIsEmpty() {
        DecodeResult result = new DecodeResult();
        result.setDataList(List.of());
        TslModel tslModel = new TslModel();
        tslModel.setProductKey("PK001");

        processor.process(result, "device-1", tslModel);

        verify(influxDBClient, never()).writePoint(org.mockito.ArgumentMatchers.any(Point.class));
    }

    @Test
    void eventLogsQuotesMeasurementNameAndUsesInclusiveBeginTime() {
        DeviceDataQuery query = new DeviceDataQuery();
        query.setDeviceKey("device-1");
        query.setBeginTime(LocalDateTime.of(2026, 6, 29, 0, 0));
        query.setEndTime(LocalDateTime.of(2026, 6, 29, 23, 59, 59));

        when(influxDBClient.queryPoints(anyString(), anyMap(), any(QueryOptions.class))).thenReturn(java.util.stream.Stream.empty());

        List<?> logs = processor.eventLogs(query);

        assertThat(logs).isEmpty();
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(influxDBClient).queryPoints(sqlCaptor.capture(), paramsCaptor.capture(), any(QueryOptions.class));
        assertThat(sqlCaptor.getValue())
                .contains("from \"events\"")
                .contains("time >= $beginTime")
                .doesNotContain("from events where");
        assertThat(paramsCaptor.getValue())
                .containsEntry("deviceKey", "device-1")
                .containsEntry("beginTime", query.getBeginTime().atZone(ZoneId.systemDefault()).toInstant().toString())
                .containsEntry("endTime", query.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toString())
                .doesNotContainKey("identifier");
    }

    @Test
    void eventLogsReturnsEmptyWhenMeasurementDoesNotExistYet() {
        DeviceDataQuery query = new DeviceDataQuery();
        query.setDeviceKey("device-1");
        query.setBeginTime(LocalDateTime.of(2026, 6, 29, 0, 0));
        query.setEndTime(LocalDateTime.of(2026, 6, 29, 23, 59, 59));
        RuntimeException missingMeasurement = new RuntimeException(
                "Error while planning query: Error during planning: table 'public.iox.events' not found");
        when(influxDBClient.queryPoints(anyString(), anyMap(), any(QueryOptions.class))).thenThrow(missingMeasurement);

        List<?> logs = processor.eventLogs(query);

        assertThat(logs).isEmpty();
    }
}
