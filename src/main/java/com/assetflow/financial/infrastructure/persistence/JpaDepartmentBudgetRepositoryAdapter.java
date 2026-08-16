package com.assetflow.financial.infrastructure.persistence;

import com.assetflow.financial.domain.DepartmentBudget;
import com.assetflow.financial.domain.DepartmentBudgetRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaDepartmentBudgetRepositoryAdapter implements DepartmentBudgetRepository {

    private final SpringDataDepartmentBudgetRepository repository;

    public JpaDepartmentBudgetRepositoryAdapter(SpringDataDepartmentBudgetRepository repository) {
        this.repository = repository;
    }

    @Override
    public DepartmentBudget save(DepartmentBudget domain) {
        DepartmentBudgetJpaEntity entity = toEntity(domain);
        DepartmentBudgetJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<DepartmentBudget> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<DepartmentBudget> findByOrganizationIdAndId(UUID organizationId, UUID id) {
        return repository.findByOrganizationIdAndId(organizationId, id).map(this::toDomain);
    }

    @Override
    public Optional<DepartmentBudget> findByOrganizationIdAndDepartmentIdAndFiscalYear(UUID organizationId, UUID departmentId, int fiscalYear) {
        return repository.findByOrganizationIdAndDepartmentIdAndFiscalYear(organizationId, departmentId, fiscalYear).map(this::toDomain);
    }

    @Override
    public List<DepartmentBudget> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<DepartmentBudget> findByOrganizationIdAndFiscalYear(UUID organizationId, int fiscalYear) {
        return repository.findByOrganizationIdAndFiscalYear(organizationId, fiscalYear).stream().map(this::toDomain).toList();
    }

    private DepartmentBudgetJpaEntity toEntity(DepartmentBudget domain) {
        DepartmentBudgetJpaEntity entity = new DepartmentBudgetJpaEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setDepartmentId(domain.getDepartmentId());
        entity.setFiscalYear(domain.getFiscalYear());
        entity.setAllocatedAmount(domain.getAllocatedAmount());
        entity.setCommittedAmount(domain.getCommittedAmount());
        entity.setSpentAmount(domain.getSpentAmount());
        entity.setCurrency(domain.getCurrency());
        entity.setStatus(domain.getStatus().name());
        entity.setNotes(domain.getNotes());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private DepartmentBudget toDomain(DepartmentBudgetJpaEntity entity) {
        DepartmentBudget.BudgetStatus status = DepartmentBudget.BudgetStatus.ACTIVE;
        try {
            status = DepartmentBudget.BudgetStatus.valueOf(entity.getStatus().toUpperCase());
        } catch (Exception ignored) {}

        return new DepartmentBudget(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getDepartmentId(),
                entity.getFiscalYear(),
                entity.getAllocatedAmount(),
                entity.getCommittedAmount(),
                entity.getSpentAmount(),
                entity.getCurrency(),
                status,
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
