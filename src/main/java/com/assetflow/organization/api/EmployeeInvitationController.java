package com.assetflow.organization.api;

import com.assetflow.organization.api.dto.AcceptInvitationRequest;
import com.assetflow.organization.api.dto.EmployeeInvitationResponse;
import com.assetflow.organization.api.dto.InviteEmployeeRequest;
import com.assetflow.organization.application.EmployeeInvitationUseCase;
import com.assetflow.organization.domain.Employee;
import com.assetflow.organization.domain.EmployeeInvitation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class EmployeeInvitationController {

    private final EmployeeInvitationUseCase useCase;

    public EmployeeInvitationController(EmployeeInvitationUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/api/v1/organizations/{organizationId}/employees/invitations")
    public ResponseEntity<EmployeeInvitationResponse> inviteEmployee(
            @PathVariable UUID organizationId,
            @Valid @RequestBody InviteEmployeeRequest request) {
        EmployeeInvitation invitation = useCase.inviteEmployee(organizationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(EmployeeInvitationResponse.fromDomain(invitation));
    }

    @GetMapping("/api/v1/organizations/{organizationId}/employees/invitations")
    public ResponseEntity<List<EmployeeInvitationResponse>> listPendingInvitations(@PathVariable UUID organizationId) {
        List<EmployeeInvitationResponse> invitations = useCase.listPendingInvitations(organizationId).stream()
                .map(EmployeeInvitationResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(invitations);
    }

    @DeleteMapping("/api/v1/organizations/{organizationId}/employees/invitations/{invitationId}")
    public ResponseEntity<Void> revokeInvitation(
            @PathVariable UUID organizationId,
            @PathVariable UUID invitationId) {
        useCase.revokeInvitation(organizationId, invitationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/employees/invitations/accept")
    public ResponseEntity<Employee> acceptInvitation(@Valid @RequestBody AcceptInvitationRequest request) {
        Employee employee = useCase.acceptInvitation(request);
        return ResponseEntity.ok(employee);
    }
}
