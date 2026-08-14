package com.assetflow.identity.api.dto;

import com.assetflow.identity.domain.Role;
import com.assetflow.identity.domain.UserStatus;

import java.util.UUID;

public class AuthResponse {

    private UUID userId;
    private UUID organizationId;
    private String email;
    private Role role;
    private UserStatus status;
    private String accessToken;
    private String refreshToken;

    public AuthResponse() {
    }

    public AuthResponse(UUID userId, UUID organizationId, String email, Role role, UserStatus status, String accessToken, String refreshToken) {
        this.userId = userId;
        this.organizationId = organizationId;
        this.email = email;
        this.role = role;
        this.status = status;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
