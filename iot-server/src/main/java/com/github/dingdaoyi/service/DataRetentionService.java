package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.PushLog;
import com.github.dingdaoyi.mapper.AlarmMapper;
import com.github.dingdaoyi.mapper.PushLogMapper;
import com.influxdb.v3.client.InfluxDBClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
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
    private final RuleExecutionLogService ruleExecutionLogService;

    @Value("${simple.iot.retention.alarm-days:90}")
    private int alarmRetentionDays;

    @Value("${simple.iot.retention.push-log-days:30}")
    private int pushLogRetentionDays;

    @Value("${simple.iot.retention.rule-exec-log-days:30}")
    private int ruleExecLogRetentionDays;

    @Value("${simple.iot.retention.telemetry-days:180}")
    private int telemetryRetentionDays;

    @Autowired(required = false)
    @Nullable
    private InfluxDBClient influxDBClient;

    @Value("${simple.iot.influxdb.database:iot}")
    private String influxDatabase;

    @Value("${simple.iot.influxdb.prop-database:telemetry}")
    private String influxPropDatabase;

    public DataRetentionService(AlarmMapper alarmMapper, PushLogMapper pushLogMapper,
                                 RuleExecutionLogService ruleExecutionLogService) {
        this.alarmMapper = alarmMapper;
        this.pushLogMapper = pushLogMapper;
        this.ruleExecutionLogService = ruleExecutionLogService;
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Shanghai")
    public void purgeOldData() {
        purgeClearedAlarms();
        purgeOldPushLogs();
        purgeOldRuleExecLogs();
        purgeOldTelemetry();
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

    private void purgeOldRuleExecLogs() {
        int deleted = ruleExecutionLogService.cleanOldLogs(ruleExecLogRetentionDays);
        if (deleted > 0) {
            log.info("Purged {} rule execution logs older than {} days", deleted, ruleExecLogRetentionDays);
        }
    }

    // ponytail: InfluxDB v3 Java client has no execute(), use query() for DELETE. DML returns empty stream.
    // No downsampling -- just hard delete. Add CQ or downsampling task when query perf degrades.
    private void purgeOldTelemetry() {
        if (influxDBClient == null) return;
        try {
            String cutoff = java.time.Instant.now()
                .minus(java.time.Duration.ofDays(telemetryRetentionDays)).toString();
            // ponytail: pattern-match all product telemetry measurements (prefix: propDatabase + "_")
            String sql = "DELETE FROM \"" + influxPropDatabase + "_%\" WHERE time < $cutoff";
            try (var stream = influxDBClient.query(sql, java.util.Map.of("cutoff", cutoff))) {
                stream.close(); // consume to trigger execution
            }
            log.info("Purged InfluxDB telemetry older than {} days", telemetryRetentionDays);
        } catch (Exception e) {
            log.warn("InfluxDB telemetry purge failed: {}", e.getMessage());
        }
    }
}
