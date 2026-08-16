package com.assetflow.organization.api.dto;

import com.assetflow.organization.domain.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateLocationRequest(
        @NotBlank(message = "Location name is required")
        @Size(max = 255)
        String name,

        @NotBlank(message = "Location code is required")
        @Size(max = 50)
        String code,

        @NotNull(message = "Location type is required")
        LocationType type,

        UUID parentLocationId,

        @Size(max = 255)
        String address,

        String description
) {}
