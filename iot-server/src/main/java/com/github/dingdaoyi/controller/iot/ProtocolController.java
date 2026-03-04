package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.ProtocolService;
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
@RequestMapping("/protocol")
@Tag(name = "协议管理")
public class ProtocolController {
    private final ProtocolService protocolService;
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "协议列表")
    public BaseResult<List<Protocol>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer protoType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String protoKey,
            @RequestParam(required = false) String scriptLang) {
        return BaseResult.success(protocolService.list(name, protoType, status, protoKey, scriptLang));
    }

    @PostMapping
    @Operation(summary = "协议添加")
    public BaseResult<Boolean> save(@RequestBody Protocol protocol) {
        return BaseResult.success(protocolService.save(protocol));
    }

    @PutMapping
    @Operation(summary = "协议修改")
    public BaseResult<Boolean> update(@RequestBody Protocol protocol) {
        return BaseResult.success(protocolService.updateById(protocol));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "协议修改")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        if (productService.existsByProtocol(id)) {
            return BaseResult.fail("协议已被引用,无法删除");
        }
        return BaseResult.success(protocolService.removeById(id));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "设置协议状态")
    public BaseResult<Boolean> setStatus(@PathVariable Integer id, @RequestParam Boolean enable) {
        return BaseResult.success(protocolService.setProtocolStatus(id, enable));
    }
}
