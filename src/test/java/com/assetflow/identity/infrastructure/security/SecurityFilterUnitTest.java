package com.assetflow.identity.infrastructure.security;

import com.assetflow.identity.application.port.TokenProviderPort;
import jakarta.servlet.FilterChain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterUnitTest {

    @Mock
    private TokenProviderPort tokenProvider;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtFilter = new JwtAuthenticationFilter(tokenProvider);
    }

    @Test
    void jwtFilterSetsAuthenticationContextForValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getUserIdFromToken(token)).thenReturn(userId);
        when(tokenProvider.getOrganizationIdFromToken(token)).thenReturn(orgId);
        when(tokenProvider.getRoleFromToken(token)).thenReturn("ADMIN");

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(userId.toString());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void jwtFilterIgnoresMissingOrInvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
