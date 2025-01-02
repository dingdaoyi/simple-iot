package com.github.dingdaoyi.iot.impl;

import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.IotRule;
import com.github.dingdaoyi.entity.MessageReceive;
import com.github.dingdaoyi.entity.enu.RuleType;
import com.github.dingdaoyi.iot.DataProcessor;
import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import com.github.dingdaoyi.service.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.$;
import net.dreamlu.mica.core.utils.DatePattern;
import net.dreamlu.mica.core.utils.DateUtil;
import net.dreamlu.mica.core.utils.JsonUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dingyunwei
 */
@Slf4j
@Service
public class RuleDataProcessor implements DataProcessor {
    @Resource
    private IotRuleService rulesService;

    private Map<NotifyType,NotificationService> notificationServiceMap;


    @Resource

    private MessageReceiveService messageReceiveService;

    @Resource
    private DeviceService deviceService;

    @Resource
    public void setNotificationServices(List<NotificationService> notificationServiceList) {
        this.notificationServiceMap = notificationServiceList
                .stream().collect(Collectors.toMap(NotificationService::getNotifyType,item->item));
    }
    @Override
    public void process(DecodeResult message, String deviceKey,TslModel tslModel) {
        Device device = deviceService.getByDeviceKey(deviceKey).get();
        List<IotRule> rules= rulesService.queryByDeviceKey(deviceKey);
       if ($.isEmpty(rules)) {
           return;
       }
        List<DeviceData> dataList = message.getDataList();
        Map<String, DeviceData> deviceDataMap = dataList.stream().collect(Collectors.toMap(DeviceData::identifier, item -> item));
        for (IotRule rule : rules) {
           switch (rule.getInputType()) {
               case PROP_DATA -> {
                   if (deviceDataMap.containsKey(rule.getIdentifier())) {
                       // 存在属性
                       DeviceData deviceData = deviceDataMap.get(rule.getIdentifier());
                       Object resultValue = rule.parse(deviceData.value());
                       switch (rule.getRuleType()) {
                           case FILTER -> {
                               if ((Boolean) resultValue) {
                                   log.info("命中属性过滤:{}|{}",deviceKey, JsonUtil.toJson(deviceData));
                                   tslModel.getProperties().stream().filter(item -> item.getIdentifier().equals(rule.getIdentifier()))
                                           .findAny().ifPresent(tslProperty ->doFilterHitRule(deviceData,deviceKey,rule,device,tslProperty) );
                               }
                           }
                           case FUNCTION -> {
                               tslModel.getProperties().stream().filter(item -> item.getIdentifier().equals(rule.getIdentifier()))
                                       .findAny().ifPresent(tslProperty ->doCoverHitRule(deviceData,deviceKey,rule,device,tslProperty,resultValue) );
                           }

                       }
                   }

               }
           }
       }
    }

    private void doCoverHitRule(DeviceData deviceData, String deviceKey, IotRule rule, Device device, TslProperty tslProperty,Object value) {
        //TODO  属性转换后会干啥?

    }

    private void doFilterHitRule(DeviceData deviceData, String deviceKey, IotRule rule, Device device,TslProperty tslProperty) {
        switch (rule.getTargetType()) {
            case MESSAGE ->{
                MessageReceive messageReceive = messageReceiveService.getById(rule.getTargetId());
                if (messageReceive != null) {
                    NotifyType notifyType = messageReceive.getNotifyType();
                    String templateId = getMessageTemplateId(notifyType);
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("deviceKey", deviceKey);
                    params.put("deviceName", device.getDeviceName());
                    params.put("eventTime", DateUtil.format(Instant.now(),DatePattern.NORM_DATETIME_PATTERN));
                    params.put("eventTypeName", rule.getName());
                    HashMap<String, String> eventContent = new HashMap<>();
                    eventContent.put(tslProperty.getName(), deviceData.value() + tslProperty.getUnit());
                    eventContent.put("规则描述",rule.getRemark());
                    params.put("eventContent",eventContent);
                    notificationServiceMap.get(notifyType)
                            .sendMessage(messageReceive.getReceiver(),
                                    templateId,params);
                }
            }

        }
    }

    private String getMessageTemplateId(NotifyType notifyType) {
        return switch (notifyType) {
            case EMAIL -> "event_notification.ftl";
            default -> "";
        };
    }
}
