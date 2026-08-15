# AssetFlow Engineering Roadmap (M0 - M14)

## Overview

This roadmap defines the sequential development progression for AssetFlow from initial foundation (`M0`) to stable release (`v1.0.0` / `M14`). Each milestone represents a self-contained operational release capability.

---

## Milestone Schedule

### M0: Repository & Engineering Foundation
* **Goal**: Modular monolith architecture, build setup, database migrations, CI quality gates.
* **Key Deliverables**:
  - Java 21 & Spring Boot 3.4.2 configuration
  - Flyway baseline schema (V1 - V43)
  - ArchUnit architecture test suite
  - Initial ADR documentation (001 - 003)

### M1: Identity & Multi-Tenant Security
* **Goal**: Robust authentication, JWT access & refresh token management, organization tenant context propagation.
* **Key Deliverables**:
  - Authentication controller & signup workflow
  - Tenant context filter and ThreadLocal security context holder
  - Password strength verification & rate-limiting protection
  - Refresh token rotation storage and reuse detection

### M2: Organization & Employee Administration
* **Goal**: Organizational hierarchy, departments, multi-location support, employee directory.
* **Key Deliverables**:
  - Parent-child department tree management
  - Employee profile & invitation workflow
  - Role-based authorization & delegation policy
  - Location, building, and room catalog

### M3: Asset Catalog & Lifecycle Engine
* **Goal**: Concurrency-safe asset registry, categories, custom fields, lifecycle state transitions.
* **Key Deliverables**:
  - Asset tag generation and serial tracking
  - Hierarchical categories & field definition versioning
  - Lifecycle state machine (AVAILABLE, ALLOCATED, UNDER_MAINTENANCE, RETIRED, DISPOSED)
  - Photo, attachment, and QR/barcode generation support

### M4: Allocation, Custody & Transfer Workflows
* **Goal**: Single-holder allocation invariant enforcement, employee checkout, handover approval, transfer tracking.
* **Key Deliverables**:
  - Active uniqueness constraint (`uq_active_asset_allocation`)
  - Digital handover acknowledgement & condition records
  - Inter-department asset transfer request & approval state machine
  - Overdue allocation reminder and escalation jobs

### M5: Shared Resource Booking & Calendar
* **Goal**: Conflict-free resource reservations using half-open time interval calculations.
* **Key Deliverables**:
  - Half-open interval overlap detection (`[start, end)`)
  - PostgreSQL exclusion constraint integration
  - Blackout period configuration & maintenance windows
  - Resource calendar availability query API

### M6: Maintenance & Warranty Engine
* **Goal**: Work order execution, technician scheduling, SLA policy alerts, warranty tracking.
* **Key Deliverables**:
  - Work order workflow (DRAFT -> APPROVED -> TECHNICIAN_ASSIGNED -> IN_PROGRESS -> RESOLVED)
  - Technician skill matching & workload balancer
  - SLA breach monitoring & escalation timers
  - Warranty policy documentation & claim management

### M7: Audits & Compliance Engine
* **Goal**: Periodic asset auditing, snapshot-based scope definitions, discrepancy resolution.
* **Key Deliverables**:
  - Audit cycle snapshot generator
  - Auditor QR verification & condition score recording
  - Immutable audit closing transaction
  - Data retention & immutable activity log archive policies

### M8: Notifications, Outbox & Integration Webhooks
* **Goal**: Asynchronous event publishing, in-app notifications, webhooks with retry capabilities.
* **Key Deliverables**:
  - Transactional Outbox pattern engine
  - In-app notification preferences & digests
  - Signed webhook payload delivery with exponential backoff
  - Dead-letter queue management and replay APIs

### M9: Search Engine & Analytical Reports
* **Goal**: Fast multi-attribute full-text asset search, asynchronous PDF & CSV report generation.
* **Key Deliverables**:
  - PostgreSQL trigram and tsvector full-text search indexes
  - Saved search presets & recent search history
  - Async PDF report renderer (Apache PDFBox)
  - Bulk CSV export engine with expiring download signed URLs

### M10: Scalability, Performance & Caching
* **Goal**: High-throughput query performance, multi-node lock coordination.
* **Key Deliverables**:
  - ShedLock distributed job scheduling
  - Multi-tenant aware Redis / Spring cache layer
  - Cursor pagination for high-volume activity logs
  - Bulk asset import staging engine (`/api/v1/import-batches`)

### M11: Security Hardening & Vulnerability Audit
* **Goal**: OWASP ASVS compliance, rate limiting, security logging, input sanitization.
* **Key Deliverables**:
  - Token bucket rate limiting filter
  - Sensitive data redaction in structured JSON logs
  - OpenAPI compatibility & breaking change validation
  - Dependency vulnerability scanning

### M12: Production Operations & Monitoring
* **Goal**: Deployment containerization, observability metrics, health checks.
* **Key Deliverables**:
  - Multi-stage Docker container build
  - Spring Boot Actuator custom health indicators
  - Prometheus metrics & Grafana dashboard specs
  - Database disaster recovery & restore documentation

### M13: Release Candidate & End-to-End Validation
* **Goal**: Full integration test suite run, staging deployment verification, release candidate testing.

### M14: Stable Release v1.0.0
* **Goal**: Final documentation wrap-up, tag release, production deployment readiness.
