package com.assetflow.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findById(UUID id);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllActiveByUserId(UUID userId);
    void revokeAllByUserId(UUID userId);
}
