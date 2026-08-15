package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.DeviceSessionDto;
import com.assetflow.identity.domain.RefreshToken;
import com.assetflow.identity.domain.RefreshTokenRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionManagementUseCaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private SessionManagementUseCase sessionManagementUseCase;

    @BeforeEach
    void setUp() {
        sessionManagementUseCase = new SessionManagementUseCase(refreshTokenRepository);
    }

    @Test
    @DisplayName("Should list active device sessions for target user")
    void shouldListActiveSessions() {
        UUID userId = UUID.randomUUID();
        RefreshToken token = RefreshToken.create(userId, "hash123", Instant.now().plusSeconds(3600));

        when(refreshTokenRepository.findAllActiveByUserId(userId)).thenReturn(List.of(token));

        List<DeviceSessionDto> sessions = sessionManagementUseCase.listActiveSessions(userId);

        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).sessionId()).isEqualTo(token.getId());
        assertThat(sessions.get(0).userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should revoke target session if owned by current user")
    void shouldRevokeTargetSessionWhenOwnedByUser() {
        UUID userId = UUID.randomUUID();
        RefreshToken token = RefreshToken.create(userId, "hash123", Instant.now().plusSeconds(3600));

        when(refreshTokenRepository.findById(token.getId())).thenReturn(Optional.of(token));

        sessionManagementUseCase.revokeSession(token.getId(), userId);

        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    @Test
    @DisplayName("Should reject revoking session belonging to another user")
    void shouldRejectRevokingSessionOfAnotherUser() {
        UUID userId = UUID.randomUUID();
        UUID attackerId = UUID.randomUUID();
        RefreshToken token = RefreshToken.create(userId, "hash123", Instant.now().plusSeconds(3600));

        when(refreshTokenRepository.findById(token.getId())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> sessionManagementUseCase.revokeSession(token.getId(), attackerId))
                .isInstanceOf(DomainViolationException.class)
                .hasMessageContaining("Cannot revoke a session belonging to another user");
    }
}
