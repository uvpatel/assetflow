# ADR 003: Concurrency Control and Invariant Protection Strategies

## Status
Accepted

## Context
As an enterprise asset and resource management system, AssetFlow must guarantee strict data integrity under concurrent user operations. Crucially:
- An asset must never have more than one active allocation (`returned_at IS NULL`).
- A shared resource must never have overlapping active bookings `[start_time, end_time)`.
- Asset lifecycle state transitions must strictly adhere to valid state machine paths.

## Decision
1. **Double Allocation Prevention**:
   - Application layer: Optimistic concurrency control (`@Version`) on `Asset` and `AssetAllocation`.
   - Database layer: Partial unique index enforcing a single active allocation per asset:
     ```sql
     CREATE UNIQUE INDEX uq_active_asset_allocation 
     ON asset_allocations(asset_id) 
     WHERE returned_at IS NULL;
     ```
2. **Booking Overlap Protection**:
   - Booking intervals use half-open timestamps `[start_time, end_time)`.
   - Database layer: PostgreSQL `EXCLUDE` constraint using `btree_gist` / `tsrange` for overlapping interval prevention.
   - Application layer transactional check: `existing.start < requested.end AND existing.end > requested.start`.
3. **Asset Lifecycle Transition Policy**:
   - All status transitions MUST be processed via `AssetLifecycleService`. Controllers and direct persistence modifications are prohibited.
   - Terminal states (`DISPOSED`) cannot return to service.

## Consequences
- Guaranteed zero double-allocations or overlapping bookings even under heavy concurrent API requests.
- Race conditions fail fast with clean `409 Conflict` (RFC 9457) API responses.
