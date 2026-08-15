package com.assetflow.reporting.infrastructure.persistence;

import com.assetflow.reporting.domain.SavedSearchPreset;
import com.assetflow.reporting.domain.SavedSearchPresetRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaSavedSearchPresetRepositoryAdapter implements SavedSearchPresetRepository {

    private final SpringDataSavedSearchPresetRepository repository;

    public JpaSavedSearchPresetRepositoryAdapter(SpringDataSavedSearchPresetRepository repository) {
        this.repository = repository;
    }

    @Override
    public SavedSearchPreset save(SavedSearchPreset preset) {
        SavedSearchPresetJpaEntity entity = toEntity(preset);
        SavedSearchPresetJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<SavedSearchPreset> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<SavedSearchPreset> findAvailablePresets(UUID organizationId, UUID userId, String targetEntity) {
        return repository.findAvailablePresets(organizationId, userId, targetEntity)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private SavedSearchPresetJpaEntity toEntity(SavedSearchPreset domain) {
        SavedSearchPresetJpaEntity entity = new SavedSearchPresetJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setUserId(domain.getUserId());
        entity.setName(domain.getName());
        entity.setTargetEntity(domain.getTargetEntity());
        entity.setFilterJson(domain.getFilterJson());
        entity.setShared(domain.isShared());
        entity.setDefault(domain.isDefault());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private SavedSearchPreset toDomain(SavedSearchPresetJpaEntity entity) {
        return new SavedSearchPreset(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getUserId(),
                entity.getName(),
                entity.getTargetEntity(),
                entity.getFilterJson(),
                entity.isShared(),
                entity.isDefault(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
