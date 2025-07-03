package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.model.enu.SmsTemplateType;
import com.github.dingdaoyi.model.query.SmsSendWithTemplateQuery;
import com.github.dingdaoyi.service.NotificationService;
import com.github.dingdaoyi.service.SmsConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 短信通知服务
 * @author dingyunwei
 */
@Service
@Slf4j
public class SmsNotificationService implements NotificationService {
    
    @Resource
    private SmsConfigService smsConfigService;
    
    @Override
    public NotifyType getNotifyType() {
        return NotifyType.SMS;
    }
    
    @Override
    public void sendMessage(String receiver, String templateId, Map<String, Object> model) {
        SmsSendWithTemplateQuery query = new SmsSendWithTemplateQuery();
        query.setPhone(receiver);
        query.setTemplateType(SmsTemplateType.fromCode(templateId));
        query.setTemplateParams(model);
        smsConfigService.sendSmsWithTemplate(query);
    }
    
    private String buildMessage(String templateId, Map<String, Object> model) {
        // 简单的模板替换，实际项目中可以使用更复杂的模板引擎
        String message = templateId;
        if (model != null) {
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                message = message.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }
        }
        return message;
    }
}