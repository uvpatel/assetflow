package com.assetflow.identity.application;

import com.assetflow.identity.domain.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GlobalLogoutUseCaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private GlobalLogoutUseCase globalLogoutUseCase;

    @BeforeEach
    void setUp() {
        globalLogoutUseCase = new GlobalLogoutUseCase(refreshTokenRepository);
    }

    @Test
    @DisplayName("Should revoke all active sessions for target user during global logout")
    void shouldRevokeAllSessionsOnGlobalLogout() {
        UUID userId = UUID.randomUUID();

        globalLogoutUseCase.execute(userId);

        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }
}
