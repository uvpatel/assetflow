package com.assetflow.allocation.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemporaryAssetCheckoutRepository {
    TemporaryAssetCheckout save(TemporaryAssetCheckout checkout);
    Optional<TemporaryAssetCheckout> findById(UUID id);
    Optional<TemporaryAssetCheckout> findActiveCheckoutByAssetId(UUID organizationId, UUID assetId);
    List<TemporaryAssetCheckout> findByOrganizationIdAndBorrowerEmployeeId(UUID organizationId, UUID borrowerEmployeeId);
}
