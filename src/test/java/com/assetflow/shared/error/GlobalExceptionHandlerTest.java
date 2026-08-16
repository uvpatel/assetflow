package com.assetflow.shared.error;

import com.assetflow.shared.logging.CorrelationIdFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldHandleBusinessException() {
        MDC.put(CorrelationIdFilter.MDC_CORRELATION_ID_KEY, "corr-test-123");

        ResourceNotFoundException ex = new ResourceNotFoundException("Asset", "AST-1");
        ProblemDetail problemDetail = handler.handleBusinessException(ex);

        assertThat(problemDetail.getStatus()).isEqualTo(404);
        assertThat(problemDetail.getTitle()).isEqualTo("Resource Not Found");
        assertThat(problemDetail.getDetail()).contains("AST-1");
        assertThat(problemDetail.getProperties()).containsKey("timestamp");
        assertThat(problemDetail.getProperties().get("correlationId")).isEqualTo("corr-test-123");
    }

    @Test
    void shouldHandleOptimisticLockingFailure() {
        MDC.put(CorrelationIdFilter.MDC_CORRELATION_ID_KEY, "corr-lock-456");

        ObjectOptimisticLockingFailureException ex = new ObjectOptimisticLockingFailureException("Entity", 1L);
        ProblemDetail problemDetail = handler.handleOptimisticLockingFailure(ex);

        assertThat(problemDetail.getStatus()).isEqualTo(409);
        assertThat(problemDetail.getTitle()).isEqualTo("Concurrency Conflict");
        assertThat(problemDetail.getProperties().get("correlationId")).isEqualTo("corr-lock-456");
    }

    @Test
    void shouldHandleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("Forbidden action");
        ProblemDetail problemDetail = handler.handleAccessDenied(ex);

        assertThat(problemDetail.getStatus()).isEqualTo(403);
        assertThat(problemDetail.getDetail()).isEqualTo("Forbidden action");
    }

    @Test
    void shouldHandleGenericException() {
        RuntimeException ex = new RuntimeException("Unexpected failure");
        ProblemDetail problemDetail = handler.handleGenericException(ex);

        assertThat(problemDetail.getStatus()).isEqualTo(500);
        assertThat(problemDetail.getTitle()).isEqualTo("Internal Server Error");
    }
}
