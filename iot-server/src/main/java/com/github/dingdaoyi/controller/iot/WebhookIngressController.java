package com.github.dingdaoyi.controller.iot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.WebhookIngress;
import com.github.dingdaoyi.mapper.DeviceMapper;
import com.github.dingdaoyi.mapper.WebhookIngressMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;

@RestController
@RequestMapping("/webhook/config")
@Tag(name = "Webhook数据接入")
@AllArgsConstructor
public class WebhookIngressController {

    private final WebhookIngressMapper mapper;
    private final DeviceMapper deviceMapper;

    @GetMapping
    @Operation(summary = "接入列表")
    public BaseResult<List<WebhookIngress>> list(@RequestParam(required = false) Integer deviceId) {
        QueryWrapper<WebhookIngress> qw = new QueryWrapper<>();
        if (deviceId != null) qw.eq("device_id", deviceId);
        qw.orderByDesc("create_time");
        List<WebhookIngress> list = mapper.selectList(qw);
        // ponytail: 不返回 secret 给前端，只显示 token
        list.forEach(w -> w.setSecret(null));
        return BaseResult.success(list);
    }

    @PostMapping
    @Operation(summary = "创建接入（自动生成token和secret）")
    public BaseResult<WebhookIngress> create(@RequestBody WebhookIngress config) {
        if (config.getName() == null || config.getName().isBlank()) {
            return BaseResult.fail("名称不能为空");
        }
        if (config.getDeviceId() == null || deviceMapper.selectById(config.getDeviceId()) == null) {
            return BaseResult.fail("设备不存在");
        }
        config.setToken(generateToken());
        config.setSecret(generateSecret());
        if (config.getEnabled() == null) config.setEnabled(true);
        mapper.insert(config);
        return BaseResult.success(config);
    }

    @PutMapping
    @Operation(summary = "修改接入")
    public BaseResult<Boolean> update(@RequestBody WebhookIngress config) {
        WebhookIngress existing = mapper.selectById(config.getId());
        if (existing == null) return BaseResult.fail("配置不存在");
        // token/secret 不可通过此接口修改
        config.setToken(existing.getToken());
        config.setSecret(existing.getSecret());
        mapper.updateById(config);
        return BaseResult.success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除接入")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        mapper.deleteById(id);
        return BaseResult.success(true);
    }

    @PostMapping("/{id}/regenerate-secret")
    @Operation(summary = "重新生成密钥")
    public BaseResult<String> regenerateSecret(@PathVariable Integer id) {
        WebhookIngress config = mapper.selectById(id);
        if (config == null) return BaseResult.fail("配置不存在");
        String newSecret = generateSecret();
        config.setSecret(newSecret);
        mapper.updateById(config);
        return BaseResult.success(newSecret);
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    private static String generateToken() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private static String generateSecret() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
