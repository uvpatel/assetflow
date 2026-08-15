package com.assetflow.asset.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SoftwareLicenseRepository {
    SoftwareLicense save(SoftwareLicense license);
    Optional<SoftwareLicense> findById(UUID id);
    List<SoftwareLicense> findByOrganizationId(UUID organizationId);
}
