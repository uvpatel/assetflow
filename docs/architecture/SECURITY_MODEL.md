# Security Model & Authorization Specification

## 1. Overview

AssetFlow enforces security across authentication, authorization, multi-tenant isolation, data protection, and immutable activity auditing. The security model follows OWASP ASVS (Application Security Verification Standard) guidelines.

---

## 2. Authentication & Session Security

### Authentication Mechanisms
* **Public Signup**: Public signup creates standard `ROLE_EMPLOYEE` user accounts strictly bound to the requesting organization. Request payloads cannot specify or elevate roles.
* **JWT Access Tokens**: Short-lived JSON Web Tokens (JWT) signed using HMAC-SHA512 with configurable expiration (`jwt.expiration-ms`).
* **Refresh Token Rotation**: Rotating refresh tokens stored as secure hashes in PostgreSQL with reuse detection capabilities and automatic revocation upon compromise.
* **Password Hashing**: Passwords stored using BCrypt with adaptive cost factor. Plaintext passwords are never logged or stored in memory longer than necessary.
* **Device Session Management**: Individual and global session invalidation endpoints (`/api/v1/sessions/current`, `/api/v1/sessions/all`).

### Account Protection & Rate Limiting
* **Token Bucket Rate Limiting**: `RateLimitingFilter` restricts `/api/v1/auth/*` endpoints to 10 req/min and general API endpoints to 100 req/min per IP.
* **Problem Details on Throttle**: Returns RFC 9457 formatted `429 Too Many Requests` responses with `X-RateLimit-*` headers.

---

## 3. Role-Based Access Control (RBAC)

Authorization is managed via Spring Security method-level annotations (`@PreAuthorize`) and fine-grained permission checks.

### Standard System Roles
1. `ROLE_ORGANIZATION_OWNER`: Full administrative control over organization settings, billing, and global roles.
2. `ROLE_ADMIN`: Employee directory management, role delegation, asset category setup.
3. `ROLE_ASSET_MANAGER`: Asset registration, depreciation policy, retirement, and disposal approvals.
4. `ROLE_DEPARTMENT_HEAD`: Department resource approval, inter-department transfer authorizations.
5. `ROLE_MAINTENANCE_MANAGER`: Maintenance request triage, technician scheduling, work order resolution.
6. `ROLE_TECHNICIAN`: Work order execution, checklist completion, parts consumption logging.
7. `ROLE_AUDITOR`: Audit cycle snapshot execution, QR code scanning, discrepancy logging.
8. `ROLE_EMPLOYEE`: Personal asset allocation requests, return requests, booking shared resources.
9. `ROLE_READ_ONLY_REPORTER`: Operational KPI viewing and report export without mutation rights.

### Scoped Permissions Matrix
Permissions follow `<domain>:<action>` format:
- `asset:create`, `asset:read`, `asset:update`, `asset:allocate`, `asset:retire`, `asset:dispose`
- `allocation:approve`, `transfer:approve`, `booking:create`, `booking:approve`
- `maintenance:approve`, `maintenance:assign`, `audit:create`, `audit:verify`, `audit:close`
- `report:organization`, `employee:promote`, `budget:manage`

---

## 4. Operational Hardening & Data Protection

1. **HTTP Security Headers**: Strict-Transport-Security (HSTS), Content-Security-Policy (CSP), X-Content-Type-Options (`nosniff`), X-Frame-Options (`DENY`), and Cache-Control (`no-store, no-cache`) for API responses.
2. **Sensitive Data Redaction**: Passwords, JWT secrets, refresh tokens, and API key hashes are automatically masked in log outputs.
3. **SSRF & Input Sanitization**: Private object storage URLs and external webhook endpoints are validated against private IP range usage.
4. **Immutable Audit Logging**: Operational actions emit structured event records into the append-only `activity_log` table with trace correlation IDs.
