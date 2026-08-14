package com.assetflow.organization.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository {
    Employee save(Employee employee);
    Optional<Employee> findById(UUID id);
    List<Employee> findByOrganizationId(UUID organizationId);
    Optional<Employee> findByOrganizationIdAndEmployeeNumber(UUID organizationId, String employeeNumber);
}
