package com.github.dingdaoyi.service.push;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PushWebhookSignerTest {

    @Test
    void signBuildsStableHmacSha256HexFromTimestampAndBody() {
        PushWebhookSigner signer = new PushWebhookSigner();

        String signature = signer.sign("demo-secret", "1710000000000", "{\"temperature\":36.5}");

        assertThat(signature).isEqualTo("bb1f7c40d561d7d581154010dbddacbc19ce32ec4c578532ea56a60fd93ada4d");
    }

    @Test
    void signTreatsNullBodyAsEmptyString() {
        PushWebhookSigner signer = new PushWebhookSigner();

        String nullBodySignature = signer.sign("demo-secret", "1710000000000", null);
        String emptyBodySignature = signer.sign("demo-secret", "1710000000000", "");

        assertThat(nullBodySignature).isEqualTo(emptyBodySignature);
    }
}
