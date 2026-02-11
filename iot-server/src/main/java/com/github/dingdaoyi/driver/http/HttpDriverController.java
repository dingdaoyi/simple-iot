package com.github.dingdaoyi.driver.http;

import com.github.dingdaoyi.core.driver.DeviceKeyParser;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.driver.tcp.core.DeviceKeyParserLoader;
import com.github.dingdaoyi.iot.IotDataProcessor;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.service.DeviceService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

/**
 * @author dingyunwei
 */
@RestController
@Slf4j
@RequestMapping("/driver/http")
public class HttpDriverController {
    @Resource
    private IotDataProcessor ioTDataProcessor;

    @Resource
    private DeviceService deviceService;

    @PostMapping("/{driverName}")
    public void receiveData(
            @PathVariable String driverName,
            @RequestBody byte[] body,
            HttpServletResponse response) {
        DeviceKeyParser parser = DeviceKeyParserLoader.getParser(driverName);
        if (parser == null) {
            new HttpDeviceConnection("", "", response).writeFail(ResultCode.PARAM_VALID_ERROR, "未找到对应驱动解析器");
            return;
        }
        if (!parser.hasDeviceKey(body)) {
            new HttpDeviceConnection("", "", response).writeFail(ResultCode.PARAM_VALID_ERROR, "无法解析设备编号");
            return;
        }

        parser.deviceKey(body).flatMap(deviceKey ->
                deviceService.getByDeviceKey(deviceKey)
        ).ifPresent(device -> {
            DeviceRequest deviceRequest = new DeviceRequest();
            deviceRequest.setDeviceKey(device.getDeviceKey());
            deviceRequest.setProtoKey(device.getProtoKey());
            deviceRequest.setProductKey(device.getProductKey());
            deviceRequest.setData(body);
            try (HttpDeviceConnection connection = new HttpDeviceConnection(device.getDeviceKey(), device.getProductKey(), response)) {
                deviceRequest.setConnection(connection);
                ioTDataProcessor.messageUp(deviceRequest);
            } catch (IOException e) {
                log.error("IO异常: {}", e.getMessage());
            }
        });
    }
}
