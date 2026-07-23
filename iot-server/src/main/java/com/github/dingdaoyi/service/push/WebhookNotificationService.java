package com.github.dingdaoyi.service.push;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.entity.ImPushConfig;
import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.service.ImPushConfigService;
import com.github.dingdaoyi.service.NotificationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Shared webhook push for DingTalk / WeCom / Feishu.
 * Each subclass just declares its NotifyType and payload format.
 * @author dingyunwei
 */
@Slf4j
public abstract class WebhookNotificationService implements NotificationService {

    @Resource
    protected ImPushConfigService imPushConfigService;

    protected abstract NotifyType platform();

    protected abstract Map<String, Object> buildPayload(String title, String content, Map<String, Object> model);

    @Override
    public NotifyType getNotifyType() {
        return platform();
    }

    @Override
    public void sendMessage(String receiver, String templateId, Map<String, Object> model) {
        var configOpt = imPushConfigService.getEnabledByPlatform(platform());
        if (configOpt.isEmpty()) {
            log.warn("IM推送配置不存在或未启用: {}", platform());
            return;
        }
        ImPushConfig config = configOpt.get();

        String title = (String) model.getOrDefault("title", "设备告警通知");
        String content = (String) model.getOrDefault("content", "");

        Map<String, Object> payload = buildPayload(title, content, model);
        String url = config.getWebhookUrl();

        // DingTalk sign
        if (platform() == NotifyType.DINGTALK && config.getSecret() != null && !config.getSecret().isBlank()) {
            long timestamp = System.currentTimeMillis();
            String sign = dingtalkSign(timestamp, config.getSecret());
            url += "&timestamp=" + timestamp + "&sign=" + sign;
        }

        try {
            String body = JSONUtil.toJsonStr(payload);
            String resp = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(body)
                .timeout(5000)
                .execute()
                .body();
            log.info("IM推送[{}]: resp={}", platform(), resp);
        } catch (Exception e) {
            log.error("IM推送失败[{}]: {}", platform(), e.getMessage());
        }
    }

    private String dingtalkSign(long timestamp, String secret) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(Base64.getEncoder().encodeToString(signData), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("DingTalk sign error: {}", e.getMessage());
            return "";
        }
    }
}
