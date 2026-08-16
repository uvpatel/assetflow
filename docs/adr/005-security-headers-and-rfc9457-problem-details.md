# ADR 005: Enterprise HTTP Security Headers and RFC 9457 Problem Details Correlation

## Status
Accepted

## Context
As an enterprise asset and resource management system handling multi-tenant organizational data, AssetFlow requires standardized HTTP security protection against clickjacking, MIME sniffing, cross-site scripting, and caching of sensitive API responses. Furthermore, distributed observability requires that every RFC 9457 Problem Details error response contains machine-readable error codes and the active request correlation ID (`X-Correlation-ID`) from MDC.

## Decision
1. **HTTP Security Headers**:
   Configure Spring Security's `SecurityFilterChain` with:
   - `Content-Security-Policy: default-src 'self'; frame-ancestors 'none';`
   - `X-Frame-Options: DENY`
   - `X-Content-Type-Options: nosniff`
   - `Strict-Transport-Security: max-age=31536000; includeSubDomains`
   - `Cache-Control: no-cache, no-store, max-age=0, must-revalidate` on API responses.
2. **RFC 9457 Problem Details Enrichment**:
   - `GlobalExceptionHandler` extracts the active correlation ID (`MDC.get("correlationId")`) and includes it in the `ProblemDetail` properties (`"correlationId"`).
   - Standard error response properties include `timestamp` (`Instant.now()`), `type`, `title`, `status`, and `detail`.
3. **Automated Verification**:
   - WebMvc security tests and integration tests verify the presence of all required security headers and error correlation properties.

## Consequences
- Protects API consumers from common web vulnerabilities (OWASP ASVS compliance).
- Drastically simplifies support and distributed log debugging by linking error responses directly to backend trace logs via `correlationId`.
- Ensures zero client-side caching of sensitive tenant assets and financial data.
