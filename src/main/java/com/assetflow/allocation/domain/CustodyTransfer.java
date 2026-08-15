package com.assetflow.allocation.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "custody_transfers")
public class CustodyTransfer extends BaseEntity {

    public enum CustodyTransferStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "sender_employee_id", nullable = false)
    private UUID senderEmployeeId;

    @Column(name = "recipient_employee_id", nullable = false)
    private UUID recipientEmployeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustodyTransferStatus status;

    @Column(name = "digital_signature", columnDefinition = "TEXT")
    private String digitalSignature;

    @Column(name = "signed_at")
    private Instant signedAt;

    private String notes;

    public CustodyTransfer() {
    }

    public CustodyTransfer(UUID organizationId, UUID senderEmployeeId, UUID recipientEmployeeId, String notes) {
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.senderEmployeeId = Objects.requireNonNull(senderEmployeeId, "Sender employee ID cannot be null");
        this.recipientEmployeeId = Objects.requireNonNull(recipientEmployeeId, "Recipient employee ID cannot be null");
        this.notes = notes;
        this.status = CustodyTransferStatus.PENDING;
    }

    public void accept(String digitalSignature, Instant now) {
        if (status != CustodyTransferStatus.PENDING) {
            throw new IllegalStateException("Only PENDING custody transfers can be accepted");
        }
        if (digitalSignature == null || digitalSignature.isBlank()) {
            throw new IllegalArgumentException("Digital signature is required to complete custody transfer");
        }
        this.digitalSignature = digitalSignature;
        this.signedAt = Objects.requireNonNull(now, "SignedAt cannot be null");
        this.status = CustodyTransferStatus.ACCEPTED;
    }

    public void reject() {
        if (status != CustodyTransferStatus.PENDING) {
            throw new IllegalStateException("Only PENDING custody transfers can be rejected");
        }
        this.status = CustodyTransferStatus.REJECTED;
    }

    public void cancel() {
        if (status != CustodyTransferStatus.PENDING) {
            throw new IllegalStateException("Only PENDING custody transfers can be cancelled");
        }
        this.status = CustodyTransferStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getSenderEmployeeId() {
        return senderEmployeeId;
    }

    public UUID getRecipientEmployeeId() {
        return recipientEmployeeId;
    }

    public CustodyTransferStatus getStatus() {
        return status;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public Instant getSignedAt() {
        return signedAt;
    }

    public String getNotes() {
        return notes;
    }
}
