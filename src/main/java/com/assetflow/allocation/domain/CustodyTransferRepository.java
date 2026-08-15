package com.assetflow.allocation.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustodyTransferRepository {
    CustodyTransfer save(CustodyTransfer transfer);
    Optional<CustodyTransfer> findById(UUID id);
    List<CustodyTransfer> findByOrganizationId(UUID organizationId);
    List<CustodyTransfer> findByOrganizationIdAndRecipientEmployeeId(UUID organizationId, UUID recipientEmployeeId);
}
