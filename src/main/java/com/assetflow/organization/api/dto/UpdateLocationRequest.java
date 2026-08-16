package com.assetflow.organization.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateLocationRequest(
        @NotBlank(message = "Location name is required")
        @Size(max = 255)
        String name,

        UUID parentLocationId,

        @Size(max = 255)
        String address,

        String description
) {}
