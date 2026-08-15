package com.assetflow.webhook.infrastructure.persistence;

import com.assetflow.webhook.domain.ApiKey;
import com.assetflow.webhook.domain.ApiKeyRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaApiKeyRepositoryAdapter implements ApiKeyRepository {

    private final SpringDataApiKeyRepository repository;

    public JpaApiKeyRepositoryAdapter(SpringDataApiKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public ApiKey save(ApiKey apiKey) {
        ApiKeyJpaEntity entity = toEntity(apiKey);
        ApiKeyJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ApiKey> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ApiKey> findByKeyHash(String keyHash) {
        return repository.findByKeyHash(keyHash).map(this::toDomain);
    }

    @Override
    public List<ApiKey> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private ApiKeyJpaEntity toEntity(ApiKey domain) {
        ApiKeyJpaEntity entity = new ApiKeyJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setName(domain.getName());
        entity.setKeyPrefix(domain.getKeyPrefix());
        entity.setKeyHash(domain.getKeyHash());
        entity.setScopes(domain.getScopes());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setRevoked(domain.isRevoked());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private ApiKey toDomain(ApiKeyJpaEntity entity) {
        return new ApiKey(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getName(),
                entity.getKeyPrefix(),
                entity.getKeyHash(),
                entity.getScopes(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
