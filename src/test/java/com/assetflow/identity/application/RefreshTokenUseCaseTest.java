package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.RefreshTokenRequest;
import com.assetflow.identity.api.dto.TokenRefreshResponse;
import com.assetflow.identity.application.port.TokenProviderPort;
import com.assetflow.identity.domain.RefreshToken;
import com.assetflow.identity.domain.RefreshTokenRepository;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.identity.domain.UserRole;
import com.assetflow.identity.domain.UserStatus;
import com.assetflow.identity.infrastructure.security.JwtProperties;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserAccountRepository userRepository;

    @Mock
    private TokenProviderPort tokenProvider;

    private JwtProperties jwtProperties;
    private RefreshTokenUseCase refreshTokenUseCase;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setAccessTokenExpirationSeconds(900);
        jwtProperties.setRefreshTokenExpirationSeconds(604800);
        refreshTokenUseCase = new RefreshTokenUseCase(refreshTokenRepository, userRepository, tokenProvider, jwtProperties);
    }

    @Test
    void shouldRefreshTokenSuccessfullyAndRotate() {
        String rawToken = "valid-refresh-token";
        String tokenHash = LoginUseCase.hashToken(rawToken);
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        RefreshToken existingToken = RefreshToken.create(userId, tokenHash, Instant.now().plusSeconds(3600));
        UserAccount user = new UserAccount(userId, "user@example.com", "hash", orgId, UserStatus.ACTIVE, UserRole.EMPLOYEE, Instant.now(), Instant.now());

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(existingToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenProvider.generateAccessToken(user)).thenReturn("newAccessToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        TokenRefreshResponse response = refreshTokenUseCase.execute(new RefreshTokenRequest(rawToken));

        assertThat(response.accessToken()).isEqualTo("newAccessToken");
        assertThat(response.refreshToken()).isNotBlank().isNotEqualTo(rawToken);
        assertThat(existingToken.isRevoked()).isTrue();
    }

    @Test
    void shouldTriggerReuseDetectionWhenRevokedTokenPresented() {
        String rawToken = "stolen-revoked-token";
        String tokenHash = LoginUseCase.hashToken(rawToken);
        UUID userId = UUID.randomUUID();

        RefreshToken revokedToken = RefreshToken.create(userId, tokenHash, Instant.now().plusSeconds(3600));
        revokedToken.revoke(Instant.now());

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(revokedToken));

        assertThatThrownBy(() -> refreshTokenUseCase.execute(new RefreshTokenRequest(rawToken)))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("Refresh token reuse detected");

        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    @Test
    void shouldRejectExpiredRefreshToken() {
        String rawToken = "expired-token";
        String tokenHash = LoginUseCase.hashToken(rawToken);
        UUID userId = UUID.randomUUID();

        RefreshToken expiredToken = RefreshToken.create(userId, tokenHash, Instant.now().minusSeconds(10));

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> refreshTokenUseCase.execute(new RefreshTokenRequest(rawToken)))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("Refresh token has expired");
    }

    @Test
    void shouldRejectUnknownRefreshToken() {
        String rawToken = "unknown-token";
        String tokenHash = LoginUseCase.hashToken(rawToken);

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenUseCase.execute(new RefreshTokenRequest(rawToken)))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("Refresh token is invalid");
    }
}
