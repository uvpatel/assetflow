package com.assetflow.webhook.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository {
    ApiKey save(ApiKey apiKey);
    Optional<ApiKey> findById(UUID id);
    Optional<ApiKey> findByKeyHash(String keyHash);
    List<ApiKey> findByOrganizationId(UUID organizationId);
}
