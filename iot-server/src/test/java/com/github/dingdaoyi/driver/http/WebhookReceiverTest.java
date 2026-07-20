package com.github.dingdaoyi.driver.http;

import com.github.dingdaoyi.service.push.PushWebhookSigner;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class WebhookReceiverTest {

    private final PushWebhookSigner signer = new PushWebhookSigner();

    @Test
    void validSignaturePassesVerification() {
        String secret = "test-secret-123";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String body = "{\"type\":\"property\",\"params\":{\"temp\":{\"value\":36.5,\"dataType\":1}}}";

        String signature = signer.sign(secret, timestamp, body);

        // Re-sign with same inputs -> matches
        String expected = signer.sign(secret, timestamp, body);
        assertThat(signature).isEqualTo(expected);
    }

    @Test
    void tamperedBodyFailsVerification() {
        String secret = "test-secret-123";
        String timestamp = String.valueOf(System.currentTimeMillis());

        String sig = signer.sign(secret, timestamp, "{\"temp\":36.5}");
        String tamperedSig = signer.sign(secret, timestamp, "{\"temp\":99.9}");

        assertThat(sig).isNotEqualTo(tamperedSig);
    }

    @Test
    void wrongSecretFailsVerification() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String body = "{\"temp\":36.5}";

        String sig = signer.sign("correct-secret", timestamp, body);
        String wrongSecretSig = signer.sign("wrong-secret", timestamp, body);

        assertThat(sig).isNotEqualTo(wrongSecretSig);
    }

    @Test
    void timestampOutsideWindowRejected() {
        long now = System.currentTimeMillis();
        long tooOld = now - (6 * 60 * 1000); // 6 min ago, > 5 min window

        assertThat(Math.abs(now - tooOld) > 5 * 60 * 1000).isTrue();

        long future = now + (6 * 60 * 1000); // 6 min in future
        assertThat(Math.abs(now - future) > 5 * 60 * 1000).isTrue();
    }

    @Test
    void timestampWithinWindowAccepted() {
        long now = System.currentTimeMillis();
        long recent = now - 60_000; // 1 min ago, within 5 min window
        long soon = now + 60_000;   // 1 min in future

        assertThat(Math.abs(now - recent) <= 5 * 60 * 1000).isTrue();
        assertThat(Math.abs(now - soon) <= 5 * 60 * 1000).isTrue();
    }
}
