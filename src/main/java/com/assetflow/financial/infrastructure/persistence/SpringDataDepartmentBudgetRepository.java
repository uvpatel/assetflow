package com.assetflow.financial.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataDepartmentBudgetRepository extends JpaRepository<DepartmentBudgetJpaEntity, UUID> {
    Optional<DepartmentBudgetJpaEntity> findByOrganizationIdAndId(UUID organizationId, UUID id);
    Optional<DepartmentBudgetJpaEntity> findByOrganizationIdAndDepartmentIdAndFiscalYear(UUID organizationId, UUID departmentId, int fiscalYear);
    List<DepartmentBudgetJpaEntity> findByOrganizationId(UUID organizationId);
    List<DepartmentBudgetJpaEntity> findByOrganizationIdAndFiscalYear(UUID organizationId, int fiscalYear);
}
