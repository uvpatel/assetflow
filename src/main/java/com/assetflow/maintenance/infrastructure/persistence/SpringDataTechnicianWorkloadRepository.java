package com.assetflow.maintenance.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTechnicianWorkloadRepository extends JpaRepository<TechnicianWorkloadJpaEntity, UUID> {
    Optional<TechnicianWorkloadJpaEntity> findByOrganizationIdAndTechnicianEmployeeId(UUID organizationId, UUID technicianEmployeeId);
    List<TechnicianWorkloadJpaEntity> findByOrganizationId(UUID organizationId);

    @Query("SELECT t FROM TechnicianWorkloadJpaEntity t WHERE t.organizationId = :organizationId AND t.activeTaskCount < t.maxTaskCapacity")
    List<TechnicianWorkloadJpaEntity> findAvailableTechnicians(UUID organizationId);
}
