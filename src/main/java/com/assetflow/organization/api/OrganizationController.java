package com.assetflow.organization.api;

import com.assetflow.organization.api.dto.CreateDepartmentRequest;
import com.assetflow.organization.api.dto.CreateEmployeeRequest;
import com.assetflow.organization.application.DepartmentUseCase;
import com.assetflow.organization.application.EmployeeUseCase;
import com.assetflow.organization.domain.Department;
import com.assetflow.organization.domain.Employee;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

    private final DepartmentUseCase departmentUseCase;
    private final EmployeeUseCase employeeUseCase;

    public OrganizationController(DepartmentUseCase departmentUseCase, EmployeeUseCase employeeUseCase) {
        this.departmentUseCase = departmentUseCase;
        this.employeeUseCase = employeeUseCase;
    }

    @PostMapping("/departments")
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        Department department = departmentUseCase.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(department);
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> listDepartments() {
        return ResponseEntity.ok(departmentUseCase.listDepartments());
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        Employee employee = employeeUseCase.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> listEmployees() {
        return ResponseEntity.ok(employeeUseCase.listEmployees());
    }
}
