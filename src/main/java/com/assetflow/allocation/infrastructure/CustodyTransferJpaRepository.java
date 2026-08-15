package com.assetflow.allocation.infrastructure;

import com.assetflow.allocation.domain.CustodyTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustodyTransferJpaRepository extends JpaRepository<CustodyTransfer, UUID> {
    List<CustodyTransfer> findByOrganizationId(UUID organizationId);
    List<CustodyTransfer> findByOrganizationIdAndRecipientEmployeeId(UUID organizationId, UUID recipientEmployeeId);
}
