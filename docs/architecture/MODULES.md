# AssetFlow Architecture: Modular Monolith Specification

## 1. Architectural Strategy

AssetFlow is built as a **Modular Monolith** in Java 21 and Spring Boot 3.4+. It prioritizes strong internal module boundaries, ACID transactional integrity across related domain aggregates, and explicit application use-case boundaries.

The system is designed so that bounded contexts can be cleanly extracted into independent microservices if operational requirements, team division, or extreme scale demand it in the future.

---

## 2. Bounded Context Modules

The application consists of 14 bounded business contexts under `com.assetflow`:

```text
com.assetflow/
├── shared/            # Shared kernel, Security context, Base Exceptions, DB Config
├── identity/          # Authentication, User Accounts, JWT, Password Policies
├── organization/      # Multi-tenant Organization, Departments, Employee directory
├── asset/             # Asset Catalog, Categories, Dynamic Fields, Bundles, Depreciation
├── allocation/        # Asset Checkout, Transfers, Digital Handover, Active Uniqueness
├── booking/           # Shared Resource Calendar, Availability, Overlap Prevention
├── maintenance/       # Work Orders, Technician Matching, Parts Cost, SLA Policies
├── audit/             # Audit Cycles, Snapshot Scope, QR Verification, Discrepancies
├── notification/      # In-App Notifications, Outbox Pattern Email Dispatcher
├── activitylog/       # Immutable Governance Audit Log
├── reporting/         # Dashboard Projections, Analytics, PDF/CSV Reports
├── financial/         # Currency Rates, Asset Tax Write-Off Engine
├── webhook/           # Domain Event Subscriptions, Signed Webhook Retries
└── AssetFlowApplication.java
```

---

## 3. Internal Module Layering & Rules

Each bounded module must implement four internal layers:

```text
module/
├── api/               # REST Controllers, Request DTOs, Response DTOs, OpenAPI spec
├── application/       # Use Case Services, Commands, Queries, Use Case Ports
├── domain/            # Domain Entities, Aggregates, Value Objects, Domain Policies, Domain Repositories
└── infrastructure/    # Spring Data JPA Repositories, DB Adapters, External Clients
```

### Module Dependency Invariants
1. `api` depends strictly on `application`.
2. `application` depends strictly on `domain`.
3. `infrastructure` implements `application` / `domain` repository ports.
4. `domain` MUST NOT depend on Spring Web, JPA Entities, or External Libraries.
5. Direct cross-module persistence repository access is strictly forbidden (e.g. `AllocationService` cannot call `AssetJpaRepository`).
6. Modules communicate strictly via public application interfaces or transactional domain events.
7. Modulith and ArchUnit rules automatically enforce package structure in CI build pipelines (`ArchitectureTest.java`).
