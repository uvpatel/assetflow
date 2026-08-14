package com.assetflow.shared.error;

import org.junit.jupiter.api.Test;

import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleBusinessException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Asset", "AST-1");
        ProblemDetail problemDetail = handler.handleBusinessException(ex);

        assertThat(problemDetail.getStatus()).isEqualTo(404);
        assertThat(problemDetail.getTitle()).isEqualTo("Resource Not Found");
        assertThat(problemDetail.getDetail()).contains("AST-1");
        assertThat(problemDetail.getProperties()).containsKey("timestamp");
    }

    @Test
    void shouldHandleOptimisticLockingFailure() {
        ObjectOptimisticLockingFailureException ex = new ObjectOptimisticLockingFailureException("Entity", 1L);
        ProblemDetail problemDetail = handler.handleOptimisticLockingFailure(ex);

        assertThat(problemDetail.getStatus()).isEqualTo(409);
        assertThat(problemDetail.getTitle()).isEqualTo("Concurrency Conflict");
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
