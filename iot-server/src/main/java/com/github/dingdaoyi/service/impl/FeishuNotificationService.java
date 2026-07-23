package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.service.push.WebhookNotificationService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 飞书机器人推送
 * @author dingyunwei
 */
@Service
public class FeishuNotificationService extends WebhookNotificationService {

    @Override
    protected NotifyType platform() {
        return NotifyType.FEISHU;
    }

    @Override
    protected Map<String, Object> buildPayload(String title, String content, Map<String, Object> model) {
        String md = String.format("**%s**\n\n%s", title, content);
        return Map.of(
            "msg_type", "interactive",
            "card", Map.of(
                "header", Map.of("title", Map.of("tag", "plain_text", "content", title)),
                "elements", new Object[]{
                    Map.of("tag", "markdown", "content", md)
                }
            )
        );
    }
}
