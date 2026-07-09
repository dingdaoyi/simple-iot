package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.enu.AlarmSeverity;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.config.AlarmCreateConfig;
import com.github.dingdaoyi.service.AlarmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmCreateNodeTest {

    @Mock
    private AlarmService alarmService;

    @InjectMocks
    private AlarmCreateNode node;

    private RuleContext buildContext() {
        RuleContext ctx = new RuleContext();
        ctx.setDeviceKey("dev-1");
        ctx.setDeviceName("测试设备");
        ctx.setDeviceId(1);
        ctx.setRuleChainId(100);

        DeviceData temp = new DeviceData();
        temp.setIdentifier("temperature");
        temp.setValue(42.0);
        DecodeResult dr = new DecodeResult();
        dr.setDataList(List.of(temp));
        ctx.setDecodeResult(dr);
        return ctx;
    }

    @Test
    void getNodeTypeReturnsAlarmCreate() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.ALARM_CREATE);
    }

    @Test
    void failsWhenAlarmTypeIsNull() {
        AlarmCreateConfig config = new AlarmCreateConfig(); // alarmType is null

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("告警类型不能为空");
        verifyNoInteractions(alarmService);
    }

    @Test
    void skipsWhenActiveAlarmExists() {
        when(alarmService.findActiveAlarm("dev-1", "high_temp"))
                .thenReturn(Optional.of(new Alarm()));

        AlarmCreateConfig config = new AlarmCreateConfig();
        config.setAlarmType("high_temp");
        config.setAlarmName("高温告警");

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("已存在相同类型");
        verify(alarmService, never()).save(any());
    }

    @Test
    void createsAlarmSuccessfully() {
        when(alarmService.findActiveAlarm("dev-1", "high_temp"))
                .thenReturn(Optional.empty());

        AlarmCreateConfig config = new AlarmCreateConfig();
        config.setAlarmType("high_temp");
        config.setAlarmName("高温告警");
        config.setSeverity("CRITICAL");

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("创建告警");
        assertThat(result.message()).contains("严重"); // AlarmSeverity.CRITICAL.getName() = "严重"

        ArgumentCaptor<Alarm> captor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmService).save(captor.capture());
        Alarm saved = captor.getValue();
        assertThat(saved.getAlarmType()).isEqualTo("high_temp");
        assertThat(saved.getAlarmName()).isEqualTo("高温告警");
        assertThat(saved.getSeverity()).isEqualTo(AlarmSeverity.CRITICAL);
        assertThat(saved.getDeviceKey()).isEqualTo("dev-1");
    }

    @Test
    void usesDefaultMessageWhenNotConfigured() {
        when(alarmService.findActiveAlarm("dev-1", "high_temp"))
                .thenReturn(Optional.empty());

        AlarmCreateConfig config = new AlarmCreateConfig();
        config.setAlarmType("high_temp");
        config.setAlarmName("高温告警");

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isTrue();

        ArgumentCaptor<Alarm> captor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmService).save(captor.capture());
        assertThat(captor.getValue().getMessage()).contains("测试设备").contains("high_temp");
    }

    @Test
    void usesCustomMessageTemplate() {
        when(alarmService.findActiveAlarm("dev-1", "high_temp"))
                .thenReturn(Optional.empty());

        AlarmCreateConfig config = new AlarmCreateConfig();
        config.setAlarmType("high_temp");
        config.setAlarmName("高温告警");
        config.setMessage("设备${deviceName}温度${temperature}");

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isTrue();

        ArgumentCaptor<Alarm> captor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmService).save(captor.capture());
        assertThat(captor.getValue().getMessage()).isEqualTo("设备测试设备温度42.0");
    }

    @Test
    void defaultsToWarningSeverityWhenInvalid() {
        when(alarmService.findActiveAlarm("dev-1", "high_temp"))
                .thenReturn(Optional.empty());

        AlarmCreateConfig config = new AlarmCreateConfig();
        config.setAlarmType("high_temp");
        config.setAlarmName("高温告警");
        config.setSeverity("BOGUS");

        node.execute(buildContext(), config);

        ArgumentCaptor<Alarm> captor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmService).save(captor.capture());
        assertThat(captor.getValue().getSeverity()).isEqualTo(AlarmSeverity.WARNING);
    }
}
