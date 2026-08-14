# AssetFlow

> Enterprise Asset & Resource Management System built with Java 21, Spring Boot 3/4, PostgreSQL, and Next.js 15/16.

[![CI Pipeline](https://github.com/assetflow/assetflow/actions/workflows/ci.yml/badge.svg)](https://github.com/assetflow/assetflow/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

AssetFlow is a modular monolith ERP application for enterprise asset management, shared resource booking, maintenance workflow, multi-tenant organization control, and audit cycle execution.

---

## 🏗 System Architecture

```text
assetflow/
├── backend/            # Spring Boot REST API (Modular Monolith, Java 21)
├── frontend/           # Next.js Web Application (React, Tailwind CSS, shadcn/ui)
├── infrastructure/     # Docker Compose, Monitoring & Deployment configs
├── docs/               # Architecture decisions (ADRs) & API specs
└── PLAN.md             # Master Engineering & Delivery Plan
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
- **Java 21 LTS**
- **Node.js** (v20+ or v24+)

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
cd backend
./mvnw spring-boot:run
```

The REST API will start at `http://localhost:8080/api/v1`.
OpenAPI / Swagger documentation available at `http://localhost:8080/swagger-ui.html`.

### 3. Run Frontend (Next.js)

```bash
cd frontend
npm install
npm run dev
```

The web application will start at `http://localhost:3000`.

---

## 🧪 Testing

### Run Backend Tests (Unit + Integration + ArchUnit)

```bash
cd backend
./mvnw clean verify
```

### Run Frontend Tests

```bash
cd frontend
npm run test
```

---

## 📜 Documentation

- [Master Engineering Plan (PLAN.md)](PLAN.md)
- [Architecture Decision Records (ADRs)](docs/adr/)
- [Contributing Guidelines](CONTRIBUTING.md)
- [Security Policy](SECURITY.md)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
