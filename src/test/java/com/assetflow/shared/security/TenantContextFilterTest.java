package com.assetflow.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TenantContextFilterTest {

    private final TenantContextFilter filter = new TenantContextFilter();

    @AfterEach
    void tearDown() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldResolveTenantFromHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        UUID tenantId = UUID.randomUUID();
        request.addHeader(TenantContextFilter.TENANT_HEADER, tenantId.toString());

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, (req, res) -> {
            assertThat(TenantContext.getOrganizationId()).isEqualTo(tenantId);
        });

        assertThat(TenantContext.getOrganizationId()).isNull(); // cleared after filter execution
    }

    @Test
    void shouldResolveTenantFromAuthenticationDetails() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        UUID tenantId = UUID.randomUUID();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user", "pass", Collections.emptyList());
        auth.setDetails(new TenantAuthenticationDetails(tenantId));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filter.doFilterInternal(request, response, (req, res) -> {
            assertThat(TenantContext.getOrganizationId()).isEqualTo(tenantId);
        });

        assertThat(TenantContext.getOrganizationId()).isNull();
    }
}
