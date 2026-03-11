package com.github.dingdaoyi.rule;

import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.iot.DataProcessor;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.RuleChainService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 规则链数据处理器
 * 替代原有的 RuleDataProcessor
 * @author dingyunwei
 */
@Slf4j
@Service
public class RuleChainDataProcessor implements DataProcessor {

    @Resource
    private RuleChainService ruleChainService;

    @Resource
    private RuleChainEngine ruleChainEngine;

    @Resource
    private DeviceService deviceService;

    @Override
    public void process(DecodeResult decodeResult, String deviceKey, TslModel tslModel) {
        // 1. 获取设备信息
        Device device = deviceService.getByDeviceKey(deviceKey).orElse(null);
        if (device == null) {
            log.warn("设备不存在: {}", deviceKey);
            return;
        }

        // 2. 构建规则上下文
        RuleContext context = buildContext(decodeResult, device, tslModel);

        // 3. 获取该设备关联的规则链
        List<RuleChain> ruleChains = ruleChainService.getByDeviceKey(deviceKey);

        if (ruleChains.isEmpty()) {
            log.debug("设备 {} 没有关联的规则链", deviceKey);
            return;
        }

        // 4. 依次执行规则链
        for (RuleChain ruleChain : ruleChains) {
            try {
                ruleChainEngine.execute(ruleChain, context);
                log.debug("规则链执行完成: {} - {}", ruleChain.getName(), context.getTraces());
            } catch (Exception e) {
                log.error("规则链执行失败: {} - {}", ruleChain.getName(), e.getMessage(), e);
            }
        }
    }

    /**
     * 处理设备上线事件
     */
    public void processOnline(String deviceKey) {
        processOnlineOffline(deviceKey, true);
    }

    /**
     * 处理设备下线事件
     */
    public void processOffline(String deviceKey) {
        processOnlineOffline(deviceKey, false);
    }

    /**
     * 处理设备上下线事件
     */
    private void processOnlineOffline(String deviceKey, boolean online) {
        Device device = deviceService.getByDeviceKey(deviceKey).orElse(null);
        if (device == null) {
            log.warn("设备不存在: {}", deviceKey);
            return;
        }

        // 构建上下文
        RuleContext context = new RuleContext();
        context.setDeviceKey(device.getDeviceKey());
        context.setDeviceId(device.getId());
        context.setDeviceName(device.getDeviceName());
        context.setEventTime(LocalDateTime.now());
        context.setMessageType(online ? RuleContext.MessageType.ONLINE : RuleContext.MessageType.OFFLINE);

        // 获取规则链并执行
        List<RuleChain> ruleChains = ruleChainService.getByDeviceKey(deviceKey);
        for (RuleChain ruleChain : ruleChains) {
            try {
                ruleChainEngine.execute(ruleChain, context);
                log.debug("规则链执行完成({}): {} - {}", online ? "上线" : "下线", ruleChain.getName(), context.getTraces());
            } catch (Exception e) {
                log.error("规则链执行失败: {} - {}", ruleChain.getName(), e.getMessage(), e);
            }
        }
    }

    private RuleContext buildContext(DecodeResult decodeResult, Device device, TslModel tslModel) {
        RuleContext context = new RuleContext();
        context.setDeviceKey(device.getDeviceKey());
        context.setDeviceId(device.getId());
        context.setDeviceName(device.getDeviceName());
        context.setTslModel(tslModel);
        context.setDecodeResult(decodeResult);
        context.setEventTime(LocalDateTime.now());

        // 判断消息类型
        DeviceEventData eventData = decodeResult.getEventData();
        if (eventData != null) {
            context.setMessageType(RuleContext.MessageType.EVENT);
        } else if (decodeResult.getServiceResData() != null) {
            context.setMessageType(RuleContext.MessageType.SERVICE_RES);
        } else {
            context.setMessageType(RuleContext.MessageType.PROPERTY);
        }

        return context;
    }
}
