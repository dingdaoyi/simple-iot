package com.github.dingdaoyi.driver.tcp.core;

import com.github.dingdaoyi.core.driver.DeviceKeyParser;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.iot.IoTDataProcessor;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.iot.proto.ProtocolFactory;
import com.github.dingdaoyi.service.DeviceService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

@Slf4j
public class TcpAuthProcessor {
    private final DeviceService deviceService;
    private final IoTDataProcessor dataProcessor;

    public TcpAuthProcessor(DeviceService deviceService, IoTDataProcessor dataProcessor) {
        this.deviceService = deviceService;
        this.dataProcessor = dataProcessor;
    }

    public void process(Channel channel, byte[] data, TcpChannelManager.ChannelContext context, DeviceKeyParser parser) {
        if (!context.isAuthenticated()) {
            if (parser.hasDeviceKey(data)) {
                String deviceKey = parser.deviceKey(data);
                Optional<DeviceDTO> deviceOpt = deviceService.getByDeviceKey(deviceKey);
                if (deviceOpt.isPresent()) {
                    context.setAuthenticated(true);
                    context.setDeviceKey(deviceKey);
                    // 认证通过后，处理当前数据和缓存数据
                    handleDeviceData(deviceOpt.get(), data);
                    for (byte[] cacheData : context.getCache()) {
                        handleDeviceData(deviceOpt.get(), cacheData);
                    }
                    context.getCache().clear();
                } else {
                    log.warn("设备未注册: {}", deviceKey);
                    channel.close();
                }
            } else {
                // 不能解析设备号，暂存数据
                context.getCache().add(data);
                log.info("驱动[{}]端口[{}]收到无法解析设备号的数据，暂存，等待后续认证", context.getDriverName(), context.getPort());
            }
        } else {
            // 已认证，直接处理
            Optional<DeviceDTO> deviceOpt = deviceService.getByDeviceKey(context.getDeviceKey());
            deviceOpt.ifPresent(device -> handleDeviceData(device, data));
        }
    }

    private void handleDeviceData(DeviceDTO device, byte[] data) {
        String protocolKey = device.getProtoKey();
        String productKey = device.getProductKey();
        DeviceRequest request = new DeviceRequest();
        request.setDeviceKey(device.getDeviceKey());
        request.setProtoKey(protocolKey);
        request.setProductKey(productKey);
        request.setData(data);
        try {
            dataProcessor.messageUp(request);
        } catch (Exception e) {
            log.error("协议解析失败", e);
        }
    }
} 