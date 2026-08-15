package com.assetflow.shared.infrastructure.persistence;

import com.assetflow.shared.domain.FeatureFlag;
import com.assetflow.shared.domain.FeatureFlagRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaFeatureFlagRepositoryAdapter implements FeatureFlagRepository {

    private final SpringDataFeatureFlagRepository repository;

    public JpaFeatureFlagRepositoryAdapter(SpringDataFeatureFlagRepository repository) {
        this.repository = repository;
    }

    @Override
    public FeatureFlag save(FeatureFlag flag) {
        FeatureFlagJpaEntity entity = toEntity(flag);
        FeatureFlagJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<FeatureFlag> findByOrganizationIdAndFlagKey(UUID organizationId, String flagKey) {
        return repository.findByOrganizationIdAndFlagKey(organizationId, flagKey.toLowerCase()).map(this::toDomain);
    }

    @Override
    public List<FeatureFlag> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private FeatureFlagJpaEntity toEntity(FeatureFlag domain) {
        FeatureFlagJpaEntity entity = new FeatureFlagJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setFlagKey(domain.getFlagKey());
        entity.setDescription(domain.getDescription());
        entity.setEnabled(domain.isEnabled());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private FeatureFlag toDomain(FeatureFlagJpaEntity entity) {
        return new FeatureFlag(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getFlagKey(),
                entity.getDescription(),
                entity.isEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
