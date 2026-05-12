# Implementation Plan: Pantallas Separadas para Login Admin y CRUD de Empleados

**Branch**: `002-admin-login-empleados-pantallas` | **Date**: 2026-04-06 | **Spec**: [specs/002-admin-login-empleados-pantallas/spec.md](specs/002-admin-login-empleados-pantallas/spec.md)
**Input**: Feature specification from `/specs/002-admin-login-empleados-pantallas/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implement a 2-screen frontend flow in Angular 21: (1) dedicated admin login and
(2) employee management screen for list/create/edit/logical-delete operations.
Authentication will use HTTP Basic against existing protected backend endpoints,
session state will be in-memory only, and UI must meet WCAG 2.1 AA for critical
flows. Backend impact includes logical delete support and listing behavior that
hides inactive employees by default.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Java 17 (backend), TypeScript (Angular 21 frontend)  
**Primary Dependencies**: Spring Boot 3.x, Spring Security (HTTP Basic), Spring Data JPA, Flyway, springdoc-openapi, Angular 21, Angular Router, Reactive Forms  
**Storage**: PostgreSQL (mandatory)  
**Testing**: JUnit 5 + Spring Boot Test + integration tests (backend), Angular component/integration tests, accessibility verification for critical screens  
**Target Platform**: Docker-based local runtime (backend + DB) and Angular dev/build runtime
**Project Type**: Full-stack web application  
**Performance Goals**: Login success <= 5s (P95), employee list <= 3s (P95), delete reflect in UI <= 3s (P95)  
**Constraints**: HTTP Basic only for this iteration, session in frontend memory only, logical delete required, conflict handling required, WCAG 2.1 AA for login + CRUD screens, secrets by environment variables  
**API Versioning**: Public endpoints MUST use `/api/v{major}/...` (current `/api/v1/...`)  
**Scale/Scope**: Single admin role, two primary screens, CRUD for employees with logical delete and optimistic concurrency handling

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Full-stack scope confirmed (Spring Boot backend + Angular 21 frontend)
- [x] Java 17 and Spring Boot 3.x compatibility confirmed
- [x] Angular 21 compatibility and frontend architecture confirmed
- [x] Basic Auth enforced for exposed endpoints
- [x] PostgreSQL persistence strategy and migrations defined
- [x] Docker execution/build strategy documented for backend and frontend
- [x] Swagger/OpenAPI documentation impact defined
- [x] API path versioning strategy defined (`/api/v{major}/...`)
- [x] UI accessibility/usability baseline defined (keyboard, focus, contrast, validation feedback)

## Project Structure

### Documentation (this feature)

```text
specs/002-admin-login-empleados-pantallas/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
src/
├── main/
│   ├── java/com/dsw01/practica02/
│   │   ├── config/
│   │   ├── empleados/
│   │   │   ├── api/
│   │   │   ├── domain/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── common/
│   └── resources/
│       └── db/migration/
└── test/java/

frontend/
├── src/
│   ├── app/
│   │   ├── auth/           # planned login screen module
│   │   ├── empleados/      # planned employee CRUD screen module
│   │   ├── core/           # planned session/auth state + guards
│   │   └── shared/         # planned reusable accessibility-aware UI blocks
│   ├── main.ts
│   └── styles.css
└── package.json
```

**Structure Decision**: Keep existing monorepo style with Spring Boot backend in `src/`
and Angular frontend in `frontend/`. Implement login and employee CRUD as separate
frontend screen modules while preserving the existing versioned backend API.

## Phase 0 Output

- [research.md](specs/002-admin-login-empleados-pantallas/research.md)

## Phase 1 Output

- [data-model.md](specs/002-admin-login-empleados-pantallas/data-model.md)
- [contracts/frontend-empleados-api.yaml](specs/002-admin-login-empleados-pantallas/contracts/frontend-empleados-api.yaml)
- [contracts/ui-navigation-contract.md](specs/002-admin-login-empleados-pantallas/contracts/ui-navigation-contract.md)
- [quickstart.md](specs/002-admin-login-empleados-pantallas/quickstart.md)

## Post-Design Constitution Check

- [x] Full-stack scope still aligned with Angular 21 + Spring Boot 3.x.
- [x] HTTP Basic flow explicitly retained for this iteration.
- [x] PostgreSQL impact captured (logical delete state for employees).
- [x] API versioning remains under `/api/v1/...`.
- [x] Docker runtime behavior documented for backend + frontend startup.
- [x] Accessibility/usability target formalized as WCAG 2.1 AA for critical screens.
- [x] OpenAPI contract impacts documented in contracts output.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No constitution violations identified.
