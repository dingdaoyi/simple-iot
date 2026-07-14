package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.PushLog;
import com.github.dingdaoyi.mapper.AlarmMapper;
import com.github.dingdaoyi.mapper.PushLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Periodically purges old historical data to prevent silent disk fill-up.
 * <p>
 * ponytail: fixed 90-day retention, hardcoded schedule. Add per-table config if retention needs vary.
 */
@Service
public class DataRetentionService {

    private static final Logger log = LoggerFactory.getLogger(DataRetentionService.class);

    private final AlarmMapper alarmMapper;
    private final PushLogMapper pushLogMapper;

    @Value("${simple.iot.retention.alarm-days:90}")
    private int alarmRetentionDays;

    @Value("${simple.iot.retention.push-log-days:30}")
    private int pushLogRetentionDays;

    public DataRetentionService(AlarmMapper alarmMapper, PushLogMapper pushLogMapper) {
        this.alarmMapper = alarmMapper;
        this.pushLogMapper = pushLogMapper;
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Shanghai")
    public void purgeOldData() {
        purgeClearedAlarms();
        purgeOldPushLogs();
    }

    private void purgeClearedAlarms() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(alarmRetentionDays);
        LambdaQueryWrapper<Alarm> wrapper = new LambdaQueryWrapper<Alarm>()
                .eq(Alarm::getStatus, "CLEARED")
                .lt(Alarm::getClearTs, cutoff);
        int deleted = alarmMapper.delete(wrapper);
        if (deleted > 0) {
            log.info("Purged {} cleared alarms older than {} days", deleted, alarmRetentionDays);
        }
    }

    private void purgeOldPushLogs() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(pushLogRetentionDays);
        LambdaQueryWrapper<PushLog> wrapper = new LambdaQueryWrapper<PushLog>()
                .lt(PushLog::getCreateTime, cutoff);
        int deleted = pushLogMapper.delete(wrapper);
        if (deleted > 0) {
            log.info("Purged {} push logs older than {} days", deleted, pushLogRetentionDays);
        }
    }
}
