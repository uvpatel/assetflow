package com.assetflow.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Token bucket rate limiting filter enforcing bounded request limits across API endpoints.
 * Enforces tighter bucket rates on sensitive authentication endpoints (/api/v1/auth/*)
 * and standard rates for operational API routes (/api/v1/*).
 * Returns RFC 9457 Problem Details when limits are exceeded.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    public static final int AUTH_LIMIT_PER_MINUTE = 10;
    public static final int GENERAL_LIMIT_PER_MINUTE = 100;
    private static final long WINDOW_MS = 60_000L;

    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    public static class RequestCounter {
        private final long resetTimeMs;
        private final int limit;
        private final AtomicInteger count;

        public RequestCounter(long resetTimeMs, int limit) {
            this.resetTimeMs = resetTimeMs;
            this.limit = limit;
            this.count = new AtomicInteger(0);
        }

        public boolean incrementAndCheck(long nowMs) {
            if (nowMs > resetTimeMs) {
                return false;
            }
            return count.incrementAndGet() > limit;
        }

        public int getRemaining() {
            int remaining = limit - count.get();
            return Math.max(0, remaining);
        }

        public long getResetSeconds(long nowMs) {
            long remainingMs = resetTimeMs - nowMs;
            return Math.max(1, remainingMs / 1000L);
        }

        public int getLimit() {
            return limit;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // Skip non-API requests (swagger, actuator, etc.)
        if (!requestUri.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isAuthEndpoint = requestUri.startsWith("/api/v1/auth/");
        int limit = isAuthEndpoint ? AUTH_LIMIT_PER_MINUTE : GENERAL_LIMIT_PER_MINUTE;

        String clientIp = resolveClientIp(request);
        String bucketKey = isAuthEndpoint ? "auth:" + clientIp : "gen:" + clientIp;
        long now = System.currentTimeMillis();

        RequestCounter counter = requestCounts.compute(bucketKey, (key, existing) -> {
            if (existing == null || now > existing.resetTimeMs) {
                return new RequestCounter(now + WINDOW_MS, limit);
            }
            return existing;
        });

        long resetSeconds = counter.getResetSeconds(now);
        boolean limitExceeded = counter.incrementAndCheck(now);

        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(counter.getRemaining()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(resetSeconds));

        if (limitExceeded) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            String jsonBody = String.format(
                    "{\"type\":\"https://api.assetflow.io/errors/rate-limit-exceeded\"," +
                    "\"title\":\"Rate Limit Exceeded\"," +
                    "\"status\":429," +
                    "\"detail\":\"Too many requests. Please wait %d seconds before retrying.\"," +
                    "\"instance\":\"%s\"}",
                    resetSeconds, requestUri
            );
            response.getWriter().write(jsonBody);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
