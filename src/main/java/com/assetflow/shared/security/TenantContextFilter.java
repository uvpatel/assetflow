package com.assetflow.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;

@Component
public class TenantContextFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantContextFilter.class);
    public static final String TENANT_HEADER = "X-Organization-ID";
    public static final String MDC_TENANT_ID_KEY = "tenantId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            UUID tenantId = resolveTenantId(request);
            if (tenantId != null) {
                TenantContext.setOrganizationId(tenantId);
                MDC.put(MDC_TENANT_ID_KEY, tenantId.toString());
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            MDC.remove(MDC_TENANT_ID_KEY);
        }
    }

    private UUID resolveTenantId(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof TenantAuthenticationDetails details) {
            if (details.getOrganizationId() != null) {
                return details.getOrganizationId();
            }
        }

        String headerValue = request.getHeader(TENANT_HEADER);
        if (headerValue != null && !headerValue.isBlank()) {
            try {
                return UUID.fromString(headerValue.trim());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid UUID format provided in {} header: {}", TENANT_HEADER, headerValue);
            }
        }

        return null;
    }
}
