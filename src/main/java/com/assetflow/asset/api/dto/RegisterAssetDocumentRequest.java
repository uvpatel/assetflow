package com.assetflow.asset.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class RegisterAssetDocumentRequest {

    @NotNull(message = "Asset ID is required")
    private UUID assetId;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "File type is required")
    private String fileType;

    @NotNull(message = "File size is required")
    private Long fileSize;

    @NotBlank(message = "File URL is required")
    private String fileUrl;

    @NotBlank(message = "Document type is required")
    private String documentType;

    private String description;

    public RegisterAssetDocumentRequest() {
    }

    public RegisterAssetDocumentRequest(UUID assetId, String fileName, String fileType, Long fileSize, String fileUrl, String documentType, String description) {
        this.assetId = assetId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.documentType = documentType;
        this.description = description;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
