package com.assetflow.asset.infrastructure.persistence;

import com.assetflow.asset.domain.LicenseComplianceAudit;
import com.assetflow.asset.domain.LicenseComplianceAuditRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaLicenseComplianceAuditRepositoryAdapter implements LicenseComplianceAuditRepository {

    private final SpringDataLicenseComplianceAuditRepository repository;

    public JpaLicenseComplianceAuditRepositoryAdapter(SpringDataLicenseComplianceAuditRepository repository) {
        this.repository = repository;
    }

    @Override
    public LicenseComplianceAudit save(LicenseComplianceAudit audit) {
        LicenseComplianceAuditJpaEntity entity = toEntity(audit);
        LicenseComplianceAuditJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<LicenseComplianceAudit> findByOrganizationIdAndLicenseId(UUID organizationId, UUID licenseId) {
        return repository.findByOrganizationIdAndLicenseId(organizationId, licenseId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private LicenseComplianceAuditJpaEntity toEntity(LicenseComplianceAudit domain) {
        LicenseComplianceAuditJpaEntity entity = new LicenseComplianceAuditJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setLicenseId(domain.getLicenseId());
        entity.setTotalSeats(domain.getTotalSeats());
        entity.setAllocatedSeats(domain.getAllocatedSeats());
        entity.setComplianceStatus(domain.getComplianceStatus().name());
        entity.setAuditNotes(domain.getAuditNotes());
        entity.setAuditedAt(domain.getAuditedAt());
        return entity;
    }

    private LicenseComplianceAudit toDomain(LicenseComplianceAuditJpaEntity entity) {
        return new LicenseComplianceAudit(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getLicenseId(),
                entity.getTotalSeats(),
                entity.getAllocatedSeats(),
                LicenseComplianceAudit.ComplianceStatus.valueOf(entity.getComplianceStatus()),
                entity.getAuditNotes(),
                entity.getAuditedAt()
        );
    }
}
