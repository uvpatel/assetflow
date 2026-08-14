package com.assetflow.allocation.application;

import com.assetflow.allocation.api.dto.CreateTransferRequest;
import com.assetflow.allocation.domain.TransferRequest;
import com.assetflow.allocation.domain.TransferRequestRepository;
import com.assetflow.allocation.domain.TransferRequestStatus;
import com.assetflow.organization.domain.Department;
import com.assetflow.organization.domain.DepartmentRepository;
import com.assetflow.organization.domain.Employee;
import com.assetflow.organization.domain.EmployeeRepository;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.shared.error.ResourceNotFoundException;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferUseCaseTest {

    @Mock
    private TransferRequestRepository transferRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private TransferUseCase transferUseCase;

    private UUID orgId;
    private UUID employeeId;
    private UUID fromDeptId;
    private UUID toDeptId;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        employeeId = UUID.randomUUID();
        fromDeptId = UUID.randomUUID();
        toDeptId = UUID.randomUUID();
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void requestTransfer_success() {
        CreateTransferRequest request = new CreateTransferRequest(employeeId, toDeptId, "Career advancement");
        Employee employee = new Employee(orgId, null, fromDeptId, "EMP001", "John", "Doe", "john@example.com");
        Department toDept = new Department(orgId, "Engineering", "ENG", null);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(toDeptId)).thenReturn(Optional.of(toDept));
        when(transferRequestRepository.save(any(TransferRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransferRequest result = transferUseCase.requestTransfer(request);

        assertThat(result).isNotNull();
        assertThat(result.getOrganizationId()).isEqualTo(orgId);
        assertThat(result.getEmployeeId()).isEqualTo(employeeId);
        assertThat(result.getFromDepartmentId()).isEqualTo(fromDeptId);
        assertThat(result.getToDepartmentId()).isEqualTo(toDeptId);
        assertThat(result.getReason()).isEqualTo("Career advancement");
        assertThat(result.getStatus()).isEqualTo(TransferRequestStatus.PENDING);
    }

    @Test
    void requestTransfer_employeeNotFound() {
        CreateTransferRequest request = new CreateTransferRequest(employeeId, toDeptId, "Transfer");
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferUseCase.requestTransfer(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void approveTransfer_success_updatesEmployeeDepartment() {
        UUID transferRequestId = UUID.randomUUID();
        TransferRequest pendingRequest = new TransferRequest(orgId, employeeId, fromDeptId, toDeptId, "Transfer");
        Employee employee = new Employee(orgId, null, fromDeptId, "EMP001", "John", "Doe", "john@example.com");

        when(transferRequestRepository.findById(transferRequestId)).thenReturn(Optional.of(pendingRequest));
        when(transferRequestRepository.save(any(TransferRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        TransferRequest approved = transferUseCase.approveTransfer(transferRequestId, "admin@example.com");

        assertThat(approved.getStatus()).isEqualTo(TransferRequestStatus.APPROVED);
        assertThat(approved.getReviewedBy()).isEqualTo("admin@example.com");
        assertThat(approved.getReviewedAt()).isNotNull();

        ArgumentCaptor<Employee> empCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(empCaptor.capture());
        assertThat(empCaptor.getValue().getDepartmentId()).isEqualTo(toDeptId);
    }

    @Test
    void approveTransfer_notPending_throwsException() {
        UUID transferRequestId = UUID.randomUUID();
        TransferRequest request = new TransferRequest(orgId, employeeId, fromDeptId, toDeptId, "Transfer");
        request.approve("reviewer1");

        when(transferRequestRepository.findById(transferRequestId)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> transferUseCase.approveTransfer(transferRequestId, "reviewer2"))
                .isInstanceOf(DomainViolationException.class);
    }

    @Test
    void rejectTransfer_success() {
        UUID transferRequestId = UUID.randomUUID();
        TransferRequest pendingRequest = new TransferRequest(orgId, employeeId, fromDeptId, toDeptId, "Transfer");

        when(transferRequestRepository.findById(transferRequestId)).thenReturn(Optional.of(pendingRequest));
        when(transferRequestRepository.save(any(TransferRequest.class))).thenAnswer(i -> i.getArgument(0));

        TransferRequest rejected = transferUseCase.rejectTransfer(transferRequestId, "admin@example.com", "Headcount freeze");

        assertThat(rejected.getStatus()).isEqualTo(TransferRequestStatus.REJECTED);
        assertThat(rejected.getReviewedBy()).isEqualTo("admin@example.com");
        assertThat(rejected.getRejectionReason()).isEqualTo("Headcount freeze");
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void listTransferRequests_success() {
        TransferRequest req = new TransferRequest(orgId, employeeId, fromDeptId, toDeptId, "Reason");
        when(transferRequestRepository.findByOrganizationId(orgId)).thenReturn(List.of(req));

        List<TransferRequest> result = transferUseCase.listTransferRequests();

        assertThat(result).hasSize(1);
    }
}
