package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.model.query.PushConfigAddQuery;
import com.github.dingdaoyi.model.query.PushConfigPageQuery;
import com.github.dingdaoyi.model.query.PushConfigUpdateQuery;
import com.github.dingdaoyi.model.vo.PushConfigDetailVo;
import com.github.dingdaoyi.model.vo.PushConfigPageVo;
import com.github.dingdaoyi.service.PushConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 推送配置控制器
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/push-config")
@Tag(name = "推送配置管理")
public class PushConfigController {

    private final PushConfigService pushConfigService;

    @GetMapping("{id}")
    @Operation(summary = "配置详情")
    public BaseResult<PushConfigDetailVo> details(@PathVariable Integer id) {
        Optional<PushConfigDetailVo> optional = pushConfigService.details(id);
        return optional.map(BaseResult::success)
                .orElse(BaseResult.fail(ResultCode.NOT_FOUND.getCode(), "配置不存在"));
    }

    @PostMapping
    @Operation(summary = "添加配置")
    public BaseResult<Boolean> save(@RequestBody @Valid PushConfigAddQuery query) {
        return BaseResult.success(pushConfigService.save(query.toEntity()));
    }

    @PutMapping
    @Operation(summary = "修改配置")
    public BaseResult<Boolean> update(@RequestBody @Valid PushConfigUpdateQuery query) {
        return BaseResult.success(pushConfigService.updateById(query.toEntity()));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除配置")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        return BaseResult.success(pushConfigService.removeById(id));
    }

    @PostMapping("page")
    @Operation(summary = "配置分页列表")
    public PageResult<PushConfigPageVo> page(@RequestBody @Valid PushConfigPageQuery query) {
        return pushConfigService.pageByQuery(query);
    }

    @GetMapping("list")
    @Operation(summary = "获取启用的配置列表")
    public BaseResult<List<PushConfig>> list(
            @RequestParam(required = false) com.github.dingdaoyi.entity.enu.PushConfigType type) {
        List<PushConfig> list = pushConfigService.listEnabled();
        if (type != null) {
            list = list.stream().filter(c -> c.getType() == type).toList();
        }
        return BaseResult.success(list);
    }
}
