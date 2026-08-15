package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.DeviceSessionDto;
import com.assetflow.identity.domain.RefreshToken;
import com.assetflow.identity.domain.RefreshTokenRepository;
import com.assetflow.shared.error.DomainViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SessionManagementUseCase {

    private static final Logger log = LoggerFactory.getLogger(SessionManagementUseCase.class);
    private final RefreshTokenRepository refreshTokenRepository;

    public SessionManagementUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional(readOnly = true)
    public List<DeviceSessionDto> listActiveSessions(UUID userId) {
        return refreshTokenRepository.findAllActiveByUserId(userId).stream()
                .map(DeviceSessionDto::fromDomain)
                .toList();
    }

    @Transactional
    public void revokeSession(UUID sessionId, UUID currentUserId) {
        RefreshToken token = refreshTokenRepository.findById(sessionId)
                .orElseThrow(() -> new DomainViolationException("Session Not Found", "Target session was not found."));

        if (!token.getUserId().equals(currentUserId)) {
            throw new DomainViolationException("Access Denied", "Cannot revoke a session belonging to another user.");
        }

        if (!token.isRevoked()) {
            token.revoke(Instant.now());
            refreshTokenRepository.save(token);
            log.info("Successfully revoked session {} for user {}", sessionId, currentUserId);
        }
    }
}
