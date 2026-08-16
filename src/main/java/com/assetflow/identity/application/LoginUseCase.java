package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.AuthResponse;
import com.assetflow.identity.api.dto.LoginRequest;
import com.assetflow.identity.api.dto.UserSummaryDto;
import com.assetflow.identity.application.port.PasswordEncoderPort;
import com.assetflow.identity.application.port.TokenProviderPort;
import com.assetflow.identity.domain.RefreshToken;
import com.assetflow.identity.domain.RefreshTokenRepository;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.identity.infrastructure.security.JwtProperties;
import com.assetflow.shared.error.DomainViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class LoginUseCase {

    private final UserAccountRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final JwtProperties jwtProperties;

    public LoginUseCase(UserAccountRepository userRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        PasswordEncoderPort passwordEncoder,
                        TokenProviderPort tokenProvider,
                        JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public AuthResponse execute(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        UserAccount user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new DomainViolationException("Authentication Failed", "Invalid email or password."));

        Instant now = Instant.now();

        if (user.isLocked(now)) {
            throw new DomainViolationException("Account Locked", "Account is temporarily locked due to multiple failed login attempts. Please try again later.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.recordFailedLogin(now, UserAccount.DEFAULT_MAX_FAILED_ATTEMPTS, UserAccount.DEFAULT_LOCKOUT_DURATION_SECONDS);
            userRepository.save(user);
            throw new DomainViolationException("Authentication Failed", "Invalid email or password.");
        }

        if (!user.isActive()) {
            throw new DomainViolationException("Account Inactive", "User account is not active.");
        }

        user.recordSuccessfulLogin(now);
        userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(user);
        String rawRefreshToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawRefreshToken);

        Instant expiresAt = now.plusSeconds(jwtProperties.getRefreshTokenExpirationSeconds());
        RefreshToken refreshToken = RefreshToken.create(user.getId(), tokenHash, expiresAt);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                rawRefreshToken,
                jwtProperties.getAccessTokenExpirationSeconds(),
                UserSummaryDto.fromDomain(user)
        );
    }

    public static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
