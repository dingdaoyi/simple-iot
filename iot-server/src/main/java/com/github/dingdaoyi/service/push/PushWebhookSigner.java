package com.github.dingdaoyi.service.push;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * HMAC-SHA256 signer for outgoing HTTP webhook pushes.
 */
@Component
public class PushWebhookSigner {

    private static final String HMAC_SHA256 = "HmacSHA256";

    public String sign(String secret, String timestamp, String body) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            String payload = timestamp + "." + (body == null ? "" : body);
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Webhook 签名生成失败", e);
        }
    }
}
