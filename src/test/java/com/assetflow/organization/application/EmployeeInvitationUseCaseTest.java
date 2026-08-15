package com.assetflow.organization.application;

import com.assetflow.identity.application.port.PasswordEncoderPort;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.organization.api.dto.AcceptInvitationRequest;
import com.assetflow.organization.api.dto.InviteEmployeeRequest;
import com.assetflow.organization.domain.Employee;
import com.assetflow.organization.domain.EmployeeInvitation;
import com.assetflow.organization.domain.EmployeeInvitationRepository;
import com.assetflow.organization.domain.EmployeeRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeInvitationUseCaseTest {

    @Mock
    private EmployeeInvitationRepository invitationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserAccountRepository userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    private EmployeeInvitationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new EmployeeInvitationUseCase(invitationRepository, employeeRepository, userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should successfully create employee invitation")
    void shouldInviteEmployee() {
        UUID orgId = UUID.randomUUID();
        InviteEmployeeRequest request = new InviteEmployeeRequest(
                "newuser@example.com",
                "EMPLOYEE",
                null,
                "Jane",
                "Doe",
                "EMP-100"
        );

        when(invitationRepository.save(any(EmployeeInvitation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        EmployeeInvitation invitation = useCase.inviteEmployee(orgId, request);

        assertThat(invitation.getOrganizationId()).isEqualTo(orgId);
        assertThat(invitation.getEmail()).isEqualTo("newuser@example.com");
        assertThat(invitation.getRole()).isEqualTo("EMPLOYEE");
        assertThat(invitation.getInvitationToken()).isNotBlank();
    }

    @Test
    @DisplayName("Should accept invitation and create user account and employee record")
    void shouldAcceptInvitation() {
        UUID orgId = UUID.randomUUID();
        String token = "valid-token-123";
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        EmployeeInvitation invitation = new EmployeeInvitation(
                orgId,
                "invited@example.com",
                "EMPLOYEE",
                null,
                token,
                expiresAt
        );

        when(invitationRepository.findByInvitationToken(token)).thenReturn(Optional.of(invitation));
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$10$hashedpassword");
        when(userRepository.findByEmail("invited@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserAccount.class))).thenAnswer(inv -> inv.getArgument(0));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        AcceptInvitationRequest request = new AcceptInvitationRequest(token, "SecurePass123!");
        Employee employee = useCase.acceptInvitation(request);

        assertThat(employee.getEmail()).isEqualTo("invited@example.com");
        assertThat(invitation.getStatus()).isEqualTo(EmployeeInvitation.InvitationStatus.ACCEPTED);
        verify(invitationRepository).save(invitation);
    }

    @Test
    @DisplayName("Should throw exception when invitation token is invalid")
    void shouldThrowExceptionWhenTokenNotFound() {
        when(invitationRepository.findByInvitationToken("invalid-token")).thenReturn(Optional.empty());

        AcceptInvitationRequest request = new AcceptInvitationRequest("invalid-token", "Password123!");

        assertThatThrownBy(() -> useCase.acceptInvitation(request))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("Invalid or unknown invitation token.");
    }
}
