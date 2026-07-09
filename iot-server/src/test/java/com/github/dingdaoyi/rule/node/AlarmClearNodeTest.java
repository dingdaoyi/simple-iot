package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.config.AlarmClearConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.dingdaoyi.service.AlarmService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AlarmClearNodeTest {

    @Mock
    private AlarmService alarmService;

    private AlarmClearNode node;

    @BeforeEach
    void setUp() {
        node = new AlarmClearNode();
        // inject mock via reflection — ponytail: cleaner than @InjectMocks for field injection
        try {
            var field = AlarmClearNode.class.getDeclaredField("alarmService");
            field.setAccessible(true);
            field.set(node, alarmService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getNodeTypeReturnsAlarmClear() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.ALARM_CLEAR);
    }

    @Test
    void clearSpecificAlarmType() {
        RuleContext context = new RuleContext();
        context.setDeviceKey("device-1");
        AlarmClearConfig config = new AlarmClearConfig();
        config.setAlarmType("high_temp");

        var result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("high_temp");
        verify(alarmService).clearByDeviceKeyAndType("device-1", "high_temp");
    }

    @Test
    void clearAllAlarmsWhenTypeIsNull() {
        RuleContext context = new RuleContext();
        context.setDeviceKey("device-1");
        AlarmClearConfig config = new AlarmClearConfig(); // alarmType is null

        var result = node.execute(context, config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("所有告警");
        verify(alarmService).clearAllByDeviceKey("device-1");
    }

    @Test
    void clearAllAlarmsWhenConfigIsNull() {
        RuleContext context = new RuleContext();
        context.setDeviceKey("device-2");

        var result = node.execute(context, null);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("所有告警");
        verify(alarmService).clearAllByDeviceKey("device-2");
    }
}
