package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.RefreshTokenRequest;
import com.assetflow.identity.api.dto.TokenRefreshResponse;
import com.assetflow.identity.application.port.TokenProviderPort;
import com.assetflow.identity.domain.RefreshToken;
import com.assetflow.identity.domain.RefreshTokenRepository;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.identity.infrastructure.security.JwtProperties;
import com.assetflow.shared.error.DomainViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAccountRepository userRepository;
    private final TokenProviderPort tokenProvider;
    private final JwtProperties jwtProperties;

    public RefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository,
                                UserAccountRepository userRepository,
                                TokenProviderPort tokenProvider,
                                JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public TokenRefreshResponse execute(RefreshTokenRequest request) {
        String rawToken = request.refreshToken().trim();
        String tokenHash = LoginUseCase.hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new DomainViolationException("Invalid Refresh Token", "Refresh token is invalid or does not exist."));

        Instant now = Instant.now();

        // Token Reuse Detection
        if (refreshToken.isRevoked()) {
            refreshTokenRepository.revokeAllByUserId(refreshToken.getUserId());
            throw new DomainViolationException("Security Alert", "Refresh token reuse detected. All active sessions have been invalidated for security reasons.");
        }

        if (refreshToken.isExpired(now)) {
            throw new DomainViolationException("Token Expired", "Refresh token has expired. Please log in again.");
        }

        UserAccount user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new DomainViolationException("User Not Found", "Associated user account was not found."));

        if (!user.isActive()) {
            throw new DomainViolationException("Account Inactive", "User account is not active.");
        }

        // Generate new token pair and rotate
        String newRawRefreshToken = UUID.randomUUID().toString();
        String newTokenHash = LoginUseCase.hashToken(newRawRefreshToken);

        refreshToken.revoke(newTokenHash, now);
        refreshTokenRepository.save(refreshToken);

        Instant expiresAt = now.plusSeconds(jwtProperties.getRefreshTokenExpirationSeconds());
        RefreshToken newRefreshToken = RefreshToken.create(user.getId(), newTokenHash, expiresAt);
        refreshTokenRepository.save(newRefreshToken);

        String newAccessToken = tokenProvider.generateAccessToken(user);

        return new TokenRefreshResponse(
                newAccessToken,
                newRawRefreshToken,
                jwtProperties.getAccessTokenExpirationSeconds()
        );
    }
}
