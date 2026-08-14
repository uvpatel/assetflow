# ADR 002: Authentication, Authorization and Tenant Security Boundaries

## Status
Accepted

## Context
AssetFlow is a multi-tenant enterprise ERP backend. Multiple organizations reside in the same database instance. Secure authentication, session management, non-self-elevating role promotion, and strict tenant data isolation are fundamental system requirements.

## Decision
1. **Authentication**: Stateless authentication using short-lived JWT Access Tokens accompanied by rotating Refresh Tokens stored securely (hashed) in PostgreSQL. Supports refresh token reuse detection and instant session revocation.
2. **Multi-Tenancy Isolation**:
   - Every tenant-owned table contains an `organization_id` column indexed for optimal query execution.
   - Tenant context (`organization_id`) is strictly extracted from the validated JWT claims of the caller.
   - API callers cannot pass `organization_id` in request payloads or query parameters to override tenant context. Cross-tenant access attempts will yield `404 Not Found` or `403 Forbidden`.
3. **Role & Privilege Security**:
   - Public signup requests create an `EMPLOYEE` record exclusively. Signup payloads cannot specify roles.
   - Privileged roles (`ADMIN`, `ASSET_MANAGER`, `DEPARTMENT_HEAD`) must be assigned explicitly from the employee directory by an authorized `ADMIN`.
   - Every role assignment or elevation generates an immutable record in `activity_log`.
4. **Auditor Privilege Scoping**:
   - Auditor capability is cycle-bound. An assigned employee can only inspect and verify items within their specific assigned audit cycle (`AuditAssignment`).

## Consequences
- Protects against cross-tenant data leakage and privilege escalation.
- Clear separation between public signup and administrative role management.
- Simplifies security audit compliance.
