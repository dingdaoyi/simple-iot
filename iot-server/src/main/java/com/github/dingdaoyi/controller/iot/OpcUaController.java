package com.github.dingdaoyi.controller.iot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.driver.opcua.OpcUaPollingService;
import com.github.dingdaoyi.entity.OpcUaConfig;
import com.github.dingdaoyi.mapper.OpcUaConfigMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OPC UA 驱动管理
 * @author dingyunwei
 */
@RestController
@RequestMapping("/opcua")
@Tag(name = "OPC UA驱动管理")
@AllArgsConstructor
public class OpcUaController {

    private final OpcUaConfigMapper configMapper;
    private final OpcUaPollingService pollingService;

    @GetMapping
    @Operation(summary = "配置列表")
    public BaseResult<List<OpcUaConfig>> list(@RequestParam(required = false) Integer deviceId) {
        QueryWrapper<OpcUaConfig> qw = new QueryWrapper<>();
        if (deviceId != null) qw.eq("device_id", deviceId);
        qw.orderByDesc("create_time");
        return BaseResult.success(configMapper.selectList(qw));
    }

    @GetMapping("{id}")
    @Operation(summary = "配置详情")
    public BaseResult<OpcUaConfig> get(@PathVariable Integer id) {
        return BaseResult.success(configMapper.selectById(id));
    }

    @PostMapping
    @Operation(summary = "新增配置")
    public BaseResult<Boolean> save(@RequestBody OpcUaConfig config) {
        if (config.getNodeMap() == null) config.setNodeMap("[]");
        if (config.getEnabled() == null) config.setEnabled(true);
        if (config.getSecurityMode() == null) config.setSecurityMode("NONE");
        configMapper.insert(config);
        pollingService.reload(config.getId());
        return BaseResult.success(true);
    }

    @PutMapping
    @Operation(summary = "修改配置")
    public BaseResult<Boolean> update(@RequestBody OpcUaConfig config) {
        configMapper.updateById(config);
        pollingService.reload(config.getId());
        return BaseResult.success(true);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除配置")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        OpcUaConfig cfg = configMapper.selectById(id);
        if (cfg != null && Boolean.TRUE.equals(cfg.getEnabled())) {
            cfg.setEnabled(false);
            pollingService.reload(id);
        }
        return BaseResult.success(configMapper.deleteById(id) > 0);
    }

    @PostMapping("{id}/test")
    @Operation(summary = "触发轮询测试")
    public BaseResult<Boolean> test(@PathVariable Integer id) {
        pollingService.reload(id);
        return BaseResult.success(true);
    }
}
