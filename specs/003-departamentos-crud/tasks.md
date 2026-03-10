# Implementation Tasks: Departamentos CRUD

**Feature**: 003-departamentos-crud  
**Source**: [spec.md](spec.md)  
**Generated**: March 9, 2026

## Task Organization Strategy

Tasks are organized by **User Story** to enable independent implementation and testing. Each user story represents a complete, independently testable increment of functionality.

### Priority Order
1. **Phase 1**: Setup (4 tasks) - Project initialization and dependencies
2. **Phase 2**: Foundational (8 tasks) - **CRITICAL BLOCKING** - Must complete before any user story
3. **Phase 3**: User Story 1 - P1 (8 tasks) - CRUD de Departamentos (MVP)
4. **Phase 4**: User Story 2 - P2 (7 tasks) - Asignar Empleados a Departamentos
5. **Phase 5**: User Story 3 - P3 (6 tasks) - Listar Empleados por Departamento
6. **Phase 6**: Polish (4 tasks) - Cross-cutting concerns and finalization

**Total Tasks**: 37

---

## Phase 1: Setup (4 tasks)

Foundation setup required before any feature work.

- [X] T001 Add Spring Data JPA starter dependency to pom.xml if not present
- [X] T002 Create departamentos package structure in src/main/java/com/dsw01/practica02/departamentos/
- [X] T003 Create subdirectories: domain/, repository/, service/, api/, api/dto/ in departamentos package
- [X] T004 Create test package structure in src/test/java/com/dsw01/practica02/departamentos/

---

## Phase 2: Foundational (CRITICAL - BLOCKS ALL USER STORIES)

**CHECKPOINT**: These tasks MUST be completed before implementing ANY user story. They provide the infrastructure that all stories depend on.

- [X] T005 Create Flyway migration V3__create_departamentos_table.sql in src/main/resources/db/migration/
- [X] T006 [P] Create Departamento entity in src/main/java/com/dsw01/practica02/departamentos/domain/Departamento.java with fields: id (Long), nombre (String), activo (Boolean)
- [X] T007 [P] Create DepartamentoRepository interface in src/main/java/com/dsw01/practica02/departamentos/repository/DepartamentoRepository.java extending JpaRepository
- [X] T008 [P] Add departamentoId field (Long nullable) to Empleado entity in src/main/java/com/dsw01/practica02/empleados/domain/Empleado.java with @ManyToOne relationship
- [X] T009 [P] Create DepartamentoNotFoundException in src/main/java/com/dsw01/practica02/departamentos/exception/DepartamentoNotFoundException.java
- [X] T010 [P] Create DepartamentoConEmpleadosException in src/main/java/com/dsw01/practica02/departamentos/exception/DepartamentoConEmpleadosException.java
- [X] T011 [P] Add exception handlers for departamentos exceptions in src/main/java/com/dsw01/practica02/common/GlobalExceptionHandler.java (404, 409)
- [X] T012 Update OpenAPI configuration to include departamentos endpoints in src/main/java/com/dsw01/practica02/config/OpenApiConfig.java

**Validation**: After Phase 2, run `mvn compile` to verify no build errors. All entities, repositories, and exception handlers must compile successfully.

---

## Phase 3: User Story 1 - Crear y Gestionar Departamentos (Priority: P1)

**Goal**: Implement complete CRUD operations for departamentos with soft delete.

**Independent Test**: Create departamento via POST, verify persistence with GET, update with PUT, soft delete with DELETE, list with pagination.

### Tasks

- [X] T013 [US1] Create DepartamentoCreateRequest DTO in src/main/java/com/dsw01/practica02/departamentos/api/dto/DepartamentoCreateRequest.java with validation (nombre required, max 100 chars)
- [X] T014 [US1] Create DepartamentoUpdateRequest DTO in src/main/java/com/dsw01/practica02/departamentos/api/dto/DepartamentoUpdateRequest.java with validation
- [X] T015 [US1] Create DepartamentoResponse DTO in src/main/java/com/dsw01/practica02/departamentos/api/dto/DepartamentoResponse.java with fields: id, nombre, activo
- [X] T016 [US1] Create DepartamentoPageResponse DTO in src/main/java/com/dsw01/practica02/departamentos/api/dto/DepartamentoPageResponse.java for paginated responses
- [X] T017 [P] [US1] Implement DepartamentoService.crearDepartamento in src/main/java/com/dsw01/practica02/departamentos/service/DepartamentoService.java with uniqueness validation
- [X] T018 [P] [US1] Implement DepartamentoService.obtenerPorId with activo=true filter in src/main/java/com/dsw01/practica02/departamentos/service/DepartamentoService.java
- [X] T019 [P] [US1] Implement DepartamentoService.listarDepartamentos with pagination (page, size, sort by nombre) in src/main/java/com/dsw01/practica02/departamentos/service/DepartamentoService.java
- [X] T020 [P] [US1] Implement DepartamentoService.actualizarDepartamento without version check in src/main/java/com/dsw01/practica02/departamentos/service/DepartamentoService.java
- [X] T021 [P] [US1] Implement DepartamentoService.eliminarDepartamento with soft delete (activo=false) and employee check (throw 409 if has employees) in src/main/java/com/dsw01/practica02/departamentos/service/DepartamentoService.java
- [X] T022 [US1] Create DepartamentoController in src/main/java/com/dsw01/practica02/departamentos/api/DepartamentoController.java with endpoints: POST, GET by ID, GET list, PUT, DELETE under /api/v1/departamentos

**Checkpoint**: After US1, departamentos CRUD is fully functional. Can create, read, update, soft delete, and list departamentos with pagination. This is the MVP.

---

## Phase 4: User Story 2 - Asignar Empleados a Departamentos (Priority: P2)

**Goal**: Enable assigning employees to departamentos and including departamento info in employee responses.

**Independent Test**: Create departamento, create/update employee with departamentoId, verify employee shows nested departamento object with id and nombre only.

### Tasks

- [X] T023 [US2] Add optional departamentoId field to EmpleadoCreateRequest in src/main/java/com/dsw01/practica02/empleados/api/dto/EmpleadoCreateRequest.java
- [X] T024 [US2] Add optional departamentoId field to EmpleadoUpdateRequest in src/main/java/com/dsw01/practica02/empleados/api/dto/EmpleadoUpdateRequest.java
- [X] T025 [US2] Create DepartamentoSummaryDTO in src/main/java/com/dsw01/practica02/departamentos/api/dto/DepartamentoSummaryDTO.java with only id and nombre fields
- [X] T026 [US2] Add departamento field (DepartamentoSummaryDTO) to EmpleadoResponse in src/main/java/com/dsw01/practica02/empleados/api/dto/EmpleadoResponse.java
- [X] T027 [P] [US2] Update EmpleadoService.crearEmpleado to validate departamentoId exists (call DepartamentoService) in src/main/java/com/dsw01/practica02/empleados/service/EmpleadoService.java
- [X] T028 [P] [US2] Update EmpleadoService.actualizarEmpleado to validate departamentoId and allow null (unassign) in src/main/java/com/dsw01/practica02/empleados/service/EmpleadoService.java
- [X] T029 [US2] Update EmpleadoResponse mapping to include departamento summary when present in src/main/java/com/dsw01/practica02/empleados/service/EmpleadoService.java

**Checkpoint**: After US2, employees can be assigned to departamentos. Employee GET responses include nested departamento object (id, nombre only). Can assign, unassign, and see departamento in employee data.

---

## Phase 5: User Story 3 - Listar Empleados por Departamento (Priority: P3)

**Goal**: Enable querying employees by departamento using dedicated endpoint and query filter.

**Independent Test**: Create departamento with employees, query GET /api/v1/departamentos/{id}/empleados, verify returns only employees of that departamento. Query GET /api/v1/empleados?departamentoId=X, verify filter works.

### Tasks

- [X] T030 [P] [US3] Add method findByDepartamentoIdAndActivoTrue to EmpleadoRepository in src/main/java/com/dsw01/practica02/empleados/repository/EmpleadoRepository.java
- [X] T031 [P] [US3] Implement DepartamentoService.listarEmpleadosPorDepartamento with pagination in src/main/java/com/dsw01/practica02/departamentos/service/DepartamentoService.java
- [X] T032 [US3] Add endpoint GET /api/v1/departamentos/{id}/empleados to DepartamentoController in src/main/java/com/dsw01/practica02/departamentos/api/DepartamentoController.java
- [X] T033 [US3] Add departamentoId query parameter to EmpleadoController.listarEmpleados in src/main/java/com/dsw01/practica02/empleados/api/EmpleadoController.java
- [X] T034 [P] [US3] Update EmpleadoService.listarEmpleados to filter by departamentoId when provided in src/main/java/com/dsw01/practica02/empleados/service/EmpleadoService.java
- [X] T035 [US3] Update OpenAPI schemas to document departamentoId filter and /departamentos/{id}/empleados endpoint in OpenApiConfig or controller annotations

**Checkpoint**: After US3, all three user stories are complete. Can query employees by departamento using dedicated endpoint or query parameter. Full departamentos feature is functional.

---

## Phase 6: Polish & Cross-Cutting Concerns (4 tasks)

Final touches, validation, and documentation updates.

- [ ] T036 Validate Flyway migration executes successfully (run application, check schema has departamentos table with activo column and empleados.departamento_id FK)
- [X] T037 Run full test suite: `mvn test` and verify no regressions in existing empleados tests
- [ ] T038 [P] Update API documentation/Swagger UI to verify departamentos endpoints are visible and correctly documented
- [ ] T039 Manual end-to-end test: Create departamento, assign employee, update departamento, attempt delete (should fail), unassign employee, delete departamento (should succeed as soft delete)

**Final Validation**: All 37 tasks completed, application runs without errors, API endpoints functional, soft delete working, pagination consistent with empleados.

---

## Dependency Visualization

```
Phase 1 (Setup)
  ↓
Phase 2 (Foundational) ← BLOCKS EVERYTHING
  ↓
  ├─→ Phase 3 (US1 - P1) ← MVP
  ├─→ Phase 4 (US2 - P2) ← Depends on US1 (needs departamentos to exist)
  └─→ Phase 5 (US3 - P3) ← Depends on US2 (needs employees assigned to departamentos)
  ↓
Phase 6 (Polish)
```

## Parallel Execution Opportunities

After **Phase 2** is complete, the following tasks can be worked on in parallel:

**Batch 1** (US1 Foundation - can run fully parallel):
- T013, T014, T015, T016 (DTOs)
- T017, T018, T019, T020, T021 (Service methods)

**Batch 2** (After US1 complete):
- T023, T024, T025, T026 (US2 DTOs)
- T027, T028, T029 (US2 Service updates)

**Batch 3** (After US2 complete):
- T030, T031, T032, T033, T034, T035 (US3 - all [P] tasks)

**Batch 4** (Polish - after all user stories):
- T036, T037, T038, T039 (can run mostly in parallel except T37 needs T36)

## Implementation Strategy

**MVP Approach**: Implement in order Phase 1 → Phase 2 → Phase 3 (US1). This delivers a working departamentos CRUD MVP.

**Full Feature**: Continue with Phase 4 (US2) → Phase 5 (US3) → Phase 6 (Polish) for complete functionality.

**Story Independence**: Each user story (US1, US2, US3) delivers value independently:
- US1 alone = Departamentos can be managed
- US1 + US2 = Employees can be organized by departamento
- US1 + US2 + US3 = Full querying and reporting capabilities

## Notes

- **No tests defined**: Spec does not request TDD approach, so no test tasks included
- **Soft delete**: All departamento queries filter by activo=true by default (FR-005)
- **No version control**: Departamentos don't use optimistic locking (clarification session 2026-03-09)
- **Pagination**: Follows same pattern as empleados (page=0, size=20, sort=asc, ordered by nombre)
- **Nested objects**: Employee responses include departamento summary (id, nombre only) when assigned
