package com.assetflow.organization.api.dto;

import com.assetflow.organization.domain.LocationType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record LocationTreeNodeDto(
        UUID id,
        String name,
        String code,
        LocationType type,
        UUID parentLocationId,
        String address,
        boolean active,
        List<LocationTreeNodeDto> children
) {
    public LocationTreeNodeDto(UUID id, String name, String code, LocationType type, UUID parentLocationId, String address, boolean active) {
        this(id, name, code, type, parentLocationId, address, active, new ArrayList<>());
    }
}
