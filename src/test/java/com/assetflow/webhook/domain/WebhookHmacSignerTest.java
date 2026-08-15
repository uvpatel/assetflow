package com.assetflow.webhook.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebhookHmacSignerTest {

    @Test
    @DisplayName("Should compute correct HMAC-SHA256 signature with sha256= prefix")
    void shouldComputeHmacSignature() {
        String payload = "{\"event\":\"ASSET_CREATED\",\"id\":\"12345\"}";
        String secret = "whsec_supersecretkey123";

        String signature = WebhookHmacSigner.calculateSignature(payload, secret);

        assertThat(signature).startsWith("sha256=");
        assertThat(signature.length()).isEqualTo(7 + 64); // sha256= + 64 hex chars
    }

    @Test
    @DisplayName("Should verify valid HMAC signature successfully")
    void shouldVerifyValidSignature() {
        String payload = "{\"event\":\"ASSET_ALLOCATED\",\"id\":\"999\"}";
        String secret = "whsec_secret";

        String signature = WebhookHmacSigner.calculateSignature(payload, secret);
        boolean isValid = WebhookHmacSigner.verifySignature(payload, secret, signature);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject tampered payload in signature verification")
    void shouldRejectTamperedPayload() {
        String originalPayload = "{\"event\":\"ASSET_ALLOCATED\",\"id\":\"999\"}";
        String tamperedPayload = "{\"event\":\"ASSET_ALLOCATED\",\"id\":\"9999\"}";
        String secret = "whsec_secret";

        String signature = WebhookHmacSigner.calculateSignature(originalPayload, secret);
        boolean isValid = WebhookHmacSigner.verifySignature(tamperedPayload, secret, signature);

        assertThat(isValid).isFalse();
    }
}
