package com.assetflow.organization.api.dto;

import com.assetflow.organization.domain.Location;
import com.assetflow.organization.domain.LocationType;

import java.time.Instant;
import java.util.UUID;

public record LocationResponse(
        UUID id,
        UUID organizationId,
        String name,
        String code,
        LocationType type,
        UUID parentLocationId,
        String address,
        String description,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static LocationResponse fromDomain(Location loc) {
        return new LocationResponse(
                loc.getId(),
                loc.getOrganizationId(),
                loc.getName(),
                loc.getCode(),
                loc.getType(),
                loc.getParentLocationId(),
                loc.getAddress(),
                loc.getDescription(),
                loc.isActive(),
                loc.getCreatedAt(),
                loc.getUpdatedAt()
        );
    }
}
