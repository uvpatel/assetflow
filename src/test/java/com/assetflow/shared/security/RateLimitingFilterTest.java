package com.assetflow.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RateLimitingFilterTest {

    private RateLimitingFilter rateLimitingFilter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        rateLimitingFilter = new RateLimitingFilter();
        filterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("Should allow requests under limit and set rate limit headers")
    void shouldAllowRequestsUnderLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/assets");
        request.setRemoteAddr("192.168.1.100");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitingFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader("X-RateLimit-Limit")).isEqualTo("100");
        assertThat(response.getHeader("X-RateLimit-Remaining")).isEqualTo("99");
        assertThat(response.getHeader("X-RateLimit-Reset")).isNotNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should enforce tighter rate limit on authentication endpoints")
    void shouldEnforceTighterLimitOnAuthEndpoints() throws ServletException, IOException {
        String clientIp = "192.168.1.101";

        for (int i = 1; i <= RateLimitingFilter.AUTH_LIMIT_PER_MINUTE; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
            request.setRemoteAddr(clientIp);
            MockHttpServletResponse response = new MockHttpServletResponse();

            rateLimitingFilter.doFilter(request, response, filterChain);
            assertThat(response.getStatus()).isEqualTo(200);
        }

        // 11th request should be rate-limited with HTTP 429
        MockHttpServletRequest rateLimitedRequest = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        rateLimitedRequest.setRemoteAddr(clientIp);
        MockHttpServletResponse rateLimitedResponse = new MockHttpServletResponse();

        rateLimitingFilter.doFilter(rateLimitedRequest, rateLimitedResponse, filterChain);

        assertThat(rateLimitedResponse.getStatus()).isEqualTo(429);
        assertThat(rateLimitedResponse.getContentType()).contains("application/problem+json");
        assertThat(rateLimitedResponse.getContentAsString()).contains("Rate Limit Exceeded");
        assertThat(rateLimitedResponse.getHeader("X-RateLimit-Remaining")).isEqualTo("0");
    }

    @Test
    @DisplayName("Should skip non-API requests like swagger or actuator")
    void shouldSkipNonApiRequests() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/swagger-ui.html");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitingFilter.doFilter(request, response, filterChain);

        assertThat(response.getHeader("X-RateLimit-Limit")).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
