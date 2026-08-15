# ADR 004: Production Data Seeding and Initial Multi-Tenant Bootstrapping

## Status
Accepted

## Context
For testing, staging, demonstration environments, and quick onboarding, AssetFlow requires an automated, production-safe data seeder capability that can bootstrap default organizations, initial administrative accounts, sample departments, asset categories, assets, allocations, bookings, maintenance requests, and audit cycles without corrupting production environments or re-creating duplicate data on application restart.

## Decision
1. **Conditional Activation**: `DataSeeder` implements Spring Boot `CommandLineRunner` and triggers ONLY when `app.seed.enabled=true` (or environment variable `APP_SEED_ENABLED=true`). It is disabled by default in production.
2. **Natural Key Idempotency**:
   - `DataSeeder` performs natural key existence checks before saving entities (e.g. checking existing email for `UserAccount`, organization code for `Organization`, department code for `Department`, category code for `AssetCategory`).
   - If entities already exist, the seeder reuses existing entities without re-inserting or throwing unique key violation errors.
3. **Database Seed Migrations**: Baseline SQL migration `V42` seeds default organization UUIDs and `V43` seeds baseline asset category codes to ensure clean initial relational integrity across all environments.

## Consequences
- Developers and CI integration pipelines can instantly seed full operational test data by enabling `APP_SEED_ENABLED=true`.
- Prevents database primary key or unique constraint violations on application restarts.
- Production environments remain clean and unseeded when `app.seed.enabled` defaults to `false`.
