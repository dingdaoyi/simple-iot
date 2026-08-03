package com.github.dingdaoyi.driver.mqtt;

import com.github.dingdaoyi.config.base.IotConfigProperties;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.service.DeviceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.mica.mqtt.core.server.auth.IMqttServerAuthHandler;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.dingdaoyi.driver.mqtt.MqttServerUniqueIdService.ERROR_UNIQUE_ID;

/**
 * @author dingyunwei
 */
@Slf4j
@Component
public class MqttServerAuthHandler implements IMqttServerAuthHandler {

    private static final int MAX_FAIL = 5;
    private static final long LOCK_MS = 60_000;

    @Resource
    private DeviceService deviceService;

    @Resource
    private IotConfigProperties iotConfigProperties;

    // ponytail: in-memory per-IP failure tracking, lost on restart. Redis for multi-node.
    private final ConcurrentHashMap<String, FailTracker> failMap = new ConcurrentHashMap<>();

    @Override
    public boolean authenticate(ChannelContext context, String uniqueId, String clientId, String userName, String password) {
        String ip = context.getClientNode() == null ? "unknown" : context.getClientNode().getIp();
        log.info("authenticate client {} ip {} user {} password {}", clientId, ip, userName, maskSecret(password));

        if (ERROR_UNIQUE_ID.equals(uniqueId)) {
            return false;
        }

        // IP 限流: 连续失败 5 次锁定 60s
        FailTracker tracker = failMap.computeIfAbsent(ip, k -> new FailTracker());
        if (tracker.isLocked()) {
            log.warn("IP {} MQTT 认证被锁定 {}s", ip, (tracker.lockUntil - System.currentTimeMillis()) / 1000);
            return false;
        }

        Optional<DeviceDTO> optionalDevice = deviceService.getByDeviceKey(uniqueId);
        // 自动注册
        if (optionalDevice.isEmpty() && iotConfigProperties.isAutoRegister()) {
            optionalDevice = autoRegister(uniqueId, password);
        }
        boolean ok = optionalDevice
                .map(item -> (!iotConfigProperties.isEnableDeviceSecret())
                                          || StringUtils.equals(password, item.getDeviceSecret()))
                .orElse(false);

        if (ok) {
            tracker.reset();
            optionalDevice.ifPresent(device -> context.set("device", device));
        } else {
            int fails = tracker.increment();
            log.warn("MQTT 认证失败 ip={} deviceKey={} fails={}/{}", ip, uniqueId, fails, MAX_FAIL);
        }
        return ok;
    }

    /** 自动注册设备，返回新创建的 DeviceDTO */
    private Optional<DeviceDTO> autoRegister(String deviceKey, String deviceSecret) {
        Integer productId = iotConfigProperties.getAutoRegisterProductId();
        if (productId == null) {
            log.warn("自动注册失败: 未配置 simple.iot.auto-register-product-id, deviceKey={}", deviceKey);
            return Optional.empty();
        }
        Device device = new Device();
        device.setDeviceKey(deviceKey);
        device.setDeviceName(deviceKey);
        device.setProductId(productId);
        device.setOnline(false);
        device.setActiveStatus(false);
        if (StringUtils.isNotBlank(deviceSecret)) {
            device.setDeviceSecret(deviceSecret);
        }
        try {
            deviceService.save(device);
            log.info("自动注册设备成功: deviceKey={}, productId={}", deviceKey, productId);
            return deviceService.getByDeviceKey(deviceKey);
        } catch (Exception e) {
            log.warn("自动注册设备失败: deviceKey={}", deviceKey, e);
            return Optional.empty();
        }
    }

    static String maskSecret(String secret) {
        if (StringUtils.isBlank(secret)) {
            return "<empty>";
        }
        if (secret.length() <= 4) {
            return "****";
        }
        return StringUtils.repeat('*', secret.length() - 4) + secret.substring(secret.length() - 4);
    }

    private static class FailTracker {
        final AtomicInteger fails = new AtomicInteger(0);
        volatile long lockUntil = 0;

        int increment() {
            int n = fails.incrementAndGet();
            if (n >= MAX_FAIL) {
                lockUntil = System.currentTimeMillis() + LOCK_MS;
            }
            return n;
        }

        boolean isLocked() {
            return lockUntil > System.currentTimeMillis();
        }

        void reset() {
            fails.set(0);
            lockUntil = 0;
        }
    }
}
