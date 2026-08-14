package com.assetflow.identity.application.port;

import com.assetflow.identity.domain.UserAccount;

import java.util.UUID;

public interface TokenProviderPort {
    String generateAccessToken(UserAccount user);
    boolean validateToken(String token);
    UUID getUserIdFromToken(String token);
    UUID getOrganizationIdFromToken(String token);
    String getRoleFromToken(String token);
}
