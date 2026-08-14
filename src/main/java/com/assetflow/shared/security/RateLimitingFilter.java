package com.assetflow.shared.security;

import com.assetflow.shared.error.DomainViolationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    private static class RequestCounter {
        private final long resetTime;
        private final AtomicInteger count;

        public RequestCounter(long resetTime) {
            this.resetTime = resetTime;
            this.count = new AtomicInteger(1);
        }

        public boolean incrementAndCheck(long now) {
            if (now > resetTime) {
                return false;
            }
            return count.incrementAndGet() > MAX_REQUESTS_PER_MINUTE;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        long now = System.currentTimeMillis();
        long currentWindow = now + 60000;

        RequestCounter counter = requestCounts.compute(clientIp, (ip, existing) -> {
            if (existing == null || now > existing.resetTime) {
                return new RequestCounter(currentWindow);
            }
            return existing;
        });

        if (counter.incrementAndCheck(now)) {
            response.setStatus(429);
            response.getWriter().write("{\"title\":\"Rate Limit Exceeded\",\"detail\":\"Too many requests. Please try again later.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
