package com.assetflow.organization.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeInvitationRepository {
    EmployeeInvitation save(EmployeeInvitation invitation);
    Optional<EmployeeInvitation> findById(UUID id);
    Optional<EmployeeInvitation> findByInvitationToken(String invitationToken);
    List<EmployeeInvitation> findByOrganizationIdAndStatus(UUID organizationId, EmployeeInvitation.InvitationStatus status);
}
