# AssetFlow — Engineering & Delivery Plan

> Backend-only Enterprise Asset & Resource Management System built with Java, Spring Boot, PostgreSQL, and production-grade REST APIs.

## 1. Document Purpose

This document is the master backend implementation plan for AssetFlow. It defines product scope, architecture, domain rules, security boundaries, REST APIs, development phases, testing strategy, GitHub workflow, and the path toward 1,000+ meaningful commits and 200+ reviewable pull requests.

The project should be developed as a production-style ERP system, not as a collection of disconnected CRUD screens.

---

## 2. Product Vision

AssetFlow enables organizations to centrally manage physical assets and shared resources across their complete operational lifecycle.

The system will answer the following questions reliably:

- What assets does the organization own?
- Who currently holds each asset?
- Where is each asset located?
- What condition and lifecycle state is each asset in?
- Which shared resources are available at a requested time?
- Which returns, bookings, maintenance jobs, and audits require attention?
- Who performed every important action and when?

AssetFlow intentionally excludes purchasing, invoicing, payroll, and accounting.

---

## 3. Product Goals

- Replace spreadsheets and paper logs with structured workflows.
- Prevent double allocation and overlapping bookings.
- Enforce valid asset lifecycle transitions.
- Provide secure, non-self-elevating role management.
- Maintain immutable operational history and activity logs.
- Support multiple organizations with strict tenant isolation.
- Deliver actionable dashboards, notifications, and reports.
- Demonstrate professional Java, Spring Boot, testing, CI/CD, and Git practices.

---

## 4. Non-Goals for Version 1

- Procurement and vendor management
- Purchase orders and invoices
- General ledger or accounting integration
- Payroll and attendance
- IoT device tracking
- Web and native mobile user interfaces
- Microservices
- AI-based predictive maintenance

These can be evaluated only after the core platform is stable.

---

## 5. Architecture & Module Layout

AssetFlow is structured as a **Modular Monolith** using **Package-by-Feature**:

```text
com.assetflow
├── shared
│   ├── config
│   ├── error
│   ├── security
│   ├── persistence
│   └── events
├── identity
├── organization
├── asset
├── allocation
├── booking
├── maintenance
├── audit
├── notification
├── reporting
└── activitylog
```

Each business module adheres to clean internal layering:

```text
module
├── api              # controllers and HTTP DTOs
├── application      # use cases, commands, queries, transaction boundaries
├── domain           # entities, value objects, policies, repository contracts
└── infrastructure   # JPA, storage, messaging, integrations
```

### Module Dependency Rules
- API depends on application.
- Application depends on domain.
- Infrastructure implements domain ports.
- Domain must not depend on controllers, JPA repositories, or external services.
- Modules communicate through application interfaces or domain events.
- Direct access to another module's database repository is prohibited.
- ArchUnit tests enforce these rules.

---

## 6. Core Domain Rules & Invariants

### 1. Account Creation & Role Elevation Security
- Public signup creates an Employee only.
- Signup requests never accept a role field.
- Privileged roles are assigned only from the employee directory by an authorized Admin.
- The initial organization Admin is created through a secure bootstrap or invitation workflow.
- Every role change creates an activity-log entry.

### 2. Multi-Tenant Isolation
- Every tenant-owned table contains `organization_id`.
- Tenant context is strictly resolved from the authenticated JWT session.
- Accessing cross-tenant data yields `404 Not Found` or `403 Forbidden`.

### 3. Asset Lifecycle State Machine
Supported statuses: `AVAILABLE`, `ALLOCATED`, `RESERVED`, `UNDER_MAINTENANCE`, `LOST`, `RETIRED`, `DISPOSED`.
- Lifecycle updates pass exclusively through `AssetLifecycleService`.
- Disposed assets cannot return to service. Retired assets require an explicit restoration policy. Lost assets cannot be allocated or booked.

### 4. Double-Allocation Prevention
- Application-level pessimistic/optimistic locking during allocation.
- Database level partial unique index:
  ```sql
  CREATE UNIQUE INDEX uq_active_asset_allocation 
  ON asset_allocations(asset_id) 
  WHERE returned_at IS NULL;
  ```

### 5. Resource Booking Overlap Prevention
- Time slots use half-open intervals `[start_time, end_time)`.
- Overlap query rule: `existing.start < requested.end AND existing.end > requested.start`.
- Enforced via PostgreSQL exclusion constraints and transactional validation.

### 6. Audit Immutability
- An open audit contains a snapshot of assets in scope.
- Closing generates discrepancies, applies approved state changes, writes logs, and locks the cycle in one transaction.
- Closed cycles and their verification results are immutable.

---

## 7. Recommended Technology Stack

- **Core Platform**: Java 25 LTS, Spring Boot 4.1.x, Spring Framework 7.0.x, PostgreSQL, Flyway, Spring Security, Spring Data JPA, Spring Modulith, Bean Validation, MapStruct, springdoc-openapi, Jackson, Lombok (boilerplate only).
- **Reporting & Storage**: Apache Commons CSV, Apache PDFBox, MinIO (local) / S3 (prod), Mailpit (local) / Transactional Email (prod).
- **Testing**: JUnit 5, Mockito, AssertJ, Testcontainers, ArchUnit, Awaitility, REST Assured, WireMock, PIT (Mutation Testing).
- **Infrastructure**: Docker Compose, ShedLock (distributed scheduling), Micrometer & Spring Boot Actuator.

---

## 8. Implementation Roadmap Summary

- **Phase 0**: Foundation & Governance (ADRs, CI Quality Gates, Docker Compose, Architecture).
- **Phase 1**: Identity & Multi-Tenancy (Signup, JWT, Refresh Tokens, RBAC, Tenant Security).
- **Phase 2**: Organization Setup (Departments, Employee Directory, Role Assignments, Locations).
- **Phase 3**: Asset Catalog & Directory (Categories, Dynamic Fields, Tags, Files, Lifecycle Machine).
- **Phase 4**: Allocation, Transfer & Return (Double-allocation checks, Overdue Jobs, Check-in/out).
- **Phase 5**: Shared Resource Booking (Overlap constraints, Availability calendar, Reminders).
- **Phase 6**: Maintenance Workflow (Requests, Technician assignment, Resolution & Asset Restoration).
- **Phase 7**: Audit Cycles (Scope snapshot, Auditor verification, Discrepancies, Immutable closing).
- **Phase 8**: Dashboard, Reports & Exports (projections, CSV/PDF async exports, Audit logs).
- **Phase 9**: Notifications & Operational Hardening (Outbox pattern, ShedLock, Actuator, Security Audit).
- **Phase 10**: Release Packaging & v1.0.0 (API Docs, Postman collection, Release tag).

---

## 9. Delivery Strategy: PR & Commit Plan (250+ PRs Target)

| Workstream | Target PRs |
|---|---:|
| Product docs, ADRs, repository foundation | 14 |
| Build, CI/CD, environments, and quality tooling | 18 |
| Shared kernel, API conventions, and error handling | 16 |
| Authentication, sessions, authorization, and tenant security | 28 |
| Organization, departments, employees, and roles | 22 |
| Asset categories, assets, files, search, and lifecycle | 30 |
| Allocation, transfer, returns, and overdue rules | 28 |
| Resource booking and reminders | 22 |
| Maintenance workflow | 20 |
| Audit cycles and discrepancies | 20 |
| Dashboard queries, reports, and exports | 14 |
| Notifications, logs, observability, hardening | 20 |
| **Total** | **252** |
