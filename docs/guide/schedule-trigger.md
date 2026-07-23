# Schedule Trigger Node

Simple IoT's rule engine supports a scheduled trigger node that executes rule chains on a cron schedule.

## Node Type

`INPUT_SCHEDULE` - scheduled trigger input node, belongs to the INPUT category.

## Configuration

| Parameter | Description | Example |
|-----------|-------------|---------|
| `cron` | 6-field cron expression (sec min hour day month weekday) | `0 0 23 * * ?` (daily at 23:00) |
| `deviceKey` | Device key for the trigger context (optional) | `gateway-001` |
| `timezone` | Timezone, defaults to system timezone | `Asia/Shanghai` |

## Cron Expression

Simple IoT uses Spring's 6-field cron (with seconds):

```
Sec  Min  Hour  Day  Month  Weekday
0    0    23    *    *      ?        Daily at 23:00
0    */30 *     *    *      ?        Every 30 minutes
0    0    9     ?    *      MON      Every Monday at 9:00
```

> Day and weekday cannot both be `*`; one must be `?`.

## How It Works

1. On startup, `ScheduleTriggerService` scans all enabled rule chains
2. Finds chains with `INPUT_SCHEDULE` nodes, reads their cron expressions
3. Registers scheduled tasks via `ScheduledExecutorService`
4. At trigger time, constructs a synthetic `RuleContext` and calls `RuleChainEngine.execute()`
5. Rule chain CRUD automatically re-registers scheduled tasks

## Frontend

1. Go to Rule Engine -> edit or create a rule chain
2. Drag the "Schedule Trigger" node from the left panel
3. Click the node, fill in the cron expression in the right config panel
4. Save the rule chain - the schedule takes effect immediately

## Typical Scenarios

- **Nightly check**: `0 0 23 * * ?` - check offline devices at 23:00
- **Hourly stats**: `0 0 */1 * * ?` - generate device online rate every hour
- **Weekly report**: `0 0 18 ? * FRI` - generate weekly report every Friday at 18:00
