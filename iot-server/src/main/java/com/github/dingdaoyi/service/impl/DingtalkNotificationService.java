package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.service.push.WebhookNotificationService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 钉钉机器人推送
 * @author dingyunwei
 */
@Service
public class DingtalkNotificationService extends WebhookNotificationService {

    @Override
    protected NotifyType platform() {
        return NotifyType.DINGTALK;
    }

    @Override
    protected Map<String, Object> buildPayload(String title, String content, Map<String, Object> model) {
        String md = String.format("### %s\n\n%s", title, content);
        return Map.of(
            "msgtype", "markdown",
            "markdown", Map.of("title", title, "text", md)
        );
    }
}
