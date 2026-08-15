package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.AssetConditionAssessment;
import com.assetflow.asset.domain.AssetConditionAssessmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaAssetConditionAssessmentRepositoryAdapter implements AssetConditionAssessmentRepository {

    private final SpringDataAssetConditionAssessmentRepository repository;

    public JpaAssetConditionAssessmentRepositoryAdapter(SpringDataAssetConditionAssessmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssetConditionAssessment save(AssetConditionAssessment assessment) {
        AssetConditionAssessmentJpaEntity entity = toEntity(assessment);
        AssetConditionAssessmentJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AssetConditionAssessment> findByOrganizationIdAndAssetId(UUID organizationId, UUID assetId) {
        return repository.findByOrganizationIdAndAssetId(organizationId, assetId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private AssetConditionAssessmentJpaEntity toEntity(AssetConditionAssessment domain) {
        AssetConditionAssessmentJpaEntity entity = new AssetConditionAssessmentJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setAssetId(domain.getAssetId());
        entity.setAssessorEmployeeId(domain.getAssessorEmployeeId());
        entity.setConditionScore(domain.getConditionScore());
        entity.setAssessmentNotes(domain.getAssessmentNotes());
        entity.setAssessedAt(domain.getAssessedAt());
        return entity;
    }

    private AssetConditionAssessment toDomain(AssetConditionAssessmentJpaEntity entity) {
        return new AssetConditionAssessment(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAssetId(),
                entity.getAssessorEmployeeId(),
                entity.getConditionScore(),
                entity.getAssessmentNotes(),
                entity.getAssessedAt()
        );
    }
}
