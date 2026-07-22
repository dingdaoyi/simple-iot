package com.github.dingdaoyi.controller.iot;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.DeviceShadow;
import com.github.dingdaoyi.service.DeviceShadowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * 设备影子
 * @author dingyunwei
 */
@RestController
@RequestMapping("/device/shadow")
@Tag(name = "设备影子")
public class DeviceShadowController {

    @Resource
    private DeviceShadowService deviceShadowService;

    @GetMapping("/{deviceId}")
    @Operation(summary = "获取设备影子")
    public BaseResult<DeviceShadow> get(@PathVariable Integer deviceId) {
        Optional<DeviceShadow> shadow = deviceShadowService.getByDeviceId(deviceId);
        return BaseResult.success(shadow.orElse(null));
    }

    @PostMapping("/{deviceId}/desired")
    @Operation(summary = "更新期望状态")
    public BaseResult<DeviceShadow> updateDesired(@PathVariable Integer deviceId, @RequestBody Map<String, Object> desired) {
        JSONObject json = new JSONObject(desired);
        return BaseResult.success(deviceShadowService.updateDesired(deviceId, json));
    }

    @DeleteMapping("/{deviceId}/desired")
    @Operation(summary = "清除期望状态")
    public BaseResult<Void> clearDesired(@PathVariable Integer deviceId) {
        deviceShadowService.clearDesired(deviceId);
        return BaseResult.success();
    }
}
