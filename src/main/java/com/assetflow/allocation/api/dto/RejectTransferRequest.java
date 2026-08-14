package com.assetflow.allocation.api.dto;

public class RejectTransferRequest {

    private String rejectionReason;

    public RejectTransferRequest() {
    }

    public RejectTransferRequest(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
