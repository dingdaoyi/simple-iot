package com.github.dingdaoyi.drivers;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.core.driver.DeviceKeyParser;
import com.github.dingdaoyi.core.driver.DriverTypeEnum;
import com.github.dingdaoyi.core.model.DeviceBase;
import com.github.dingdaoyi.core.service.DeviceProvider;

import java.util.List;
import java.util.Optional;

/**
 * 电信aep接入
 * <p>
 * <a href="https://www.ctwing.cn/dyts/105#see">参考文档</a>
 *
 * @author dingyunwei
 */
public class AepDeviceKeyParser implements DeviceKeyParser {

    private DeviceProvider deviceProvider;

    @Override
    public void setDeviceProvider(DeviceProvider deviceProvider) {
        this.deviceProvider = deviceProvider;
    }

    @Override
    public List<DriverTypeEnum> driverTypes() {
        return List.of(DriverTypeEnum.HTTP);
    }

    @Override
    public String driverName() {
        return "AEP";
    }

    @Override
    public boolean hasDeviceKey(byte[] data) {
        return true;
    }

    @Override
    public Optional<String> deviceKey(byte[] data) {
        JSONObject jsonObject = JSONUtil.parseObj(new String(data));
        if (jsonObject.containsKey("imei")) {
            return Optional.of(jsonObject.getStr("imei"));
        }else if (jsonObject.containsKey("IMEI")) {
            return Optional.of(jsonObject.getStr("IMEI"));
        }else if (jsonObject.containsKey("deviceId")) {
            Optional<DeviceBase> optional = deviceProvider.getByThreadDeviceId(jsonObject.getStr("deviceId"));
            return optional.map(DeviceBase::getDeviceKey);
        }
        return Optional.empty();
    }
}