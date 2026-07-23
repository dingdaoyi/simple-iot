# Data Retention Policy

Simple IoT periodically purges historical data to prevent disk bloat.

## Purge Schedule

Runs daily at 3:00 AM (Asia/Shanghai), scheduled by `DataRetentionService`.

### PostgreSQL Data

| Data | Default Retention | Config Key |
|------|-------------------|-----------|
| Cleared alarms | 90 days | `simple.iot.retention.alarm-days` |
| Push logs | 30 days | `simple.iot.retention.push-log-days` |
| Rule execution logs | 30 days | `simple.iot.retention.rule-exec-log-days` |

### InfluxDB Telemetry

| Data | Default Retention | Config Key |
|------|-------------------|-----------|
| Device telemetry (properties) | 180 days | `simple.iot.retention.telemetry-days` |

> ponytail: hard delete, no downsampling. Add CQ or downsampling task when query performance degrades.

## Configuration

```yaml
simple:
  iot:
    retention:
      alarm-days: 90
      push-log-days: 30
      rule-exec-log-days: 30
      telemetry-days: 180
```
