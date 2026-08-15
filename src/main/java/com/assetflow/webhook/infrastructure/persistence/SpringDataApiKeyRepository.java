package com.assetflow.webhook.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataApiKeyRepository extends JpaRepository<ApiKeyJpaEntity, UUID> {
    Optional<ApiKeyJpaEntity> findByKeyHash(String keyHash);
    List<ApiKeyJpaEntity> findByOrganizationId(UUID organizationId);
}
