package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.DeviceTag;
import com.github.dingdaoyi.service.DeviceTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/device-tag")
@Tag(name = "设备标签")
@AllArgsConstructor
public class DeviceTagController {

    private final DeviceTagService deviceTagService;

    @GetMapping("/all")
    @Operation(summary = "所有标签")
    public BaseResult<List<DeviceTag>> all() {
        return BaseResult.success(deviceTagService.all());
    }

    @PostMapping
    @Operation(summary = "新增标签")
    public BaseResult<Boolean> save(@RequestBody DeviceTag tag) {
        return BaseResult.success(deviceTagService.save(tag));
    }

    @PutMapping
    @Operation(summary = "修改标签")
    public BaseResult<Boolean> update(@RequestBody DeviceTag tag) {
        return BaseResult.success(deviceTagService.updateById(tag));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除标签")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        return BaseResult.success(deviceTagService.removeById(id));
    }

    @PostMapping("{tagId}/devices")
    @Operation(summary = "给设备打标签")
    public BaseResult<Boolean> tagDevices(@PathVariable Integer tagId,
                                           @RequestBody List<Integer> deviceIds) {
        deviceTagService.tagDevices(tagId, deviceIds);
        return BaseResult.success(true);
    }

    @DeleteMapping("{tagId}/devices")
    @Operation(summary = "移除设备标签")
    public BaseResult<Boolean> untagDevices(@PathVariable Integer tagId,
                                             @RequestBody List<Integer> deviceIds) {
        deviceTagService.untagDevices(tagId, deviceIds);
        return BaseResult.success(true);
    }

    @GetMapping("{tagId}/devices")
    @Operation(summary = "标签下设备列表")
    public BaseResult<List<Device>> listDevices(@PathVariable Integer tagId) {
        return BaseResult.success(deviceTagService.listDevicesByTagId(tagId));
    }

    @GetMapping("/device/{deviceId}")
    @Operation(summary = "设备的标签列表")
    public BaseResult<List<DeviceTag>> listByDevice(@PathVariable Integer deviceId) {
        return BaseResult.success(deviceTagService.listByDeviceId(deviceId));
    }
}
