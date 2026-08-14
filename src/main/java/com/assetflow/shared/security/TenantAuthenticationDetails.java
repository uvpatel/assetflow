package com.assetflow.shared.security;

import java.io.Serializable;
import java.util.UUID;

public class TenantAuthenticationDetails implements Serializable {

    private final UUID organizationId;

    public TenantAuthenticationDetails(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }
}
