package com.assetflow.organization.application;

import com.assetflow.identity.application.port.PasswordEncoderPort;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.identity.domain.UserRole;
import com.assetflow.identity.domain.UserStatus;
import com.assetflow.organization.api.dto.AcceptInvitationRequest;
import com.assetflow.organization.api.dto.InviteEmployeeRequest;
import com.assetflow.organization.domain.Employee;
import com.assetflow.organization.domain.EmployeeInvitation;
import com.assetflow.organization.domain.EmployeeInvitationRepository;
import com.assetflow.organization.domain.EmployeeRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeInvitationUseCase {

    private static final Logger log = LoggerFactory.getLogger(EmployeeInvitationUseCase.class);
    private final EmployeeInvitationRepository invitationRepository;
    private final EmployeeRepository employeeRepository;
    private final UserAccountRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public EmployeeInvitationUseCase(EmployeeInvitationRepository invitationRepository,
                                     EmployeeRepository employeeRepository,
                                     UserAccountRepository userRepository,
                                     PasswordEncoderPort passwordEncoder) {
        this.invitationRepository = invitationRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EmployeeInvitation inviteEmployee(UUID organizationId, InviteEmployeeRequest request) {
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        EmployeeInvitation invitation = new EmployeeInvitation(
                organizationId,
                request.email(),
                request.role(),
                request.departmentId(),
                token,
                expiresAt
        );

        log.info("Creating employee invitation for email {} in organization {}", request.email(), organizationId);
        return invitationRepository.save(invitation);
    }

    @Transactional
    public Employee acceptInvitation(AcceptInvitationRequest request) {
        EmployeeInvitation invitation = invitationRepository.findByInvitationToken(request.invitationToken())
                .orElseThrow(() -> new DomainViolationException("Invitation Not Found", "Invalid or unknown invitation token."));

        Instant now = Instant.now();
        invitation.accept(now);
        invitationRepository.save(invitation);

        String email = invitation.getEmail();
        UserRole assignedRole = parseRole(invitation.getRole());
        String passwordHash = passwordEncoder.encode(request.password());

        UserAccount userAccount = userRepository.findByEmail(email)
                .orElseGet(() -> new UserAccount(
                        UUID.randomUUID(),
                        email,
                        passwordHash,
                        invitation.getOrganizationId(),
                        UserStatus.ACTIVE,
                        assignedRole,
                        now,
                        now
                ));

        UserAccount savedUser = userRepository.save(userAccount);

        Employee employee = new Employee(
                invitation.getOrganizationId(),
                savedUser.getId(),
                invitation.getDepartmentId(),
                "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                email.split("@")[0],
                "User",
                email
        );

        log.info("Accepted invitation and created employee {} for user {}", employee.getId(), savedUser.getId());
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeInvitation> listPendingInvitations(UUID organizationId) {
        return invitationRepository.findByOrganizationIdAndStatus(organizationId, EmployeeInvitation.InvitationStatus.PENDING);
    }

    @Transactional
    public void revokeInvitation(UUID organizationId, UUID invitationId) {
        EmployeeInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new DomainViolationException("Invitation Not Found", "Target invitation was not found."));

        if (!invitation.getOrganizationId().equals(organizationId)) {
            throw new DomainViolationException("Access Denied", "Cannot modify invitation belonging to another organization.");
        }

        invitation.revoke();
        invitationRepository.save(invitation);
        log.info("Revoked invitation {} for organization {}", invitationId, organizationId);
    }

    private UserRole parseRole(String roleName) {
        try {
            return UserRole.valueOf(roleName.toUpperCase());
        } catch (Exception e) {
            return UserRole.EMPLOYEE;
        }
    }
}
