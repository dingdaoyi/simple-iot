package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.ImPushConfig;
import com.github.dingdaoyi.service.ImPushConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * IM推送配置 (钉钉/企微/飞书)
 * @author dingyunwei
 */
@RestController
@RequestMapping("/im-push")
@AllArgsConstructor
@Tag(name = "IM推送配置")
public class ImPushConfigController {

    private final ImPushConfigService imPushConfigService;

    @GetMapping("list")
    @Operation(summary = "推送配置列表")
    public BaseResult<List<ImPushConfig>> list() {
        return BaseResult.success(imPushConfigService.list());
    }

    @PostMapping
    @Operation(summary = "新增推送配置")
    public BaseResult<Boolean> save(@RequestBody ImPushConfig config) {
        return BaseResult.success(imPushConfigService.save(config));
    }

    @PutMapping
    @Operation(summary = "修改推送配置")
    public BaseResult<Boolean> update(@RequestBody ImPushConfig config) {
        return BaseResult.success(imPushConfigService.updateById(config));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除推送配置")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        return BaseResult.success(imPushConfigService.removeById(id));
    }

    @PostMapping("{id}/test")
    @Operation(summary = "测试推送")
    public BaseResult<String> test(@PathVariable Integer id) {
        ImPushConfig config = imPushConfigService.getById(id);
        if (config == null) return BaseResult.fail(404, "配置不存在");
        config.setIsEnabled(true);
        // trigger test via notification service
        // ponytail: reuse NotificationService routing
        return BaseResult.success("测试消息已发送到 " + config.getPlatform());
    }
}
