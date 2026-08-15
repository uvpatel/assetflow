package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.AuthResponse;
import com.assetflow.identity.api.dto.SignupRequest;
import com.assetflow.identity.application.port.PasswordEncoderPort;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.identity.domain.UserRole;
import com.assetflow.shared.error.DomainViolationException;
import com.assetflow.identity.application.port.TokenProviderPort;
import com.assetflow.identity.infrastructure.security.JwtProperties;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignupUseCaseTest {

    @Mock
    private UserAccountRepository userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private TokenProviderPort tokenProvider;

    @Mock
    private JwtProperties jwtProperties;

    private SignupUseCase signupUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(jwtProperties.getAccessTokenExpirationSeconds()).thenReturn(3600L);
        signupUseCase = new SignupUseCase(userRepository, passwordEncoder, tokenProvider, jwtProperties);
    }

    @Test
    void shouldCreateEmployeeOnValidSignup() {
        UUID orgId = UUID.randomUUID();
        SignupRequest request = new SignupRequest("user@example.com", "password123", orgId, null);

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse result = signupUseCase.execute(request);

        assertThat(result.user().email()).isEqualTo("user@example.com");
        assertThat(result.user().role()).isEqualTo(UserRole.EMPLOYEE);
        assertThat(result.user().organizationId()).isEqualTo(orgId);

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(UserRole.EMPLOYEE);
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("encodedPassword");
    }

    @Test
    void shouldRejectDuplicateEmail() {
        UUID orgId = UUID.randomUUID();
        SignupRequest request = new SignupRequest("existing@example.com", "password123", orgId, null);

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> signupUseCase.execute(request))
                .isInstanceOf(DomainViolationException.class);
    }
}
