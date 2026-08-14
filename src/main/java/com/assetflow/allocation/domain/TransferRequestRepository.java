package com.assetflow.allocation.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferRequestRepository {
    TransferRequest save(TransferRequest transferRequest);
    Optional<TransferRequest> findById(UUID id);
    List<TransferRequest> findByOrganizationId(UUID organizationId);
    List<TransferRequest> findByEmployeeId(UUID employeeId);
}
