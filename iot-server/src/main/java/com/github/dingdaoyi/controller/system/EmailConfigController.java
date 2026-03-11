package com.github.dingdaoyi.controller.system;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.model.query.EmailConfigAddQuery;
import com.github.dingdaoyi.model.query.EmailConfigPageQuery;
import com.github.dingdaoyi.model.query.EmailConfigUpdateQuery;
import com.github.dingdaoyi.model.vo.EmailConfigVo;
import com.github.dingdaoyi.service.EmailConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 邮箱配置控制器
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/email-config")
@Tag(name = "邮箱配置")
public class EmailConfigController {

    private final EmailConfigService emailConfigService;

    @PostMapping("/page")
    @Operation(summary = "分页列表")
    public PageResult<EmailConfigVo> page(@RequestBody @Valid EmailConfigPageQuery query) {
        return emailConfigService.pageByQuery(query);
    }

    @PostMapping
    @Operation(summary = "添加配置")
    public BaseResult<Boolean> save(@RequestBody @Valid EmailConfigAddQuery query) {
        return BaseResult.success(emailConfigService.save(query));
    }

    @PutMapping
    @Operation(summary = "更新配置")
    public BaseResult<Boolean> update(@RequestBody @Valid EmailConfigUpdateQuery query) {
        return BaseResult.success(emailConfigService.update(query));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除配置")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        return BaseResult.success(emailConfigService.removeById(id));
    }

    @PutMapping("/default/{id}")
    @Operation(summary = "设为默认配置")
    public BaseResult<Boolean> setDefault(@PathVariable Integer id) {
        emailConfigService.setDefault(id);
        return BaseResult.success(true);
    }

    @PutMapping("/status/{id}")
    @Operation(summary = "更新状态")
    public BaseResult<Boolean> updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        emailConfigService.updateStatus(id, status);
        return BaseResult.success(true);
    }

    @GetMapping("/default")
    @Operation(summary = "获取默认配置")
    public BaseResult<EmailConfigVo> getDefault() {
        return BaseResult.success(emailConfigService.getDefaultConfigVo().orElse(null));
    }
}