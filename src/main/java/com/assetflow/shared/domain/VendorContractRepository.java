package com.assetflow.shared.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendorContractRepository {
    VendorContract save(VendorContract contract);
    Optional<VendorContract> findById(UUID id);
    List<VendorContract> findByOrganizationIdAndVendorId(UUID organizationId, UUID vendorId);
    List<VendorContract> findByOrganizationId(UUID organizationId);
}
