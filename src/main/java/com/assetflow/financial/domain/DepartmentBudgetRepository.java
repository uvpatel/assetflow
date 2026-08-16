package com.assetflow.financial.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentBudgetRepository {
    DepartmentBudget save(DepartmentBudget budget);
    Optional<DepartmentBudget> findById(UUID id);
    Optional<DepartmentBudget> findByOrganizationIdAndId(UUID organizationId, UUID id);
    Optional<DepartmentBudget> findByOrganizationIdAndDepartmentIdAndFiscalYear(UUID organizationId, UUID departmentId, int fiscalYear);
    List<DepartmentBudget> findByOrganizationId(UUID organizationId);
    List<DepartmentBudget> findByOrganizationIdAndFiscalYear(UUID organizationId, int fiscalYear);
}
