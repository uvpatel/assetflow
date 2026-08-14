package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.AuthResponse;
import com.assetflow.identity.api.dto.LoginRequest;
import com.assetflow.identity.application.port.PasswordEncoderPort;
import com.assetflow.identity.domain.RefreshToken;
import com.assetflow.identity.domain.RefreshTokenRepository;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.identity.domain.UserRole;
import com.assetflow.identity.domain.UserStatus;
import com.assetflow.identity.infrastructure.security.JwtProperties;
import com.assetflow.identity.infrastructure.security.JwtTokenProvider;
import com.assetflow.shared.error.DomainViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserAccountRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private JwtProperties jwtProperties;
    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setAccessTokenExpirationSeconds(900);
        jwtProperties.setRefreshTokenExpirationSeconds(604800);
        loginUseCase = new LoginUseCase(userRepository, refreshTokenRepository, passwordEncoder, jwtTokenProvider, jwtProperties);
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UserAccount user = new UserAccount(
                userId, "user@example.com", "encodedHash", orgId,
                UserStatus.ACTIVE, UserRole.EMPLOYEE, Instant.now(), Instant.now()
        );

        LoginRequest request = new LoginRequest("user@example.com", "password123");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedHash")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn("mockedAccessToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = loginUseCase.execute(request);

        assertThat(response.accessToken()).isEqualTo("mockedAccessToken");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.expiresIn()).isEqualTo(900);
        assertThat(response.user().email()).isEqualTo("user@example.com");

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldFailLoginWithInvalidPassword() {
        UserAccount user = new UserAccount(
                UUID.randomUUID(), "user@example.com", "encodedHash", UUID.randomUUID(),
                UserStatus.ACTIVE, UserRole.EMPLOYEE, Instant.now(), Instant.now()
        );

        LoginRequest request = new LoginRequest("user@example.com", "wrongPassword");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedHash")).thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void shouldFailLoginForNonExistentUser() {
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void shouldFailLoginForInactiveUser() {
        UserAccount user = new UserAccount(
                UUID.randomUUID(), "inactive@example.com", "encodedHash", UUID.randomUUID(),
                UserStatus.INACTIVE, UserRole.EMPLOYEE, Instant.now(), Instant.now()
        );

        LoginRequest request = new LoginRequest("inactive@example.com", "password123");

        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedHash")).thenReturn(true);

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("User account is not active");
    }
}
