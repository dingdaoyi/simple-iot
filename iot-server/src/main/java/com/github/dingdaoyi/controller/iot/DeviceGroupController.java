package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.DeviceGroup;
import com.github.dingdaoyi.service.DeviceGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/device-group")
@Tag(name = "设备分组")
@AllArgsConstructor
public class DeviceGroupController {

    private final DeviceGroupService deviceGroupService;

    @GetMapping("/tree")
    @Operation(summary = "分组树")
    public BaseResult<List<DeviceGroup>> tree() {
        return BaseResult.success(deviceGroupService.tree());
    }

    @GetMapping("{id}/children")
    @Operation(summary = "子分组")
    public BaseResult<List<DeviceGroup>> children(@PathVariable Integer id) {
        return BaseResult.success(deviceGroupService.children(id));
    }

    @PostMapping
    @Operation(summary = "新增分组")
    public BaseResult<Boolean> save(@RequestBody DeviceGroup group) {
        return BaseResult.success(deviceGroupService.save(group));
    }

    @PutMapping
    @Operation(summary = "修改分组")
    public BaseResult<Boolean> update(@RequestBody DeviceGroup group) {
        return BaseResult.success(deviceGroupService.updateById(group));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除分组")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        return BaseResult.success(deviceGroupService.removeById(id));
    }

    @PostMapping("{groupId}/devices")
    @Operation(summary = "分配设备到分组")
    public BaseResult<Boolean> assignDevices(@PathVariable Integer groupId,
                                              @RequestBody List<Integer> deviceIds) {
        deviceGroupService.assignDevices(groupId, deviceIds);
        return BaseResult.success(true);
    }

    @DeleteMapping("{groupId}/devices")
    @Operation(summary = "从分组移除设备")
    public BaseResult<Boolean> removeDevices(@PathVariable Integer groupId,
                                              @RequestBody List<Integer> deviceIds) {
        deviceGroupService.removeDevices(groupId, deviceIds);
        return BaseResult.success(true);
    }

    @GetMapping("{groupId}/devices")
    @Operation(summary = "分组下设备列表")
    public BaseResult<List<Device>> listDevices(@PathVariable Integer groupId) {
        return BaseResult.success(deviceGroupService.listDevices(groupId));
    }
}
