package com.assetflow.allocation.application;

import com.assetflow.allocation.api.dto.CreateTransferRequest;
import com.assetflow.allocation.domain.TransferRequest;
import com.assetflow.allocation.domain.TransferRequestRepository;
import com.assetflow.organization.domain.Department;
import com.assetflow.organization.domain.DepartmentRepository;
import com.assetflow.organization.domain.Employee;
import com.assetflow.organization.domain.EmployeeRepository;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TransferUseCase {

    private final TransferRequestRepository transferRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public TransferUseCase(TransferRequestRepository transferRequestRepository,
                           EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository) {
        this.transferRequestRepository = transferRequestRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public TransferRequest requestTransfer(CreateTransferRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

        Department toDepartment = departmentRepository.findById(request.getToDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.getToDepartmentId()));

        TransferRequest transferRequest = new TransferRequest(
                organizationId,
                request.getEmployeeId(),
                employee.getDepartmentId(),
                request.getToDepartmentId(),
                request.getReason()
        );

        return transferRequestRepository.save(transferRequest);
    }

    @Transactional
    public TransferRequest approveTransfer(UUID transferRequestId, String reviewer) {
        TransferRequest transferRequest = transferRequestRepository.findById(transferRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("TransferRequest", transferRequestId));

        if (!transferRequest.isPending()) {
            throw new DomainViolationException("Transfer request is not in PENDING status.");
        }

        transferRequest.approve(reviewer);
        TransferRequest saved = transferRequestRepository.save(transferRequest);

        Employee employee = employeeRepository.findById(transferRequest.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", transferRequest.getEmployeeId()));

        employee.setDepartmentId(transferRequest.getToDepartmentId());
        employeeRepository.save(employee);

        return saved;
    }

    @Transactional
    public TransferRequest rejectTransfer(UUID transferRequestId, String reviewer, String rejectionReason) {
        TransferRequest transferRequest = transferRequestRepository.findById(transferRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("TransferRequest", transferRequestId));

        if (!transferRequest.isPending()) {
            throw new DomainViolationException("Transfer request is not in PENDING status.");
        }

        transferRequest.reject(reviewer, rejectionReason);
        return transferRequestRepository.save(transferRequest);
    }

    @Transactional(readOnly = true)
    public List<TransferRequest> listTransferRequests() {
        UUID organizationId = TenantContext.requireOrganizationId();
        return transferRequestRepository.findByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public TransferRequest getTransferRequest(UUID id) {
        return transferRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransferRequest", id));
    }
}
