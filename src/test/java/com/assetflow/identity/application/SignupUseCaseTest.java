package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.SignupRequest;
import com.assetflow.identity.api.dto.UserSummaryDto;
import com.assetflow.identity.application.port.PasswordEncoderPort;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.identity.domain.UserRole;
import com.assetflow.shared.error.DomainViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignupUseCaseTest {

    @Mock
    private UserAccountRepository userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    private SignupUseCase signupUseCase;

    @BeforeEach
    void setUp() {
        signupUseCase = new SignupUseCase(userRepository, passwordEncoder);
    }

    @Test
    void shouldCreateEmployeeOnValidSignup() {
        UUID orgId = UUID.randomUUID();
        SignupRequest request = new SignupRequest("user@example.com", "password123", orgId, null);

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserSummaryDto result = signupUseCase.execute(request);

        assertThat(result.email()).isEqualTo("user@example.com");
        assertThat(result.role()).isEqualTo(UserRole.EMPLOYEE);
        assertThat(result.organizationId()).isEqualTo(orgId);

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(UserRole.EMPLOYEE);
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("encodedPassword");
    }

    @Test
    void shouldRejectSignupWithCustomRole() {
        UUID orgId = UUID.randomUUID();
        SignupRequest request = new SignupRequest("admin@example.com", "password123", orgId, "ADMIN");

        assertThatThrownBy(() -> signupUseCase.execute(request))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("Public signup does not allow specifying custom roles");
    }

    @Test
    void shouldAllowSignupWithExplicitEmployeeRole() {
        UUID orgId = UUID.randomUUID();
        SignupRequest request = new SignupRequest("user@example.com", "password123", orgId, "EMPLOYEE");

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserSummaryDto result = signupUseCase.execute(request);

        assertThat(result.role()).isEqualTo(UserRole.EMPLOYEE);
    }

    @Test
    void shouldRejectDuplicateEmail() {
        UUID orgId = UUID.randomUUID();
        SignupRequest request = new SignupRequest("existing@example.com", "password123", orgId, null);

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> signupUseCase.execute(request))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("An account with email existing@example.com already exists");
    }
}
