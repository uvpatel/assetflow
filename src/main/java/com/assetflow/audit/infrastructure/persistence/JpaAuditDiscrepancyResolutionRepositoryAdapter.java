package com.assetflow.audit.infrastructure.persistence;

import com.assetflow.audit.domain.AuditDiscrepancyResolution;
import com.assetflow.audit.domain.AuditDiscrepancyResolutionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaAuditDiscrepancyResolutionRepositoryAdapter implements AuditDiscrepancyResolutionRepository {

    private final SpringDataAuditDiscrepancyResolutionRepository repository;

    public JpaAuditDiscrepancyResolutionRepositoryAdapter(SpringDataAuditDiscrepancyResolutionRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditDiscrepancyResolution save(AuditDiscrepancyResolution resolution) {
        AuditDiscrepancyResolutionJpaEntity entity = toEntity(resolution);
        AuditDiscrepancyResolutionJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AuditDiscrepancyResolution> findByOrganizationIdAndAuditCycleId(UUID organizationId, UUID auditCycleId) {
        return repository.findByOrganizationIdAndAuditCycleId(organizationId, auditCycleId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private AuditDiscrepancyResolutionJpaEntity toEntity(AuditDiscrepancyResolution domain) {
        AuditDiscrepancyResolutionJpaEntity entity = new AuditDiscrepancyResolutionJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setAuditCycleId(domain.getAuditCycleId());
        entity.setAssetId(domain.getAssetId());
        entity.setDiscrepancyType(domain.getDiscrepancyType().name());
        entity.setResolutionAction(domain.getResolutionAction().name());
        entity.setResolvedBy(domain.getResolvedBy());
        entity.setNotes(domain.getNotes());
        entity.setResolvedAt(domain.getResolvedAt());
        return entity;
    }

    private AuditDiscrepancyResolution toDomain(AuditDiscrepancyResolutionJpaEntity entity) {
        return new AuditDiscrepancyResolution(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAuditCycleId(),
                entity.getAssetId(),
                AuditDiscrepancyResolution.DiscrepancyType.valueOf(entity.getDiscrepancyType()),
                AuditDiscrepancyResolution.ResolutionAction.valueOf(entity.getResolutionAction()),
                entity.getResolvedBy(),
                entity.getNotes(),
                entity.getResolvedAt()
        );
    }
}
