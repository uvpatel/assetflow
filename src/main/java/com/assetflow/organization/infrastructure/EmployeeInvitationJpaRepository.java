package com.assetflow.organization.infrastructure;

import com.assetflow.organization.domain.EmployeeInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeInvitationJpaRepository extends JpaRepository<EmployeeInvitation, UUID> {
    Optional<EmployeeInvitation> findByInvitationToken(String invitationToken);
    List<EmployeeInvitation> findByOrganizationIdAndStatus(UUID organizationId, EmployeeInvitation.InvitationStatus status);
}
