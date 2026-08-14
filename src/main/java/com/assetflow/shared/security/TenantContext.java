package com.assetflow.shared.security;

import java.util.Optional;
import java.util.UUID;

public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setOrganizationId(UUID organizationId) {
        CURRENT_TENANT.set(organizationId);
    }

    public static UUID getOrganizationId() {
        return CURRENT_TENANT.get();
    }

    public static Optional<UUID> getOrganizationIdOptional() {
        return Optional.ofNullable(CURRENT_TENANT.get());
    }

    public static UUID requireOrganizationId() {
        UUID organizationId = CURRENT_TENANT.get();
        if (organizationId == null) {
            throw new IllegalStateException("No organization context found for the current request thread.");
        }
        return organizationId;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
