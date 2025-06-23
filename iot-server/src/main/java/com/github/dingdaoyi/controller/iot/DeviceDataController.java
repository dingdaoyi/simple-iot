package com.github.dingdaoyi.controller.iot;


import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.model.query.DeviceDataQuery;
import com.github.dingdaoyi.model.query.DeviceEventDataVo;
import com.github.dingdaoyi.proto.model.KeyValue;
import com.github.dingdaoyi.service.DeviceDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/device/data")
@Tag(name = "设备日志")
public class DeviceDataController {

    @Resource
    private DeviceDataService deviceDataService;

    @GetMapping("property/last/{deviceKey}")
    @Operation(summary = "设备最后日志")
    public BaseResult<List<KeyValue<String, Object>>> lastPropData(@PathVariable String deviceKey) {
        List<KeyValue<String, Object>> latestData = deviceDataService.getLatestData(deviceKey);
        return BaseResult.success(latestData);
    }
    @PostMapping("property/metric")
    @Operation(summary = "设备指标统计")
    public BaseResult<List<KeyValue<String, Object>>> metric(@RequestBody DeviceDataQuery query) {
        List<KeyValue<String, Object>> latestData = deviceDataService.metric(query);
        return BaseResult.success(latestData);
    }

    @PostMapping("event/logs")
    @Operation(summary = "设备日志数据")
    public BaseResult<List<DeviceEventDataVo>> eventLog(@RequestBody DeviceDataQuery query) {
        List<DeviceEventDataVo> deviceEventDataVos = deviceDataService.eventLogs(query);
        return BaseResult.success(deviceEventDataVos);
    }
}
