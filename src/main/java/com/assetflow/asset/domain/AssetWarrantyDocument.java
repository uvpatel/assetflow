package com.assetflow.asset.domain;

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
@Table(name = "asset_warranty_documents")
public class AssetWarrantyDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "warranty_id", nullable = false)
    private UUID warrantyId;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "file_size_bytes", nullable = false)
    private long fileSizeBytes;

    @Column(name = "storage_url", nullable = false)
    private String storageUrl;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt = Instant.now();

    public AssetWarrantyDocument() {
    }

    public AssetWarrantyDocument(UUID organizationId, UUID warrantyId, String documentName, String fileType, long fileSizeBytes, String storageUrl) {
        this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null");
        this.warrantyId = Objects.requireNonNull(warrantyId, "Warranty ID cannot be null");
        this.documentName = Objects.requireNonNull(documentName, "Document name cannot be null");
        this.fileType = Objects.requireNonNull(fileType, "File type cannot be null");
        if (fileSizeBytes <= 0) {
            throw new IllegalArgumentException("File size must be greater than zero");
        }
        this.storageUrl = Objects.requireNonNull(storageUrl, "Storage URL cannot be null");
        this.fileSizeBytes = fileSizeBytes;
        this.uploadedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getWarrantyId() { return warrantyId; }
    public String getDocumentName() { return documentName; }
    public String getFileType() { return fileType; }
    public long getFileSizeBytes() { return fileSizeBytes; }
    public String getStorageUrl() { return storageUrl; }
    public Instant getUploadedAt() { return uploadedAt; }
}
