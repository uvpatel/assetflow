package com.assetflow.identity.application;

import com.assetflow.identity.api.dto.AuthResponse;
import com.assetflow.identity.api.dto.LoginRequest;
import com.assetflow.identity.api.dto.UserSummaryDto;
import com.assetflow.identity.application.port.PasswordEncoderPort;
import com.assetflow.identity.domain.RefreshToken;
import com.assetflow.identity.domain.RefreshTokenRepository;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.identity.infrastructure.security.JwtProperties;
import com.assetflow.identity.infrastructure.security.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public LoginUseCase(UserAccountRepository userRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        PasswordEncoderPort passwordEncoder,
                        JwtTokenProvider jwtTokenProvider,
                        JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public AuthResponse execute(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        UserAccount user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new DomainViolationException("Authentication Failed", "Invalid email or password."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new DomainViolationException("Authentication Failed", "Invalid email or password.");
        }

        if (!user.isActive()) {
            throw new DomainViolationException("Account Inactive", "User account is not active.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String rawRefreshToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawRefreshToken);

        Instant expiresAt = Instant.now().plusSeconds(jwtProperties.getRefreshTokenExpirationSeconds());
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
