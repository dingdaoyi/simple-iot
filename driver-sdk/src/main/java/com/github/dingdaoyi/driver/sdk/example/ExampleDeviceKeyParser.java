package com.github.dingdaoyi.driver.sdk.example;

import com.github.dingdaoyi.core.driver.DeviceKeyParser;
import com.github.dingdaoyi.core.driver.DriverTypeEnum;
import com.github.dingdaoyi.core.service.DeviceProvider;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * 示例: 从上报数据解析设备 Key
 * 协议格式: {"deviceKey":"xxx",...}
 * ponytail: 最简实现, 不做缓存
 * @author dingyunwei
 */
public class ExampleDeviceKeyParser implements DeviceKeyParser {

    private DeviceProvider deviceProvider;

    @Override
    public void setDeviceProvider(DeviceProvider deviceProvider) {
        this.deviceProvider = deviceProvider;
    }

    @Override
    public List<DriverTypeEnum> driverTypes() {
        return List.of(DriverTypeEnum.CUSTOM);
    }

    @Override
    public String driverName() {
        return "example-json";
    }

    @Override
    public boolean hasDeviceKey(byte[] data) {
        return deviceKey(data).isPresent();
    }

    @Override
    public Optional<String> deviceKey(byte[] data) {
        String json = new String(data, StandardCharsets.UTF_8);
        try {
            var obj = cn.hutool.json.JSONUtil.parseObj(json);
            String key = obj.getStr("deviceKey");
            return key != null && !key.isBlank() ? Optional.of(key) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
