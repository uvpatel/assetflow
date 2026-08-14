# AssetFlow

> Backend-only Enterprise Asset & Resource Management System built with Java 25, Spring Boot 4.1, PostgreSQL, and production-grade REST APIs.

[![CI Pipeline](https://github.com/assetflow/assetflow/actions/workflows/ci.yml/badge.svg)](https://github.com/assetflow/assetflow/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

AssetFlow is a modular monolith ERP backend system for enterprise asset management, shared resource booking, maintenance workflow, multi-tenant organization control, and audit cycle execution.

---

## 🏗 System Architecture

```text
assetflow/
├── src/main/java/com/assetflow/  # Spring Boot REST API (Modular Monolith, Java 25)
├── src/main/resources/          # DB Migrations (Flyway) & App Configs
├── src/test/java/com/assetflow/  # Unit, Integration & ArchUnit Tests
├── infrastructure/               # Docker Compose, Monitoring & Deployment configs
├── docs/                         # Architecture decisions (ADRs) & API specs
└── PLAN.md                       # Master Backend Engineering & Delivery Plan
```

### Module Boundaries (`com.assetflow`)

- `identity`: User accounts, JWT auth, refresh tokens, password reset.
- `organization`: Multi-tenancy, departments, employees, role promotions.
- `asset`: Asset catalog, dynamic category fields, tags, photos/documents, lifecycle states.
- `allocation`: Asset allocations, concurrency-safe active uniqueness, transfer & return workflows.
- `booking`: Resource calendar, half-open interval booking overlap prevention.
- `maintenance`: Request workflow, approvals, technician assignment, resolution & state syncing.
- `audit`: Audit cycles, scope snapshots, auditor verification, discrepancy closing.
- `notification`: In-app alerts, transactional outbox email delivery.
- `activitylog`: Immutable governance audit logs.
- `reporting`: Operational analytics, utilization reports, PDF/CSV export engine.

---

## ⚡ Quick Start

### Prerequisites

- **Docker & Docker Compose** (v20+)
- **Java 25 LTS**

### 1. Launch Local Infrastructure (PostgreSQL, MinIO, Mailpit)

```bash
docker compose up -d
```

Services will be accessible at:
- **PostgreSQL**: `localhost:5432` (db: `assetflow`, user: `assetflow`, pass: `assetflow`)
- **MinIO S3 Console**: `http://localhost:9001` (user: `minioadmin`, pass: `minioadmin`)
- **Mailpit Web UI**: `http://localhost:8025`

### 2. Run Backend (Spring Boot)

```bash
./mvnw spring-boot:run
```

The REST API will start at `http://localhost:8080/api/v1`.
OpenAPI / Swagger documentation available at `http://localhost:8080/swagger-ui.html`.

---

## 🧪 Testing

### Run Backend Tests (Unit + Integration + ArchUnit)

```bash
./mvnw clean verify
```

---

## 📜 Documentation

- [Master Backend Engineering Plan (PLAN.md)](PLAN.md)
- [Architecture Decision Records (ADRs)](docs/adr/)
- [Contributing Guidelines](CONTRIBUTING.md)
- [Security Policy](SECURITY.md)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
