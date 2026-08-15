package com.assetflow.organization.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentHierarchyRepository {
    DepartmentHierarchy save(DepartmentHierarchy hierarchy);
    Optional<DepartmentHierarchy> findByDepartmentId(UUID departmentId);
    List<DepartmentHierarchy> findByParentDepartmentId(UUID parentDepartmentId);
    List<DepartmentHierarchy> findByOrganizationId(UUID organizationId);
}
