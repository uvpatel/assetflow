package com.assetflow.allocation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustodyTransferTest {

    @Test
    @DisplayName("Should create valid pending custody transfer")
    void shouldCreateValidCustodyTransfer() {
        UUID orgId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();

        CustodyTransfer transfer = new CustodyTransfer(orgId, senderId, recipientId, "Handover laptops");

        assertThat(transfer.getOrganizationId()).isEqualTo(orgId);
        assertThat(transfer.getSenderEmployeeId()).isEqualTo(senderId);
        assertThat(transfer.getRecipientEmployeeId()).isEqualTo(recipientId);
        assertThat(transfer.getStatus()).isEqualTo(CustodyTransfer.CustodyTransferStatus.PENDING);
        assertThat(transfer.getDigitalSignature()).isNull();
    }

    @Test
    @DisplayName("Should transition status to ACCEPTED on accept with digital signature")
    void shouldAcceptWithDigitalSignature() {
        UUID orgId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();

        CustodyTransfer transfer = new CustodyTransfer(orgId, senderId, recipientId, "Handover laptops");
        String signature = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjwvc3ZnPg==";
        Instant now = Instant.now();

        transfer.accept(signature, now);

        assertThat(transfer.getStatus()).isEqualTo(CustodyTransfer.CustodyTransferStatus.ACCEPTED);
        assertThat(transfer.getDigitalSignature()).isEqualTo(signature);
        assertThat(transfer.getSignedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should reject accept when digital signature is missing")
    void shouldRejectAcceptWithoutDigitalSignature() {
        UUID orgId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();

        CustodyTransfer transfer = new CustodyTransfer(orgId, senderId, recipientId, "Handover laptops");

        assertThatThrownBy(() -> transfer.accept("   ", Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Digital signature is required");
    }

    @Test
    @DisplayName("Should transition status to REJECTED on reject")
    void shouldRejectCustodyTransfer() {
        UUID orgId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();

        CustodyTransfer transfer = new CustodyTransfer(orgId, senderId, recipientId, "Handover laptops");
        transfer.reject();

        assertThat(transfer.getStatus()).isEqualTo(CustodyTransfer.CustodyTransferStatus.REJECTED);
    }
}
