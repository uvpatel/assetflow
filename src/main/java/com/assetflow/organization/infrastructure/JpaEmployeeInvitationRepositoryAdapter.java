package com.assetflow.organization.infrastructure;

import com.assetflow.organization.domain.EmployeeInvitation;
import com.assetflow.organization.domain.EmployeeInvitationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaEmployeeInvitationRepositoryAdapter implements EmployeeInvitationRepository {

    private final EmployeeInvitationJpaRepository jpaRepository;

    public JpaEmployeeInvitationRepositoryAdapter(EmployeeInvitationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public EmployeeInvitation save(EmployeeInvitation invitation) {
        return jpaRepository.save(invitation);
    }

    @Override
    public Optional<EmployeeInvitation> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<EmployeeInvitation> findByInvitationToken(String invitationToken) {
        return jpaRepository.findByInvitationToken(invitationToken);
    }

    @Override
    public List<EmployeeInvitation> findByOrganizationIdAndStatus(UUID organizationId, EmployeeInvitation.InvitationStatus status) {
        return jpaRepository.findByOrganizationIdAndStatus(organizationId, status);
    }
}
