# Tasks: CRUD de Empleados

**Input**: Design documents from `/specs/001-crud-empleados/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/empleados-api.yaml

## Phase 1: Setup

- [x] T001 Crear estructura base del módulo empleados en `src/main/java/**/empleados/`
- [x] T002 Configurar dependencias requeridas (web, validation, data-jpa, security, openapi) en `pom.xml`
- [x] T003 [P] Configurar properties para PostgreSQL y seguridad básica en `src/main/resources/application.properties`

---

## Phase 2: Foundational

- [x] T004 Crear migración SQL para tabla `empleados` con `clave`, `nombre`, `direccion`, `telefono`, `version` en `src/main/resources/db/migration/`
- [x] T005 [P] Implementar entidad `Empleado` + `@Version` en `src/main/java/**/empleados/domain/Empleado.java`
- [x] T006 [P] Implementar repositorio `EmpleadoRepository` en `src/main/java/**/empleados/repository/EmpleadoRepository.java`
- [x] T007 Implementar generador de clave `E-001..E-999` sin reutilización en `src/main/java/**/empleados/service/ClaveGeneratorService.java`
- [x] T008 Implementar configuración de seguridad Basic Auth (`admin/admin123` dev) en `src/main/java/**/config/SecurityConfig.java`
- [x] T009 Implementar manejo global de errores (`400/404/409`) en `src/main/java/**/common/GlobalExceptionHandler.java`
- [x] T010 [P] Ajustar OpenAPI/Swagger security scheme en configuración SpringDoc

**Checkpoint**: Base técnica lista para historias.

---

## Phase 3: User Story 1 - Alta y consulta por clave (P1)

**Goal**: Crear empleado y consultar por `clave`.

### Tests (US1)
- [x] T011 [P] [US1] Pruebas de contrato para `POST /api/empleados` y `GET /api/empleados/{clave}` en `src/test/java/**/contract/`
- [x] T012 [P] [US1] Pruebas de integración de alta/consulta en `src/test/java/**/integration/`

### Implementation (US1)
- [x] T013 [US1] Crear DTOs request/response de empleado en `src/main/java/**/empleados/api/dto/`
- [x] T014 [US1] Implementar servicio de alta con generación de `clave` y límite `E-999` en `src/main/java/**/empleados/service/EmpleadoService.java`
- [x] T015 [US1] Implementar endpoint `POST /api/empleados` en `src/main/java/**/empleados/api/EmpleadoController.java`
- [x] T016 [US1] Implementar endpoint `GET /api/empleados/{clave}` en `src/main/java/**/empleados/api/EmpleadoController.java`
- [x] T017 [US1] Validar restricciones de longitud exacta 100 y rechazo de `clave` en payload de alta

**Checkpoint**: Alta + consulta funcionales.

---

## Phase 4: User Story 2 - Actualización con concurrencia (P2)

**Goal**: Actualizar empleado con control de concurrencia optimista por `version`.

### Tests (US2)
- [x] T018 [P] [US2] Pruebas de contrato para `PUT /api/empleados/{clave}` en `src/test/java/**/contract/`
- [x] T019 [P] [US2] Pruebas de integración para actualización exitosa, `version` inválida y conflicto `409`

### Implementation (US2)
- [x] T020 [US2] Implementar lógica de actualización con validación de `version` en `EmpleadoService`
- [x] T021 [US2] Implementar endpoint `PUT /api/empleados/{clave}` en `EmpleadoController`
- [x] T022 [US2] Ajustar mapeo de errores para `400`, `404`, `409`

**Checkpoint**: Actualización y concurrencia funcionales.

---

## Phase 5: User Story 3 - Baja física (P3)

**Goal**: Eliminar empleado físicamente por `clave`.

### Tests (US3)
- [x] T023 [P] [US3] Pruebas de contrato para `DELETE /api/empleados/{clave}`
- [x] T024 [P] [US3] Pruebas de integración para eliminación y `404` en inexistente

### Implementation (US3)
- [x] T025 [US3] Implementar lógica de eliminación física en `EmpleadoService`
- [x] T026 [US3] Implementar endpoint `DELETE /api/empleados/{clave}` en `EmpleadoController`

**Checkpoint**: Baja física funcional.

---

## Phase 6: User Story 4 - Listado paginado, filtros y sort (P2)

**Goal**: Listar empleados con paginación (`page`,`size`), filtros (`nombre` contains, `clave` exact) y `sort` por `clave` (`asc|desc`).

### Tests (US4)
- [x] T027 [P] [US4] Pruebas de contrato para `GET /api/empleados` con parámetros
- [x] T028 [P] [US4] Pruebas de integración para paginación por defecto, `size>100`, filtros y `sort` inválido

### Implementation (US4)
- [x] T029 [US4] Implementar consulta paginada/filtrada en `EmpleadoRepository` (Specification/Query methods)
- [x] T030 [US4] Implementar lógica de listado en `EmpleadoService`
- [x] T031 [US4] Implementar endpoint `GET /api/empleados` en `EmpleadoController`
- [x] T032 [US4] Implementar respuesta tipo Spring Page (`content`,`number`,`size`,`totalElements`,`totalPages`,`first`,`last`)

**Checkpoint**: Listado funcional con paginación/filtros/sort.

---

## Phase 7: Polish

- [x] T033 [P] Actualizar contrato OpenAPI final en `specs/001-crud-empleados/contracts/empleados-api.yaml`
- [x] T034 [P] Documentar ejemplos de uso en `specs/001-crud-empleados/quickstart.md`
- [x] T035 Ejecutar suite de pruebas y corregir fallos de implementación del feature
- [x] T036 Ejecutar verificación de build (`mvn clean verify`) y validar Swagger UI
- [x] T037 [P] Validar containerización: `docker build` y `docker run` con configuración mínima documentada
- [x] T038 [P] Medir rendimiento básico CRUD/listado y validar objetivo p95 < 300 ms en entorno local

---

## Dependencies & Order

- Setup (T001-T003) → Foundational (T004-T010) → US1 (T011-T017) → US2/US3/US4.
- US2 depende de US1 (requiere alta/consulta base).
- US3 depende de US1 (requiere entidad persistida).
- US4 depende de Foundational y puede avanzar en paralelo con US2/US3.
- Polish al final (incluye T037 containerización y T038 validación de performance).
