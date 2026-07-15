package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.Dashboard;
import com.github.dingdaoyi.mapper.DashboardMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "自定义仪表盘")
public class DashboardController {

    private final DashboardMapper dashboardMapper;

    @GetMapping("list")
    @Operation(summary = "仪表盘列表")
    public BaseResult<List<Dashboard>> list() {
        return BaseResult.success(dashboardMapper.selectList(null));
    }

    @GetMapping("{id}")
    @Operation(summary = "仪表盘详情")
    public BaseResult<Dashboard> getById(@PathVariable Integer id) {
        return BaseResult.success(dashboardMapper.selectById(id));
    }

    @PostMapping
    @Operation(summary = "创建仪表盘")
    public BaseResult<Boolean> save(@RequestBody Dashboard dashboard) {
        return BaseResult.success(dashboardMapper.insert(dashboard) > 0);
    }

    @PutMapping
    @Operation(summary = "更新仪表盘")
    public BaseResult<Boolean> update(@RequestBody Dashboard dashboard) {
        return BaseResult.success(dashboardMapper.updateById(dashboard) > 0);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除仪表盘")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        return BaseResult.success(dashboardMapper.deleteById(id) > 0);
    }
}
