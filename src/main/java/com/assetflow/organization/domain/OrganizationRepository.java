package com.assetflow.organization.domain;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository {
    Organization save(Organization organization);
    Optional<Organization> findById(UUID id);
    Optional<Organization> findByCode(String code);
    boolean existsByCode(String code);
}
