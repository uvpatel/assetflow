# AssetFlow — Master Engineering & Delivery Plan

> Enterprise Asset & Resource Management Backend System — Java 21 LTS, Spring Boot 3.4+, PostgreSQL 16+, RESTful APIs, Modular Monolith Architecture.

---

## 1. Executive Summary & Vision

AssetFlow is a production-grade enterprise asset and resource management backend built for multi-tenant organizations managing equipment, furniture, vehicles, facilities, tools, accessories, consumables, digital records, maintenance operations, resource reservations, custody transfers, and audit cycles.

It provides complete operational visibility into asset lifecycles, active allocations, resource bookings, work order executions, and compliance audit trails with strict organization-level multi-tenancy.

---

## 2. Planning Targets & Long-term Metrics

To deliver a battle-tested enterprise codebase, the development is tracked against the following long-term engineering benchmarks:

* **500–550** reviewable Pull Requests
* **2,000–2,500** meaningful Conventional Commits
* **1,500+** valuable automated tests (Unit, Integration, Architecture, Mutation, Security)
* **200–300** documented API endpoints (OpenAPI / Swagger)
* **80–120** immutable Flyway database migrations
* **30–40** Architecture Decision Records (ADRs)
* **15–20** bounded modular monolith feature packages
* **14** release milestones (`M0` to `M14`, targeting `v1.0.0`)

---

## 3. Workstream Allocations & Roadmap Targets

| Milestone / Workstream | Target PRs | Target Commits | Focus Areas |
| :--- | ---: | ---: | :--- |
| **Product Docs & ADRs** | 25 | 100 | Design docs, architecture ADRs, security models |
| **Shared Kernel & Baseline** | 30 | 135 | Core domain specs, error handlers, Flyway setup |
| **CI/CD & Developer Tooling** | 30 | 130 | JaCoCo, Checkstyle, ArchUnit, GitHub Actions |
| **Identity & Authentication** | 38 | 180 | JWT, refresh tokens, auth policies, lockout |
| **Authorization & Tenancy** | 32 | 160 | Organization tenant scoping, fine-grained RBAC |
| **Organization & Employees** | 30 | 135 | Department hierarchies, employee directory |
| **Categories & Custom Fields** | 28 | 125 | Dynamic fields, category versioning, tags |
| **Asset Catalog & Lifecycle** | 45 | 220 | State machine, serials, QR/barcode generation |
| **Allocation & Transfers** | 38 | 180 | Active uniqueness, custody transfer approval |
| **Reservations & Bookings** | 30 | 135 | Calendar overlap protection, blackout slots |
| **Maintenance & SLA Engine** | 38 | 180 | Work orders, technician matching, SLA alerts |
| **Consumables & Inventory** | 22 | 95 | Stock tracking, reorder thresholds, batch lots |
| **Audits & Compliance** | 32 | 145 | Immutable audit snapshots, discrepancies |
| **Notifications & Outbox** | 28 | 125 | In-app alerts, transactional outbox pattern |
| **Search, Reports & Exports** | 28 | 125 | PostgreSQL full-text search, async PDF/CSV |
| **Files & Integrations** | 24 | 105 | S3 object storage, webhook delivery retries |
| **Performance & Resilience** | 20 | 90 | Rate limiting, circuit breakers, caching |
| **Security & Hardening** | 12 | 55 | Audit log integrity, OWASP validation |
| **Release & Operations** | 8 | 35 | Docker packaging, smoke tests, v1.0.0 release |
| **Total Planning Targets** | **550** | **2,550** | Production-grade Enterprise Baseline |

---

## 4. Architectural Rules & Invariants

1. **Modular Monolith Layout**: `com.assetflow.<module>` package-by-feature layout.
2. **Layering Integrity**: `api` -> `application` -> `domain` <- `infrastructure`.
3. **Database Isolation**: Cross-module database repository calls are strictly forbidden. Modules communicate via public application use case ports or domain events.
4. **Single Active Allocation**: An asset can have at most one active allocation (`returned_at IS NULL`), enforced via pessimistic locking and database partial unique indexes (`uq_active_asset_allocation`).
5. **Booking Overlap Invariant**: Half-open intervals `[start_time, end_time)`. Overlap condition: `existing.start < requested.end AND existing.end > requested.start`.
6. **Immutable Audit Closing**: Closing an audit cycle generates discrepancies, applies status transitions, records activity logs, and locks the audit cycle transactionally.
7. **Strict Organization Tenancy**: `organization_id` on all tenant-owned records; context derived solely from authenticated principal.
8. **Stateless Scalability**: Application instances remain stateless. Distributed scheduling coordinated via ShedLock.
9. **RFC 9457 Problem Details**: Standardized error responses containing machine-readable error types, titles, status codes, and correlation IDs.

---

## 5. Implementation Milestones (M0 to M14)

* **M0**: Repository and Engineering Foundation
* **M1**: Identity and Tenant Isolation
* **M2**: Organization and Employee Administration
* **M3**: Asset Catalog and Lifecycle
* **M4**: Allocation, Custody and Transfer
* **M5**: Reservation and Booking
* **M6**: Maintenance and Warranty
* **M7**: Audits and Compliance
* **M8**: Notifications and Integrations
* **M9**: Search and Reporting
* **M10**: Scalability and Performance
* **M11**: Security Hardening
* **M12**: Production Operations
* **M13**: Release Candidate
* **M14**: Stable Release `v1.0.0`

---

## 6. Response Contract & Operating Protocol

Every capability follows the mandatory pre-change and post-change execution contract:

### Pre-Change Contract
```text
Repository state:
Selected milestone:
Selected issue:
Goal:
Domain invariants:
Security considerations:
Concurrency considerations:
Files/modules likely affected:
Tests planned:
Commit plan:
```

### Post-Change Contract
```text
Implemented:
Files changed:
Database migrations:
Tests added:
Commands executed:
Validation results:
Branch:
Commits:
Pull request:
Known limitations:
Next recommended issue:
```
