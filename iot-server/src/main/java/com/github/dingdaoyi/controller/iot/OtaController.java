package com.github.dingdaoyi.controller.iot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.DeviceGroupRelation;
import com.github.dingdaoyi.entity.Firmware;
import com.github.dingdaoyi.entity.OtaTask;
import com.github.dingdaoyi.mapper.DeviceGroupRelationMapper;
import com.github.dingdaoyi.mapper.DeviceMapper;
import com.github.dingdaoyi.mapper.FirmwareMapper;
import com.github.dingdaoyi.mapper.OtaTaskMapper;
import com.github.dingdaoyi.service.ServiceHandler;
import com.github.dingdaoyi.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OTA固件管理
 * ponytail: 复用已有 StorageService 上传 + ServiceHandler 下发指令
 */
@RestController
@RequestMapping("ota")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "OTA固件管理")
public class OtaController {
    private final FirmwareMapper firmwareMapper;
    private final OtaTaskMapper otaTaskMapper;
    private final DeviceMapper deviceMapper;
    private final DeviceGroupRelationMapper groupRelationMapper;
    private final StorageService storageService;
    private final ServiceHandler serviceHandler;

    // === Firmware CRUD ===

    @GetMapping("firmware")
    @Operation(summary = "固件列表")
    public BaseResult<List<Firmware>> listFirmware(
            @RequestParam(required = false) Integer productId) {
        QueryWrapper<Firmware> qw = new QueryWrapper<>();
        if (productId != null) qw.eq("product_id", productId);
        qw.orderByDesc("created_time");
        return BaseResult.success(firmwareMapper.selectList(qw));
    }

    @PostMapping("firmware/upload")
    @Operation(summary = "上传固件")
    public BaseResult<Firmware> uploadFirmware(
            @RequestParam Integer productId,
            @RequestParam String name,
            @RequestParam String version,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String description) {
        String path = storageService.uploadFile(file);
        Firmware fw = new Firmware();
        fw.setProductId(productId);
        fw.setName(name);
        fw.setVersion(version);
        fw.setFilePath(path);
        fw.setFileSize(file.getSize());
        fw.setDescription(description);
        fw.setStatus("DRAFT");
        fw.setCreatedBy("admin");
        firmwareMapper.insert(fw);
        return BaseResult.success(fw);
    }

    @PutMapping("firmware/{id}/publish")
    @Operation(summary = "发布固件")
    public BaseResult<Boolean> publishFirmware(@PathVariable Integer id) {
        Firmware fw = firmwareMapper.selectById(id);
        if (fw == null) return BaseResult.fail("固件不存在");
        fw.setStatus("PUBLISHED");
        firmwareMapper.updateById(fw);
        return BaseResult.success(true);
    }

    @DeleteMapping("firmware/{id}")
    @Operation(summary = "删除固件")
    public BaseResult<Boolean> deleteFirmware(@PathVariable Integer id) {
        Firmware fw = firmwareMapper.selectById(id);
        if (fw != null) storageService.deleteFile(fw.getFilePath());
        firmwareMapper.deleteById(id);
        return BaseResult.success(true);
    }

    // === OTA Tasks ===

    @GetMapping("task")
    @Operation(summary = "升级任务列表")
    public BaseResult<List<OtaTask>> listTasks(
            @RequestParam(required = false) Integer firmwareId) {
        QueryWrapper<OtaTask> qw = new QueryWrapper<>();
        if (firmwareId != null) qw.eq("firmware_id", firmwareId);
        qw.orderByDesc("created_time");
        return BaseResult.success(otaTaskMapper.selectList(qw));
    }

    @GetMapping("task/{id}")
    @Operation(summary = "升级任务详情")
    public BaseResult<OtaTask> getTask(@PathVariable Integer id) {
        return BaseResult.success(otaTaskMapper.selectById(id));
    }

    @PostMapping("task")
    @Operation(summary = "创建升级任务并下发")
    public BaseResult<OtaTask> createTask(
            @RequestParam Integer firmwareId,
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) List<Integer> deviceIds) {
        Firmware fw = firmwareMapper.selectById(firmwareId);
        if (fw == null) return BaseResult.fail("固件不存在");
        if (!"PUBLISHED".equals(fw.getStatus())) return BaseResult.fail("固件未发布");

        // 收集目标设备
        List<Integer> targets;
        if (groupId != null) {
            targets = groupRelationMapper.selectList(
                    new QueryWrapper<DeviceGroupRelation>().eq("group_id", groupId))
                    .stream().map(DeviceGroupRelation::getDeviceId).collect(Collectors.toList());
        } else if (deviceIds != null && !deviceIds.isEmpty()) {
            targets = deviceIds;
        } else {
            return BaseResult.fail("请指定目标设备或分组");
        }

        OtaTask task = new OtaTask();
        task.setFirmwareId(firmwareId);
        task.setGroupId(groupId);
        task.setDeviceIds(targets);
        task.setStatus("RUNNING");
        task.setTotal(targets.size());
        task.setSuccess(0);
        task.setFail(0);
        Map<String, String> progress = new HashMap<>();
        for (Integer devId : targets) progress.put(String.valueOf(devId), "UPGRADING");
        task.setProgress(progress);
        otaTaskMapper.insert(task);

        // 异步下发升级指令到每个设备
        List<Device> devices = deviceMapper.selectBatchIds(targets);
        for (Device dev : devices) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("firmwareUrl", fw.getFilePath());
                params.put("version", fw.getVersion());
                params.put("checksum", fw.getChecksum());
                serviceHandler.sendMessage(dev.getDeviceKey(), "ota_upgrade", params);
            } catch (Exception e) {
                log.warn("OTA下发失败 device={}: {}", dev.getDeviceKey(), e.getMessage());
                progress.put(String.valueOf(dev.getId()), "FAIL");
            }
        }
        otaTaskMapper.updateById(task);
        return BaseResult.success(task);
    }
}
