package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.enu.AlarmSeverity;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import com.github.dingdaoyi.mapper.AlarmMapper;
import com.github.dingdaoyi.model.query.AlarmPageQuery;
import com.github.dingdaoyi.model.vo.AlarmPageVo;
import com.github.dingdaoyi.service.AlarmService;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 告警服务实现
 * @author dingyunwei
 */
@Slf4j
@Service
public class AlarmServiceImpl extends ServiceImpl<AlarmMapper, Alarm> implements AlarmService {

    @Override
    public PageResult<AlarmPageVo> pageByQuery(AlarmPageQuery query) {
        Page<AlarmPageVo> page = PageHelper.page(query);
        Page<AlarmPageVo> result = baseMapper.pageByQuery(page, query);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Alarm createAlarm(Alarm alarm) {
        alarm.setStatus(AlarmStatus.ACTIVE);
        alarm.setStartTs(LocalDateTime.now());
        save(alarm);
        log.info("创建告警: {} - {}", alarm.getAlarmType(), alarm.getMessage());
        return alarm;
    }

    @Override
    public boolean clearAlarm(Integer alarmId, String clearBy) {
        Alarm alarm = getById(alarmId);
        if (alarm == null || alarm.getStatus() == AlarmStatus.CLEARED) {
            return false;
        }

        alarm.setStatus(AlarmStatus.CLEARED);
        alarm.setClearTs(LocalDateTime.now());
        alarm.setEndTs(LocalDateTime.now());
        alarm.setClearBy(clearBy);

        boolean updated = updateById(alarm);
        if (updated) {
            log.info("清除告警: {} - {}", alarm.getAlarmType(), alarm.getMessage());
        }
        return updated;
    }

    @Value("${simple.iot.alarm.suppression-seconds:300}")
    private int suppressionSeconds;

    @Override
    public Optional<Alarm> findActiveAlarm(String deviceKey, String alarmType) {
        return baseMapper.findActiveAlarm(deviceKey, alarmType, suppressionSeconds);
    }

    @Override
    public long countActiveAlarms() {
        return count(Wrappers.<Alarm>lambdaQuery()
            .eq(Alarm::getStatus, AlarmStatus.ACTIVE));
    }

    @Override
    public int clearByDeviceKeyAndType(String deviceKey, String alarmType) {
        return baseMapper.update(Wrappers.<Alarm>lambdaUpdate()
            .eq(Alarm::getDeviceKey, deviceKey)
            .eq(Alarm::getAlarmType, alarmType)
            .eq(Alarm::getStatus, AlarmStatus.ACTIVE)
            .set(Alarm::getStatus, AlarmStatus.CLEARED)
            .set(Alarm::getClearTs, LocalDateTime.now())
            .set(Alarm::getEndTs, LocalDateTime.now())
            .set(Alarm::getClearBy, "rule_chain"));
    }

    @Override
    public int clearAllByDeviceKey(String deviceKey) {
        return baseMapper.update(Wrappers.<Alarm>lambdaUpdate()
            .eq(Alarm::getDeviceKey, deviceKey)
            .eq(Alarm::getStatus, AlarmStatus.ACTIVE)
            .set(Alarm::getStatus, AlarmStatus.CLEARED)
            .set(Alarm::getClearTs, LocalDateTime.now())
            .set(Alarm::getEndTs, LocalDateTime.now())
            .set(Alarm::getClearBy, "rule_chain"));
    }

    @Value("${simple.iot.alarm.escalation-minutes:30}")
    private int escalationMinutes;

    /**
     * 定时升级：WARNING/MINOR 级活动告警超过 escalationMinutes 未清除 -> 升级为 CRITICAL
     * ponytail: global scheduled scan, per-alarm scheduler if precision matters
     */
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 */5 * * * *", zone = "Asia/Shanghai")
    public void escalateStaleAlarms() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(escalationMinutes);
        int escalated = baseMapper.update(Wrappers.<Alarm>lambdaUpdate()
            .eq(Alarm::getStatus, AlarmStatus.ACTIVE)
            .in(Alarm::getSeverity, AlarmSeverity.WARNING, AlarmSeverity.MINOR)
            .lt(Alarm::getStartTs, cutoff)
            .set(Alarm::getSeverity, AlarmSeverity.CRITICAL));
        if (escalated > 0) {
            log.info("告警升级: {} 条 WARNING/MINOR -> CRITICAL (超时 {} 分钟)", escalated, escalationMinutes);
        }
    }
}
