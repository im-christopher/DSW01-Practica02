# Research: CRUD de Empleados

## Decisiones

### Decision: Mantener Java 17 + Spring Boot 3.x con arquitectura en capas
- **Rationale**: Alinea el feature con la constitución del proyecto y permite un servicio mantenible con responsabilidades claras por capa.
- **Alternatives considered**:
  - Usar stack distinto (rechazado por incumplir constitución).
  - Arquitectura monolítica sin capas (rechazado por menor mantenibilidad y testabilidad).

### Decision: Persistencia en PostgreSQL con `clave` PK `VARCHAR(5)` generada por backend
- **Rationale**: El requerimiento funcional final exige `clave` autogenerada en formato `E-001`, sin reutilización y con límite en `E-999`.
- **Alternatives considered**:
  - `clave` enviada por cliente (rechazado para evitar inconsistencias).
  - Formato alfanumérico libre de 10 chars (rechazado por decisión funcional final).

### Decision: Concurrencia optimista para actualización con respuesta `409 Conflict`
- **Rationale**: Evita sobrescrituras silenciosas y cumple la aclaración de conflictos concurrentes.
- **Alternatives considered**:
  - Última escritura gana (rechazado por riesgo de pérdida de datos).
  - Mezcla automática de cambios (rechazado por complejidad innecesaria para este alcance).

### Decision: Seguridad con HTTP Basic Authentication en endpoints CRUD
- **Rationale**: Cumple el baseline de seguridad constitucional y evita exposición no autenticada.
- **Alternatives considered**:
  - Endpoints públicos sin autenticación (rechazado por incumplimiento constitucional).
  - OAuth2/JWT para MVP (rechazado por sobrealcance para el feature actual).

### Decision: Contrato OpenAPI explícito para CRUD y errores de validación/concurrencia
- **Rationale**: Permite trazabilidad entre requerimientos, implementación y pruebas de contrato.
- **Alternatives considered**:
  - Documentación solo narrativa (rechazado por menor precisión verificable).

### Decision: Incluir listado con paginación, filtros y orden controlado
- **Rationale**: La operación requiere exploración del padrón sin degradar consistencia ni complejidad del MVP.
- **Alternatives considered**:
  - Sin listado (rechazado por necesidad operativa).
  - Filtros/orden libres en todos los campos (rechazado por sobrealcance para MVP).

## Resolución de NEEDS CLARIFICATION

No existen marcadores `NEEDS CLARIFICATION` pendientes en `spec.md`.
