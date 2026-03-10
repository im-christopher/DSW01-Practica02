# Implementation Plan: Autenticación de Empleados con Contraseña

**Branch**: `002-empleado-auth` | **Date**: 2026-03-08 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/002-empleado-auth/spec.md`

## Summary

Extender la entidad `Empleado` con capacidad de autenticación mediante contraseña. Se agregarán: (1) campo `password_hash` con almacenamiento seguro vía hashing, (2) endpoint `/api/v1/empleados/login` para autenticación con clave + contraseña retornando token/sesión, (3) endpoint `/api/v1/empleados/{clave}/password` para cambio de contraseña verificando contraseña actual. Se implementará validación de contraseñas (mínimo 8 caracteres, letra + número), protección contra fuerza bruta (5 intentos/15 min), audit logging de eventos de autenticación, e invalidación de sesiones al cambiar contraseña. Compatibilidad con empleados sin contraseña (migración gradual).

## Technical Context

**Language/Version**: Java 17 (mandatory)  
**Primary Dependencies**: Spring Boot 3.x, Spring Security (BCryptPasswordEncoder), Spring Data JPA, springdoc-openapi, spring-boot-starter-cache (Caffeine), jjwt (JWT tokens)  
**Storage**: PostgreSQL (mandatory) - nueva columna `password_hash VARCHAR(255) NULLABLE`, `password_changed_at TIMESTAMP NULLABLE`, tabla audit `auth_events`  
**Testing**: JUnit 5 + Spring Boot Test (unit + integration) - cobertura de autenticación, rate limiting, password change  
**Target Platform**: Linux container runtime (Docker)  
**Project Type**: Backend web-service  
**Performance Goals**: Autenticación < 2s (p95), establecer contraseña < 1 minuto, cambio contraseña < 30s  
**Constraints**: Basic Auth en endpoints administrativos; login endpoint puede estar exento; contraseñas nunca en texto plano; secrets via environment variables (JWT_SECRET)  
**API Versioning**: Nuevos endpoints en `/api/v1/empleados/login` y `/api/v1/empleados/{clave}/password`  
**Scale/Scope**: 2 nuevos endpoints, modificación a entidad Empleado (password_hash, password_changed_at), 1 nueva tabla audit, rate limiting via Caffeine in-memory cache, JWT stateless tokens (no session storage backend required)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Backend-only scope confirmed (no frontend scope added) - Solo endpoints REST, sin UI
- [x] Java 17 and Spring Boot 3.x compatibility confirmed - Usa dependencias existentes del proyecto
- [x] Basic Auth enforced for exposed endpoints - Login endpoint exento (necesario para autenticación), `/password` endpoint requiere autenticación
- [x] PostgreSQL persistence strategy and migrations defined - Migración Flyway para columna `password_hash`, tabla `auth_events`
- [x] Docker execution/build strategy documented - Sin cambios en runtime/build baseline
- [x] Swagger/OpenAPI documentation impact defined - Nuevos endpoints `/api/v1/empleados/login`, `/api/v1/empleados/{clave}/password` en OpenAPI
- [x] API path versioning strategy defined (`/api/v{major}/...`) - Ambos endpoints usan `/api/v1` prefix, sin breaking changes a endpoints existentes

## Project Structure

### Documentation (this feature)

```text
specs/002-empleado-auth/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── auth-api.yaml    # Contrato OpenAPI para endpoints de autenticación
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── dsw01/
    │           └── practica02/
    │               ├── empleados/           # Entidad Empleado, controller, service, repository
    │               ├── auth/                # NUEVO: Servicios de autenticación, DTOs, controllers
    │               ├── security/            # Configuración Spring Security (existente + nuevas reglas)
    │               └── common/              # Exception handlers, utilities
    └── resources/
        ├── application.properties
        └── db/
            └── migration/                   # NUEVO: Migraciones Flyway para password_hash, auth_events

src/test/
└── java/
    └── com/
        └── dsw01/
            └── practica02/
                ├── empleados/
                │   ├── contract/            # Tests existentes
                │   └── integration/         # Tests existentes
                └── auth/                    # NUEVO: Tests de autenticación
                    ├── contract/
                    └── integration/
```

**Structure Decision**: Proyecto backend único Spring Boot con layout estándar Maven. Se agregará paquete `auth` para lógica de autenticación separada de CRUD de empleados, manteniendo separación de concerns.

## Complexity Tracking

No hay violaciones constitucionales ni excepciones de complejidad que justificar.

## Post-Design Constitution Check

- [x] Research y diseño mantienen alcance backend exclusivamente (sin UI, solo REST endpoints)
- [x] Modelo y contrato preservan baseline Java 17 / Spring Boot 3.x (BCrypt, JWT, Caffeine compatibles)
- [x] Contrato API define seguridad: login exento de Basic Auth (necesario para bootstrap), password change requiere JWT Bearer token
- [x] Persistencia PostgreSQL y restricciones de datos definidas en `data-model.md` (columnas password_hash, password_changed_at, tabla auth_events con índices)
- [x] Ejecución Docker sin cambios en runtime/build baseline (nuevas dependencias compatibles con Dockerfile existente)
- [x] Contrato OpenAPI generado en `contracts/auth-api.yaml` con esquemas, ejemplos, y códigos de error documentados
- [x] API path versioning cumplido: todos los endpoints usan `/api/v1/empleados/login` y `/api/v1/empleados/{clave}/password` (sin breaking changes a endpoints existentes)
