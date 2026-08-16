# Contributing to AssetFlow

Thank you for contributing to AssetFlow! As an enterprise-grade backend system, we follow rigorous architectural, testing, and Git conventions.

---

## 1. Branching & Commit Conventions

### Branch Naming
All branches must follow the pattern `<type>/<issue-number>-<short-description>`:
- `feat/142-asset-registration`
- `fix/207-booking-overlap-race`
- `test/318-allocation-concurrency`
- `refactor/361-maintenance-policy`
- `docs/404-webhook-security`
- `chore/447-java-upgrade`
- `perf/481-asset-search-index`

### Conventional Commits
Commit messages must follow the Conventional Commits format: `<type>(<scope>): <description>`:
- `feat(asset): introduce asset tag value object`
- `feat(booking): add PostgreSQL overlap constraint`
- `fix(allocation): enforce unique active allocation`
- `test(security): reject cross-tenant asset lookup`
- `refactor(maintenance): extract transition policy`
- `docs(api): document audit closing conflicts`
- `chore(ci): validate OpenAPI compatibility`

---

## 2. Mandatory Operating Protocol

Before modifying anything:
1. Inspect the repository.
2. Read `README.md`, `PLAN.md`, `CONTRIBUTING.md`, and relevant ADRs.
3. Inspect the current Git branch and working tree.
4. Select one focused capability.
5. Identify domain invariants and security boundaries.
6. Plan the implementation.

After completing the capability:
1. Run formatting checks.
2. Run static analysis.
3. Run affected unit and integration tests (`./mvnw test`).
4. Run architecture tests (`ArchitectureTest`).
5. Verify zero test failures or regressions.
6. Create focused Conventional Commits (3–6 commits per capability).
7. Submit a clean, reviewable pull request.

---

## 3. Definition of Done (DoD)

An issue is complete only when:
- Acceptance criteria pass.
- Business rules are enforced in the domain/application layer.
- Authorization and tenant isolation are verified.
- Concurrency invariants are protected (database constraints + application locking).
- Unit and integration tests pass cleanly.
- Error handling conforms to RFC 9457 Problem Details.
- No secrets or sensitive personal data are logged.
- Changes are fully documented.
