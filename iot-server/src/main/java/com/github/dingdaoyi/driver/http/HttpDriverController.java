package com.github.dingdaoyi.driver.http;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.driver.DeviceKeyParser;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.driver.tcp.core.DeviceKeyParserLoader;
import com.github.dingdaoyi.iot.IotDataProcessor;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author dingyunwei
 */
@RestController
@RequestMapping("/driver/http")
public class HttpDriverController {
    @Resource
    private IotDataProcessor ioTDataProcessor;

    @Resource
    private DeviceService deviceService;

    @PostMapping("/{driverName}")
    public BaseResult<String> receiveData(
            @PathVariable String driverName,
            @RequestBody byte[] body) {
        DeviceKeyParser parser = DeviceKeyParserLoader.getParser(driverName);
        if (parser == null) {
            return BaseResult.fail(ResultCode.PARAM_VALID_ERROR, "未找到对应驱动解析器");
        }
        if (!parser.hasDeviceKey(body)) {
            return BaseResult.fail(ResultCode.PARAM_VALID_ERROR, "无法解析设备编号");
        }
        parser.deviceKey(body).flatMap(deviceKey ->
                deviceService.getByDeviceKey(deviceKey)
        ).ifPresent(device -> {
            DeviceRequest deviceRequest = new DeviceRequest();
            deviceRequest.setDeviceKey(device.getDeviceKey());
            deviceRequest.setProtoKey(device.getProtoKey());
            deviceRequest.setProductKey(device.getProductKey());
            deviceRequest.setData(body);
            ioTDataProcessor.messageUp(deviceRequest);
        });
        return BaseResult.success("数据接收成功");
    }
} 