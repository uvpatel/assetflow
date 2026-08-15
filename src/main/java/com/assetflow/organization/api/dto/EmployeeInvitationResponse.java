package com.assetflow.organization.api.dto;

import com.assetflow.organization.domain.EmployeeInvitation;

import java.time.Instant;
import java.util.UUID;

public record EmployeeInvitationResponse(
        UUID id,
        UUID organizationId,
        String email,
        String role,
        UUID departmentId,
        String invitationToken,
        String status,
        Instant expiresAt
) {
    public static EmployeeInvitationResponse fromDomain(EmployeeInvitation invitation) {
        return new EmployeeInvitationResponse(
                invitation.getId(),
                invitation.getOrganizationId(),
                invitation.getEmail(),
                invitation.getRole(),
                invitation.getDepartmentId(),
                invitation.getInvitationToken(),
                invitation.getStatus().name(),
                invitation.getExpiresAt()
        );
    }
}
