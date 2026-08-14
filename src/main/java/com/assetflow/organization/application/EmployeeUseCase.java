package com.assetflow.organization.application;

import com.assetflow.organization.api.dto.CreateEmployeeRequest;
import com.assetflow.organization.domain.Employee;
import com.assetflow.organization.domain.EmployeeRepository;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public EmployeeUseCase(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Employee createEmployee(CreateEmployeeRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();
        
        employeeRepository.findByOrganizationIdAndEmployeeNumber(organizationId, request.getEmployeeNumber())
                .ifPresent(existing -> {
                    throw new DomainViolationException("Employee number " + request.getEmployeeNumber() + " already exists in this organization");
                });

        Employee employee = new Employee(
                organizationId,
                request.getUserAccountId(),
                request.getDepartmentId(),
                request.getEmployeeNumber(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail()
        );
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<Employee> listEmployees() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return employeeRepository.findByOrganizationId(organizationId);
    }
}
