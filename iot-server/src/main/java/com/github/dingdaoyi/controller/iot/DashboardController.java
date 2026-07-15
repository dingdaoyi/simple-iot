package com.github.dingdaoyi.controller.iot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.Dashboard;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.mapper.AlarmMapper;
import com.github.dingdaoyi.mapper.DashboardMapper;
import com.github.dingdaoyi.mapper.DeviceMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "自定义仪表盘")
public class DashboardController {

    private final DashboardMapper dashboardMapper;
    private final DeviceMapper deviceMapper;
    private final AlarmMapper alarmMapper;

    @GetMapping("statistics")
    @Operation(summary = "首页统计概览")
    public BaseResult<Map<String, Object>> statistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 设备统计
        List<Device> devices = deviceMapper.selectList(null);
        long total = devices.size();
        long online = devices.stream().filter(d -> Boolean.TRUE.equals(d.getOnline())).count();
        long offline = total - online;
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayAdd = devices.stream()
                .filter(d -> d.getCreateTime() != null && d.getCreateTime().isAfter(todayStart))
                .count();

        stats.put("total", total);
        stats.put("online", online);
        stats.put("offline", offline);
        stats.put("todayAdd", todayAdd);

        // 告警统计
        long activeAlarms = alarmMapper.selectCount(
                new QueryWrapper<Alarm>().eq("status", "ACTIVE"));
        long todayAlarms = alarmMapper.selectCount(
                new QueryWrapper<Alarm>().ge("alarm_time", todayStart));
        stats.put("activeAlarms", activeAlarms);
        stats.put("todayAlarms", todayAlarms);

        return BaseResult.success(stats);
    }

    @GetMapping("list")
    @Operation(summary = "仪表盘列表")
    public BaseResult<List<Dashboard>> list() {
        return BaseResult.success(dashboardMapper.selectList(null));
    }

    @GetMapping("{id}")
    @Operation(summary = "仪表盘详情")
    public BaseResult<Dashboard> getById(@PathVariable Integer id) {
        return BaseResult.success(dashboardMapper.selectById(id));
    }

    @PostMapping
    @Operation(summary = "创建仪表盘")
    public BaseResult<Boolean> save(@RequestBody Dashboard dashboard) {
        return BaseResult.success(dashboardMapper.insert(dashboard) > 0);
    }

    @PutMapping
    @Operation(summary = "更新仪表盘")
    public BaseResult<Boolean> update(@RequestBody Dashboard dashboard) {
        return BaseResult.success(dashboardMapper.updateById(dashboard) > 0);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除仪表盘")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        return BaseResult.success(dashboardMapper.deleteById(id) > 0);
    }
}
