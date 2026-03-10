# Data Model: Autenticación de Empleados

**Feature**: 002-empleado-auth  
**Date**: 2026-03-08  
**Status**: Design

## Overview

Este documento define el modelo de datos para la funcionalidad de autenticación de empleados, extendiendo la entidad existente `Empleado` con capacidad de contraseña y agregando auditoría de eventos de autenticación.

---

## Entity: Empleado (Modified)

**Purpose**: Entidad principal que representa un empleado, extendida con campos de autenticación.

### Attributes

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | `Long` | `PRIMARY KEY`, `AUTO_INCREMENT` | Identificador interno (existente) |
| `clave` | `String` | `UNIQUE`, `NOT NULL`, pattern `E-\d{3}` | Clave única empleado (existente) |
| `nombre` | `String` | `NOT NULL`, max 100 chars | Nombre empleado (existente) |
| `direccion` | `String` | `NOT NULL`, max 200 chars | Dirección empleado (existente) |
| `telefono` | `String` | `NOT NULL`, max 20 chars | Teléfono empleado (existente) |
| `version` | `Long` | `NOT NULL`, optimistic lock | Control concurrencia JPA (existente) |
| `password_hash` | `String` | `NULLABLE`, max 255 chars | **NUEVO**: Hash BCrypt de contraseña (nullable para migración gradual) |
| `password_changed_at` | `Instant` | `NULLABLE` | **NUEVO**: Timestamp último cambio contraseña (para invalidación JWT) |

### Validation Rules

**Existing validations** (from feature 001):
- `nombre`: requerido, no vacío
- `direccion`: requerido, no vacío
- `telefono`: requerido, no vacío
- `clave`: generado automáticamente, formato E-XXX, único

**New validations** (feature 002):
- `password_hash`: 
  - Opcional en creation/update (compatibilidad con empleados sin contraseña)
  - Si se proporciona password en request, DEBE cumplir: mínimo 8 caracteres, al menos 1 letra y 1 número
  - Hash generado server-side con BCrypt (nunca almacenar plain text)
  - Hash debe ser válido formato BCrypt (regex `^\$2[aby]\$\d{2}\$[./A-Za-z0-9]{53}$`)
- `password_changed_at`:
  - Actualizado automáticamente al cambiar contraseña
  - Usado para invalidar tokens JWT emitidos antes del cambio

### State Transitions

```text
[Empleado sin contraseña]
         |
         | POST /empleados (con password) o PUT /empleados/{clave} (agrega password)
         v
[Empleado con contraseña]
         |
         | POST /empleados/login (autentica)
         v
[Empleado autenticado - token JWT emitido]
         |
         | PUT /empleados/{clave}/password
         v
[Contraseña actualizada - tokens antiguos invalidados]
```

### Persistence Mapping

- **Table**: `empleados` (existente)
- **JPA Entity**: `com.dsw01.practica02.empleados.Empleado`
- **New Columns**:
  - `password_hash VARCHAR(255)` - nullable
  - `password_changed_at TIMESTAMP` - nullable
- **Migration**: Flyway script `V2__add_employee_authentication.sql`

### Relationships

- No hay nuevas relaciones directas
- Relación conceptual con `AuthEvent` (1-to-many) vía `clave` (no FK física por simplicidad)

---

## Entity: AuthEvent (New)

**Purpose**: Registro de auditoría para eventos de autenticación (login exitoso, fallido, cambio contraseña).

### Attributes

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | `Long` | `PRIMARY KEY`, `AUTO_INCREMENT` | Identificador único evento |
| `clave` | `String` | `NOT NULL`, max 10 chars | Clave empleado relacionado (no FK por performance) |
| `event_type` | `String` | `NOT NULL`, enum values | Tipo evento: `LOGIN_SUCCESS`, `LOGIN_FAILURE`, `PASSWORD_CHANGED` |
| `timestamp` | `Instant` | `NOT NULL`, default `CURRENT_TIMESTAMP` | Momento del evento |
| `ip_address` | `String` | `NULLABLE`, max 45 chars | IP cliente (IPv4/IPv6) |
| `user_agent` | `String` | `NULLABLE`, max 255 chars | User-Agent header |
| `details` | `String` | `NULLABLE`, max 500 chars | Detalles adicionales (ej: razón fallo) |

### Validation Rules

- `clave`: requerido, formato `E-\d{3}` (validación aplicación, no FK constraint)
- `event_type`: debe ser uno de `LOGIN_SUCCESS`, `LOGIN_FAILURE`, `PASSWORD_CHANGED`
- `timestamp`: generado automáticamente si no provisto
- `ip_address`: validación formato IP (opcional)
- `details`: texto libre, limitado a 500 chars

### Indexes

- `idx_auth_events_clave` on `clave` - búsquedas por empleado
- `idx_auth_events_timestamp` on `timestamp` - queries ordenados por tiempo, cleanup de eventos antiguos
- Composite index `idx_auth_events_clave_timestamp` on `(clave, timestamp DESC)` - rate limiting queries

### Persistence Mapping

- **Table**: `auth_events`
- **JPA Entity**: `com.dsw01.practica02.auth.AuthEvent`
- **Migration**: Flyway script `V2__add_employee_authentication.sql`

### Event Types

| Type | Description | Triggered When |
|------|-------------|----------------|
| `LOGIN_SUCCESS` | Autenticación exitosa | POST /login con credenciales válidas |
| `LOGIN_FAILURE` | Intento fallido | POST /login con credenciales inválidas |
| `PASSWORD_CHANGED` | Cambio contraseña | PUT /empleados/{clave}/password exitoso |

### Retention Policy

- **Propósito**: Auditoría y análisis de seguridad
- **Retention**: No hay cleanup automático en v1 (feature futura puede agregar scheduled task para borrar eventos > 90 días)
- **Growth**: Asumir ~10-50 eventos/día (escala pequeña), tabla crecerá ~18K-90K registros/año (aceptable sin partitioning)

---

## Entity: RateLimitCache (Conceptual - not persisted)

**Purpose**: Tracking de intentos fallidos de login para rate limiting (5 intentos / 15 min).

### Implementation

- **Storage**: Caffeine in-memory cache (no persistencia)
- **Key**: `"auth-attempts:" + clave`
- **Value**: `AttemptCounter` (POJO con count + timestamp primer intento)
- **TTL**: 15 minutos

### Structure

```java
class AttemptCounter {
    int count;              // Número intentos fallidos
    Instant firstAttempt;   // Timestamp primer intento en ventana actual
}
```

### Eviction Policy

- **TTL-based**: Entradas expiran automáticamente 15 min después de creación
- **Max size**: 1000 entradas (suficiente para E-001 a E-999 + margen)
- **On overflow**: Evict LRU (Least Recently Used)

### Cache Operations

| Operation | Trigger | Action |
|-----------|---------|--------|
| **Increment** | Login fallido | Incrementar count; si count >= 5, bloquear hasta expiración TTL |
| **Reset** | Login exitoso | Remover entrada del cache (resetear contador) |
| **Check** | Pre-login | Verificar si count >= 5; si sí, rechazar con 429 Too Many Requests |

---

## Migration Strategy

### Phase 1: Schema Update (Sprint actual)

**Migration Script**: `V2__add_employee_authentication.sql`

```sql
-- Add authentication columns to existing empleados table
ALTER TABLE empleados 
    ADD COLUMN password_hash VARCHAR(255),
    ADD COLUMN password_changed_at TIMESTAMP;

-- Create auth_events table for audit logging
CREATE TABLE auth_events (
    id BIGSERIAL PRIMARY KEY,
    clave VARCHAR(10) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    details VARCHAR(500),
    
    CONSTRAINT chk_event_type CHECK (event_type IN ('LOGIN_SUCCESS', 'LOGIN_FAILURE', 'PASSWORD_CHANGED'))
);

-- Indexes for performance
CREATE INDEX idx_auth_events_clave ON auth_events(clave);
CREATE INDEX idx_auth_events_timestamp ON auth_events(timestamp);
CREATE INDEX idx_auth_events_clave_timestamp ON auth_events(clave, timestamp DESC);

-- Comments for documentation
COMMENT ON COLUMN empleados.password_hash IS 'BCrypt hash of employee password (nullable for gradual migration)';
COMMENT ON COLUMN empleados.password_changed_at IS 'Timestamp of last password change (used for JWT invalidation)';
COMMENT ON TABLE auth_events IS 'Audit log for authentication events (login attempts, password changes)';
```

### Backward Compatibility

- ✅ Empleados existentes sin contraseña siguen funcionando (CRUD operations)
- ✅ `password_hash` nullable permite creación de empleados sin contraseña (opcional)
- ✅ Endpoints existentes (`/api/v1/empleados` CRUD) no requieren cambios
- ✅ Tests existentes continúan pasando sin modificación

### Data Migration

**No data migration required**:
- Empleados existentes tendrán `password_hash = NULL` → no pueden autenticarse hasta establecer contraseña
- No hay default password (seguridad: fuerza establecimiento explícito)
- Nuevos empleados pueden crearse con o sin contraseña (opcional en DTO)

---

## Security Considerations

### Password Storage

- ✅ **Never plain text**: Contraseñas almacenadas solo como BCrypt hash
- ✅ **Salt included**: BCrypt incluye salt automáticamente en hash
- ✅ **Cost factor**: BCrypt default strength 10 (suficiente para seguridad actual)
- ❌ **No encryption**: Hash es one-way (no reversible), adecuado para autenticación

### Sensitive Data Exposure

- ✅ `password_hash` nunca expuesto en API responses (excluido en DTOs)
- ✅ `password_changed_at` puede exponerse (no sensible, útil para UI)
- ✅ `auth_events.details` no debe contener contraseñas o tokens (solo mensajes descriptivos)

### JWT Token Claims

**Claims incluidos**:
- `sub` (subject): clave empleado (ej: "E-001")
- `iat` (issued at): timestamp emisión token
- `exp` (expiration): timestamp expiración (iat + 24 horas)

**Claims excluidos** (no enviar info sensible):
- ❌ password_hash
- ❌ roles/permissions (out of scope v1)
- ❌ nombre/dirección (minimizar data en token)

### Rate Limiting Strategy

- **Scope**: Por `clave` (no por IP) - protege cuentas específicas
- **Threshold**: 5 intentos fallidos en 15 minutos (balance usability/security)
- **Reset**: Automático al expirar TTL o al login exitoso
- **Distributed**: No soportado en v1 (Caffeine es single-instance); migrar a Redis si múltiples instancias

---

## Validation Summary

| Requirement | Implementation | Validation Method |
|-------------|---------------|-------------------|
| FR-001: Agregar contraseña en creación | `password_hash` nullable, opcional en DTO | Test: crear empleado con y sin password |
| FR-002: Actualizar contraseña existente | PUT /empleados/{clave}/password endpoint | Test: agregar password a empleado sin password |
| FR-003: Validar requisitos contraseña | Validation annotations + custom validator | Test: rechazar passwords < 8 chars, sin letra+número |
| FR-004: Almacenamiento seguro (hashing) | BCryptPasswordEncoder | Test: verificar password_hash != plain password |
| FR-008: Rate limiting (5/15min) | Caffeine cache + AttemptCounter | Test: 6to intento rechazado, reseteo después 15 min |
| FR-011: Audit logging | AuthEvent entity, insert en cada evento | Test: verificar registro en auth_events |
| FR-012: Invalidar tokens al cambiar password | Comparar JWT.iat < password_changed_at | Test: token antiguo rechazado después cambio password |

---

## Open Issues / Future Enhancements

1. **Cleanup de auth_events**: Scheduled task para borrar eventos > 90 días (no crítico v1)
2. **Distributed rate limiting**: Migrar a Redis si múltiples instancias backend requeridas
3. **Password expiration policy**: Forzar cambio cada 90 días (out of scope v1)
4. **Account lockout**: Bloqueo permanente después N intentos (usar auth_events para detectar)
5. **MFA support**: Agregar segundo factor autenticación (out of scope v1)
