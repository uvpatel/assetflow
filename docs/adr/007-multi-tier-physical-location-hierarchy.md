# ADR 007: Multi-Tier Physical Location Hierarchy Engine

## Status
Accepted

## Context
Organizations need to track the physical placement of assets across complex campuses, buildings, floors, rooms, and storage areas. The location model must support arbitrary tree depth while enforcing strict multi-tenant isolation and preventing circular references or cross-tenant parent assignments.

## Decision
1. **Self-Referencing Tree Model**:
   - `locations` table with `parent_location_id UUID REFERENCES locations(id) ON DELETE RESTRICT`.
   - `type VARCHAR(50)` categorized by `LocationType` enum (`CAMPUS`, `BUILDING`, `FLOOR`, `ROOM`, `STORAGE_AREA`).
   - Unique composite natural key constraint: `UNIQUE (organization_id, code)`.
2. **Invariants & Validation**:
   - A location cannot be its own parent (`id != parent_location_id`).
   - Parent location must belong to the exact same tenant (`parent.organization_id == child.organization_id`).
   - Deleting parent locations with active sub-locations is restricted at the database level.
3. **Hierarchy Projections**:
   - `LocationUseCase.buildLocationTree(organizationId)` returns a nested `LocationTreeNodeDto` forest for UI tree rendering and spatial navigation.

## Consequences
- Clean representation of complex real-world facilities.
- Fast sub-tree lookups and category-based filtering.
- Guaranteed multi-tenant integrity across all hierarchy levels.
