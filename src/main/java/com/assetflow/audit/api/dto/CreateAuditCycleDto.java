package com.assetflow.audit.api.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateAuditCycleDto {

    @NotBlank(message = "Audit cycle name is required")
    private String name;

    private String description;

    public CreateAuditCycleDto() {
    }

    public CreateAuditCycleDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
