package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import com.github.dingdaoyi.mapper.AlarmMapper;
import com.github.dingdaoyi.model.query.AlarmPageQuery;
import com.github.dingdaoyi.model.vo.AlarmPageVo;
import com.github.dingdaoyi.service.AlarmService;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public Optional<Alarm> findActiveAlarm(String deviceKey, String alarmType) {
        return baseMapper.findActiveAlarm(deviceKey, alarmType);
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
}
