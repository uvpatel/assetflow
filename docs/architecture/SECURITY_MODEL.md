# Security Model & Authorization Specification

## 1. Overview

AssetFlow enforces security across authentication, authorization, multi-tenant isolation, data protection, and immutable activity auditing. The security model follows OWASP ASVS (Application Security Verification Standard) guidelines.

---

## 2. Authentication & Session Security

### Authentication Mechanisms
* **Public Signup**: Public signup creates standard `ROLE_EMPLOYEE` user accounts strictly bound to the requesting organization. Request payloads cannot specify or elevate roles.
* **JWT Access Tokens**: Short-lived JSON Web Tokens (JWT) signed using HMAC-SHA512.
* **Refresh Token Rotation**: Rotating refresh tokens stored as secure hashes in database with reuse detection capabilities.
* **Password Hashing**: Passwords stored using BCrypt cost factor 12. Plaintext passwords are never logged or stored.

### Account Lockout & Protection
* Authentication rate limiting prevents brute-force attempts.
* Consecutive failed login attempts trigger temporary account lockout.

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

### Scoped Permissions
Permissions follow `<domain>:<action>` format:
- `asset:create`, `asset:read`, `asset:update`, `asset:allocate`, `asset:retire`, `asset:dispose`
- `allocation:approve`, `transfer:approve`, `booking:create`, `booking:approve`
- `maintenance:approve`, `maintenance:assign`, `audit:create`, `audit:verify`, `audit:close`

---

## 4. Operational Hardening & Data Protection

1. **Sensitive Data Redaction**: Passwords, JWT secrets, refresh tokens, and API key hashes are automatically masked in log outputs.
2. **SSRF & Input Sanitization**: Private object storage URLs and external webhook endpoints are validated against private IP range usage.
3. **Immutable Audit Logging**: Operational actions emit structured event records into the append-only `activity_log` table.
