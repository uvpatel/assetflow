package com.assetflow.shared.infrastructure.persistence;

import com.assetflow.shared.domain.VendorPerformanceEvaluation;
import com.assetflow.shared.domain.VendorPerformanceEvaluationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaVendorPerformanceEvaluationRepositoryAdapter implements VendorPerformanceEvaluationRepository {

    private final SpringDataVendorPerformanceEvaluationRepository repository;

    public JpaVendorPerformanceEvaluationRepositoryAdapter(SpringDataVendorPerformanceEvaluationRepository repository) {
        this.repository = repository;
    }

    @Override
    public VendorPerformanceEvaluation save(VendorPerformanceEvaluation evaluation) {
        VendorPerformanceEvaluationJpaEntity entity = toEntity(evaluation);
        VendorPerformanceEvaluationJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<VendorPerformanceEvaluation> findByOrganizationIdAndVendorId(UUID organizationId, UUID vendorId) {
        return repository.findByOrganizationIdAndVendorId(organizationId, vendorId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private VendorPerformanceEvaluationJpaEntity toEntity(VendorPerformanceEvaluation domain) {
        VendorPerformanceEvaluationJpaEntity entity = new VendorPerformanceEvaluationJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setVendorId(domain.getVendorId());
        entity.setEvaluatorUserId(domain.getEvaluatorUserId());
        entity.setDeliveryScore(domain.getDeliveryScore());
        entity.setQualityScore(domain.getQualityScore());
        entity.setCommunicationScore(domain.getCommunicationScore());
        entity.setOverallRating(domain.getOverallRating());
        entity.setEvaluationNotes(domain.getEvaluationNotes());
        entity.setEvaluatedAt(domain.getEvaluatedAt());
        return entity;
    }

    private VendorPerformanceEvaluation toDomain(VendorPerformanceEvaluationJpaEntity entity) {
        return new VendorPerformanceEvaluation(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getVendorId(),
                entity.getEvaluatorUserId(),
                entity.getDeliveryScore(),
                entity.getQualityScore(),
                entity.getCommunicationScore(),
                entity.getOverallRating(),
                entity.getEvaluationNotes(),
                entity.getEvaluatedAt()
        );
    }
}
