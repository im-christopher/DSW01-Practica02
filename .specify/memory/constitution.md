<!--
Sync Impact Report
- Version change: 1.1.0 → 2.0.0
- Modified principles:
	- I. Backend-Only Architecture → I. Full-Stack Delivery with Angular 21 Frontend
	- V. Containerization & Versioned API Contract Transparency → V. Containerized Full-Stack & Versioned API Transparency
- Added sections:
	- None
- Removed sections:
	- None
- Templates requiring updates:
	- ✅ updated: .specify/templates/plan-template.md
	- ✅ updated: .specify/templates/spec-template.md
	- ✅ updated: .specify/templates/tasks-template.md
	- ✅ updated: .github/agents/copilot-instructions.md
	- ✅ not applicable: .specify/templates/commands/*.md (directory not present)
- Deferred TODOs:
	- None
-->

# DSW01-Practica02 Constitution

## Core Principles

### I. Full-Stack Delivery with Angular 21 Frontend
All scoped features MUST support full-stack delivery. Backend capabilities MUST be
implemented in Spring Boot services, and user-facing interfaces MUST be implemented in
Angular 21. New features that impact user workflows MUST include both API behavior and
frontend interaction updates in the same delivery slice.

Rationale: Coordinated full-stack delivery reduces integration drift and produces
testable value for end users in each increment.

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

### V. Containerized Full-Stack & Versioned API Transparency
Backend and frontend applications MUST be executable in containerized environments for
development and CI validation. Services MUST be documented with OpenAPI/Swagger, and
every public endpoint MUST include authentication expectations and response models.
Every public backend route MUST be explicitly versioned in the path using
`/api/v{major}/...` (for example `/api/v1/empleados`), and breaking API changes MUST
increment the major version path.

Rationale: Containerized full-stack execution and explicit API contracts improve
deployment predictability and integration readiness.

### VI. Accessibility & Usability Baseline for UI
Angular 21 interfaces MUST satisfy baseline accessibility and usability requirements.
At minimum, user-critical screens MUST provide semantic structure, keyboard operability,
visible focus indicators, color contrast suitable for normal vision conditions, and
clear form validation/error feedback. Usability acceptance criteria MUST include task
completion clarity (labels, navigation intent, and actionable error messages).

Rationale: Accessibility and usability are quality requirements, not optional polish,
and directly impact adoption and error rates.

## Technical Standards

- Framework: Spring Boot 3.x (Web, Security, Validation, Data JPA as needed).
- Language: Java 17.
- Frontend Framework: Angular 21 (mandatory for user-facing web UI).
- Database: PostgreSQL as system of record.
- API Docs: springdoc-openapi/Swagger UI enabled in non-production by default.
- API Versioning: Public routes MUST use `/api/v{major}/...`; non-versioned public
	routes are non-compliant.
- UI Accessibility: Semantic HTML, keyboard navigation, visible focus state, and WCAG-
	aligned contrast for critical flows are mandatory.
- UI Usability: Critical workflows MUST provide clear labels, actionable validation,
	and predictable navigation outcomes.
- Container Runtime: Dockerfile/compose definitions and local container execution MUST
	be maintained for backend and frontend components.
- Configuration: Environment variables MUST be preferred for secrets and deploy-specific
	values; committed defaults MAY exist only for local development and MUST be clearly
	non-production.

## Delivery Workflow & Quality Gates

1. Specifications and plans MUST include constitution checks before implementation.
2. Pull requests MUST confirm: Java 17/Spring Boot 3 compliance, Angular 21 frontend
	alignment, Basic Auth coverage, PostgreSQL alignment, Docker viability, versioned API
	paths, Swagger documentation completeness, and UI accessibility/usability checks.
3. Changes affecting authentication, persistence, or API contracts MUST include tests
	 and updated operational documentation.
4. Changes affecting UI flows MUST include frontend tests that verify accessibility and
	validation behavior for critical user paths.
5. CI pipelines SHOULD run backend unit/integration tests and frontend validation tests;
	failures in security, migration, or accessibility checks MUST block merge.

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
- Every task breakdown MUST include security, data, containerization, API-doc, and UI
	accessibility/usability tasks when applicable.
- Every API-impacting change MUST include explicit verification of route versioning and
	OpenAPI path updates.
- Every UI-impacting change MUST include explicit verification of keyboard navigation,
	focus visibility, readable contrast, and clear validation messaging.
- Non-compliant changes MUST be remediated before merge or explicitly waived by approved
	governance exception.

**Version**: 2.0.0 | **Ratified**: 2026-02-25 | **Last Amended**: 2026-04-05
