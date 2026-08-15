package com.assetflow.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "user_id", "notification_type"})
})
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "notification_type", nullable = false)
    private String notificationType = "ALL";

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled = true;

    @Column(name = "in_app_enabled", nullable = false)
    private boolean inAppEnabled = true;

    @Column(name = "sms_enabled")
    private boolean smsEnabled = false;

    @Column(name = "slack_enabled")
    private boolean slackEnabled = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public NotificationPreference() {
    }

    public NotificationPreference(UUID organizationId, UUID userId, String notificationType, boolean emailEnabled, boolean inAppEnabled, boolean smsEnabled, boolean slackEnabled) {
        this.organizationId = organizationId;
        this.userId = userId;
        this.notificationType = (notificationType != null && !notificationType.isBlank()) ? notificationType : "ALL";
        this.emailEnabled = emailEnabled;
        this.inAppEnabled = inAppEnabled;
        this.smsEnabled = smsEnabled;
        this.slackEnabled = slackEnabled;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public NotificationPreference(UUID organizationId, UUID userId, String notificationType, boolean emailEnabled, boolean inAppEnabled) {
        this(organizationId, userId, notificationType, emailEnabled, inAppEnabled, false, false);
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }

    public boolean isInAppEnabled() { return inAppEnabled; }
    public void setInAppEnabled(boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }

    public boolean isSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(boolean smsEnabled) { this.smsEnabled = smsEnabled; }

    public boolean isSlackEnabled() { return slackEnabled; }
    public void setSlackEnabled(boolean slackEnabled) { this.slackEnabled = slackEnabled; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
