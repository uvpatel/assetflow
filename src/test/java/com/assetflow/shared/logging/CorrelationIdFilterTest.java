package com.assetflow.shared.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class CorrelationIdFilterTest {

    private CorrelationIdFilter correlationIdFilter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        correlationIdFilter = new CorrelationIdFilter();
        filterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("Should generate correlation ID if missing and populate MDC and response header")
    void shouldGenerateCorrelationIdWhenMissing() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/assets");
        MockHttpServletResponse response = new MockHttpServletResponse();

        doAnswer(invocation -> {
            assertThat(MDC.get(CorrelationIdFilter.MDC_CORRELATION_ID_KEY)).isNotBlank();
            return null;
        }).when(filterChain).doFilter(request, response);

        correlationIdFilter.doFilter(request, response, filterChain);

        String responseHeader = response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertThat(responseHeader).isNotBlank();
        // MDC context must be cleared after filter execution
        assertThat(MDC.get(CorrelationIdFilter.MDC_CORRELATION_ID_KEY)).isNull();
    }

    @Test
    @DisplayName("Should reuse incoming correlation ID header if provided")
    void shouldReuseIncomingCorrelationId() throws ServletException, IOException {
        String existingCorrelationId = "test-correlation-id-12345";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/assets");
        request.addHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, existingCorrelationId);
        MockHttpServletResponse response = new MockHttpServletResponse();

        doAnswer(invocation -> {
            assertThat(MDC.get(CorrelationIdFilter.MDC_CORRELATION_ID_KEY)).isEqualTo(existingCorrelationId);
            return null;
        }).when(filterChain).doFilter(request, response);

        correlationIdFilter.doFilter(request, response, filterChain);

        assertThat(response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).isEqualTo(existingCorrelationId);
        assertThat(MDC.get(CorrelationIdFilter.MDC_CORRELATION_ID_KEY)).isNull();
    }
}
