package com.assetflow.identity.api.dto;

import com.assetflow.identity.domain.RefreshToken;

import java.time.Instant;
import java.util.UUID;

public record DeviceSessionDto(
        UUID sessionId,
        UUID userId,
        Instant createdAt,
        Instant expiresAt,
        boolean isRevoked
) {
    public static DeviceSessionDto fromDomain(RefreshToken token) {
        return new DeviceSessionDto(
                token.getId(),
                token.getUserId(),
                token.getCreatedAt(),
                token.getExpiresAt(),
                token.isRevoked()
        );
    }
}
