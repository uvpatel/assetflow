package com.assetflow.notification.domain;

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
@Table(name = "notification_queue_messages")
public class NotificationQueueMessage extends BaseEntity {

    public enum QueueStatus {
        PENDING,
        SENT,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "recipient_user_id", nullable = false)
    private UUID recipientUserId;

    @Column(nullable = false)
    private String channel;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(nullable = false)
    private String status = QueueStatus.PENDING.name();

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries = 3;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "processed_at")
    private Instant processedAt;

    public NotificationQueueMessage() {
    }

    public NotificationQueueMessage(UUID organizationId, UUID recipientUserId, String channel, String eventType, String subject, String body) {
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.recipientUserId = Objects.requireNonNull(recipientUserId, "Recipient User ID cannot be null");
        this.channel = Objects.requireNonNull(channel, "Channel cannot be null");
        this.eventType = Objects.requireNonNull(eventType, "Event type cannot be null");
        this.subject = Objects.requireNonNull(subject, "Subject cannot be null");
        this.body = Objects.requireNonNull(body, "Body cannot be null");
        this.status = QueueStatus.PENDING.name();
        this.createdAt = Instant.now();
    }

    public void markSent() {
        this.status = QueueStatus.SENT.name();
        this.processedAt = Instant.now();
    }

    public void markFailed(String error) {
        this.retryCount++;
        this.errorMessage = error;
        if (this.retryCount >= this.maxRetries) {
            this.status = QueueStatus.FAILED.name();
        }
        this.processedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getRecipientUserId() { return recipientUserId; }
    public String getChannel() { return channel; }
    public String getEventType() { return eventType; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getStatus() { return status; }
    public int getRetryCount() { return retryCount; }
    public int getMaxRetries() { return maxRetries; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getProcessedAt() { return processedAt; }
}
