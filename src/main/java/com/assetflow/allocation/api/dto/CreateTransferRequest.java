package com.assetflow.allocation.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreateTransferRequest {

    @NotNull
    private UUID employeeId;

    @NotNull
    private UUID toDepartmentId;

    private String reason;

    public CreateTransferRequest() {
    }

    public CreateTransferRequest(UUID employeeId, UUID toDepartmentId, String reason) {
        this.employeeId = employeeId;
        this.toDepartmentId = toDepartmentId;
        this.reason = reason;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public UUID getToDepartmentId() {
        return toDepartmentId;
    }

    public void setToDepartmentId(UUID toDepartmentId) {
        this.toDepartmentId = toDepartmentId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
