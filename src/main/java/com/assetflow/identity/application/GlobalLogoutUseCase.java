package com.assetflow.identity.application;

import com.assetflow.identity.domain.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GlobalLogoutUseCase {

    private static final Logger log = LoggerFactory.getLogger(GlobalLogoutUseCase.class);
    private final RefreshTokenRepository refreshTokenRepository;

    public GlobalLogoutUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void execute(UUID userId) {
        if (userId == null) {
            return;
        }
        log.info("Executing global logout: revoking all active sessions for user {}", userId);
        refreshTokenRepository.revokeAllByUserId(userId);
    }
}
