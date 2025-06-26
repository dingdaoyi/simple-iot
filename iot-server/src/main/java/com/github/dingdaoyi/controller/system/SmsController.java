package com.github.dingdaoyi.controller.system;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.SmsConfig;
import com.github.dingdaoyi.model.query.SmsConfigAddQuery;
import com.github.dingdaoyi.model.query.SmsConfigUpdateQuery;
import com.github.dingdaoyi.model.query.SmsSendQuery;
import com.github.dingdaoyi.service.SmsConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短信配置管理
 * @author dingyunwei
 */
@RestController
@RequestMapping("/sms")
@Tag(name = "短信管理")
@AllArgsConstructor
public class SmsController {
    
    private final SmsConfigService smsConfigService;
    
    @GetMapping("/config")
    @Operation(summary = "获取短信配置列表")
    public BaseResult<List<SmsConfig>> listConfig(@RequestParam(required = false) String name) {
        return BaseResult.success(smsConfigService.list(
                Wrappers.<SmsConfig>lambdaQuery()
                        .like(StringUtils.isNotBlank(name), SmsConfig::getName, name)
                        .orderByDesc(SmsConfig::getIsDefault)
                        .orderByDesc(SmsConfig::getCreateTime)
        ));
    }
    
    @PostMapping("/config")
    @Operation(summary = "添加短信配置")
    public BaseResult<Boolean> addConfig(@RequestBody @Valid SmsConfigAddQuery query) {
        return BaseResult.success(smsConfigService.save(query.toEntity()));
    }
    
    @PutMapping("/config")
    @Operation(summary = "更新短信配置")
    public BaseResult<Boolean> updateConfig(@RequestBody @Valid SmsConfigUpdateQuery query) {
        SmsConfig config = query.toEntity();
        config.setId(query.getId());
        return BaseResult.success(smsConfigService.updateById(config));
    }
    
    @DeleteMapping("/config/{id}")
    @Operation(summary = "删除短信配置")
    public BaseResult<Boolean> deleteConfig(@PathVariable Integer id) {
        SmsConfig config = smsConfigService.getById(id);
        if (config != null && config.getIsDefault()) {
            return BaseResult.fail("默认配置不能删除");
        }
        return BaseResult.success(smsConfigService.removeById(id));
    }
    
    @PutMapping("/config/{id}/default")
    @Operation(summary = "设置默认配置")
    public BaseResult<Boolean> setDefault(@PathVariable Integer id) {
        return BaseResult.success(smsConfigService.setDefault(id));
    }
    
    @PutMapping("/config/{id}/status")
    @Operation(summary = "更新配置状态")
    public BaseResult<Boolean> updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        return BaseResult.success(smsConfigService.update(
                Wrappers.<SmsConfig>lambdaUpdate()
                        .set(SmsConfig::getStatus, status)
                        .eq(SmsConfig::getId, id)
        ));
    }
    
    @PostMapping("/send")
    @Operation(summary = "发送短信")
    public BaseResult<Boolean> sendSms(@RequestBody @Valid SmsSendQuery query) {
        Boolean result;
        result = smsConfigService.sendSms(query.getPhone(), query.getMessage());
        return BaseResult.success(result);
    }
}