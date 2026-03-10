# Feature Specification: Autenticación de Empleados con Contraseña

**Feature Branch**: `002-empleado-auth`  
**Created**: 2026-03-08  
**Status**: Draft  
**Input**: User description: "que la autenticación se haga en la entidad de empleados, agrega una contraseña"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registro de Empleado con Contraseña (Priority: P1)

Los empleados existentes o nuevos deben poder establecer una contraseña segura que les permita autenticarse posteriormente en el sistema. Esta contraseña debe ser obligatoria para todos los empleados y cumplir con requisitos mínimos de seguridad.

**Why this priority**: Es la base fundamental del sistema de autenticación. Sin contraseñas almacenadas, no es posible implementar ninguna funcionalidad de login o autenticación posterior.

**Independent Test**: Se puede probar creando o actualizando un empleado con una contraseña válida y verificando que: (1) la contraseña se almacena de forma segura (no en texto plano), (2) cumple validaciones mínimas, y (3) se puede usar posteriormente para autenticación.

**Acceptance Scenarios**:

1. **Given** un empleado nuevo sin contraseña, **When** se crea el empleado proporcionando nombre, dirección, teléfono y una contraseña válida, **Then** el sistema genera la clave única (E-XXX), almacena la contraseña de forma segura, y retorna los datos del empleado sin exponer la contraseña
2. **Given** un empleado existente sin contraseña, **When** se actualiza agregando una contraseña válida, **Then** el sistema almacena la contraseña de forma segura y permite autenticación posterior
3. **Given** se intenta crear/actualizar un empleado, **When** la contraseña proporcionada no cumple requisitos mínimos (menos de 8 caracteres), **Then** el sistema rechaza la operación con error 400 indicando los requisitos

---

### User Story 2 - Autenticación de Empleado (Priority: P2)

Los empleados deben poder autenticarse en el sistema usando su clave única (E-XXX) y su contraseña personal para acceder a operaciones protegidas.

**Why this priority**: Una vez que los empleados tienen contraseñas, necesitan un mecanismo para autenticarse y acceder al sistema de manera segura.

**Independent Test**: Se puede probar intentando autenticar con una clave y contraseña válidas y verificando que: (1) la autenticación es exitosa y retorna un token o sesión, (2) credenciales inválidas son rechazadas, (3) la autenticación falla de forma segura sin revelar si la clave o contraseña es incorrecta.

**Acceptance Scenarios**:

1. **Given** un empleado con clave E-001 y contraseña establecida, **When** el empleado envía su clave y contraseña correctas, **Then** el sistema valida las credenciales y retorna un token/sesión válido
2. **Given** un empleado con clave E-001, **When** el empleado envía su clave correcta pero contraseña incorrecta, **Then** el sistema rechaza la autenticación con error 401 sin revelar cuál credencial es incorrecta
3. **Given** una clave inexistente, **When** se intenta autenticar, **Then** el sistema rechaza con error 401 sin revelar si la clave existe o no
4. **Given** múltiples intentos fallidos de autenticación (5+ intentos en 15 minutos), **When** se alcanza el límite, **Then** el sistema bloquea temporalmente los intentos de autenticación para esa clave

---

### User Story 3 - Cambio de Contraseña (Priority: P3)

Los empleados autenticados deben poder cambiar su contraseña actual por una nueva, proporcionando la contraseña anterior como verificación.

**Why this priority**: Permite a los empleados mantener la seguridad de sus cuentas y cumplir con políticas de rotación de contraseñas.

**Independent Test**: Se puede probar autenticándose como empleado, enviando la contraseña actual y una nueva contraseña válida, y verificando que: (1) solo la contraseña actual correcta permite el cambio, (2) la nueva contraseña cumple requisitos, (3) posteriores autenticaciones requieren la nueva contraseña.

**Acceptance Scenarios**:

1. **Given** un empleado autenticado, **When** el empleado proporciona su contraseña actual correcta y una nueva contraseña válida, **Then** el sistema actualiza la contraseña y posteriores autenticaciones requieren la nueva contraseña
2. **Given** un empleado autenticado, **When** la contraseña actual proporcionada es incorrecta, **Then** el sistema rechaza el cambio con error 403
3. **Given** un empleado autenticado, **When** la nueva contraseña no cumple requisitos mínimos, **Then** el sistema rechaza el cambio con error 400 indicando los requisitos

---

### Edge Cases

- ¿Qué sucede cuando un empleado pierde/olvida su contraseña? (recuperación de contraseña fuera de scope inicial)
- ¿Cómo se maneja la migración de empleados existentes sin contraseña?
- ¿Qué pasa si se intenta crear dos empleados con la misma contraseña? (permitido, las contraseñas no necesitan ser únicas)
- ¿Cómo se previenen ataques de fuerza bruta en autenticación?
- ¿Se invalidan sesiones activas cuando se cambia la contraseña?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Sistema DEBE permitir agregar contraseña al crear un empleado nuevo (opcional en creación para compatibilidad con feature 001)
- **FR-002**: Sistema DEBE permitir establecer/actualizar contraseña de un empleado existente
- **FR-003**: Sistema DEBE validar que contraseñas cumplan requisitos mínimos: al menos 8 caracteres, contener al menos una letra y un número
- **FR-004**: Sistema DEBE almacenar contraseñas de forma segura usando hashing (nunca en texto plano)
- **FR-005**: Sistema DEBE proporcionar endpoint de autenticación que acepte clave de empleado y contraseña
- **FR-006**: Sistema DEBE retornar token o sesión válida después de autenticación exitosa
- **FR-007**: Sistema DEBE rechazar autenticaciones con credenciales inválidas sin revelar cuál parte (clave o contraseña) es incorrecta
- **FR-008**: Sistema DEBE implementar protección contra ataques de fuerza bruta limitando intentos de autenticación (máximo 5 intentos fallidos por clave en ventana de 15 minutos)
- **FR-009**: Sistema DEBE permitir a empleados autenticados cambiar su contraseña proporcionando la contraseña actual como verificación
- **FR-010**: Sistema NUNCA DEBE exponer contraseñas en respuestas de API (ni en texto plano ni hasheadas)
- **FR-011**: Sistema DEBE registrar eventos de autenticación (exitosa y fallida) para auditoría
- **FR-012**: Sistema DEBE invalidar sesiones/tokens existentes cuando un empleado cambia su contraseña

### Constitution Alignment *(mandatory)*

- **CA-001**: Feature MUST remain backend-only and compatible with Spring Boot 3.x + Java 17.
- **CA-002**: Feature MUST enforce HTTP Basic Authentication on exposed endpoints unless explicitly exempted by governance. Los endpoints de autenticación (/login) pueden estar exentos para permitir el proceso de login.
- **CA-003**: Feature MUST define PostgreSQL persistence impact (schema, migrations, and data integrity behavior). Se añadirá columna `password_hash` a tabla `empleados` con migración.
- **CA-004**: Feature MUST document Docker runtime/build impact. Sin cambios en runtime/build baseline.
- **CA-005**: Feature MUST document OpenAPI/Swagger changes for all affected endpoints. Se añadirán endpoints `/api/v1/empleados/login`, `/api/v1/empleados/{clave}/password`.
- **CA-006**: Feature MUST expose affected endpoints under versioned paths (`/api/v{major}/...`) and define major-version bump impact for breaking changes. Usar `/api/v1/` para nuevos endpoints; no se modifica estructura existente de empleados.

### Key Entities

- **Empleado**: Entidad existente que se extenderá con campo de contraseña hasheada. Atributos relevantes: clave (identificador único E-XXX), password_hash (almacenamiento seguro de contraseña).
- **AuthToken/Session**: Representa una sesión de autenticación válida. Atributos: token único, empleado asociado, fecha de expiración, fecha de creación.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Empleados pueden establecer contraseña durante creación o actualización en menos de 1 minuto
- **SC-002**: Empleados pueden autenticarse con clave y contraseña en menos de 2 segundos (tiempo de respuesta p95)
- **SC-003**: Sistema rechaza 100% de autenticaciones con credenciales inválidas sin fugas de información
- **SC-004**: Sistema protege contra ataques de fuerza bruta bloqueando intentos después de 5 fallos en 15 minutos por clave
- **SC-005**: Cero contraseñas almacenadas en texto plano (100% hasheadas)
- **SC-006**: Empleados autenticados pueden cambiar su contraseña de forma exitosa en menos de 30 segundos
- **SC-007**: Sistema registra 100% de eventos de autenticación (exitosa y fallida) para auditoría

### Assumptions

- Las contraseñas no requieren reglas de complejidad extremas (mayúsculas, caracteres especiales) para el alcance inicial
- La recuperación de contraseña olvidada está fuera del alcance de esta feature
- Los tokens/sesiones tienen expiración de 24 horas por defecto (configurable)
- La migración de empleados existentes sin contraseña a empleados con contraseña se hará gradualmente (contraseña opcional inicialmente)
- El mecanismo de autenticación coexistirá con el Basic Auth existente para endpoints administrativos