package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.enu.AlarmSeverity;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import com.github.dingdaoyi.mapper.AlarmMapper;
import com.github.dingdaoyi.service.impl.AlarmServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 告警服务测试 - 守护 createAlarm/clearAlarm 逻辑
 * @author dingyunwei
 */
@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmMapper alarmMapper;

    private AlarmServiceImpl alarmService;

    @BeforeEach
    void setUp() {
        alarmService = new AlarmServiceImpl();
        ReflectionTestUtils.setField(alarmService, "baseMapper", alarmMapper);
        ReflectionTestUtils.setField(alarmService, "suppressionSeconds", 300);
    }

    @Test
    void createAlarmSetsActiveStatusAndTimestamps() {
        Alarm alarm = new Alarm();
        alarm.setAlarmType("high_temp");
        alarm.setSeverity(AlarmSeverity.MAJOR);
        alarm.setMessage("温度过高");
        alarm.setDeviceKey("sensor-001");
        when(alarmMapper.insert(any(Alarm.class))).thenReturn(1);

        Alarm result = alarmService.createAlarm(alarm);

        assertThat(result.getStatus()).isEqualTo(AlarmStatus.ACTIVE);
        assertThat(result.getStartTs()).isNotNull();
        verify(alarmMapper).insert(any(Alarm.class));
    }

    @Test
    void clearAlarmReturnsFalseWhenNotFound() {
        when(alarmMapper.selectById(999)).thenReturn(null);

        boolean result = alarmService.clearAlarm(999, "admin");

        assertThat(result).isFalse();
        verify(alarmMapper, org.mockito.Mockito.never()).updateById(any(Alarm.class));
    }

    @Test
    void clearAlarmReturnsFalseWhenAlreadyCleared() {
        Alarm alarm = new Alarm();
        alarm.setId(1);
        alarm.setStatus(AlarmStatus.CLEARED);
        when(alarmMapper.selectById(1)).thenReturn(alarm);

        boolean result = alarmService.clearAlarm(1, "admin");

        assertThat(result).isFalse();
        verify(alarmMapper, org.mockito.Mockito.never()).updateById(any(Alarm.class));
    }

    @Test
    void clearAlarmSucceedsAndSetsClearFields() {
        Alarm alarm = new Alarm();
        alarm.setId(1);
        alarm.setStatus(AlarmStatus.ACTIVE);
        alarm.setAlarmType("high_temp");
        when(alarmMapper.selectById(1)).thenReturn(alarm);
        when(alarmMapper.updateById(any(Alarm.class))).thenReturn(1);

        boolean result = alarmService.clearAlarm(1, "operator");

        assertThat(result).isTrue();
        assertThat(alarm.getStatus()).isEqualTo(AlarmStatus.CLEARED);
        assertThat(alarm.getClearBy()).isEqualTo("operator");
        assertThat(alarm.getClearTs()).isNotNull();
        assertThat(alarm.getEndTs()).isNotNull();
    }
}
