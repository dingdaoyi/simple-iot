package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.model.enu.NotifyType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ImPushPayloadTest {

    @Test
    void dingtalkPayloadIsMarkdownWithTitle() {
        DingtalkNotificationService svc = new DingtalkNotificationService();
        Map<String, Object> payload = svc.buildPayload("告警", "温度过高", Map.of());

        assertThat(payload).containsEntry("msgtype", "markdown");
        @SuppressWarnings("unchecked")
        Map<String, Object> md = (Map<String, Object>) payload.get("markdown");
        assertThat(md).containsEntry("title", "告警");
        assertThat((String) md.get("text")).contains("告警").contains("温度过高");
    }

    @Test
    void wecomPayloadIsMarkdownWithContent() {
        WecomNotificationService svc = new WecomNotificationService();
        Map<String, Object> payload = svc.buildPayload("告警", "温度过高", Map.of());

        assertThat(payload).containsEntry("msgtype", "markdown");
        @SuppressWarnings("unchecked")
        Map<String, Object> md = (Map<String, Object>) payload.get("markdown");
        assertThat((String) md.get("content")).contains("告警").contains("温度过高");
    }

    @Test
    void feishuPayloadIsInteractiveCard() {
        FeishuNotificationService svc = new FeishuNotificationService();
        Map<String, Object> payload = svc.buildPayload("告警", "温度过高", Map.of());

        assertThat(payload).containsEntry("msg_type", "interactive");
        assertThat(payload).containsKey("card");
        @SuppressWarnings("unchecked")
        Map<String, Object> card = (Map<String, Object>) payload.get("card");
        @SuppressWarnings("unchecked")
        Map<String, Object> header = (Map<String, Object>) card.get("header");
        @SuppressWarnings("unchecked")
        Map<String, Object> headerTitle = (Map<String, Object>) header.get("title");
        assertThat(headerTitle).containsEntry("content", "告警");
    }

    @Test
    void platformTypesAreCorrect() {
        assertThat(new DingtalkNotificationService().getNotifyType()).isEqualTo(NotifyType.DINGTALK);
        assertThat(new WecomNotificationService().getNotifyType()).isEqualTo(NotifyType.WECOM);
        assertThat(new FeishuNotificationService().getNotifyType()).isEqualTo(NotifyType.FEISHU);
    }
}
