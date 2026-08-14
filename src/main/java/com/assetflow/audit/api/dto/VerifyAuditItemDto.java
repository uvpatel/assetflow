package com.assetflow.audit.api.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyAuditItemDto {

    @NotBlank(message = "Verified condition state is required")
    private String verifiedCondition;

    @NotBlank(message = "Verification status is required")
    private String verificationStatus;

    private String notes;

    public VerifyAuditItemDto() {
    }

    public VerifyAuditItemDto(String verifiedCondition, String verificationStatus, String notes) {
        this.verifiedCondition = verifiedCondition;
        this.verificationStatus = verificationStatus;
        this.notes = notes;
    }

    public String getVerifiedCondition() {
        return verifiedCondition;
    }

    public void setVerifiedCondition(String verifiedCondition) {
        this.verifiedCondition = verifiedCondition;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
