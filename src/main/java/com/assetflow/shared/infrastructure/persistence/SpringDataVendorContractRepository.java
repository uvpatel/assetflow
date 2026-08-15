package com.assetflow.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataVendorContractRepository extends JpaRepository<VendorContractJpaEntity, UUID> {
    List<VendorContractJpaEntity> findByOrganizationIdAndVendorId(UUID organizationId, UUID vendorId);
    List<VendorContractJpaEntity> findByOrganizationId(UUID organizationId);
}
