package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.MessageReceive;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.config.OutputMessageConfig;
import com.github.dingdaoyi.service.MessageReceiveService;
import com.github.dingdaoyi.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageOutputNodeTest {

    @Mock
    private MessageReceiveService messageReceiveService;
    @Mock
    private NotificationService smsNotificationService;

    @InjectMocks
    private MessageOutputNode node;

    @BeforeEach
    void setUp() {
        when(smsNotificationService.getNotifyType()).thenReturn(NotifyType.SMS);
        // Manually inject services since @InjectMocks only handles single-type fields
        // ponytail: init() needs the list, inject + call PostConstruct by hand
        try {
            var field = MessageOutputNode.class.getDeclaredField("notificationServices");
            field.setAccessible(true);
            field.set(node, List.of(smsNotificationService));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        node.init();
    }

    private RuleContext buildContext() {
        RuleContext ctx = new RuleContext();
        ctx.setDeviceKey("dev-1");
        ctx.setDeviceName("测试设备");
        ctx.setDeviceId(1);
        return ctx;
    }

    private MessageReceive buildMessageReceive() {
        MessageReceive mr = new MessageReceive();
        mr.setId(10);
        mr.setName("告警通知组");
        mr.setNotifyType(NotifyType.SMS);
        mr.setReceiver("+8613800138000");
        return mr;
    }

    @Test
    void getNodeTypeReturnsOutputMessage() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.OUTPUT_MESSAGE);
    }

    @Test
    void failsWhenMessageReceiveIdIsNull() {
        OutputMessageConfig config = new OutputMessageConfig(); // messageReceiveId is null

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("消息配置ID不能为空");
    }

    @Test
    void failsWhenMessageReceiveNotFound() {
        when(messageReceiveService.getById(10)).thenReturn(null);
        OutputMessageConfig config = new OutputMessageConfig();
        config.setMessageReceiveId(10);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("消息配置不存在");
    }

    @Test
    void sendsNotificationSuccessfully() {
        MessageReceive mr = buildMessageReceive();
        when(messageReceiveService.getById(10)).thenReturn(mr);

        OutputMessageConfig config = new OutputMessageConfig();
        config.setMessageReceiveId(10);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("告警通知组");
        assertThat(result.message()).contains("SMS");
        verify(smsNotificationService).sendMessage(eq("+8613800138000"), eq("alarm_sms"), anyMap());
    }

    @Test
    void usesCustomTitleAndContent() {
        MessageReceive mr = buildMessageReceive();
        when(messageReceiveService.getById(10)).thenReturn(mr);

        OutputMessageConfig config = new OutputMessageConfig();
        config.setMessageReceiveId(10);
        config.setTitle("设备${deviceName}告警");
        config.setContent("温度过高: ${temperature}");

        node.execute(buildContext(), config);

        verify(smsNotificationService).sendMessage(
                eq("+8613800138000"),
                eq("alarm_sms"),
                argThat(params ->
                        "设备测试设备告警".equals(params.get("title")) &&
                                "温度过高: ".equals(params.get("content")) // no temperature in context
                ));
    }

    @Test
    void failsWhenNotifyTypeNotSupported() {
        MessageReceive mr = buildMessageReceive();
        mr.setNotifyType(NotifyType.EMAIL); // no EMAIL notification service in the list
        when(messageReceiveService.getById(10)).thenReturn(mr);

        OutputMessageConfig config = new OutputMessageConfig();
        config.setMessageReceiveId(10);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("不支持的通知类型");
    }
}
