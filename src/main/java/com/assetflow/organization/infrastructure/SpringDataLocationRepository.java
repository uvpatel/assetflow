package com.assetflow.organization.infrastructure;

import com.assetflow.organization.domain.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataLocationRepository extends JpaRepository<LocationJpaEntity, UUID> {

    Optional<LocationJpaEntity> findByOrganizationIdAndCode(UUID organizationId, String code);

    List<LocationJpaEntity> findByOrganizationIdOrderByCodeAsc(UUID organizationId);

    List<LocationJpaEntity> findByOrganizationIdAndParentLocationIdOrderByCodeAsc(UUID organizationId, UUID parentLocationId);

    List<LocationJpaEntity> findByOrganizationIdAndTypeOrderByCodeAsc(UUID organizationId, LocationType type);

    boolean existsByOrganizationIdAndCode(UUID organizationId, String code);
}
