# 数据保留策略

Simple IoT 定期清理历史数据，防止磁盘膨胀。

## 清理任务

每天凌晨 3:00（Asia/Shanghai）自动执行，由 `DataRetentionService` 调度。

### PostgreSQL 数据

| 数据 | 默认保留 | 配置项 |
|------|---------|--------|
| 已清除告警 | 90 天 | `simple.iot.retention.alarm-days` |
| 推送日志 | 30 天 | `simple.iot.retention.push-log-days` |
| 规则执行日志 | 30 天 | `simple.iot.retention.rule-exec-log-days` |

### InfluxDB 遥测数据

| 数据 | 默认保留 | 配置项 |
|------|---------|--------|
| 设备遥测（属性） | 180 天 | `simple.iot.retention.telemetry-days` |

> ponytail: 硬删除，无降采样。查询性能下降时再加 CQ 或降采样任务。

## 配置示例

```yaml
simple:
  iot:
    retention:
      alarm-days: 90
      push-log-days: 30
      rule-exec-log-days: 30
      telemetry-days: 180
```

## 手动触发

清理任务基于 Spring `@Scheduled`，可通过管理接口或重启服务触发下一次执行。
