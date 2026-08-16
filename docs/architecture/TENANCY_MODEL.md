# Multi-Tenancy & Data Isolation Model

## 1. Overview

AssetFlow enforces strict organization-level multi-tenancy. Every tenant (Organization) is logically isolated at the application and database layers. Cross-tenant data leaks are treated as critical security violations.

---

## 2. Tenant Context Resolution Flow

```text
HTTP Request -> JwtAuthenticationFilter -> Extract org_id from JWT claims -> TenantAuthenticationDetails -> TenantContextHolder (ThreadLocal)
```

1. **Context Source**: The tenant identity (`organization_id`) is retrieved exclusively from the authenticated JWT token claims or authenticated principal context.
2. **Untrusted Inputs Rejected**: The application NEVER trusts tenant identity passed in HTTP request bodies or query parameters.
3. **Thread Scoping**: `TenantContextHolder` stores the active tenant context in a `ThreadLocal` wrapper, ensuring clean cleanup via filter `finally` blocks.
4. **MDC Correlation**: Active tenant ID is attached to SLF4J MDC (`tenantId`) during request execution and stripped upon request completion.

---

## 3. Database Isolation Strategy

### Shared Database, Discriminator Column (`organization_id`)
* **Schema Requirement**: Every tenant-owned database table includes an `organization_id UUID NOT NULL` column linked via foreign key to the `organizations` table.
* **Composite Indexes**: Query indexes include `organization_id` as the leading column (e.g., `idx_assets_org_tag ON assets(organization_id, asset_tag)`).
* **Composite Foreign Keys**: Relationships between tenant-owned entities include `organization_id` to guarantee cross-tenant entity references are impossible at the foreign-key constraint level.

```sql
-- Example composite foreign key ensuring cross-tenant references are rejected by DB
ALTER TABLE asset_allocations
ADD CONSTRAINT fk_allocations_asset
FOREIGN KEY (organization_id, asset_id)
REFERENCES assets (organization_id, id);
```

---

## 4. Cross-Tenant Leakage Prevention

1. **Query Filtering**: All JPA and custom SQL queries filter by `organization_id = :tenantId`.
2. **404 Information Concealment**: Requesting an entity ID belonging to another tenant returns `404 Not Found` rather than `403 Forbidden` to prevent resource enumeration attacks.
3. **Background Job Scoping**: Scheduled jobs and Outbox notification listeners restore and validate tenant context before processing background tasks.
4. **Tenant Security Tests**: Automated tests verify that tenant A cannot read, update, allocate, or delete entities belonging to tenant B.
