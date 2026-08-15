package com.assetflow.maintenance.infrastructure.persistence;

import com.assetflow.maintenance.domain.TechnicianWorkload;
import com.assetflow.maintenance.domain.TechnicianWorkloadRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaTechnicianWorkloadRepositoryAdapter implements TechnicianWorkloadRepository {

    private final SpringDataTechnicianWorkloadRepository repository;

    public JpaTechnicianWorkloadRepositoryAdapter(SpringDataTechnicianWorkloadRepository repository) {
        this.repository = repository;
    }

    @Override
    public TechnicianWorkload save(TechnicianWorkload workload) {
        TechnicianWorkloadJpaEntity entity = toEntity(workload);
        TechnicianWorkloadJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TechnicianWorkload> findByOrganizationIdAndTechnicianEmployeeId(UUID organizationId, UUID technicianEmployeeId) {
        return repository.findByOrganizationIdAndTechnicianEmployeeId(organizationId, technicianEmployeeId).map(this::toDomain);
    }

    @Override
    public List<TechnicianWorkload> findAvailableTechnicians(UUID organizationId) {
        return repository.findAvailableTechnicians(organizationId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<TechnicianWorkload> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).stream().map(this::toDomain).toList();
    }

    private TechnicianWorkloadJpaEntity toEntity(TechnicianWorkload domain) {
        TechnicianWorkloadJpaEntity entity = new TechnicianWorkloadJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setTechnicianEmployeeId(domain.getTechnicianEmployeeId());
        entity.setActiveTaskCount(domain.getActiveTaskCount());
        entity.setMaxTaskCapacity(domain.getMaxTaskCapacity());
        entity.setSpecialization(domain.getSpecialization());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private TechnicianWorkload toDomain(TechnicianWorkloadJpaEntity entity) {
        return new TechnicianWorkload(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getTechnicianEmployeeId(),
                entity.getActiveTaskCount(),
                entity.getMaxTaskCapacity(),
                entity.getSpecialization(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
