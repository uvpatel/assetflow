# AssetFlow

> Enterprise Asset & Resource Management System — A production-grade backend built with Java 21, Spring Boot 3.4+, PostgreSQL 16, and RESTful APIs.

[![CI Pipeline](https://github.com/assetflow/assetflow/actions/workflows/ci.yml/badge.svg)](https://github.com/assetflow/assetflow/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4+-green.svg)](#)
[![Java](https://img.shields.io/badge/Java-21%2B-red.svg)](#)

AssetFlow is a **modular monolith ERP backend system** designed for enterprise asset management, shared resource booking, maintenance workflows, multi-tenant organization control, and comprehensive audit cycles. It demonstrates professional Spring Boot development, domain-driven design, and production-grade testing practices.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Technology Stack](#technology-stack)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Development Workflow](#development-workflow)
- [Testing](#testing)
- [API Documentation](#api-documentation)
- [Database](#database)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [Troubleshooting](#troubleshooting)
- [Documentation](#documentation)
- [License](#license)

---

## Overview

AssetFlow solves critical enterprise challenges:

- **Asset Lifecycle Management** — Track physical assets from acquisition through depreciation and retirement
- **Resource Optimization** — Prevent double allocation and overlapping bookings with concurrency-safe state management
- **Workflow Automation** — Orchestrate complex maintenance requests, approvals, and technician assignments
- **Audit & Compliance** — Maintain immutable activity logs and execute comprehensive audit cycles
- **Multi-Tenant Security** — Support multiple organizations with complete tenant isolation
- **Operational Intelligence** — Generate dashboards, utilization reports, and actionable alerts

### What AssetFlow Does NOT Include (V1)

- Procurement and vendor management
- Purchase orders and invoicing
- General ledger or accounting integration
- Payroll and attendance tracking
- IoT device tracking

---

## Features

✅ **Multi-Tenant Architecture**
- Secure organization isolation with database-level enforcement
- Role-based access control (RBAC) with non-self-elevating permissions
- Department hierarchies and employee management

✅ **Asset Management**
- Complete asset lifecycle (purchased, in-use, maintenance, retired)
- Dynamic category-specific fields for flexible asset categorization
- Photo/document attachments and asset tagging
- Condition assessments and warranty tracking
- Depreciation schedule management

✅ **Resource Booking**
- Calendar-based half-open interval booking model
- Automatic overlap detection and prevention
- Real-time availability queries

✅ **Maintenance Workflows**
- Request initiation, approval routing, and technician assignment
- Skilled technician matching
- SLA policies and workload management
- Parts inventory tracking and consumption

✅ **Asset Allocation**
- Concurrency-safe active uniqueness (one holder per asset)
- Transfer workflows with approval
- Return and checkout operations
- Location tracking and assignment history

✅ **Audit Cycles**
- Snapshot-based audit scope definition
- Auditor verification and discrepancy flagging
- Resolution tracking and audit completion

✅ **Notifications**
- In-app alerts for pending actions
- Transactional outbox pattern for reliable email delivery
- Event-driven notification publishing

✅ **Activity Logging**
- Immutable governance audit logs
- Complete audit trail for compliance
- User action tracking and timestamps

✅ **Financial Management**
- Currency exchange rate management
- Tax write-off scheduling
- Cost tracking and reporting

✅ **Comprehensive Reporting**
- Operational analytics and utilization metrics
- PDF and CSV export capabilities
- Query-based report generation

---

## System Architecture

```text
assetflow/
├── src/main/java/com/assetflow/          # Spring Boot REST API (Modular Monolith)
│   ├── identity/                         # User accounts, JWT auth, password reset
│   ├── organization/                     # Multi-tenancy, departments, employees
│   ├── asset/                            # Asset catalog, categories, lifecycle
│   ├── allocation/                       # Asset allocation, transfer workflows
│   ├── booking/                          # Resource calendar, availability
│   ├── maintenance/                      # Request workflow, technician assignment
│   ├── audit/                            # Audit cycles, discrepancy tracking
│   ├── notification/                     # In-app alerts, email delivery
│   ├── activitylog/                      # Immutable governance audit logs
│   ├── reporting/                        # Analytics, PDF/CSV reports
│   ├── financial/                        # Currency, tax write-off management
│   ├── webhook/                          # Event publishing and webhooks
│   ├── shared/                           # Cross-cutting: auth, tenancy, filters
│   └── AssetFlowApplication.java         # Spring Boot entry point
│
├── src/main/resources/
│   ├── application.yml                   # Spring Boot configuration
│   └── db/migration/                     # Flyway database migrations
│
├── src/test/java/com/assetflow/          # Unit, integration, architecture tests
│
├── infrastructure/                       # Docker Compose & deployment configs
├── docs/adr/                             # Architecture Decision Records
├── PLAN.md                               # Master engineering & delivery plan
├── pom.xml                               # Maven dependencies & build config
└── docker-compose.yml                    # Local development services
```

### Module Organization

AssetFlow uses **layered domain architecture** within each module:

| Layer | Purpose | Files |
|-------|---------|-------|
| **Domain** | Business logic, aggregates, invariants | `*Domain.java`, `*Repository.java` |
| **Application** | Use cases, DTOs, coordination | `*UseCase.java`, `*Service.java` |
| **API** | HTTP controllers, request/response mapping | `*Controller.java`, `*Request.java` |
| **Infrastructure** | Persistence, external services | `*JpaRepository.java`, `*Adapter.java` |

### Module Dependency Graph

```
identity ─┐
          ├─→ organization ──┐
shared    │                  ├─→ asset ──┐
          │                  │           ├─→ allocation
          │                  │           │
          ├─→ booking ────────┘           ├─→ maintenance
          │                               │
          ├─→ audit ─────────────────────┘
          │
          ├─→ notification, reporting, financial, activitylog, webhook
```

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 21+ |
| **Framework** | Spring Boot | 3.4+ |
| **Web** | Spring Web MVC | 3.4+ |
| **Database** | PostgreSQL | 16+ |
| **ORM** | JPA/Hibernate | 6.4+ |
| **Migrations** | Flyway | 10+ |
| **Testing** | JUnit 5, Testcontainers | 5.10+, 1.20+ |
| **Security** | Spring Security, JWT | 3.4+ |
| **API Docs** | SpringDoc OpenAPI (Swagger) | 2.1+ |
| **Build** | Maven | 3.8+ |
| **Containerization** | Docker | 20+ |

---

## ⚡ Quick Start

### 1. Prerequisites

```bash
# Required tools
- Docker & Docker Compose (v20+)
- Java 21 or newer (JDK, not JRE)
- Maven 3.8+
- Git

# Verify installation
docker --version    # Docker version 20+
java -version       # Java 21+
mvn --version       # Maven 3.8+
```

### 2. Clone and Setup

```bash
git clone https://github.com/assetflow/assetflow.git
cd assetflow
```

### 3. Start Infrastructure Services

```bash
docker compose up -d
```

Verify services are healthy:

```bash
docker compose ps
```

Services now running:
- **PostgreSQL**: `jdbc:postgresql://localhost:5432/assetflow`
  - User: `assetflow` | Password: `assetflow`
- **MinIO (S3)**: `http://localhost:9000` (API) | `http://localhost:9001` (Console)
  - User: `minioadmin` | Password: `minioadmin`
- **Mailpit (SMTP)**: `localhost:1025` (SMTP) | `http://localhost:8025` (Web UI)

### 4. Build and Run Application

```bash
# Build the project
mvn clean install

# Run the application
./mvnw spring-boot:run
```

Or run tests:

```bash
mvn clean verify
```

### 5. Access Services

| Service | URL |
|---------|-----|
| **API Base** | `http://localhost:8080/api/v1` |
| **OpenAPI Spec** | `http://localhost:8080/api-docs` |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **Health Check** | `http://localhost:8080/actuator/health` |
| **Metrics** | `http://localhost:8080/actuator/metrics` |
| **MinIO Console** | `http://localhost:9001` |
| **Mailpit Web UI** | `http://localhost:8025` |

---

## Project Structure

### Core Modules

#### 🔐 Identity Module (`identity/`)
User authentication and authorization
- User signup, login, password reset
- JWT token generation and validation
- Refresh token rotation
- **Key Classes**: `AuthController`, `LoginUseCase`, `JwtTokenProvider`

#### 🏢 Organization Module (`organization/`)
Multi-tenant organizational structure
- Organization management and tenant isolation
- Department hierarchies
- Employee and team management
- Role and permission system
- **Key Classes**: `DepartmentHierarchy`, `EmployeeRepository`

#### 📦 Asset Module (`asset/`)
Asset lifecycle and categorization
- Asset creation and management
- Category-specific dynamic fields
- Lifecycle state management (purchased → in-use → retired)
- Condition assessments, warranties, and depreciation
- Photo and document attachments
- **Key Classes**: `AssetAggregate`, `AssetCategory`, `ConsumableAsset`

#### 🔄 Allocation Module (`allocation/`)
Asset assignment and transfer workflows
- Concurrency-safe active allocation (one holder per asset)
- Transfer request and approval workflows
- Return and checkout operations
- Active uniqueness constraint enforcement
- **Key Classes**: `AllocationAggregate`, `TransferUseCase`, `TemporaryAssetCheckout`

#### 📅 Booking Module (`booking/`)
Shared resource calendar and availability
- Half-open interval booking model (start-inclusive, end-exclusive)
- Automatic overlap detection
- Resource availability queries
- Booking approval workflows
- **Key Classes**: `BookingAggregate`, `BookingQuery`

#### 🔧 Maintenance Module (`maintenance/`)
Maintenance request and workflow management
- Request initiation and routing
- Approval workflows
- Technician assignment and workload management
- SLA policy enforcement
- Parts and material tracking
- **Key Classes**: `MaintenanceRequest`, `TechnicianAssignment`, `MaintenanceSlaPolicyTest`

#### ✓ Audit Module (`audit/`)
Audit cycle and compliance management
- Audit cycle creation and scope definition
- Snapshot-based asset verification
- Discrepancy identification and resolution
- Auditor assignment
- **Key Classes**: `AuditCycleAggregate`, `AuditDiscrepancyResolution`

#### 🔔 Notification Module (`notification/`)
In-app alerts and email notifications
- Event-driven notification publishing
- Transactional outbox pattern for reliability
- Email delivery with templates
- Notification preferences and subscriptions
- **Key Classes**: `UserNotificationUseCase`, `OutboxNotificationSender`

#### 📊 Reporting Module (`reporting/`)
Operational analytics and export
- Utilization and occupancy reports
- Asset lifecycle analytics
- PDF and CSV export
- Custom report generation
- **Key Classes**: `PdfReportService`, `UtilizationReportQuery`

#### 💰 Financial Module (`financial/`)
Financial and cost tracking
- Currency exchange rate management
- Tax write-off scheduling
- Depreciation calculations
- Cost tracking and reporting
- **Key Classes**: `CurrencyExchangeRate`, `AssetTaxWriteOff`

#### 📝 Activity Log Module (`activitylog/`)
Immutable governance audit logging
- User action tracking
- Complete audit trail
- Compliance and governance
- **Key Classes**: `ActivityLogAggregate`

#### 🪝 Webhook Module (`webhook/`)
Event publishing and external system integration
- Domain event publishing
- External webhook calls
- Retry mechanisms
- **Key Classes**: `WebhookPublisher`

#### 🔒 Shared Module (`shared/`)
Cross-cutting concerns
- Tenant context management
- Authentication filters
- Security configuration
- Common utilities
- **Key Classes**: `TenantContextFilter`, `TenantContextHolder`

---

## Development Workflow

### Local Setup for Contributors

```bash
# 1. Fork and clone the repository
git clone https://github.com/YOUR_USERNAME/assetflow.git
cd assetflow

# 2. Add upstream remote
git remote add upstream https://github.com/assetflow/assetflow.git

# 3. Create a feature branch
git checkout -b feature/your-feature-name

# 4. Start development environment
docker compose up -d
mvn clean install

# 5. Run tests to verify setup
mvn test
```

### Making Changes

#### Single Feature Branch

```bash
# Create meaningful commits as you work
git add src/main/java/com/assetflow/asset/
git commit -m "feat(asset): add condition assessment tracking

- Implement ConditionAssessment value object
- Add assessment history to Asset aggregate
- Create ConditionAssessmentRepository for persistence
- Add unit tests for condition assessment logic"

# Push to your fork
git push origin feature/your-feature-name

# Create Pull Request on GitHub
```

#### Commit Message Convention

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

- **Types**: `feat`, `fix`, `refactor`, `test`, `docs`, `style`, `chore`, `perf`
- **Scope**: Module name or component (e.g., `asset`, `allocation`, `identity`)
- **Subject**: Imperative mood, lowercase, max 50 chars
- **Body**: Detailed explanation of the change (wrap at 72 chars)
- **Footer**: Reference issues (e.g., `Closes #123`)

### Example Workflow

```bash
# 1. Pick a task from PLAN.md
# 2. Create feature branch
git checkout -b feat/asset-depreciation-schedule

# 3. Implement with tests
mvn test                          # Run tests frequently
mvn checkstyle:check             # Verify code style

# 4. Commit with detailed message
git commit -m "feat(asset): implement depreciation schedule

- Add AssetDepreciationSchedule aggregate with state machine
- Support straight-line, accelerated, and custom schedules
- Implement schedule validation and calculation
- Add comprehensive test coverage (15 new tests)
- Integrate with asset lifecycle state transitions

Closes #42"

# 5. Push and create PR
git push origin feat/asset-depreciation-schedule
```

---

## 🧪 Testing

AssetFlow maintains high code quality through comprehensive testing:

### Test Types

| Type | Command | Coverage |
|------|---------|----------|
| **Unit Tests** | `mvn test` | Individual classes and methods |
| **Integration Tests** | `mvn verify` | Database, repository, service interactions |
| **Architecture Tests** | `mvn verify` | Layer violations, module boundaries |
| **Test + Coverage** | `mvn verify -Dgroups=coverage` | Code coverage reports |

### Running Tests

```bash
# Run all tests
mvn clean verify

# Run specific test class
mvn test -Dtest=AssetDepreciationScheduleTest

# Run tests matching pattern
mvn test -Dtest=*AllocationTest

# Skip tests during build
mvn clean install -DskipTests

# Run with coverage report
mvn clean verify jacoco:report
# View report at: target/site/jacoco/index.html
```

### Test Organization

```
src/test/java/com/assetflow/
├── <module>/
│   ├── domain/              # Domain model tests
│   ├── application/         # Use case tests
│   ├── api/                 # Controller tests
│   └── infrastructure/      # Repository tests
└── ArchitectureTest.java    # Modular monolith structure validation
```

### Testing Best Practices

- ✅ Unit tests for domain logic (arrange-act-assert)
- ✅ Integration tests using Testcontainers (PostgreSQL)
- ✅ Test names describe behavior (e.g., `shouldPreventDoubleAllocation()`)
- ✅ Comprehensive edge case coverage
- ✅ Architecture tests enforce layer boundaries
- ✅ Fixture builders for test data creation

### Example Unit Test

```java
@DisplayName("Allocation Invariant: Active Uniqueness")
class AllocationActiveUniquenessTest {
    
    @Test
    @DisplayName("should prevent allocating same asset to two holders")
    void shouldPreventDoubleAllocation() {
        // arrange
        Asset asset = Asset.newInstance("LAPTOP-001");
        Holder holder1 = Holder.from("USER-1");
        Holder holder2 = Holder.from("USER-2");
        
        // act
        allocation.allocateTo(holder1);
        
        // assert
        assertThrows(AllocationInvariantViolation.class,
            () -> allocation.allocateTo(holder2));
    }
}
```

---

## API Documentation

### Base URL

```
http://localhost:8080/api/v1
```

### Authentication

AssetFlow uses JWT (Bearer Token) authentication:

```bash
# 1. Signup
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "secure-password"
  }'

# 2. Login to get token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "secure-password"
  }'
# Returns: { "accessToken": "...", "refreshToken": "..." }

# 3. Use token in requests
curl -X GET http://localhost:8080/api/v1/assets \
  -H "Authorization: Bearer <accessToken>"
```

### API Documentation Tools

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`
- **Detailed API Specs**: See [PLAN.md](PLAN.md) for endpoint reference

### Common Endpoints

```
# Asset Management
GET    /api/v1/assets                  # List assets
POST   /api/v1/assets                  # Create asset
GET    /api/v1/assets/{id}             # Get asset details
PATCH  /api/v1/assets/{id}             # Update asset

# Allocations
POST   /api/v1/allocations             # Allocate asset
GET    /api/v1/allocations/{id}        # Get allocation
POST   /api/v1/allocations/{id}/transfer   # Transfer asset
POST   /api/v1/allocations/{id}/return     # Return asset

# Bookings
POST   /api/v1/resources/{id}/bookings # Book resource
GET    /api/v1/resources/{id}/availability # Check availability

# Maintenance
POST   /api/v1/maintenance/requests    # Create maintenance request
GET    /api/v1/maintenance/requests/{id}   # Get request details
PATCH  /api/v1/maintenance/requests/{id}   # Update status

# Reports
GET    /api/v1/reports/utilization    # Asset utilization report
GET    /api/v1/reports/allocation      # Allocation analytics
```

---

## Database

### PostgreSQL Schema

AssetFlow uses **Flyway** for database version control. All schema changes are tracked in migrations:

```
src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__add_asset_warranty.sql
└── V3__add_audit_cycle.sql
```

### Schema Highlights

- **Multi-tenancy**: All tables include `organization_id` partition key
- **Immutability**: `activity_log` table is append-only
- **Constraints**: Foreign keys, unique constraints, check constraints
- **Indexes**: Strategic indexes on frequently queried columns
- **Audit columns**: `created_at`, `updated_at`, `created_by_user_id`

### Connection Details (Development)

```
Host:     localhost
Port:     5432
Database: assetflow
User:     assetflow
Password: assetflow
```

### Accessing Database

```bash
# Connect via psql
psql -h localhost -U assetflow -d assetflow

# Common queries
\dt                          # List all tables
\d assets                    # Describe assets table
SELECT * FROM users;         # Query users
SELECT * FROM activity_log;  # Audit trail
```

### Database Health Check

```bash
# Via Spring Boot Actuator
curl http://localhost:8080/actuator/health/db
```

---

## Configuration

### Environment Variables

AssetFlow is configured via environment variables (12-factor app):

```bash
# Database
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=assetflow
export DB_USER=assetflow
export DB_PASSWORD=assetflow

# Server
export PORT=8080
export ENVIRONMENT=development

# Security
export JWT_SECRET=your-super-secret-key-min-32-chars
export JWT_EXPIRATION=3600000    # 1 hour in milliseconds

# S3/MinIO
export S3_ENDPOINT=http://localhost:9000
export S3_ACCESS_KEY=minioadmin
export S3_SECRET_KEY=minioadmin
export S3_BUCKET=assetflow

# Email (Mailpit)
export MAIL_HOST=localhost
export MAIL_PORT=1025
export MAIL_FROM=noreply@assetflow.local
```

### Spring Profiles

```bash
# Development (default)
./mvnw spring-boot:run

# With specific profile
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# Available profiles
- development    # Local PostgreSQL, MinIO, debug logging
- test           # In-memory H2, test fixtures
- staging        # Staging database, minimal logging
- production     # Production settings, security enabled
```

### Configuration Files

```
src/main/resources/
├── application.yml           # Default configuration
├── application-dev.yml       # Development overrides
├── application-test.yml      # Test configuration
└── application-prod.yml      # Production configuration
```

---

## Contributing

### Getting Started

1. **Read**: [PLAN.md](PLAN.md) to understand the project vision
2. **Review**: [Architecture Decision Records](docs/adr/) for design decisions
3. **Pick**: An issue from GitHub Issues or select a task from [PLAN.md](PLAN.md)
4. **Setup**: Follow [Development Workflow](#development-workflow)

### Contribution Guidelines

✅ **Do**
- Write clear commit messages (Conventional Commits)
- Include comprehensive tests for new features
- Update documentation with your changes
- Reference GitHub issues in commit messages
- Keep commits focused and logically separated
- Run `mvn clean verify` before pushing

❌ **Don't**
- Mix multiple features in one PR
- Skip tests or reduce test coverage
- Make large refactorings without discussion
- Commit directly to `main` or `develop`
- Use `git push --force` on shared branches

### PR Review Process

1. **Push** feature branch to your fork
2. **Create** Pull Request with detailed description
3. **CI Checks** run automatically (tests, code quality)
4. **Review** by maintainers and team members
5. **Address** feedback with new commits
6. **Merge** after approval

### Development Standards

| Aspect | Standard |
|--------|----------|
| **Language** | Java 21+, Spring Boot 3.4+ conventions |
| **Code Style** | Google Java Style Guide (Checkstyle) |
| **Testing** | Minimum 80% code coverage, BDD test names |
| **Documentation** | Javadoc on public APIs, ADRs for decisions |
| **Performance** | No N+1 queries, appropriate indexes |
| **Security** | No hardcoded secrets, SQL injection prevention, tenant isolation |

---

## Troubleshooting

### Common Issues

#### 1. PostgreSQL Connection Refused
```bash
# Check if Docker containers are running
docker compose ps

# If not, start them
docker compose up -d

# Check database logs
docker compose logs postgres
```

#### 2. Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or use a different port
export PORT=8081
./mvnw spring-boot:run
```

#### 3. Out of Memory During Build

```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx2g -XX:+UseG1GC"
mvn clean verify
```

#### 4. Database Migration Failed

```bash
# Clean and reset database
docker compose down -v
docker compose up -d

# Wait for database to be ready
sleep 10

# Rebuild
mvn clean install
```

#### 5. Tests Fail with "Cannot create table"

```bash
# Ensure test database is properly reset
mvn test -Dtest=AssetDepreciationScheduleTest -X

# Run with database cleanup
mvn clean test
```

### Debugging

#### Enable Debug Logging

```bash
# Add to application.yml
logging:
  level:
    root: INFO
    com.assetflow: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql: TRACE
```

#### Debug Tests

```bash
# Run single test with debugging
mvn test -Dtest=AllocationTest#shouldPreventDoubleAllocation -DdebuggerPort=5005

# Connect IDE debugger to localhost:5005
```

#### Check Spring Boot Logs

```bash
# Follow logs from running application
./mvnw spring-boot:run | grep -E "(ERROR|WARN|Started)"

# Save logs to file
./mvnw spring-boot:run > app.log 2>&1
```

---

## 📜 Documentation

- **[PLAN.md](PLAN.md)** — Master backend engineering plan, API specs, and delivery roadmap
- **[Architecture Decision Records (ADRs)](docs/adr/)** — Design decisions and trade-offs
  - [001: Modular Monolith Architecture](docs/adr/001-modular-monolith-architecture.md)
  - [002: Auth & Tenant Security](docs/adr/002-auth-and-tenant-security.md)
  - [003: Concurrency & Invariants](docs/adr/003-concurrency-and-invariants.md)
- **[Security Policy](SECURITY.md)** — Security considerations and best practices
- **[Contributing Guidelines](CONTRIBUTING.md)** — How to contribute
- **[Swagger UI](http://localhost:8080/swagger-ui.html)** — Interactive API documentation
- **[Spring Boot Docs](https://spring.io/projects/spring-boot)** — Official Spring Boot documentation
- **[PostgreSQL Docs](https://www.postgresql.org/docs/)** — Database reference

---

## 📄 License

This project is licensed under the [MIT License](LICENSE). See LICENSE file for details.

---

## Support

- **Issues**: [GitHub Issues](https://github.com/assetflow/assetflow/issues)
- **Discussions**: [GitHub Discussions](https://github.com/assetflow/assetflow/discussions)
- **Email**: support@assetflow.local
