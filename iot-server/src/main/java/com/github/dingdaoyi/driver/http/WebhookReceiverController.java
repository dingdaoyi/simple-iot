package com.github.dingdaoyi.driver.http;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.WebhookIngress;
import com.github.dingdaoyi.iot.IotDataProcessor;
import com.github.dingdaoyi.mapper.DeviceMapper;
import com.github.dingdaoyi.mapper.ProductMapper;
import com.github.dingdaoyi.mapper.WebhookIngressMapper;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import com.github.dingdaoyi.service.push.PushWebhookSigner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * HTTP Webhook 数据接入端点。
 * 第三方平台通过 POST /webhook/{token} 推送数据，HMAC-SHA256 验签。
 * 签名格式与 push 出口一致: HMAC(timestamp + "." + body, secret)
 */
@RestController
@Slf4j
@RequestMapping("/webhook")
@Tag(name = "Webhook数据接入端点")
public class WebhookReceiverController {

    private final WebhookIngressMapper ingressMapper;
    private final DeviceMapper deviceMapper;
    private final ProductMapper productMapper;
    private final IotDataProcessor dataProcessor;
    private final PushWebhookSigner signer;

    // ponytail: 5分钟时间窗口，防止重放
    private static final long MAX_TIMESTAMP_DRIFT_MS = 5 * 60 * 1000;

    public WebhookReceiverController(WebhookIngressMapper ingressMapper,
                                      DeviceMapper deviceMapper,
                                      ProductMapper productMapper,
                                      IotDataProcessor dataProcessor,
                                      PushWebhookSigner signer) {
        this.ingressMapper = ingressMapper;
        this.deviceMapper = deviceMapper;
        this.productMapper = productMapper;
        this.dataProcessor = dataProcessor;
        this.signer = signer;
    }

    @PostMapping("/{token}")
    @Operation(summary = "接收第三方推送数据")
    public void receive(@PathVariable String token,
                        @RequestBody byte[] body,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        WebhookIngress config = findByToken(token);
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) {
            writeJson(response, BaseResult.fail(ResultCode.UNAUTHORIZED, "无效的接入Token"));
            return;
        }

        String signature = request.getHeader("X-Siot-Signature");
        String timestamp = request.getHeader("X-Siot-Timestamp");

        if (signature == null || timestamp == null) {
            writeJson(response, BaseResult.fail(ResultCode.UNAUTHORIZED, "缺少签名或时间戳"));
            return;
        }

        // 时间窗口检查
        try {
            long ts = Long.parseLong(timestamp);
            long now = System.currentTimeMillis();
            if (Math.abs(now - ts) > MAX_TIMESTAMP_DRIFT_MS) {
                writeJson(response, BaseResult.fail(ResultCode.UNAUTHORIZED, "时间戳超出有效范围"));
                return;
            }
        } catch (NumberFormatException e) {
            writeJson(response, BaseResult.fail(ResultCode.UNAUTHORIZED, "时间戳格式无效"));
            return;
        }

        // HMAC 验签
        String bodyStr = body == null ? "" : new String(body, StandardCharsets.UTF_8);
        String expected = signer.sign(config.getSecret(), timestamp, bodyStr);
        if (!expected.equalsIgnoreCase(signature)) {
            log.warn("Webhook签名验证失败: token={}, name={}", token, config.getName());
            writeJson(response, BaseResult.fail(ResultCode.UNAUTHORIZED, "签名验证失败"));
            return;
        }

        // 查设备 -> 构建DeviceRequest -> 喂给 messageUp
        Device device = deviceMapper.selectById(config.getDeviceId());
        if (device == null) {
            writeJson(response, BaseResult.fail(ResultCode.BAD_REQUEST, "设备不存在"));
            return;
        }
        Product product = productMapper.selectById(device.getProductId());
        if (product == null) {
            writeJson(response, BaseResult.fail(ResultCode.BAD_REQUEST, "产品不存在"));
            return;
        }

        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setDeviceKey(device.getDeviceKey());
        deviceRequest.setProductKey(product.getProductKey());
        deviceRequest.setProtoKey("system-default");
        deviceRequest.setMessageType(ProtoMessageType.PROPERTY);
        deviceRequest.setData(body);
        dataProcessor.messageUp(deviceRequest);

        writeJson(response, BaseResult.success());
    }

    private WebhookIngress findByToken(String token) {
        return ingressMapper.selectOne(
                new QueryWrapper<WebhookIngress>().eq("token", token));
    }

    private void writeJson(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(cn.hutool.json.JSONUtil.toJsonStr(data));
        response.getWriter().flush();
    }
}
