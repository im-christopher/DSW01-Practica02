# Tasks: Autenticacion de Empleados con Contrasena

**Input**: Design documents from `/specs/002-empleado-auth/`
**Prerequisites**: `plan.md` (required), `spec.md` (required for user stories), `research.md`, `data-model.md`, `contracts/`

**Tests**: Include test tasks for authentication, persistence, API behavior, and versioned route behavior whenever a story changes those areas.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project and package setup for auth feature delivery.

- [ ] T001 Add auth and cache dependencies in `pom.xml`
- [ ] T002 Create auth package structure with placeholder classes in `src/main/java/com/dsw01/practica02/auth/`
- [ ] T003 [P] Add JWT secret and cache configuration properties in `src/main/resources/application.properties`
- [ ] T004 [P] Add test configuration defaults for auth settings in `src/test/resources/application-test.properties`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story implementation.

**CRITICAL**: No user story work can begin until this phase is complete.

- [ ] T005 Create Flyway migration for auth schema in `src/main/resources/db/migration/V2__add_employee_authentication.sql`
- [ ] T006 Extend `Empleado` entity with `passwordHash` and `passwordChangedAt` in `src/main/java/com/dsw01/practica02/empleados/domain/Empleado.java`
- [ ] T007 [P] Create `AuthEvent` JPA entity in `src/main/java/com/dsw01/practica02/auth/domain/AuthEvent.java`
- [ ] T008 [P] Create `AuthEventRepository` in `src/main/java/com/dsw01/practica02/auth/repository/AuthEventRepository.java`
- [ ] T009 Create shared auth DTOs (`LoginRequest`, `LoginResponse`, `ChangePasswordRequest`) in `src/main/java/com/dsw01/practica02/auth/api/dto/`
- [ ] T010 [P] Create JWT token service interface and implementation in `src/main/java/com/dsw01/practica02/auth/service/JwtTokenService.java` and `src/main/java/com/dsw01/practica02/auth/service/impl/JwtTokenServiceImpl.java`
- [ ] T011 [P] Create rate limiting service (`LoginAttemptService`) using Caffeine in `src/main/java/com/dsw01/practica02/auth/service/LoginAttemptService.java`
- [ ] T012 Configure security filter chain for `/api/v1/empleados/login` public and JWT-protected endpoints in `src/main/java/com/dsw01/practica02/config/SecurityConfig.java`
- [ ] T013 [P] Add JWT authentication filter/provider classes in `src/main/java/com/dsw01/practica02/auth/security/`
- [ ] T014 Add shared auth exceptions (`InvalidCredentialsException`, `RateLimitExceededException`, `PasswordChangeForbiddenException`) in `src/main/java/com/dsw01/practica02/auth/exception/`
- [ ] T015 Map auth exceptions to HTTP 400/401/403/429 in `src/main/java/com/dsw01/practica02/common/GlobalExceptionHandler.java`
- [ ] T016 Update OpenAPI security scheme and auth endpoint docs wiring in `src/main/java/com/dsw01/practica02/config/OpenApiConfig.java`

**Checkpoint**: Foundation ready. User stories can now be implemented independently.

---

## Phase 3: User Story 1 - Registro de Empleado con Contrasena (Priority: P1) MVP

**Goal**: Allow employee create/update flows to accept password, validate complexity, hash securely, and never expose it.

**Independent Test**: Create and update employee with valid/invalid password and verify password is hashed, persisted, and omitted from API responses.

### Tests for User Story 1

- [ ] T017 [P] [US1] Add contract test for create employee with password in `src/test/java/com/dsw01/practica02/empleados/contract/EmpleadoControllerContractTest.java`
- [ ] T018 [P] [US1] Add contract test for update employee password validation error (400) in `src/test/java/com/dsw01/practica02/empleados/contract/EmpleadoUpdateContractTest.java`
- [ ] T019 [P] [US1] Add integration test for password hashing and non-exposure in responses in `src/test/java/com/dsw01/practica02/empleados/integration/EmpleadoControllerIntegrationTest.java`

### Implementation for User Story 1

- [ ] T020 [P] [US1] Add optional `password` field validation to create/update DTOs in `src/main/java/com/dsw01/practica02/empleados/api/dto/EmpleadoCreateRequest.java` and `src/main/java/com/dsw01/practica02/empleados/api/dto/EmpleadoUpdateRequest.java`
- [ ] T021 [US1] Implement password complexity validator utility in `src/main/java/com/dsw01/practica02/auth/validation/PasswordPolicyValidator.java`
- [ ] T022 [US1] Integrate password hashing on create/update in `src/main/java/com/dsw01/practica02/empleados/service/EmpleadoService.java`
- [ ] T023 [US1] Ensure `passwordHash` is never returned in mapping/response DTOs in `src/main/java/com/dsw01/practica02/empleados/api/dto/EmpleadoResponse.java`
- [ ] T024 [US1] Add audit event creation for password set/update in `src/main/java/com/dsw01/practica02/empleados/service/EmpleadoService.java`
- [ ] T025 [US1] Update API contract for password fields in create/update payloads in `specs/002-empleado-auth/contracts/auth-api.yaml`

**Checkpoint**: US1 is independently functional and testable.

---

## Phase 4: User Story 2 - Autenticacion de Empleado (Priority: P2)

**Goal**: Provide login endpoint using employee clave/password with JWT issuance and brute-force protection.

**Independent Test**: Authenticate with valid credentials (200 + token), invalid credentials (401), and blocked attempts after threshold (429).

### Tests for User Story 2

- [ ] T026 [P] [US2] Add contract test for `/api/v1/empleados/login` success and response schema in `src/test/java/com/dsw01/practica02/auth/contract/EmpleadoLoginContractTest.java`
- [ ] T027 [P] [US2] Add contract test for `/api/v1/empleados/login` invalid credentials (401) in `src/test/java/com/dsw01/practica02/auth/contract/EmpleadoLoginContractTest.java`
- [ ] T028 [P] [US2] Add integration test for login rate limiting 5 attempts + 429 in `src/test/java/com/dsw01/practica02/auth/integration/EmpleadoLoginIntegrationTest.java`

### Implementation for User Story 2

- [ ] T029 [P] [US2] Implement auth service credential validation and JWT issuance in `src/main/java/com/dsw01/practica02/auth/service/AuthService.java`
- [ ] T030 [US2] Implement login endpoint `POST /api/v1/empleados/login` in `src/main/java/com/dsw01/practica02/auth/api/AuthController.java`
- [ ] T031 [US2] Integrate login attempt checks/reset flow in `src/main/java/com/dsw01/practica02/auth/service/LoginAttemptService.java`
- [ ] T032 [US2] Persist login success/failure audit events in `src/main/java/com/dsw01/practica02/auth/service/AuthAuditService.java`
- [ ] T033 [US2] Configure JWT authentication entry point/handler for consistent 401 responses in `src/main/java/com/dsw01/practica02/auth/security/`
- [ ] T034 [US2] Update OpenAPI contract for login endpoint error cases and examples in `specs/002-empleado-auth/contracts/auth-api.yaml`

**Checkpoint**: US2 is independently functional and testable.

---

## Phase 5: User Story 3 - Cambio de Contrasena (Priority: P3)

**Goal**: Allow authenticated employee to change own password after validating current password and invalidate prior tokens.

**Independent Test**: Authenticated employee changes password successfully (204), wrong current password gets 403, old token is invalid after change.

### Tests for User Story 3

- [ ] T035 [P] [US3] Add contract test for `PUT /api/v1/empleados/{clave}/password` success (204) in `src/test/java/com/dsw01/practica02/auth/contract/EmpleadoPasswordChangeContractTest.java`
- [ ] T036 [P] [US3] Add contract test for wrong current password (403) in `src/test/java/com/dsw01/practica02/auth/contract/EmpleadoPasswordChangeContractTest.java`
- [ ] T037 [P] [US3] Add integration test for token invalidation after password change in `src/test/java/com/dsw01/practica02/auth/integration/EmpleadoPasswordChangeIntegrationTest.java`

### Implementation for User Story 3

- [ ] T038 [US3] Implement password change use case with current password validation in `src/main/java/com/dsw01/practica02/auth/service/AuthService.java`
- [ ] T039 [US3] Implement endpoint `PUT /api/v1/empleados/{clave}/password` with self-ownership check in `src/main/java/com/dsw01/practica02/auth/api/AuthController.java`
- [ ] T040 [US3] Update token validation to reject tokens issued before `passwordChangedAt` in `src/main/java/com/dsw01/practica02/auth/service/impl/JwtTokenServiceImpl.java`
- [ ] T041 [US3] Persist `PASSWORD_CHANGED` audit event in `src/main/java/com/dsw01/practica02/auth/service/AuthAuditService.java`
- [ ] T042 [US3] Update OpenAPI contract for password change endpoint responses in `specs/002-empleado-auth/contracts/auth-api.yaml`

**Checkpoint**: US3 is independently functional and testable.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Consolidate documentation, quality checks, and operational validation across stories.

- [ ] T043 [P] Update quickstart auth verification commands and expected outputs in `specs/002-empleado-auth/quickstart.md`
- [ ] T044 [P] Add/adjust logging redaction to avoid password/token leaks in `src/main/java/com/dsw01/practica02/common/GlobalExceptionHandler.java`
- [ ] T045 Validate Flyway migration + application startup path in `src/main/resources/db/migration/V2__add_employee_authentication.sql` and `src/main/resources/application.properties`
- [ ] T046 Run full test suite and fix regressions in `src/test/java/com/dsw01/practica02/`
- [ ] T047 [P] Align final API contract and generated docs with implemented behavior in `specs/002-empleado-auth/contracts/auth-api.yaml` and `src/main/java/com/dsw01/practica02/config/OpenApiConfig.java`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies; start immediately.
- **Phase 2 (Foundational)**: Depends on Phase 1; blocks all user story work.
- **Phase 3 (US1)**: Depends on Phase 2.
- **Phase 4 (US2)**: Depends on Phase 2 and benefits from US1 password fields/data.
- **Phase 5 (US3)**: Depends on Phase 4 (login/JWT path must exist) and Phase 2.
- **Phase 6 (Polish)**: Depends on completed stories.

### User Story Dependencies

- **US1 (P1)**: First deliverable and MVP baseline.
- **US2 (P2)**: Requires employee passwords from US1-compatible flows.
- **US3 (P3)**: Requires authentication/token flow from US2.

### Within Each User Story

- Contract/integration tests first (expected to fail).
- DTO/model updates before service logic.
- Service logic before controller wiring.
- OpenAPI and quickstart updates before story close.

### Parallel Opportunities

- Setup tasks marked `[P]` can run in parallel.
- Foundational tasks T007/T008/T010/T011/T013 can run in parallel after T006 where needed.
- US1 tests T017/T018/T019 can run in parallel.
- US2 tests T026/T027/T028 can run in parallel.
- US3 tests T035/T036/T037 can run in parallel.
- Polish tasks T043/T044/T047 can run in parallel.

---

## Parallel Example: User Story 2

```bash
# Parallel test tasks (US2)
T026 src/test/java/com/dsw01/practica02/auth/contract/EmpleadoLoginContractTest.java
T027 src/test/java/com/dsw01/practica02/auth/contract/EmpleadoLoginContractTest.java
T028 src/test/java/com/dsw01/practica02/auth/integration/EmpleadoLoginIntegrationTest.java

# Parallel implementation tasks (US2)
T029 src/main/java/com/dsw01/practica02/auth/service/AuthService.java
T032 src/main/java/com/dsw01/practica02/auth/service/AuthAuditService.java
T033 src/main/java/com/dsw01/practica02/auth/security/
```

---

## Implementation Strategy

### MVP First (US1)

1. Complete Phase 1 and Phase 2.
2. Deliver Phase 3 (US1) end-to-end.
3. Validate US1 independently with contract + integration tests.
4. Demo/deploy MVP with password persistence and non-exposure guarantees.

### Incremental Delivery

1. Add US2 login + rate limiting and validate independently.
2. Add US3 password change + token invalidation and validate independently.
3. Finish Phase 6 cross-cutting hardening and documentation.

### Parallel Team Strategy

1. Team completes Phase 1 and 2 together.
2. Then split by story: US1 owner, US2 owner, US3 owner.
3. Rejoin for Phase 6 hardening and full-suite validation.
