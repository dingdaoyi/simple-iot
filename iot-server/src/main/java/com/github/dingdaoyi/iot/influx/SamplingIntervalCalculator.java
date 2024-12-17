package com.github.dingdaoyi.iot.influx;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author dingyunwei
 */
public class SamplingIntervalCalculator {

    /**
     * 计算采样间隔
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param numPoints 图表点位数
     * @return 采样间隔 (例如: "5m", "1h")
     */
    public static String calculateSamplingInterval(LocalDateTime startTime, LocalDateTime endTime, int numPoints) {
        // 计算时间跨度
        Duration timeSpan = Duration.between(startTime, endTime);

        // 计算每个点的时间间隔
        long secondsPerPoint = timeSpan.getSeconds() / numPoints;

        // 根据间隔秒数确定合适的时间单位
        return getTimeInterval(secondsPerPoint);
    }

    /**
     * 将秒数转换为适当的时间单位（如 "5m", "1h"）
     * @param seconds 时间间隔的秒数
     * @return 适合Flux查询的时间单位
     */
    private static String getTimeInterval(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "h";
        } else {
            return (seconds / 86400) + "d";
        }
    }

    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.now().minus(Duration.ofDays(2));
        LocalDateTime endTime = LocalDateTime.now();
        int numPoints = 10;
        // 计算采样间隔
        String samplingInterval = calculateSamplingInterval(startTime, endTime, numPoints);
        System.out.println("Calculated sampling interval: " + samplingInterval);
    }
}