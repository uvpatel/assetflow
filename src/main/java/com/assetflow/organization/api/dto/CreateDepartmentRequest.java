package com.assetflow.organization.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class CreateDepartmentRequest {

    @NotBlank(message = "Department name is required")
    private String name;

    @NotBlank(message = "Department code is required")
    private String code;

    private UUID parentDepartmentId;

    public CreateDepartmentRequest() {
    }

    public CreateDepartmentRequest(String name, String code, UUID parentDepartmentId) {
        this.name = name;
        this.code = code;
        this.parentDepartmentId = parentDepartmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UUID getParentDepartmentId() {
        return parentDepartmentId;
    }

    public void setParentDepartmentId(UUID parentDepartmentId) {
        this.parentDepartmentId = parentDepartmentId;
    }
}
