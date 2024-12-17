package com.github.dingdaoyi.controller.iot;


import com.github.dingdaoyi.model.query.DeviceDataQuery;
import com.github.dingdaoyi.proto.model.KeyValue;
import com.github.dingdaoyi.service.DeviceDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import net.dreamlu.mica.core.result.R;
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

    @GetMapping("last/{deviceKey}")
    @Operation(summary = "设备最后日志")
    public R<List<KeyValue<String, Object>>> lastPropData(@PathVariable String deviceKey) {
        List<KeyValue<String, Object>> latestData = deviceDataService.getLatestData(deviceKey);
        return R.success(latestData);
    }
    @PostMapping("metric")
    @Operation(summary = "设备指标统计")
    public R<List<KeyValue<String, Object>>> metric(@RequestBody DeviceDataQuery query) {
        List<KeyValue<String, Object>> latestData = deviceDataService.metric(query);
        return R.success(latestData);
    }
}
