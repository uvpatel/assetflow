package com.assetflow.identity.api.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserSummaryDto user
) {
    public AuthResponse(String accessToken, String refreshToken, long expiresIn, UserSummaryDto user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
