package com.assetflow.reporting.domain;

import java.time.Instant;
import java.util.UUID;

public class BulkImportBatch {
    private final UUID id;
    private final UUID organizationId;
    private final String filename;
    private final int totalRecords;
    private int processedRecords;
    private int failedRecords;
    private Status status;
    private final Instant createdAt;
    private Instant updatedAt;

    public enum Status {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    public BulkImportBatch(
            UUID id,
            UUID organizationId,
            String filename,
            int totalRecords,
            int processedRecords,
            int failedRecords,
            Status status,
            Instant createdAt,
            Instant updatedAt) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be blank");
        }
        if (totalRecords <= 0) {
            throw new IllegalArgumentException("Total records must be greater than zero");
        }
        this.id = id;
        this.organizationId = organizationId;
        this.filename = filename;
        this.totalRecords = totalRecords;
        this.processedRecords = processedRecords;
        this.failedRecords = failedRecords;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static BulkImportBatch create(UUID organizationId, String filename, int totalRecords) {
        Instant now = Instant.now();
        return new BulkImportBatch(
                UUID.randomUUID(),
                organizationId,
                filename,
                totalRecords,
                0,
                0,
                Status.PENDING,
                now,
                now
        );
    }

    public void startProcessing() {
        if (this.status != Status.PENDING) {
            throw new IllegalStateException("Only PENDING batches can start processing");
        }
        this.status = Status.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void recordSuccess() {
        this.processedRecords++;
        checkCompletion();
        this.updatedAt = Instant.now();
    }

    public void recordFailure() {
        this.failedRecords++;
        checkCompletion();
        this.updatedAt = Instant.now();
    }

    private void checkCompletion() {
        if ((processedRecords + failedRecords) >= totalRecords) {
            this.status = failedRecords == totalRecords ? Status.FAILED : Status.COMPLETED;
        }
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getFilename() { return filename; }
    public int getTotalRecords() { return totalRecords; }
    public int getProcessedRecords() { return processedRecords; }
    public int getFailedRecords() { return failedRecords; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
