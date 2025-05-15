package com.github.dingdaoyi.iot.influx;

import net.dreamlu.mica.core.utils.DateUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public  class TimeUtils {
    private TimeUtils() {}
    public static LocalDateTime toLocalDateTime(Number time) {
        long influxTimestamp = time.longValue();
        long seconds = influxTimestamp / 1_000_000_000;
        long nanos = influxTimestamp % 1_000_000_000;
        Instant instant = Instant.ofEpochSecond(seconds, nanos);
        return instant.atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * influxdb时间戳转换为日期字符串
     * @param time
     * @return
     */
    public static String toDateTimeStr(Number time) {
        return   DateUtil.format(toLocalDateTime(time), DateUtil.PATTERN_DATETIME);
    }
}
