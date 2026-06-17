package com.github.dingdaoyi.iot.influx;

import com.github.dingdaoyi.config.base.IotConfigProperties;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
        tslModel.setProductKey("PK001");

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
    void processDoesNotWritePointWhenPropertyListIsEmpty() {
        DecodeResult result = new DecodeResult();
        result.setDataList(List.of());
        TslModel tslModel = new TslModel();
        tslModel.setProductKey("PK001");

        processor.process(result, "device-1", tslModel);

        verify(influxDBClient, never()).writePoint(org.mockito.ArgumentMatchers.any(Point.class));
    }
}
