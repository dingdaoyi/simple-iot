package com.github.dingdaoyi.controller.iot;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.driver.modbus.ModbusPollingService;
import com.github.dingdaoyi.entity.ModbusConfig;
import com.github.dingdaoyi.mapper.ModbusConfigMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/modbus")
@Tag(name = "Modbus驱动管理")
@AllArgsConstructor
public class ModbusController {

    private final ModbusConfigMapper configMapper;
    private final ModbusPollingService pollingService;

    @GetMapping
    @Operation(summary = "配置列表")
    public BaseResult<List<ModbusConfig>> list(@RequestParam(required = false) Integer deviceId) {
        QueryWrapper<ModbusConfig> qw = new QueryWrapper<>();
        if (deviceId != null) qw.eq("device_id", deviceId);
        qw.orderByDesc("create_time");
        return BaseResult.success(configMapper.selectList(qw));
    }

    @GetMapping("{id}")
    @Operation(summary = "配置详情")
    public BaseResult<ModbusConfig> get(@PathVariable Integer id) {
        return BaseResult.success(configMapper.selectById(id));
    }

    @PostMapping
    @Operation(summary = "新增配置")
    public BaseResult<Boolean> save(@RequestBody ModbusConfig config) {
        if (config.getRegisterMap() == null) config.setRegisterMap("[]");
        if (config.getEnabled() == null) config.setEnabled(true);
        configMapper.insert(config);
        pollingService.reload(config.getId());
        return BaseResult.success(true);
    }

    @PutMapping
    @Operation(summary = "修改配置")
    public BaseResult<Boolean> update(@RequestBody ModbusConfig config) {
        configMapper.updateById(config);
        pollingService.reload(config.getId());
        return BaseResult.success(true);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除配置")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        ModbusConfig cfg = configMapper.selectById(id);
        if (cfg != null) {
            cfg.setEnabled(false);
            pollingService.reload(id);
        }
        configMapper.deleteById(id);
        return BaseResult.success(true);
    }

    @PostMapping("{id}/test")
    @Operation(summary = "手动触发一次轮询")
    public BaseResult<Boolean> test(@PathVariable Integer id) {
        pollingService.reload(id);
        return BaseResult.success(true);
    }
}
