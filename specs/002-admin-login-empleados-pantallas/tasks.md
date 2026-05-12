# Tasks: Pantallas Separadas para Login Admin y CRUD de Empleados

**Input**: Design documents from `/specs/002-admin-login-empleados-pantallas/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Prepare Angular screen split and shared configuration baseline.

- [X] T001 Create frontend feature folder structure in frontend/src/app/auth/, frontend/src/app/empleados/, frontend/src/app/core/, and frontend/src/app/shared/
- [X] T002 Create route configuration shell for separated screens in frontend/src/app/app.routes.ts and wire bootstrap in frontend/src/main.ts
- [X] T003 [P] Add frontend API environment configuration in frontend/src/environments/environment.ts and frontend/src/environments/environment.development.ts
- [X] T004 [P] Align local proxy and runtime API metadata in frontend/proxy.conf.json and openapi-runtime.json

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Implement shared backend/frontend foundations required before user stories.

**CRITICAL**: No user story work starts until this phase is complete.

- [X] T005 Add logical-delete schema migration for active flag in src/main/resources/db/migration/V2__add_activo_to_empleados.sql
- [X] T006 Update employee entity and API response model to include active state in src/main/java/com/dsw01/practica02/empleados/domain/Empleado.java and src/main/java/com/dsw01/practica02/empleados/api/dto/EmpleadoResponse.java
- [X] T007 [P] Add repository methods for active-by-default listing and includeInactive filter in src/main/java/com/dsw01/practica02/empleados/repository/EmpleadoRepository.java
- [X] T008 Update employee service for logical delete and filtered listing behavior in src/main/java/com/dsw01/practica02/empleados/service/EmpleadoService.java
- [X] T009 [P] Extend list endpoint query contract with includeInactive while keeping versioned path in src/main/java/com/dsw01/practica02/empleados/api/EmpleadoController.java
- [X] T010 [P] Add standardized conflict/validation error payload model in src/main/java/com/dsw01/practica02/common/api/ApiErrorResponse.java and map usage in src/main/java/com/dsw01/practica02/empleados/api/EmpleadoController.java
- [X] T011 Create frontend in-memory session state and credential holder services in frontend/src/app/core/session.service.ts and frontend/src/app/core/auth-state.service.ts
- [X] T012 [P] Implement reusable auth guard for protected routes in frontend/src/app/core/auth.guard.ts

**Checkpoint**: Foundation complete, user stories can proceed.

---

## Phase 3: User Story 1 - Login Administrativo (Priority: P1) 🎯 MVP

**Goal**: Provide dedicated admin login screen that authenticates with HTTP Basic and gates access to employee screen.

**Independent Test**: From anonymous state, valid credentials navigate to /empleados, invalid credentials stay on /login with actionable error.

### Implementation for User Story 1

- [X] T013 [US1] Create login screen component with reactive form logic in frontend/src/app/auth/login-page.component.ts
- [X] T014 [P] [US1] Build accessible login markup with labels, error region, and semantic landmarks in frontend/src/app/auth/login-page.component.html
- [X] T015 [P] [US1] Add login visual system with contrast and focus-visible states in frontend/src/app/auth/login-page.component.css
- [X] T016 [US1] Implement HTTP Basic authentication check service against protected employee endpoint in frontend/src/app/core/auth.service.ts and frontend/src/app/empleados/empleados-api.service.ts
- [X] T017 [US1] Wire navigation flow for anonymous/protected routes in frontend/src/app/app.routes.ts and frontend/src/app/core/auth.guard.ts
- [X] T018 [US1] Add auth status and error announcement utility for assistive tech in frontend/src/app/shared/a11y-announcer.service.ts and frontend/src/app/auth/login-page.component.html

**Checkpoint**: User Story 1 is independently functional and demoable.

---

## Phase 4: User Story 2 - Pantalla de Gestion de Empleados (Priority: P2)

**Goal**: Deliver single protected employee screen for list/create/edit/logical-delete with conflict recovery.

**Independent Test**: After login, admin can complete CRUD cycle including logical delete and conflict recovery on 409 update.

### Implementation for User Story 2

- [X] T019 [P] [US2] Update backend list contract behavior tests for active-default and includeInactive in src/test/java/com/dsw01/practica02/empleados/contract/EmpleadoListContractTest.java
- [X] T020 [P] [US2] Update backend delete contract test for logical-delete semantics in src/test/java/com/dsw01/practica02/empleados/contract/EmpleadoDeleteContractTest.java
- [X] T021 [P] [US2] Update backend update contract test for explicit version-conflict response in src/test/java/com/dsw01/practica02/empleados/contract/EmpleadoUpdateContractTest.java
- [X] T022 [US2] Create employee API adapter and DTO mappers for CRUD operations in frontend/src/app/empleados/empleados-api.service.ts and frontend/src/app/models.ts
- [X] T023 [P] [US2] Create employee page container logic for loading, empty, success, and error states in frontend/src/app/empleados/empleados-page.component.ts
- [X] T024 [P] [US2] Create employee page template with table/actions/form host and status messages in frontend/src/app/empleados/empleados-page.component.html
- [X] T025 [P] [US2] Add employee page responsive and accessible styling rules in frontend/src/app/empleados/empleados-page.component.css
- [X] T026 [US2] Implement employee create/edit reactive form component with field-level validation in frontend/src/app/empleados/empleado-form.component.ts and frontend/src/app/empleados/empleado-form.component.html
- [X] T027 [US2] Implement explicit logical-delete confirmation and default active filtering UI in frontend/src/app/empleados/empleados-page.component.ts and frontend/src/app/empleados/empleados-page.component.html
- [X] T028 [US2] Implement 409 conflict recovery flow (load latest + reapply option) in frontend/src/app/empleados/conflict-dialog.component.ts and frontend/src/app/empleados/empleados-page.component.ts
- [X] T029 [US2] Add route-level integration for protected employee screen in frontend/src/app/empleados/empleados.routes.ts and frontend/src/app/app.routes.ts
- [X] T030 [US2] Update API contract docs for includeInactive and logical-delete behavior in specs/002-admin-login-empleados-pantallas/contracts/frontend-empleados-api.yaml and openapi-runtime.json

**Checkpoint**: User Stories 1 and 2 work independently and satisfy CRUD requirements.

---

## Phase 5: User Story 3 - Cierre de Sesion Seguro (Priority: P3)

**Goal**: Allow admin logout that clears in-memory session and blocks protected screen access until re-login.

**Independent Test**: Authenticated admin logs out, returns to /login, and cannot reopen /empleados without new authentication.

### Implementation for User Story 3

- [X] T031 [US3] Add logout action that clears runtime session and credentials in frontend/src/app/core/session.service.ts and frontend/src/app/empleados/empleados-page.component.ts
- [X] T032 [US3] Enforce post-logout protection against browser back navigation in frontend/src/app/core/auth.guard.ts and frontend/src/app/app.routes.ts
- [X] T033 [US3] Add logout accessibility announcement and focus handoff to login heading in frontend/src/app/shared/a11y-announcer.service.ts and frontend/src/app/auth/login-page.component.ts

**Checkpoint**: All user stories are complete and independently testable.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final validation, docs, and release readiness across stories.

- [X] T034 [P] Update user flow and route documentation for two-screen UX in frontend/README.md
- [X] T035 [P] Update validation and execution checklist for this feature in specs/002-admin-login-empleados-pantallas/quickstart.md
- [X] T036 Run full verification commands and record evidence in runtime-crud-results.json and crud-evidence.json
- [X] T037 Add frontend container service and reverse proxy integration in docker-compose.yml, frontend/Dockerfile, and frontend/nginx.conf

---

## Dependencies & Execution Order

### Phase Dependencies

- Setup (Phase 1): no dependencies.
- Foundational (Phase 2): depends on Setup and blocks all user stories.
- User Story phases (Phases 3-5): depend on Foundational completion.
- Polish (Phase 6): depends on all selected user stories.

### User Story Dependencies

- US1 (P1): starts after Foundational; no dependency on other stories.
- US2 (P2): starts after Foundational; depends on US1 login navigation to be end-to-end usable.
- US3 (P3): starts after Foundational; depends on US1 session/auth flow.

### Suggested Completion Order

1. Phase 1 -> Phase 2
2. US1 (MVP)
3. US2
4. US3
5. Phase 6

---

## Parallel Opportunities

- Setup: T003 and T004 can run in parallel after T001-T002.
- Foundational: T007, T009, T010, and T012 can run in parallel once T005-T006 are done.
- US1: T014 and T015 can run in parallel with T013.
- US2: T019, T020, and T021 can run in parallel; T023, T024, and T025 can run in parallel.
- Polish: T034 and T035 can run in parallel.

### Parallel Example: User Story 1

- Execute T014 and T015 together while T013 defines component inputs/outputs.
- Execute T016 after T013, then T017 and T018 in sequence.

### Parallel Example: User Story 2

- Execute T019, T020, and T021 together as backend contract updates.
- Execute T023, T024, and T025 together once T022 service contracts are stable.

### Parallel Example: User Story 3

- Execute T031 first, then T032 and T033 can proceed in parallel.

---

## Implementation Strategy

### MVP First (US1)

1. Complete Phase 1 and Phase 2.
2. Complete Phase 3 (US1).
3. Validate independent test for login + route protection.
4. Demo and baseline before CRUD expansion.

### Incremental Delivery

1. Deliver US1 (secure access).
2. Deliver US2 (employee operations + logical delete + conflict handling).
3. Deliver US3 (secure session closure).
4. Run polish verification and update artifacts.

### Task Format Validation

All tasks in this file follow the required checklist format:
`- [X] T### [P?] [US?] Description with file path`.
