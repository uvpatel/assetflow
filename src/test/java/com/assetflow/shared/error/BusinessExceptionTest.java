package com.assetflow.shared.error;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    @Test
    void testResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Asset", "AST-100");
        assertThat(ex.getStatus()).isEqualTo(404);
        assertThat(ex.getTitle()).isEqualTo("Resource Not Found");
        assertThat(ex.getMessage()).contains("AST-100");
        assertThat(ex.getProperties()).containsEntry("resourceName", "Asset");
    }

    @Test
    void testDomainViolationException() {
        DomainViolationException ex = new DomainViolationException("Invalid lifecycle transition");
        assertThat(ex.getStatus()).isEqualTo(400);
        assertThat(ex.getTitle()).isEqualTo("Domain Rule Violation");
        assertThat(ex.getMessage()).isEqualTo("Invalid lifecycle transition");
    }

    @Test
    void testTenantAccessDeniedException() {
        TenantAccessDeniedException ex = new TenantAccessDeniedException("Cross-tenant access prohibited");
        assertThat(ex.getStatus()).isEqualTo(403);
        assertThat(ex.getTitle()).isEqualTo("Tenant Access Denied");
    }

    @Test
    void testConcurrencyConflictException() {
        ConcurrencyConflictException ex = new ConcurrencyConflictException("Asset locked by another thread");
        assertThat(ex.getStatus()).isEqualTo(409);
        assertThat(ex.getTitle()).isEqualTo("Concurrency Conflict");
    }
}
