# Implementation Plan: CRUD de Empleados

**Branch**: `001-crud-empleados` | **Date**: 2026-02-25 | **Spec**: `/specs/001-crud-empleados/spec.md`
**Input**: Feature specification from `/specs/001-crud-empleados/spec.md`

## Summary

Implementar un backend de empleados con Spring Boot 3 y Java 17, usando PostgreSQL, autenticación HTTP Basic y contrato OpenAPI explícito. La `clave` se genera en backend con formato `E-001` (prefijo `E-` + 3 dígitos), no se reutiliza, y se limita a `E-999` con rechazo `409` al agotarse. El alcance incluye alta, consulta por clave, actualización con `version` en body (`409` en conflicto), baja física y listado con paginación, filtros (`nombre` contains, `clave` exact) y `sort` por `clave` (`asc|desc`).

## Technical Context

**Language/Version**: Java 17 (mandatory)  
**Primary Dependencies**: Spring Boot 3.x, Spring Security, Spring Data JPA, springdoc-openapi  
**Storage**: PostgreSQL (mandatory)  
**Testing**: JUnit 5 + Spring Boot Test (unit + integration)  
**Target Platform**: Linux container runtime (Docker)  
**Project Type**: Backend web-service  
**Performance Goals**: p95 < 300 ms en operaciones CRUD bajo carga moderada local  
**Constraints**: Basic Auth obligatorio; Swagger/OpenAPI obligatorio; secretos por variables de entorno  
**Scale/Scope**: 1 recurso (`Empleado`), 5 endpoints (CRUD + listado), sin relaciones adicionales

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Backend-only scope confirmed (no frontend scope added)
- [x] Java 17 and Spring Boot 3.x compatibility confirmed
- [x] Basic Auth enforced for exposed endpoints
- [x] PostgreSQL persistence strategy and migrations defined
- [x] Docker execution/build strategy documented
- [x] Swagger/OpenAPI documentation impact defined

## Project Structure

### Documentation (this feature)

```text
specs/001-crud-empleados/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── empleados-api.yaml
└── tasks.md
```

### Source Code (repository root)

```text
src/
└── main/
    ├── java/
    └── resources/
        └── application.properties

src/test/
└── java/
```

**Structure Decision**: Proyecto backend único Spring Boot; se mantiene layout estándar Maven/Spring.

## Post-Design Constitution Check

- [x] Research y diseño mantienen alcance backend exclusivamente.
- [x] Modelo y contrato preservan baseline Java 17 / Spring Boot 3.
- [x] Contrato API define seguridad `basicAuth` en endpoints.
- [x] Persistencia PostgreSQL y restricciones de datos definidas en `data-model.md`.
- [x] Ejecución Docker y verificación operativa documentadas en `quickstart.md`.
- [x] Contrato OpenAPI generado en `contracts/empleados-api.yaml`.

## Complexity Tracking

No hay violaciones constitucionales ni excepciones de complejidad que justificar.
