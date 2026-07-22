package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import com.github.dingdaoyi.mapper.AlarmMapper;
import com.github.dingdaoyi.service.impl.AlarmServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlarmSuppressionTest {

    @Mock
    private AlarmMapper alarmMapper;

    private AlarmServiceImpl alarmService;

    @BeforeEach
    void setUp() {
        alarmService = new AlarmServiceImpl();
        // baseMapper is a protected field in MyBatis-Plus ServiceImpl
        ReflectionTestUtils.setField(alarmService, "baseMapper", alarmMapper);
        ReflectionTestUtils.setField(alarmService, "suppressionSeconds", 300);
    }

    @Test
    void findActiveAlarmIncludesRecentlyClearedInSuppressionWindow() {
        Alarm recentlyCleared = new Alarm();
        recentlyCleared.setStatus(AlarmStatus.CLEARED);
        recentlyCleared.setAlarmType("high_temp");

        when(alarmMapper.findActiveAlarm(anyString(), anyString(), anyInt()))
                .thenReturn(Optional.of(recentlyCleared));

        Optional<Alarm> result = alarmService.findActiveAlarm("dev-1", "high_temp");

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(AlarmStatus.CLEARED);
    }

    @Test
    void findActiveAlarmReturnsEmptyWhenNoActiveOrRecentCleared() {
        when(alarmMapper.findActiveAlarm(anyString(), anyString(), anyInt()))
                .thenReturn(Optional.empty());

        Optional<Alarm> result = alarmService.findActiveAlarm("dev-1", "high_temp");
        assertThat(result).isEmpty();
    }
}
