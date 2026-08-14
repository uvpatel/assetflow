package com.assetflow.organization.application;

import com.assetflow.organization.api.dto.CreateDepartmentRequest;
import com.assetflow.organization.domain.Department;
import com.assetflow.organization.domain.DepartmentRepository;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentUseCase {

    private final DepartmentRepository departmentRepository;

    public DepartmentUseCase(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public Department createDepartment(CreateDepartmentRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();
        Department department = new Department(
                organizationId,
                request.getName(),
                request.getCode(),
                request.getParentDepartmentId()
        );
        return departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    public List<Department> listDepartments() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return departmentRepository.findByOrganizationId(organizationId);
    }
}
