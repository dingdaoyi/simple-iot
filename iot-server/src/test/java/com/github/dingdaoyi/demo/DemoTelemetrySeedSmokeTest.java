package com.github.dingdaoyi.demo;

import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.ModelProperty;
import com.github.dingdaoyi.entity.ModelService;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import com.github.dingdaoyi.entity.enu.ProtoType;
import com.github.dingdaoyi.entity.enu.ServiceTypeEnum;
import com.github.dingdaoyi.iot.IotDataProcessor;
import com.github.dingdaoyi.iot.proto.ProtocolFactory;
import com.github.dingdaoyi.mapper.AlarmMapper;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.ModelPropertyService;
import com.github.dingdaoyi.service.ModelServiceService;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.ProtocolService;
import com.github.dingdaoyi.service.RuleChainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.influxdb.v3.client.InfluxDBClient;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest
@ActiveProfiles("integration")
class DemoTelemetrySeedSmokeTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("simple_iot_test")
            .withUsername("postgres")
            .withPassword(java.util.UUID.randomUUID().toString());

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl() + "&sslmode=disable");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @MockitoBean
    private InfluxDBClient influxDBClient;

    @Autowired
    DemoTelemetrySeeder demoTelemetrySeeder;
    @Autowired
    ProtocolService protocolService;
    @Autowired
    ProductService productService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    ModelPropertyService modelPropertyService;
    @Autowired
    ModelServiceService modelServiceService;
    @Autowired
    RuleChainService ruleChainService;
    @Autowired
    IotDataProcessor iotDataProcessor;
    @Autowired
    AlarmMapper alarmMapper;

    @Test
    void seedCreatesRunnableDemoProductDeviceProtocolAndRuleChain() {
        demoTelemetrySeeder.seed();
        demoTelemetrySeeder.seed();

        Protocol protocol = protocolService.lambdaQuery()
                .eq(Protocol::getProtoKey, DemoTelemetryConstants.PROTO_KEY)
                .one();
        assertThat(protocol).isNotNull();
        assertThat(protocol.getProtoType()).isEqualTo(ProtoType.JAVASCRIPT);
        assertThat(protocol.getScriptLang()).isEqualTo("javascript");
        assertThat(protocol.getScriptContent())
                .contains("exports.decode")
                .contains("temperature")
                .contains("eventData")
                .contains(DemoTelemetryConstants.HIGH_TEMP_EVENT_IDENTIFIER);
        assertThat(ProtocolFactory.getDecoder(DemoTelemetryConstants.PROTO_KEY)).isPresent();

        Product product = productService.lambdaQuery()
                .eq(Product::getProductKey, DemoTelemetryConstants.PRODUCT_KEY)
                .one();
        assertThat(product).isNotNull();
        assertThat(product.getProtocolId()).isEqualTo(protocol.getId());

        Device device = deviceService.lambdaQuery()
                .eq(Device::getDeviceKey, DemoTelemetryConstants.DEVICE_KEY)
                .one();
        assertThat(device).isNotNull();
        assertThat(device.getProductId()).isEqualTo(product.getId());
        assertThat(device.getDeviceSecret()).isEqualTo(DemoTelemetryConstants.DEVICE_SECRET);

        List<ModelProperty> properties = modelPropertyService.lambdaQuery()
                .eq(ModelProperty::getProductId, product.getId())
                .list();
        assertThat(properties)
                .extracting(ModelProperty::getIdentifier)
                .containsExactlyInAnyOrder("temperature", "humidity", "voltage", "online", "mode");
        assertThat(properties)
                .filteredOn(property -> "temperature".equals(property.getIdentifier()))
                .singleElement()
                .extracting(ModelProperty::getDataType)
                .isEqualTo(DataTypeEnum.DOUBLE);

        ModelService highTemperatureEvent = modelServiceService.lambdaQuery()
                .eq(ModelService::getProductId, product.getId())
                .eq(ModelService::getServiceType, ServiceTypeEnum.EVENT)
                .eq(ModelService::getIdentifier, DemoTelemetryConstants.HIGH_TEMP_EVENT_IDENTIFIER)
                .one();
        assertThat(highTemperatureEvent).isNotNull();
        assertThat(highTemperatureEvent.getName()).isEqualTo("High Temperature Event");

        RuleChain ruleChain = ruleChainService.lambdaQuery()
                .eq(RuleChain::getSourceId, product.getId())
                .one();
        assertThat(ruleChain).isNotNull();
        assertThat(ruleChain.getIsEnabled()).isTrue();
        assertThat(ruleChain.getConfiguration().getNodes())
                .extracting(RuleChain.RuleNode::getType)
                .containsExactlyInAnyOrder("INPUT_PROPERTY", "FILTER_PROPERTY", "INPUT_EVENT", "FILTER_EVENT_TYPE", "ALARM_CREATE");
    }

    @Test
    void seededDemoTelemetryCanTriggerHighTemperatureAlarmEndToEnd() {
        demoTelemetrySeeder.seed();

        DeviceRequest request = new DeviceRequest();
        request.setDeviceKey(DemoTelemetryConstants.DEVICE_KEY);
        request.setProductKey(DemoTelemetryConstants.PRODUCT_KEY);
        request.setProtoKey(DemoTelemetryConstants.PROTO_KEY);
        request.setMessageType(ProtoMessageType.PROPERTY);
        request.setData("{\"temperature\":72.5,\"humidity\":43,\"voltage\":220.8,\"online\":true,\"mode\":\"auto\"}"
                .getBytes(StandardCharsets.UTF_8));

        iotDataProcessor.messageUp(request);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            List<Alarm> alarms = alarmMapper.selectList(null);
            assertThat(alarms).hasSize(1);
        });
        Alarm alarm = alarmMapper.selectList(null).getFirst();
        assertThat(alarm.getAlarmType()).isEqualTo("demo_high_temperature");
        assertThat(alarm.getAlarmName()).contains(DemoTelemetryConstants.DEVICE_KEY).contains("High temperature");
        assertThat(alarm.getStatus()).isEqualTo(AlarmStatus.ACTIVE);
        assertThat(alarm.getDeviceKey()).isEqualTo(DemoTelemetryConstants.DEVICE_KEY);
        assertThat(alarm.getMessage()).contains("72.5");
        assertThat(alarm.getDetails()).containsKeys("temperature", "humidity", "voltage", "mode");
    }

    @Test
    void seededDemoEventTelemetryCanTriggerHighTemperatureAlarmEndToEnd() {
        demoTelemetrySeeder.seed();

        DeviceRequest request = new DeviceRequest();
        request.setDeviceKey(DemoTelemetryConstants.DEVICE_KEY);
        request.setProductKey(DemoTelemetryConstants.PRODUCT_KEY);
        request.setProtoKey(DemoTelemetryConstants.PROTO_KEY);
        request.setMessageType(ProtoMessageType.EVENT);
        request.setData(("{\"eventIdentifier\":\"" + DemoTelemetryConstants.HIGH_TEMP_EVENT_IDENTIFIER
                + "\",\"temperature\":72.5,\"humidity\":43,\"voltage\":220.8,\"online\":true,\"mode\":\"auto\"}")
                .getBytes(StandardCharsets.UTF_8));

        iotDataProcessor.messageUp(request);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            List<Alarm> alarms = alarmMapper.selectList(null);
            assertThat(alarms).hasSize(1);
        });
        Alarm alarm = alarmMapper.selectList(null).getFirst();
        assertThat(alarm.getAlarmType()).isEqualTo(DemoTelemetryConstants.HIGH_TEMP_ALARM_TYPE);
        assertThat(alarm.getDeviceKey()).isEqualTo(DemoTelemetryConstants.DEVICE_KEY);
        assertThat(alarm.getMessage()).contains("72.5");
        assertThat(alarm.getDetails()).containsKeys("temperature", "humidity", "voltage", "mode");
    }
}
