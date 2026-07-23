package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.service.push.WebhookNotificationService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 企业微信机器人推送
 * @author dingyunwei
 */
@Service
public class WecomNotificationService extends WebhookNotificationService {

    @Override
    protected NotifyType platform() {
        return NotifyType.WECOM;
    }

    @Override
    protected Map<String, Object> buildPayload(String title, String content, Map<String, Object> model) {
        String md = String.format("### %s\n\n%s", title, content);
        return Map.of(
            "msgtype", "markdown",
            "markdown", Map.of("content", md)
        );
    }
}
