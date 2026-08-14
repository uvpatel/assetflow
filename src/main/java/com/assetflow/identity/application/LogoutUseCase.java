package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.RefreshTokenRequest;
import com.assetflow.identity.domain.RefreshToken;
import com.assetflow.identity.domain.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void execute(RefreshTokenRequest request) {
        if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
            return;
        }

        String tokenHash = LoginUseCase.hashToken(request.refreshToken().trim());
        Optional<RefreshToken> optionalToken = refreshTokenRepository.findByTokenHash(tokenHash);

        if (optionalToken.isPresent()) {
            RefreshToken token = optionalToken.get();
            if (!token.isRevoked()) {
                token.revoke(Instant.now());
                refreshTokenRepository.save(token);
            }
        }
    }
}
