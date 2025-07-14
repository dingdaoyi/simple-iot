package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.Driver;
import com.github.dingdaoyi.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/driver")
@Tag(name = "驱动管理")
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    @Operation(summary = "驱动列表")
    public BaseResult<List<Driver>> list() {
        return BaseResult.success(driverService.list());
    }

    @PostMapping
    @Operation(summary = "新增驱动")
    public BaseResult<Boolean> save(@RequestBody Driver driver) {
        return BaseResult.success(driverService.save(driver));
    }

    @PutMapping
    @Operation(summary = "修改驱动")
    public BaseResult<Boolean> update(@RequestBody Driver driver) {
        return BaseResult.success(driverService.updateById(driver));
    }

    @DeleteMapping("/{driverId}")
    @Operation(summary = "删除驱动")
    public BaseResult<Boolean> delete(@PathVariable Integer driverId) {
        // 可加默认驱动保护逻辑
        return BaseResult.success(driverService.removeById(driverId));
    }
} 