package com.assetflow.shared.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldSetAndGetTenantId() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.setOrganizationId(tenantId);

        assertThat(TenantContext.getOrganizationId()).isEqualTo(tenantId);
        assertThat(TenantContext.getOrganizationIdOptional()).contains(tenantId);
        assertThat(TenantContext.requireOrganizationId()).isEqualTo(tenantId);
    }

    @Test
    void shouldThrowWhenTenantIdRequiredButMissing() {
        assertThatThrownBy(TenantContext::requireOrganizationId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No organization context found");
    }

    @Test
    void shouldClearTenantId() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.setOrganizationId(tenantId);
        TenantContext.clear();

        assertThat(TenantContext.getOrganizationId()).isNull();
        assertThat(TenantContext.getOrganizationIdOptional()).isEmpty();
    }
}
