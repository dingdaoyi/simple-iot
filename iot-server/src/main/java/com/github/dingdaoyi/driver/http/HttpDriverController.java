package com.github.dingdaoyi.driver.http;

import com.github.dingdaoyi.core.driver.DeviceKeyParser;
import com.github.dingdaoyi.driver.tcp.core.DeviceKeyParserLoader;
import com.github.dingdaoyi.iot.IoTDataProcessor;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author dingyunwei
 */
@RestController
@RequestMapping("/api/driver/http")
public class HttpDriverController {
    @Resource
    private IoTDataProcessor ioTDataProcessor;

    @Resource
    private DeviceService deviceService;

    @PostMapping("/{driverName}")
    public ResponseEntity<?> receiveData(
            @PathVariable String driverName,
            @RequestBody byte[] body) {
        DeviceKeyParser parser = DeviceKeyParserLoader.getParser(driverName);
        if (parser == null) {
            return ResponseEntity.badRequest().body("不支持的驱动: " + driverName);
        }
        if (!parser.hasDeviceKey(body)) {
            return ResponseEntity.badRequest().body("无法解析设备号");
        }
       Optional<String>  deviceKeyOpt = parser.deviceKey(body);
        if (deviceKeyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("设备未注册到平台");
        }
        Optional<DeviceDTO> deviceOpt = deviceService.getByDeviceKey(deviceKeyOpt.get());
        if (deviceOpt.isPresent()) {
            DeviceDTO device = deviceOpt.get();
            DeviceRequest deviceRequest = new DeviceRequest();
            deviceRequest.setDeviceKey(device.getDeviceKey());
            deviceRequest.setProtoKey(device.getProtoKey());
            deviceRequest.setProductKey(device.getProductKey());
            deviceRequest.setData(body);
            ioTDataProcessor.messageUp(deviceRequest);
        }

        return ResponseEntity.ok("接收成功");
    }
} 