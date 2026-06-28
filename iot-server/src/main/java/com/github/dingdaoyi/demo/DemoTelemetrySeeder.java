package com.github.dingdaoyi.demo;

import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.ModelProperty;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.ProductType;
import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.ProtoType;
import com.github.dingdaoyi.entity.enu.RuleSourceType;
import com.github.dingdaoyi.iot.proto.ProtocolFactory;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.ParamType;
import com.github.dingdaoyi.proto.model.tsl.PropertyAccessMode;
import com.github.dingdaoyi.rule.config.AlarmCreateConfig;
import com.github.dingdaoyi.rule.config.FilterPropertyConfig;
import com.github.dingdaoyi.rule.config.InputPropertyConfig;
import com.github.dingdaoyi.service.CacheService;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.ModelPropertyService;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.ProductTypeService;
import com.github.dingdaoyi.service.ProtocolService;
import com.github.dingdaoyi.service.RuleChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a small, idempotent telemetry demo that can be exercised by the simulator.
 */
@Service
@RequiredArgsConstructor
public class DemoTelemetrySeeder {

    private final ProtocolService protocolService;
    private final ProductTypeService productTypeService;
    private final ProductService productService;
    private final DeviceService deviceService;
    private final ModelPropertyService modelPropertyService;
    private final RuleChainService ruleChainService;
    private final CacheService cacheService;

    @Transactional
    public void seed() {
        Protocol protocol = upsertProtocol();
        ProductType productType = upsertProductType();
        Product product = upsertProduct(protocol, productType);
        upsertDevice(product);
        upsertModelProperties(product, productType);
        upsertRuleChain(product);
        cacheService.evictTslModel(DemoTelemetryConstants.PRODUCT_KEY);
        ProtocolFactory.reloadProtocol(DemoTelemetryConstants.PROTO_KEY);
    }

    private Protocol upsertProtocol() {
        Protocol protocol = protocolService.lambdaQuery()
                .eq(Protocol::getProtoKey, DemoTelemetryConstants.PROTO_KEY)
                .one();
        if (protocol == null) {
            protocol = new Protocol();
            protocol.setProtoKey(DemoTelemetryConstants.PROTO_KEY);
        }
        protocol.setName("Demo JSON Telemetry Protocol");
        protocol.setProtoType(ProtoType.JAVASCRIPT);
        protocol.setUrl("");
        protocol.setMark("Decodes the built-in demo JSON telemetry payload.");
        protocol.setHandlerClass("");
        protocol.setScriptLang("javascript");
        protocol.setScriptContent(demoDecodeScript());
        protocol.setStatus(1);
        protocolService.saveOrUpdate(protocol);
        return protocol;
    }

    private ProductType upsertProductType() {
        ProductType productType = productTypeService.lambdaQuery()
                .eq(ProductType::getPartTypeCode, DemoTelemetryConstants.PRODUCT_TYPE_CODE)
                .one();
        if (productType == null) {
            productType = new ProductType();
            productType.setPartTypeCode(DemoTelemetryConstants.PRODUCT_TYPE_CODE);
        }
        productType.setName("Demo Sensor");
        productType.setParentId(0);
        productType.setStatus(1);
        productType.setIconUrl("");
        productType.setMark("Built-in demo telemetry sensor category");
        productTypeService.saveOrUpdate(productType);
        return productType;
    }

    private Product upsertProduct(Protocol protocol, ProductType productType) {
        Product product = productService.lambdaQuery()
                .eq(Product::getProductKey, DemoTelemetryConstants.PRODUCT_KEY)
                .one();
        if (product == null) {
            product = new Product();
            product.setProductKey(DemoTelemetryConstants.PRODUCT_KEY);
        }
        product.setModel("Demo Smart Sensor");
        product.setManufacturer("Simple IoT");
        product.setRemark("Seeded product for 5-minute telemetry smoke testing");
        product.setProtocolId(protocol.getId());
        product.setProductTypeId(productType.getId());
        product.setIcon("sensor");
        productService.saveOrUpdate(product);
        return product;
    }

    private void upsertDevice(Product product) {
        Device device = deviceService.lambdaQuery()
                .eq(Device::getDeviceKey, DemoTelemetryConstants.DEVICE_KEY)
                .one();
        if (device == null) {
            device = new Device();
            device.setDeviceKey(DemoTelemetryConstants.DEVICE_KEY);
        }
        device.setProductId(product.getId());
        device.setDeviceName("Demo Sensor 001");
        device.setDeviceSecret(DemoTelemetryConstants.DEVICE_SECRET);
        device.setOnline(false);
        device.setActiveStatus(true);
        device.setThirdDeviceId(DemoTelemetryConstants.DEVICE_KEY);
        deviceService.saveOrUpdate(device);
    }

    private void upsertModelProperties(Product product, ProductType productType) {
        List<ModelProperty> properties = List.of(
                property(product, productType, "temperature", "Temperature", DataTypeEnum.DOUBLE, "℃", "Celsius", 100L, -40L),
                property(product, productType, "humidity", "Humidity", DataTypeEnum.INT, "%", "Percent", 100L, 0L),
                property(product, productType, "voltage", "Voltage", DataTypeEnum.DOUBLE, "V", "Volt", 260L, 0L),
                property(product, productType, "online", "Online", DataTypeEnum.BOOL, null, null, null, null),
                property(product, productType, "mode", "Mode", DataTypeEnum.TEXT, null, null, null, null)
        );

        for (ModelProperty property : properties) {
            ModelProperty existing = modelPropertyService.lambdaQuery()
                    .eq(ModelProperty::getProductId, product.getId())
                    .eq(ModelProperty::getIdentifier, property.getIdentifier())
                    .one();
            if (existing != null) {
                property.setId(existing.getId());
            }
            modelPropertyService.saveOrUpdate(property);
        }
    }

    private ModelProperty property(Product product,
                                   ProductType productType,
                                   String identifier,
                                   String name,
                                   DataTypeEnum dataType,
                                   String unit,
                                   String unitName,
                                   Long max,
                                   Long min) {
        ModelProperty property = new ModelProperty();
        property.setProductId(product.getId());
        property.setProductTypeId(productType.getId());
        property.setIdentifier(identifier);
        property.setName(name);
        property.setDataType(dataType);
        property.setAccessMode(PropertyAccessMode.READE);
        property.setParamType(ParamType.PROPERTY);
        property.setCustom(true);
        property.setParentId(-1);
        property.setRemark("Demo telemetry property: " + name);
        property.setUnit(unit);
        property.setUnitName(unitName);
        property.setMax(max);
        property.setMin(min);
        property.setStep(1L);
        property.setBool0("false");
        property.setBool1("true");
        property.setLength(64);
        property.setIconId(0);
        return property;
    }

    private void upsertRuleChain(Product product) {
        RuleChain ruleChain = ruleChainService.lambdaQuery()
                .eq(RuleChain::getSourceType, RuleSourceType.PRODUCT)
                .eq(RuleChain::getSourceId, product.getId())
                .one();
        if (ruleChain == null) {
            ruleChain = new RuleChain();
        }
        ruleChain.setName("Demo High Temperature Alarm");
        ruleChain.setDescription("Triggers an alarm when demo temperature is above " + DemoTelemetryConstants.HIGH_TEMP_THRESHOLD + "℃.");
        ruleChain.setIsRoot(true);
        ruleChain.setIsEnabled(true);
        ruleChain.setSourceType(RuleSourceType.PRODUCT);
        ruleChain.setSourceId(product.getId());
        ruleChain.setConfiguration(buildRuleChainConfiguration());
        ruleChainService.saveOrUpdate(ruleChain);
    }

    private RuleChain.RuleChainConfiguration buildRuleChainConfiguration() {
        InputPropertyConfig inputConfig = new InputPropertyConfig();
        inputConfig.setIdentifiers(List.of("temperature", "humidity", "voltage", "online", "mode"));

        FilterPropertyConfig filterConfig = new FilterPropertyConfig();
        filterConfig.setIdentifier("temperature");
        filterConfig.setOperator("GT");
        filterConfig.setValue(DemoTelemetryConstants.HIGH_TEMP_THRESHOLD);

        AlarmCreateConfig alarmConfig = new AlarmCreateConfig();
        alarmConfig.setAlarmType(DemoTelemetryConstants.HIGH_TEMP_ALARM_TYPE);
        alarmConfig.setAlarmName("High temperature alarm for ${deviceKey}");
        alarmConfig.setSeverity("MAJOR");
        alarmConfig.setMessage("Demo sensor ${deviceKey} temperature is ${temperature}℃, above " + DemoTelemetryConstants.HIGH_TEMP_THRESHOLD + "℃");

        List<RuleChain.RuleNode> nodes = new ArrayList<>();
        nodes.add(node("input-property", "Demo property report", "INPUT_PROPERTY", inputConfig));
        nodes.add(node("filter-high-temperature", "Temperature > 60℃", "FILTER_PROPERTY", filterConfig));
        nodes.add(node("create-alarm", "Create high temperature alarm", "ALARM_CREATE", alarmConfig));

        List<RuleChain.RuleConnection> connections = new ArrayList<>();
        connections.add(connection("input-property", "filter-high-temperature", "Success"));
        connections.add(connection("filter-high-temperature", "create-alarm", "True"));

        RuleChain.RuleChainConfiguration configuration = new RuleChain.RuleChainConfiguration();
        configuration.setNodes(nodes);
        configuration.setConnections(connections);
        return configuration;
    }

    private RuleChain.RuleNode node(String id, String name, String type, com.github.dingdaoyi.rule.config.NodeConfig config) {
        RuleChain.RuleNode node = new RuleChain.RuleNode();
        node.setId(id);
        node.setName(name);
        node.setType(type);
        node.setConfig(config);
        node.setX(0);
        node.setY(0);
        return node;
    }

    private RuleChain.RuleConnection connection(String source, String target, String type) {
        RuleChain.RuleConnection connection = new RuleChain.RuleConnection();
        connection.setSource(source);
        connection.setTarget(target);
        connection.setType(type);
        return connection;
    }

    private String demoDecodeScript() {
        return """
                exports.decode = function(request) {
                  const data = JSON.parse(request.data);
                  return {
                    messageId: 1,
                    rawData: request.data,
                    dataList: [
                      { identifier: 'temperature', type: 'DOUBLE', value: data.temperature },
                      { identifier: 'humidity', type: 'INT', value: data.humidity },
                      { identifier: 'voltage', type: 'DOUBLE', value: data.voltage },
                      { identifier: 'online', type: 'BOOL', value: data.online },
                      { identifier: 'mode', type: 'TEXT', value: data.mode }
                    ]
                  };
                };
                """;
    }
}
