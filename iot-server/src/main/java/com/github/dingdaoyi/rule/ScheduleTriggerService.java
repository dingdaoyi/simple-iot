package com.github.dingdaoyi.rule;

import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.config.InputScheduleConfig;
import com.github.dingdaoyi.service.RuleChainService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时触发服务: 扫描规则链中的 INPUT_SCHEDULE 节点, 注册 cron 任务
 * @author dingyunwei
 */
@Slf4j
@Service
public class ScheduleTriggerService {

    @Resource
    private RuleChainService ruleChainService;

    @Resource
    private RuleChainEngine ruleChainEngine;

    // ponytail: single-thread scheduler, per-rule-chain locks if throughput matters
    private ScheduledThreadPoolExecutor scheduler;
    private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.setRemoveOnCancelPolicy(true);
        registerAll();
    }

    @PreDestroy
    public void destroy() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    /**
     * Called by RuleChainService after CRUD operations to refresh triggers.
     */
    public void registerAll() {
        // cancel existing
        tasks.values().forEach(f -> f.cancel(false));
        tasks.clear();

        List<RuleChain> chains = ruleChainService.listEnabled();
        for (RuleChain chain : chains) {
            registerChain(chain);
        }
        log.info("Schedule triggers registered: {} (from {} chains)", tasks.size(), chains.size());
    }

    private void registerChain(RuleChain chain) {
        if (chain.getConfiguration() == null || chain.getConfiguration().getNodes() == null) return;
        chain.getConfiguration().getNodes().stream()
            .filter(n -> RuleNodeType.INPUT_SCHEDULE.name().equals(n.getType()))
            .forEach(node -> {
                Object cfg = node.getConfig();
                if (!(cfg instanceof InputScheduleConfig sc)) return;
                if (sc.getCron() == null || sc.getCron().isBlank()) return;

                try {
                    // Spring CronExpression: 6 fields (sec min hour day month weekday)
                    CronExpression cron = CronExpression.parse(sc.getCron());
                    String taskKey = chain.getId() + ":" + node.getId();

                    ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
                        fireTrigger(chain, node, sc);
                    }, getInitialDelay(cron, sc.getTimezone()), 60, TimeUnit.SECONDS);
                    // ponytail: fixed 60s poll + cron check, simpler than computing next run
                    tasks.put(taskKey, future);
                    log.info("Registered schedule trigger: {} cron={}", taskKey, sc.getCron());
                } catch (Exception e) {
                    log.warn("Failed to register schedule trigger for chain {} node {}: {}",
                        chain.getId(), node.getId(), e.getMessage());
                }
            });
    }

    private void fireTrigger(RuleChain chain, RuleChain.RuleNode node, InputScheduleConfig cfg) {
        try {
            // check if cron matches current minute
            ZoneId zone = cfg.getTimezone() != null ? ZoneId.of(cfg.getTimezone()) : ZoneId.systemDefault();
            LocalDateTime now = LocalDateTime.now(zone);
            // parse cron to check if it matches this minute
            if (!matchesCron(cfg.getCron(), now)) return;

            RuleContext context = new RuleContext();
            context.setMessageType(RuleContext.MessageType.PROPERTY);
            context.setEventTime(now);
            context.setRuleChainId(chain.getId());
            if (cfg.getDeviceKey() != null) {
                context.setDeviceKey(cfg.getDeviceKey());
            }

            ruleChainEngine.execute(chain, context);
            log.debug("Schedule trigger fired: chain={}, node={}", chain.getId(), node.getId());
        } catch (Exception e) {
            log.error("Schedule trigger failed: chain={}, node={}: {}", chain.getId(), node.getId(), e.getMessage(), e);
        }
    }

    /**
     * Check if cron expression matches the given time (truncated to minute).
     */
    private boolean matchesCron(String cronExpr, LocalDateTime time) {
        try {
            CronExpression cron = CronExpression.parse(cronExpr);
            // truncate to minute (seconds=0)
            LocalDateTime truncated = time.withSecond(0).withNano(0);
            LocalDateTime match = cron.next(truncated.minusSeconds(1));
            return match != null && match.equals(truncated);
        } catch (Exception e) {
            return false;
        }
    }

    private long getInitialDelay(CronExpression cron, String timezone) {
        ZoneId zone = timezone != null ? ZoneId.of(timezone) : ZoneId.systemDefault();
        LocalDateTime now = LocalDateTime.now(zone);
        LocalDateTime next = cron.next(now);
        if (next == null) return 60;
        long delay = java.time.Duration.between(now, next).getSeconds();
        return Math.max(1, delay);
    }
}
