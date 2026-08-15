package com.assetflow.asset.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataSoftwareLicenseRepository extends JpaRepository<SoftwareLicenseJpaEntity, UUID> {
    List<SoftwareLicenseJpaEntity> findByOrganizationId(UUID organizationId);
}
