package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dingyunwei
 */
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "大屏统计")
public class DashboardController {
    @Resource
    private DeviceService deviceService;

    @GetMapping("/statistics")
    @Operation(summary = "首页统计数据")
    public BaseResult<Map<String, Object>> statistics() {
        List<Device> all = deviceService.list(null, null, null);
        int total = all.size();
        int online = (int) all.stream().filter(d -> Boolean.TRUE.equals(d.getOnline())).count();
        int offline = total - online;
        int todayAdd = (int) all.stream().filter(d -> d.getCreateTime()!=null && d.getCreateTime().toLocalDate().equals(java.time.LocalDate.now())).count();

        // 系统资源
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = -1;
        double memUsage = -1;
        double diskUsage = -1;
        try {
            com.sun.management.OperatingSystemMXBean sunOs = (com.sun.management.OperatingSystemMXBean) os;
            cpuLoad = sunOs.getCpuLoad() * 100;
            long totalMem = sunOs.getTotalMemorySize();
            long freeMem = sunOs.getFreeMemorySize();
            memUsage = (double) (totalMem - freeMem) / totalMem * 100;
            java.io.File root = new java.io.File("/");
            long totalDisk = root.getTotalSpace();
            long freeDisk = root.getFreeSpace();
            diskUsage = (double) (totalDisk - freeDisk) / totalDisk * 100;
        } catch (Exception ignored) {}

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("online", online);
        map.put("offline", offline);
        map.put("todayAdd", todayAdd);
        map.put("cpuUsage", cpuLoad < 0 ? 0 : (int) cpuLoad);
        map.put("memoryUsage", memUsage < 0 ? 0 : (int) memUsage);
        map.put("diskUsage", diskUsage < 0 ? 0 : (int) diskUsage);
        return BaseResult.success(map);
    }
}
