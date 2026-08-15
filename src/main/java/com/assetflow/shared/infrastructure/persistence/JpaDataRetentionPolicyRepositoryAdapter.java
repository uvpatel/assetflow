package com.assetflow.shared.infrastructure.persistence;

import com.assetflow.shared.domain.DataRetentionPolicy;
import com.assetflow.shared.domain.DataRetentionPolicyRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaDataRetentionPolicyRepositoryAdapter implements DataRetentionPolicyRepository {

    private final SpringDataDataRetentionPolicyRepository repository;

    public JpaDataRetentionPolicyRepositoryAdapter(SpringDataDataRetentionPolicyRepository repository) {
        this.repository = repository;
    }

    @Override
    public DataRetentionPolicy save(DataRetentionPolicy policy) {
        DataRetentionPolicyJpaEntity entity = toEntity(policy);
        DataRetentionPolicyJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<DataRetentionPolicy> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<DataRetentionPolicy> findByOrganizationIdAndTargetModule(UUID organizationId, String targetModule) {
        return repository.findByOrganizationIdAndTargetModule(organizationId, targetModule).map(this::toDomain);
    }

    @Override
    public List<DataRetentionPolicy> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private DataRetentionPolicyJpaEntity toEntity(DataRetentionPolicy domain) {
        DataRetentionPolicyJpaEntity entity = new DataRetentionPolicyJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setTargetModule(domain.getTargetModule());
        entity.setRetentionDays(domain.getRetentionDays());
        entity.setAction(domain.getAction().name());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private DataRetentionPolicy toDomain(DataRetentionPolicyJpaEntity entity) {
        return new DataRetentionPolicy(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getTargetModule(),
                entity.getRetentionDays(),
                DataRetentionPolicy.RetentionAction.valueOf(entity.getAction()),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
