# Research Report: Autenticación de Empleados

**Feature**: 002-empleado-auth  
**Date**: 2026-03-08  
**Purpose**: Resolver decisiones técnicas de implementación para autenticación con contraseña

## Overview

Este documento resuelve las decisiones técnicas críticas identificadas en Technical Context:
1. Biblioteca de hashing de contraseñas
2. Estrategia de almacenamiento para rate limiting
3. Estrategia de almacenamiento para sesiones/tokens

Todas las decisiones priorizan simplicidad operativa, alineación con Spring Boot 3.x ecosystem, y cumplimiento de requisitos de seguridad sin agregar dependencias de infraestructura externas innecesarias.

---

## Decision 1: Password Hashing Library

**Decision**: BCrypt via `spring-security-crypto` (incluido en `spring-boot-starter-security`)

**Rationale**:
- **Ya disponible**: `spring-security-crypto` está incluido automáticamente con Spring Security, no requiere dependencia adicional
- **Integración nativa**: Spring Security proporciona `BCryptPasswordEncoder` como bean configurable directamente
- **Seguridad probada**: BCrypt es industry standard con factor de costo adaptativo (default 10 rounds, configurable hasta 31)
- **Simplicidad operativa**: No requiere configuración compleja ni librerías externas
- **Cumple requisitos**: FR-004 exige hashing seguro; BCrypt cumple con estándares actuales de seguridad (OWASP approved)

**Alternatives Considered**:
- **Argon2**: Ganador de Password Hashing Competition 2015, mejor contra ataques GPU/ASIC
  - **Rejected because**: Requiere dependencia adicional (`bouncycastle` o `argon2-jvm`), mayor complejidad de configuración, marginal benefit para proyecto educativo/interno
- **PBKDF2**: Estándar NIST, disponible en JDK
  - **Rejected because**: BCrypt más simple de usar en Spring Security ecosystem, PBKDF2 requiere más configuración manual (salt generation, iteration count)

**Implementation Notes**:
- Usar `BCryptPasswordEncoder` con default strength (10)
- Configurar como bean en `SecurityConfig`
- No exponer configuración por variables de entorno (complejidad innecesaria para v1)

---

## Decision 2: Rate Limiting Storage

**Decision**: In-memory cache con `Caffeine` (Spring Boot default) para desarrollo/producción pequeña escala

**Rationale**:
- **Simplicidad**: No requiere infraestructura adicional (Redis, memcached)
- **Cumple requisitos**: FR-008 exige 5 intentos/15 min por clave; cache con TTL cumple perfectamente
- **Performance adecuada**: Para escala de proyecto (sistema interno, no millones de usuarios), Caffeine es suficiente
- **Spring Boot native**: Caffeine es el cache provider por defecto cuando se usa `spring-boot-starter-cache`
- **Desarrollo local**: No requiere servicios externos corriendo (Docker, Redis server)

**Alternatives Considered**:
- **Redis**: Cache distribuido, persistente, escalable
  - **Rejected because**: 
    - Agrega complejidad operativa (otro servicio Docker, configuración de conexión, secrets)
    - Overkill para alcance actual (sistema interno, empleados limitados)
    - Complicaciones en desarrollo local (requiere Redis corriendo)
    - No hay requisito de distribución multi-instancia en spec
- **Database table**: Almacenar intentos en PostgreSQL
  - **Rejected because**:
    - Performance inferior para operaciones de alta frecuencia (cada intento de login = write + cleanup query)
    - Complejidad de limpieza de registros expirados (scheduled job, índices)
    - Cache es más idiomático para rate limiting temporal

**Implementation Notes**:
- Usar `@Cacheable` con Caffeine para tracking de intentos fallidos
- Key: `"auth-attempts:" + clave`
- TTL: 15 minutos (alineado con FR-008)
- Max size: 1000 entradas (suficiente para empleados E-001 a E-999)
- Eviction policy: TTL-based (expiración automática)

**Migration Path** (future):
- Si escala requiere distribución, migrar a Redis es straightforward (cambiar cache provider en configuración)
- API de servicio permanece igual (abstracción via `@Cacheable`)

---

## Decision 3: Session/Token Storage

**Decision**: JWT stateless con tokens firmados, sin almacenamiento server-side

**Rationale**:
- **Stateless**: No requiere storage backend (Redis, DB), el token es self-contained
- **Simplicidad**: Genera token con claims (clave empleado, expiration), firma con secret, retorna al cliente
- **Alineación Spring Security**: `spring-security-oauth2-resource-server` proporciona JWT support nativo
- **Cumple requisitos**: 
  - FR-006: Retornar token válido después autenticación ✓
  - FR-012: Invalidar sesiones al cambiar contraseña → usar claim `issued_at` y comparar con timestamp de último cambio de contraseña en DB
- **Operación**: No requiere cleanup jobs, expiration es automática (claim `exp`)

**Alternatives Considered**:
- **Server-side sessions (Redis)**: Almacenar session ID en Redis con datos de sesión
  - **Rejected because**:
    - Requiere Redis infrastructure
    - Stateful (requiere sincronización en múltiples instancias)
    - Complejidad de cleanup de sesiones expiradas
    - No hay requisito de revocación inmediata de sesiones en spec (invalidación al cambiar contraseña es suficiente)
- **Server-side sessions (Database)**: Tabla `sessions` en PostgreSQL
  - **Rejected because**:
    - Performance: cada request requiere DB query para validar sesión
    - Complejidad: índices, cleanup job, concurrencia
    - DB debería reservarse para data persistente, no sesiones efímeras

**Implementation Notes**:
- Usar `jjwt` (io.jsonwebtoken:jjwt-api) para generación/validación JWT
- Claims: `sub` (clave empleado), `iat` (issued at), `exp` (expiration = iat + 24h)
- Secret: Variable de entorno `JWT_SECRET` (generado/rotado por ops)
- Invalidación al cambiar contraseña: agregar columna `password_changed_at` a `empleados`, rechazar tokens con `iat < password_changed_at`

**Security Considerations**:
- Token no puede ser revocado (stateless trade-off), pero expiration corta (24h) mitiga riesgo
- Secret MUST ser fuerte (256+ bits) y rotado periódicamente
- HTTPS obligatorio en producción (token expuesto en Authorization header)

---

## Decision 4: API Endpoint Design

**Decision**: 
- `POST /api/v1/empleados/login` → body `{clave, password}` → retorna `{token, expiresIn}`
- `PUT /api/v1/empleados/{clave}/password` → body `{currentPassword, newPassword}` → retorna `204 No Content`

**Rationale**:
- **REST idiomático**: Login es POST (crea sesión/token), cambio password es PUT (actualiza recurso)
- **Seguridad**: Contraseñas en body (protegido por HTTPS), no en URL/query params
- **Versionado**: Ambos en `/api/v1` (sin breaking changes)
- **Login exento de Basic Auth**: Necesario para bootstrap autenticación; usuario no tiene credenciales previas

**Implementation Notes**:
- Login endpoint: `@PostMapping("/login")` sin `@PreAuthorize`
- Password change endpoint: `@PreAuthorize("isAuthenticated()")` + validar que usuario autenticado = {clave} en path (no puede cambiar contraseña de otro empleado)

---

## Summary of Technical Stack

| Component | Technology | Reason |
|-----------|-----------|---------|
| Password Hashing | BCrypt (spring-security-crypto) | Included, secure, simple |
| Rate Limiting | Caffeine in-memory cache | No external dependencies, sufficient scale |
| Session Management | JWT stateless tokens | Stateless, simple, Spring Security native |
| Token Library | jjwt (JSON Web Token) | Industry standard, mature |
| Cache Provider | Spring Boot + Caffeine | Default, zero config |

**Dependencies to Add**:
- `spring-boot-starter-cache` (para Caffeine)
- `com.github.ben-manes.caffeine:caffeine` (runtime)
- `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson` (JWT support)

**No External Infrastructure Required**:
- ✅ No Redis
- ✅ No message queue
- ✅ No additional databases
- ✅ Development simpificado (solo PostgreSQL + Spring Boot)

---

## Risk Assessment

| Risk | Mitigation |
|------|-----------|
| JWT tokens no pueden revocarse inmediatamente | Expiration corta (24h) + validación `password_changed_at` para invalidación al cambiar contraseña |
| In-memory cache se pierde al reiniciar | Aceptable para rate limiting (bloqueos temporales se resetean, no critico) |
| Secret rotation compleja | Documentar proceso en quickstart.md; considerar key rotation strategy en futuro |
| Brute force distribuido (múltiples IPs) | Out of scope; rate limit es por clave, no por IP (enfocado en proteger cuentas específicas) |

---

## Open Questions (defer to implementation)

- ¿Configurar BCrypt strength via property? (default 10 es adecuado)
- ¿Agregar refresh token support? (out of scope v1, spec no lo menciona)
- ¿Logging de eventos de autenticación en archivo separado? (usar logger standard Spring Boot)
