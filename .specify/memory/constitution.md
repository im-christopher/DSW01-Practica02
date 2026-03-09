<!--
Sync Impact Report
- Version change: 1.0.0 → 1.1.0
- Modified principles:
	- V. Containerization & API Contract Transparency → V. Containerization & Versioned API Contract Transparency
- Added sections:
	- None
- Removed sections:
	- None
- Templates requiring updates:
	- ✅ updated: .specify/templates/plan-template.md
	- ✅ updated: .specify/templates/spec-template.md
	- ✅ updated: .specify/templates/tasks-template.md
	- ✅ updated: specs/001-crud-empleados/quickstart.md
	- ✅ updated: .github/agents/copilot-instructions.md
	- ⚠ pending: .specify/templates/commands/*.md (directory not present; nothing to update)
- Deferred TODOs:
	- None
-->

# DSW01-Practica02 Constitution

## Core Principles

### I. Backend-Only Architecture
All scoped features MUST target backend capabilities only and MUST be implemented as
Spring Boot services and components. Frontend/UI scope MUST be excluded unless a
governance amendment explicitly introduces it. API-first service boundaries MUST be
defined before implementation begins.

Rationale: A backend-only scope keeps delivery focused, reduces ambiguity, and preserves
system cohesion.

### II. Standard Runtime Baseline
All services MUST run on Java 17 and Spring Boot 3.x. New code MUST use Spring Boot 3
idioms and compatible dependencies only. Any proposed downgrade or mixed runtime version
MUST be rejected unless approved through governance.

Rationale: A single runtime baseline improves compatibility, operability, and supportability.

### III. Security Baseline (NON-NEGOTIABLE)
All exposed endpoints MUST be protected with HTTP Basic Authentication by default.
The baseline credentials for non-production development environments are fixed as
username `admin` and password `admin123`. Production deployments MUST replace these
credentials via environment-based secure configuration and MUST NOT embed secrets in
source-controlled files.

Rationale: Enforcing default authentication and secret hygiene prevents accidental
unauthenticated exposure and hard-coded credential risks.

### IV. Data & Persistence Discipline
Primary persistence MUST be PostgreSQL. Data access MUST use explicit migrations, clear
entity constraints, and environment-specific connection configuration. Features that add
or change data behavior MUST include integration tests covering repository and API layers.

Rationale: PostgreSQL standardization and migration discipline minimize runtime drift and
data integrity regressions.

### V. Containerization & Versioned API Contract Transparency
Services MUST be executable with Docker and documented with OpenAPI/Swagger. Every
public endpoint MUST appear in generated API docs with authentication expectations and
response models. Every public backend route MUST be explicitly versioned in the path
using `/api/v{major}/...` (for example `/api/v1/empleados`), and breaking API changes
MUST increment the major version path. Container build/runtime definitions MUST be
reproducible in development and CI.

Rationale: Container-first execution and explicit API contracts improve deployment
predictability and integration readiness.

## Technical Standards

- Framework: Spring Boot 3.x (Web, Security, Validation, Data JPA as needed).
- Language: Java 17.
- Database: PostgreSQL as system of record.
- API Docs: springdoc-openapi/Swagger UI enabled in non-production by default.
- API Versioning: Public routes MUST use `/api/v{major}/...`; non-versioned public
	routes are non-compliant.
- Container Runtime: Dockerfile and local container execution MUST be maintained.
- Configuration: Environment variables MUST be preferred for secrets and deploy-specific
	values; committed defaults MAY exist only for local development and MUST be clearly
	non-production.

## Delivery Workflow & Quality Gates

1. Specifications and plans MUST include constitution checks before implementation.
2. Pull requests MUST confirm: Java 17/Spring Boot 3 compliance, Basic Auth coverage,
	 PostgreSQL alignment, Docker viability, versioned API paths, and Swagger
	 documentation completeness.
3. Changes affecting authentication, persistence, or API contracts MUST include tests
	 and updated operational documentation.
4. CI pipelines SHOULD run unit and integration tests; failures in security or migration
	 checks MUST block merge.

## Governance
This constitution supersedes local conventions for architecture and delivery quality.
Amendments require: (a) a documented change proposal, (b) impact analysis on templates
and active specs, and (c) maintainer approval.

Versioning policy:
- MAJOR: backward-incompatible governance changes or principle removal/redefinition.
- MINOR: new principle/section or materially expanded mandatory guidance.
- PATCH: wording clarifications and non-semantic refinements.

Compliance review expectations:
- Every plan and spec MUST include an explicit constitution compliance check.
- Every task breakdown MUST include security, data, containerization, and API-doc tasks
	when applicable.
- Every API-impacting change MUST include explicit verification of route versioning and
	OpenAPI path updates.
- Non-compliant changes MUST be remediated before merge or explicitly waived by approved
	governance exception.

**Version**: 1.1.0 | **Ratified**: 2026-02-25 | **Last Amended**: 2026-03-08
