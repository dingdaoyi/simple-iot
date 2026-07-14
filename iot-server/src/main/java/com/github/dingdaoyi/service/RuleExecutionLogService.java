package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.RuleExecutionLog;
import com.github.dingdaoyi.mapper.RuleExecutionLogMapper;
import com.github.dingdaoyi.rule.RuleContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 规则链执行日志服务
 * @author dingdaoyi
 */
@Slf4j
@Service
public class RuleExecutionLogService extends ServiceImpl<RuleExecutionLogMapper, RuleExecutionLog> {

    /**
     * 异步保存执行日志
     */
    @Async
    public void saveLogAsync(Integer ruleChainId, RuleContext context, long durationMs) {
        try {
            RuleExecutionLog logEntry = new RuleExecutionLog();
            logEntry.setRuleChainId(ruleChainId);
            logEntry.setDeviceKey(context.getDeviceKey());
            logEntry.setDeviceId(context.getDeviceId());
            logEntry.setDeviceName(context.getDeviceName());
            logEntry.setMessageType(context.getMessageType() != null ? context.getMessageType().name() : null);
            logEntry.setEventTime(context.getEventTime());
            logEntry.setDurationMs(durationMs);
            logEntry.setTraceCount(context.getTraces().size());

            boolean anyFailure = context.getTraces().stream()
                .anyMatch(t -> "FAILED".equals(t.getStatus()) || "ERROR".equals(t.getStatus()));
            logEntry.setSuccess(!anyFailure);

            // 序列化轨迹
            List<Map<String, Object>> traceList = new ArrayList<>();
            for (RuleContext.ExecutionTrace trace : context.getTraces()) {
                Map<String, Object> traceMap = new LinkedHashMap<>();
                traceMap.put("nodeId", trace.getNodeId());
                traceMap.put("nodeName", trace.getNodeName());
                traceMap.put("nodeType", trace.getNodeType());
                traceMap.put("connectionType", trace.getConnectionType());
                traceMap.put("status", trace.getStatus());
                traceMap.put("detail", trace.getDetail());
                traceMap.put("error", trace.getError());
                traceMap.put("durationMs", trace.getDurationMs());
                traceMap.put("timestamp", trace.getTimestamp() != null ? trace.getTimestamp().toString() : null);
                traceMap.put("input", trace.getInput());
                traceMap.put("output", trace.getOutput());
                traceList.add(traceMap);
            }
            logEntry.setTraces(traceList);

            // 保存输入快照（用于回放）
            Map<String, Object> inputSnapshot = new LinkedHashMap<>();
            inputSnapshot.put("deviceKey", context.getDeviceKey());
            inputSnapshot.put("deviceId", context.getDeviceId());
            inputSnapshot.put("deviceName", context.getDeviceName());
            inputSnapshot.put("messageType", context.getMessageType() != null ? context.getMessageType().name() : null);
            inputSnapshot.put("eventTime", context.getEventTime() != null ? context.getEventTime().toString() : null);
            inputSnapshot.put("properties", context.getAllProperties().stream()
                .collect(java.util.stream.Collectors.toMap(
                    d -> d.getIdentifier(), d -> d.getValue(), (l, r) -> r, LinkedHashMap::new)));
            logEntry.setInputSnapshot(inputSnapshot);

            save(logEntry);
        } catch (Exception e) {
            log.error("保存执行日志失败", e);
        }
    }

    /**
     * 分页查询执行日志
     */
    public PageResult<RuleExecutionLog> page(int page, int size, Integer ruleChainId, String deviceKey) {
        Page<RuleExecutionLog> p = new Page<>(page, size);
        LambdaQueryWrapper<RuleExecutionLog> wrapper = new LambdaQueryWrapper<RuleExecutionLog>()
            .eq(ruleChainId != null, RuleExecutionLog::getRuleChainId, ruleChainId)
            .eq(deviceKey != null, RuleExecutionLog::getDeviceKey, deviceKey)
            .orderByDesc(RuleExecutionLog::getCreateTime);
        Page<RuleExecutionLog> result = page(p, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    /**
     * 清理指定天数前的日志
     */
    public int cleanOldLogs(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<RuleExecutionLog> wrapper = new LambdaQueryWrapper<RuleExecutionLog>()
            .lt(RuleExecutionLog::getCreateTime, cutoff);
        return baseMapper.delete(wrapper);
    }
}
