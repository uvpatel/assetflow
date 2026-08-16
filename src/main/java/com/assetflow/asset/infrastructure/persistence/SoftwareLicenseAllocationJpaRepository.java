package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.SoftwareLicenseAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SoftwareLicenseAllocationJpaRepository extends JpaRepository<SoftwareLicenseAllocation, UUID> {
    Optional<SoftwareLicenseAllocation> findBySoftwareLicenseIdAndEmployeeId(UUID softwareLicenseId, UUID employeeId);
    List<SoftwareLicenseAllocation> findBySoftwareLicenseId(UUID softwareLicenseId);
}
