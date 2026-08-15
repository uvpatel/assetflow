package com.assetflow.reporting.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataSavedSearchPresetRepository extends JpaRepository<SavedSearchPresetJpaEntity, UUID> {
    @Query("SELECT p FROM SavedSearchPresetJpaEntity p WHERE p.organizationId = :organizationId AND p.targetEntity = :targetEntity AND (p.userId = :userId OR p.isShared = true)")
    List<SavedSearchPresetJpaEntity> findAvailablePresets(UUID organizationId, UUID userId, String targetEntity);
}
