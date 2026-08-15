package com.assetflow.allocation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTemporaryAssetCheckoutRepository extends JpaRepository<TemporaryAssetCheckoutJpaEntity, UUID> {
    List<TemporaryAssetCheckoutJpaEntity> findByOrganizationIdAndBorrowerEmployeeId(UUID organizationId, UUID borrowerEmployeeId);

    @Query("SELECT c FROM TemporaryAssetCheckoutJpaEntity c WHERE c.organizationId = :organizationId AND c.assetId = :assetId AND c.actualReturnTime IS NULL")
    Optional<TemporaryAssetCheckoutJpaEntity> findActiveCheckoutByAssetId(UUID organizationId, UUID assetId);
}
