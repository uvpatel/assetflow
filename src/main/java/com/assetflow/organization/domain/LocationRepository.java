package com.assetflow.organization.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocationRepository {

    Location save(Location location);

    Optional<Location> findById(UUID id);

    Optional<Location> findByOrganizationIdAndCode(UUID organizationId, String code);

    List<Location> findByOrganizationId(UUID organizationId);

    List<Location> findByOrganizationIdAndParentLocationId(UUID organizationId, UUID parentLocationId);

    List<Location> findByOrganizationIdAndType(UUID organizationId, LocationType type);

    boolean existsByOrganizationIdAndCode(UUID organizationId, String code);
}
