# AssetFlow Engineering Roadmap (M0 - M14)

## Overview

This roadmap defines the sequential development progression for AssetFlow from initial foundation (`M0`) to production-ready stable release (`v1.0.0` / `M14`). Each milestone represents a self-contained, reviewable, and testable operational release capability.

---

## Milestone Schedule & Delivery Sequence

### M0: Repository & Engineering Foundation
* **Goal**: Modular monolith architecture, build setup, database migrations, CI quality gates.
* **Key Deliverables**:
  - Java 21 & Spring Boot 3.4+ configuration
  - Flyway baseline schema and transactional migrations (V1 - V67+)
  - ArchUnit architecture and layer integrity test suite
  - Architecture Decision Records (ADRs 001 - 005)
  - Security headers, RFC 9457 Problem Details error handling & MDC correlation IDs

### M1: Identity & Multi-Tenant Security
* **Goal**: Robust authentication, JWT access & refresh token management, organization tenant context propagation.
* **Key Deliverables**:
  - Authentication controller & non-self-elevating signup workflow
  - Tenant context filter and ThreadLocal security context holder
  - Password strength verification & token bucket rate-limiting protection
  - Hashed refresh token rotation storage and reuse detection
  - Device session management and global logout capability

### M2: Organization & Employee Administration
* **Goal**: Organizational hierarchy, departments, multi-location support, employee directory.
* **Key Deliverables**:
  - Parent-child department tree management
  - Employee profile & invitation workflow
  - Role-based authorization & delegation policy
  - Location, building, floor, room, and storage area catalog
  - Working calendars and business holidays

### M3: Asset Catalog & Lifecycle Engine
* **Goal**: Concurrency-safe asset registry, categories, custom fields, lifecycle state transitions.
* **Key Deliverables**:
  - Asset tag generation and serial tracking
  - Hierarchical categories & field definition versioning
  - Lifecycle state machine (DRAFT, AVAILABLE, ALLOCATED, UNDER_MAINTENANCE, RETIRED, DISPOSED)
  - Photo, attachment, and QR/barcode generation and scanning support
  - Parent-child asset bundles, accessories, and replacement relationships

### M4: Allocation, Custody & Transfer Workflows
* **Goal**: Single-holder allocation invariant enforcement, employee checkout, handover approval, transfer tracking.
* **Key Deliverables**:
  - Active uniqueness constraint (`uq_active_asset_allocation`)
  - Digital handover acknowledgement & check-out condition records
  - Inter-department asset transfer request & approval state machine
  - Overdue allocation reminder and escalation scheduled jobs
  - Temporary asset checkout and return inspections

### M5: Shared Resource Booking & Calendar
* **Goal**: Conflict-free resource reservations using half-open time interval calculations.
* **Key Deliverables**:
  - Half-open interval overlap detection (`[start, end)`)
  - PostgreSQL exclusion constraint integration
  - Blackout period configuration & maintenance windows
  - Resource calendar availability query API
  - Recurring bookings and waitlist management

### M6: Maintenance & Warranty Engine
* **Goal**: Work order execution, technician scheduling, SLA policy alerts, warranty tracking.
* **Key Deliverables**:
  - Work order workflow (DRAFT -> APPROVED -> TECHNICIAN_ASSIGNED -> IN_PROGRESS -> RESOLVED)
  - Technician skill matching & workload balancer
  - SLA breach monitoring & escalation timers
  - Spare parts inventory tracking and consumption logging
  - Warranty policy documentation, claim management & expiration alerts

### M7: Audits & Compliance Engine
* **Goal**: Periodic asset auditing, snapshot-based scope definitions, discrepancy resolution.
* **Key Deliverables**:
  - Audit cycle snapshot generator (by department, location, or category)
  - Auditor QR verification & condition score recording
  - Immutable audit closing transaction and automatic lifecycle updates
  - Data retention policies & immutable activity log archive verification

### M8: Notifications, Outbox & Integration Webhooks
* **Goal**: Asynchronous event publishing, in-app notifications, webhooks with retry capabilities.
* **Key Deliverables**:
  - Transactional Outbox pattern engine
  - In-app notification preferences, queue prioritization & digests
  - Scoped API key management and rotation
  - Signed webhook payload delivery (HMAC-SHA256) with exponential backoff
  - Dead-letter queue management and replay APIs

### M9: Search Engine & Analytical Reports
* **Goal**: Fast multi-attribute full-text asset search, asynchronous PDF & CSV report generation.
* **Key Deliverables**:
  - PostgreSQL trigram and tsvector full-text search indexes
  - Saved search presets & recent search history
  - Async PDF report renderer (Apache PDFBox)
  - Bulk CSV export engine with expiring download signed URLs
  - Operational KPIs, MTTR/MTBF metrics, and utilization heatmaps

### M10: Scalability, Performance & Caching
* **Goal**: High-throughput query performance, multi-node lock coordination.
* **Key Deliverables**:
  - ShedLock distributed job scheduling
  - Multi-tenant aware Redis / Spring cache layer
  - Cursor pagination for high-volume activity logs
  - Bulk asset import staging engine (`/api/v1/import-batches`) with dry-run validation

### M11: Security Hardening & Vulnerability Audit
* **Goal**: OWASP ASVS compliance, rate limiting, security logging, input sanitization.
* **Key Deliverables**:
  - Token bucket rate limiting filter
  - Sensitive data redaction in structured JSON logs
  - OpenAPI compatibility & breaking change validation
  - Dependency vulnerability scanning & secret protection

### M12: Production Operations & Monitoring
* **Goal**: Deployment containerization, observability metrics, health checks.
* **Key Deliverables**:
  - Multi-stage Docker container build (non-root execution)
  - Spring Boot Actuator custom health indicators & readiness/liveness probes
  - Prometheus metrics & Grafana dashboard specs
  - Database disaster recovery, backup & point-in-time recovery runbooks

### M13: Release Candidate & End-to-End Validation
* **Goal**: Full integration test suite run, staging deployment verification, release candidate testing.
* **Key Deliverables**:
  - Multi-user concurrency validation
  - End-to-end REST Assured regression test suite
  - Performance profiling & latency benchmark validation

### M14: Stable Release v1.0.0
* **Goal**: Final documentation wrap-up, tag release, production deployment readiness.
* **Key Deliverables**:
  - Complete OpenAPI documentation
  - Finalized release notes & changelog
  - Tagged `v1.0.0` production deployment build
