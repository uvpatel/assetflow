package com.assetflow.webhook.domain;

import com.assetflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "webhook_subscriptions")
public class WebhookSubscription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "target_url", nullable = false)
    private String targetUrl;

    @Column(nullable = false)
    private String secret;

    @Column(name = "event_types", nullable = false)
    private String eventTypes;

    @Column(nullable = false)
    private boolean active = true;

    public WebhookSubscription() {
    }

    public WebhookSubscription(UUID organizationId, String targetUrl, String secret, String eventTypes) {
        this.organizationId = organizationId;
        this.targetUrl = targetUrl;
        this.secret = secret;
        this.eventTypes = eventTypes;
        this.active = true;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getSecret() {
        return secret;
    }

    public String getEventTypes() {
        return eventTypes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
