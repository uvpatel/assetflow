package com.assetflow.organization.infrastructure;

import com.assetflow.organization.domain.Department;
import com.assetflow.organization.domain.DepartmentRepository;
import com.assetflow.organization.domain.Employee;
import com.assetflow.organization.domain.EmployeeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataDepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByOrganizationId(UUID organizationId);
}

interface SpringDataEmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findByOrganizationId(UUID organizationId);
    Optional<Employee> findByOrganizationIdAndEmployeeNumber(UUID organizationId, String employeeNumber);
}

@Repository
public class JpaDepartmentRepositoryAdapter implements DepartmentRepository {

    private final SpringDataDepartmentRepository departmentRepo;

    public JpaDepartmentRepositoryAdapter(SpringDataDepartmentRepository departmentRepo) {
        this.departmentRepo = departmentRepo;
    }

    @Override
    public Department save(Department department) {
        return departmentRepo.save(department);
    }

    @Override
    public Optional<Department> findById(UUID id) {
        return departmentRepo.findById(id);
    }

    @Override
    public List<Department> findByOrganizationId(UUID organizationId) {
        return departmentRepo.findByOrganizationId(organizationId);
    }
}

@Repository
class JpaEmployeeRepositoryAdapter implements EmployeeRepository {

    private final SpringDataEmployeeRepository employeeRepo;

    public JpaEmployeeRepositoryAdapter(SpringDataEmployeeRepository employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    @Override
    public Employee save(Employee employee) {
        return employeeRepo.save(employee);
    }

    @Override
    public Optional<Employee> findById(UUID id) {
        return employeeRepo.findById(id);
    }

    @Override
    public List<Employee> findByOrganizationId(UUID organizationId) {
        return employeeRepo.findByOrganizationId(organizationId);
    }

    @Override
    public Optional<Employee> findByOrganizationIdAndEmployeeNumber(UUID organizationId, String employeeNumber) {
        return employeeRepo.findByOrganizationIdAndEmployeeNumber(organizationId, employeeNumber);
    }
}
