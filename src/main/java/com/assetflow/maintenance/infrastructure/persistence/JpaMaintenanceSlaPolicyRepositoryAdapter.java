package com.assetflow.maintenance.infrastructure.persistence;

import com.assetflow.maintenance.domain.MaintenanceSlaPolicy;
import com.assetflow.maintenance.domain.MaintenanceSlaPolicyRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaMaintenanceSlaPolicyRepositoryAdapter implements MaintenanceSlaPolicyRepository {

    private final SpringDataMaintenanceSlaPolicyRepository repository;

    public JpaMaintenanceSlaPolicyRepositoryAdapter(SpringDataMaintenanceSlaPolicyRepository repository) {
        this.repository = repository;
    }

    @Override
    public MaintenanceSlaPolicy save(MaintenanceSlaPolicy policy) {
        MaintenanceSlaPolicyJpaEntity entity = toEntity(policy);
        MaintenanceSlaPolicyJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MaintenanceSlaPolicy> findByOrganizationIdAndPriority(UUID organizationId, MaintenanceSlaPolicy.Priority priority) {
        return repository.findByOrganizationIdAndPriority(organizationId, priority.name()).map(this::toDomain);
    }

    @Override
    public List<MaintenanceSlaPolicy> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).stream().map(this::toDomain).toList();
    }

    private MaintenanceSlaPolicyJpaEntity toEntity(MaintenanceSlaPolicy domain) {
        MaintenanceSlaPolicyJpaEntity entity = new MaintenanceSlaPolicyJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setPriority(domain.getPriority().name());
        entity.setResponseDeadlineHours(domain.getResponseDeadlineHours());
        entity.setResolutionDeadlineHours(domain.getResolutionDeadlineHours());
        entity.setEscalationEmail(domain.getEscalationEmail());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private MaintenanceSlaPolicy toDomain(MaintenanceSlaPolicyJpaEntity entity) {
        return new MaintenanceSlaPolicy(
                entity.getId(),
                entity.getOrganizationId(),
                MaintenanceSlaPolicy.Priority.valueOf(entity.getPriority()),
                entity.getResponseDeadlineHours(),
                entity.getResolutionDeadlineHours(),
                entity.getEscalationEmail(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
