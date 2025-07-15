package com.github.dingdaoyi.drivers;

import com.github.dingdaoyi.core.driver.DeviceKeyParser;
import com.github.dingdaoyi.core.driver.DriverTypeEnum;
import com.github.dingdaoyi.core.service.DeviceProvider;

import java.util.List;
import java.util.Optional;

/**
 * 国标 GB/T 28181 驱动实现
 * 只负责通信、设备编号提取、驱动注册等
 * @author dingyunwei
 */
public class GbDeviceKeyParser implements DeviceKeyParser {

    @Override
    public void setDeviceProvider(DeviceProvider deviceProvider) {

    }

    @Override
    public List<DriverTypeEnum> driverTypes() {
        return List.of(DriverTypeEnum.TCP,DriverTypeEnum.UDP);
    }

    @Override
    public String driverName() {
        return "GB/T-28181";
    }

    @Override
    public boolean hasDeviceKey(byte[] data) {
        return true;
    }

    @Override
    public Optional<String> deviceKey(byte[] data) {
        // 特殊处理：如协议头为4040EE1F000006FF00882323，截取指定位置6字节
        int index = 12;
        if (data.length == 12) {
            index = 2;
        }
        if (data.length < index + 6) {
            throw new IllegalArgumentException("数据长度不足，无法提取设备号");
        }
        long value = 0;
        for (int i = 0; i < 6; i++) {
            value |= ((long) (data[index + i] & 0xFF)) << (8 * i);
        }
        return Optional.of(Long.toHexString(value).toUpperCase());
    }
}