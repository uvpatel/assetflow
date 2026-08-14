# ADR 001: Modular Monolith Architecture

## Context
AssetFlow requires strong transactional consistency across assets, allocations, maintenance records, audit cycles, and immutable activity logs. The system is initially developed and maintained by a focused engineering team.

## Decision
We will build AssetFlow as a **Modular Monolith** using Java 21, Spring Boot, and PostgreSQL, organized by **Package-by-Feature** (`com.assetflow.<module>`).

Modules:
- `identity`
- `organization`
- `asset`
- `allocation`
- `booking`
- `maintenance`
- `audit`
- `notification`
- `activitylog`
- `reporting`

Rules:
1. Modules must maintain strict boundary isolation.
2. Cross-module database repository access is prohibited; modules must interact via defined application service ports or asynchronous transactional domain events.
3. ArchUnit tests will automatically verify and enforce module dependency rules in CI.

## Consequences
- Single runtime deployment and simple CI/CD setup.
- Guaranteed ACID transactional boundaries when initiating complex workflows (e.g. closing an audit cycle).
- Easy path to microservices extraction in the future if high scale or separate team ownership demands it.
