package com.assetflow.webhook.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "webhook_delivery_logs")
public class WebhookDeliveryLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "subscription_id", nullable = false)
    private UUID subscriptionId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(nullable = false)
    private String signature;

    @Column(name = "delivered_at", nullable = false)
    private Instant deliveredAt;

    public WebhookDeliveryLog() {
    }

    public WebhookDeliveryLog(UUID organizationId, UUID subscriptionId, String eventType, String payload, Integer responseStatus, String signature) {
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.subscriptionId = Objects.requireNonNull(subscriptionId, "Subscription ID cannot be null");
        this.eventType = Objects.requireNonNull(eventType, "Event type cannot be null");
        this.payload = Objects.requireNonNull(payload, "Payload cannot be null");
        this.responseStatus = responseStatus;
        this.signature = Objects.requireNonNull(signature, "Signature cannot be null");
        this.deliveredAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getSubscriptionId() {
        return subscriptionId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public String getSignature() {
        return signature;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }
}
