package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.auth.AuthUtil;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.enu.AlarmSeverity;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import com.github.dingdaoyi.model.query.AlarmPageQuery;
import com.github.dingdaoyi.model.vo.AlarmPageVo;
import com.github.dingdaoyi.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 告警控制器
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/alarm")
@Tag(name = "告警管理")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("{id}")
    @Operation(summary = "告警详情")
    public BaseResult<Alarm> details(@PathVariable Integer id) {
        Alarm alarm = alarmService.getById(id);
        if (alarm == null) {
            return BaseResult.fail(ResultCode.NOT_FOUND.getCode(), "告警不存在");
        }
        return BaseResult.success(alarm);
    }

    @PostMapping("page")
    @Operation(summary = "告警分页列表")
    public PageResult<AlarmPageVo> page(@RequestBody AlarmPageQuery query) {
        return alarmService.pageByQuery(query);
    }

    @PutMapping("{id}/acknowledge")
    @Operation(summary = "确认告警")
    public BaseResult<Boolean> acknowledge(@PathVariable Integer id) {
        Alarm alarm = alarmService.getById(id);
        if (alarm == null) {
            return BaseResult.fail(ResultCode.NOT_FOUND.getCode(), "告警不存在");
        }
        alarm.setStatus(AlarmStatus.ACKNOWLEDGED);
        return BaseResult.success(alarmService.updateById(alarm));
    }

    @PutMapping("{id}/clear")
    @Operation(summary = "清除告警")
    public BaseResult<Boolean> clear(@PathVariable Integer id) {
        // 使用 AuthUtil 获取当前登录用户名
        String clearBy = AuthUtil.getUsername();
        return BaseResult.success(alarmService.clearAlarm(id, clearBy));
    }

    @GetMapping("statistics")
    @Operation(summary = "告警统计")
    public BaseResult<Map<String, Object>> statistics() {
        long activeCount = alarmService.countActiveAlarms();
        return BaseResult.success(Map.of(
            "activeCount", activeCount,
            "severityOptions", Arrays.stream(AlarmSeverity.values())
                .map(s -> Map.of("value", s.name(), "label", s.getName(), "color", s.getColor()))
                .toList(),
            "statusOptions", Arrays.stream(AlarmStatus.values())
                .map(s -> Map.of("value", s.name(), "label", s.getName()))
                .toList()
        ));
    }
}
