# Feature Specification: Pantallas Separadas para Login Admin y CRUD de Empleados

**Feature Branch**: `002-admin-login-empleados-pantallas`  
**Created**: 2026-04-06  
**Status**: Draft  
**Input**: User description: "quiero que el frontend me lo dividas en pantallas, una para el login de admin, otra donde se agreguen, editen, visualicen y eliminen empleados"

## Clarifications

### Session 2026-04-06

- Q: Como debe persistir la sesion administrativa en frontend? -> A: Persistir sesion solo en memoria de la app (se pierde al recargar/cerrar pestana).
- Q: Como se valida el login administrativo en esta iteracion? -> A: Login valida credenciales usando HTTP Basic contra endpoint protegido (sin endpoint de login dedicado).
- Q: Como debe manejarse un conflicto de version al editar (concurrencia)? -> A: Mostrar mensaje de conflicto, recargar datos actuales del empleado y pedir al admin confirmar/reaplicar cambios.
- Q: Que objetivo formal de accesibilidad debe cumplir la UI critica? -> A: Cumplir WCAG 2.1 AA en login y CRUD de empleados.
- Q: Como debe implementarse la eliminacion de empleados en esta iteracion? -> A: Eliminacion logica (se marca inactivo y no aparece en listados por defecto).

## User Scenarios & Testing *(mandatory)*

<!--
  IMPORTANT: User stories should be PRIORITIZED as user journeys ordered by importance.
  Each user story/journey must be INDEPENDENTLY TESTABLE - meaning if you implement just ONE of them,
  you should still have a viable MVP (Minimum Viable Product) that delivers value.
  
  Assign priorities (P1, P2, P3, etc.) to each story, where P1 is the most critical.
  Think of each story as a standalone slice of functionality that can be:
  - Developed independently
  - Tested independently
  - Deployed independently
  - Demonstrated to users independently
-->

### User Story 1 - Login Administrativo (Priority: P1)

Como administrador, quiero una pantalla dedicada de login para autenticarme antes de acceder a funciones de gestion de empleados.

**Why this priority**: El acceso autenticado es requisito previo para toda operacion de administracion y protege funciones criticas.

**Independent Test**: Puede validarse de forma aislada intentando iniciar sesion con credenciales validas e invalidas y verificando el resultado de acceso.

**Acceptance Scenarios**:

1. **Given** un usuario no autenticado, **When** ingresa credenciales de administrador validas, **Then** accede a la pantalla de gestion de empleados.
2. **Given** un usuario no autenticado, **When** ingresa credenciales invalidas, **Then** permanece en la pantalla de login y recibe un mensaje claro de error.
3. **Given** un usuario no autenticado, **When** intenta abrir directamente la pantalla de gestion, **Then** es redirigido o bloqueado hasta autenticarse.

---

### User Story 2 - Pantalla de Gestion de Empleados (Priority: P2)

Como administrador autenticado, quiero una pantalla unica de empleados donde pueda visualizar, agregar, editar y eliminar registros para gestionar el catalogo completo.

**Why this priority**: Esta historia entrega el valor operativo principal del frontend y cubre el flujo CRUD completo en una sola pantalla.

**Independent Test**: Puede probarse sin otras historias ejecutando CRUD completo en la pantalla de empleados tras autenticacion.

**Acceptance Scenarios**:

1. **Given** un administrador autenticado, **When** abre la pantalla de empleados, **Then** visualiza el listado actual con estados de carga y vacio.
2. **Given** un administrador autenticado, **When** captura datos validos y guarda un nuevo empleado, **Then** el registro se crea y aparece en la vista.
3. **Given** un administrador autenticado y un empleado existente, **When** modifica y confirma cambios validos, **Then** la vista refleja la edicion aplicada.
4. **Given** un administrador autenticado y un empleado existente, **When** confirma la accion de eliminacion, **Then** el registro se marca inactivo y deja de aparecer en el listado por defecto.
5. **Given** datos invalidos en alta o edicion, **When** intenta guardar, **Then** el sistema bloquea la operacion y muestra mensajes de validacion accionables.
6. **Given** un conflicto de version al editar, **When** el backend rechaza la actualizacion por concurrencia, **Then** el sistema muestra conflicto, recarga datos actuales y solicita confirmar o reaplicar cambios.

---

### User Story 3 - Cierre de Sesion Seguro (Priority: P3)

Como administrador, quiero cerrar sesion desde la pantalla de empleados para finalizar uso seguro del sistema.

**Why this priority**: Complementa el flujo de autenticacion y evita que la sesion permanezca abierta en entornos compartidos.

**Independent Test**: Puede validarse cerrando sesion y verificando que no se pueda volver a CRUD sin autenticar nuevamente.

**Acceptance Scenarios**:

1. **Given** un administrador autenticado en la pantalla de empleados, **When** selecciona cerrar sesion, **Then** regresa a la pantalla de login y se invalidan permisos de acceso a CRUD.

---

### Edge Cases

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right edge cases.
-->

- Credenciales vacias o incompletas en la pantalla de login.
- Sesion invalida o expirada durante una operacion de alta, edicion o eliminacion.
- Recarga de pagina o cierre de pestana durante sesion activa, obligando nuevo login.
- Conflicto de version al editar un empleado modificado por otro proceso.
- Intento de editar o eliminar un empleado que ya no existe.
- Fallo temporal de red durante consulta o guardado de datos.
- Doble envio involuntario de formularios por clicks repetidos.
- Eliminacion cancelada por el usuario antes de confirmarse.
- Intento de operar sobre un empleado marcado inactivo por eliminacion logica.

## Requirements *(mandatory)*

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right functional requirements.
-->

### Functional Requirements

- **FR-001**: El sistema DEBE presentar una pantalla dedicada de login para administrador separada de la pantalla de empleados.
- **FR-002**: El sistema DEBE restringir el acceso a la pantalla de empleados solo a usuarios con autenticacion administrativa valida.
- **FR-003**: El sistema DEBE mostrar mensajes de error claros cuando falle el login, sin exponer informacion sensible.
- **FR-004**: El sistema DEBE disponer de una pantalla de empleados que permita visualizar el listado de registros existentes.
- **FR-005**: El sistema DEBE permitir crear empleados desde la pantalla de empleados con validaciones de campos obligatorios.
- **FR-006**: El sistema DEBE permitir editar empleados desde la pantalla de empleados con confirmacion de resultado.
- **FR-007**: El sistema DEBE permitir eliminacion logica de empleados desde la pantalla de empleados con confirmacion explicita de la accion.
- **FR-008**: El sistema DEBE bloquear operaciones CRUD con datos invalidos y mostrar retroalimentacion accionable por campo o accion.
- **FR-009**: El sistema DEBE mostrar estados de carga, exito y error para operaciones de consulta, alta, edicion y eliminacion.
- **FR-010**: El sistema DEBE permitir cerrar sesion desde la pantalla de empleados y devolver al usuario al login.
- **FR-011**: El sistema DEBE impedir que un usuario sin sesion activa reutilice acceso previo a la pantalla de empleados.
- **FR-012**: El sistema DEBE mantener la sesion administrativa solo en memoria de ejecucion del frontend, invalidandola automaticamente al recargar o cerrar la pestana.
- **FR-013**: El sistema DEBE validar credenciales de login usando HTTP Basic contra endpoints protegidos existentes, sin requerir un endpoint dedicado de autenticacion en esta iteracion.
- **FR-014**: El sistema DEBE manejar conflictos de version en edicion mostrando un mensaje explicito, recargando el estado actual del empleado y permitiendo al administrador confirmar o reaplicar cambios.
- **FR-015**: El sistema DEBE cumplir WCAG 2.1 AA en las pantallas criticas de login y CRUD de empleados (navegacion por teclado, contraste, foco visible y semantica adecuada).
- **FR-016**: El sistema DEBE excluir por defecto del listado los empleados con estado inactivo por eliminacion logica.

### Constitution Alignment *(mandatory)*

- **CA-001**: Feature MUST remain compatible with Spring Boot 3.x + Java 17 backend and
  MUST include Angular 21 frontend scope for user-facing workflow changes.
- **CA-002**: Feature MUST enforce HTTP Basic Authentication on exposed endpoints unless
  explicitly exempted by governance.
- **CA-003**: Feature MUST define PostgreSQL persistence impact (schema, migrations,
  and data integrity behavior).
- **CA-004**: Feature MUST document Docker runtime/build impact for backend and
  frontend components.
- **CA-005**: Feature MUST document OpenAPI/Swagger changes for all affected endpoints.
- **CA-006**: Feature MUST expose affected endpoints under versioned paths
  (`/api/v{major}/...`) and define major-version bump impact for breaking changes.
- **CA-007**: Feature MUST define UI accessibility and usability acceptance criteria for
  critical user flows (semantic structure, keyboard access, visible focus, readable
  contrast, and clear validation/error feedback).

### Key Entities *(include if feature involves data)*

- **SesionAdmin**: Estado de autenticacion administrativa que habilita o bloquea el acceso a la pantalla de empleados.
- **CredencialAdmin**: Identidad y secreto de acceso usados en la pantalla de login.
- **Empleado**: Registro administrado en pantalla con datos operativos y control de version.
- **OperacionCRUD**: Acciones de consulta, creacion, edicion y eliminacion ejecutadas por administrador autenticado.
- **EstadoEmpleado**: Indicador de activo/inactivo para soportar eliminacion logica y filtrado por defecto.

### Assumptions & Dependencies

- Se asume que existe un unico rol administrativo para este alcance.
- Se asume disponibilidad de endpoints protegidos por HTTP Basic para validar credenciales y procesar CRUD de empleados.
- Se asume que reglas de negocio y persistencia de empleados residen en backend.
- Dependencia externa: conectividad estable con backend durante uso normal.
- Dependencia de proceso: consistencia entre contratos de datos backend y formularios de pantalla.

## Success Criteria *(mandatory)*

<!--
  ACTION REQUIRED: Define measurable success criteria.
  These must be technology-agnostic and measurable.
-->

### Measurable Outcomes

- **SC-001**: El 95% de logins validos completa acceso a la pantalla de empleados en menos de 5 segundos.
- **SC-002**: El 95% de consultas de listado de empleados muestra resultados o estado vacio en menos de 3 segundos.
- **SC-003**: Al menos 90% de altas y ediciones de empleados se completa al primer intento sin asistencia adicional.
- **SC-004**: Al menos 95% de eliminaciones logicas confirmadas se refleja en pantalla en menos de 3 segundos, ocultando al empleado del listado por defecto.
- **SC-005**: El 100% de errores de autenticacion y validacion presenta mensajes comprensibles y accionables para el usuario.
- **SC-006**: El 100% de criterios WCAG 2.1 AA aplicables a login y CRUD de empleados se valida sin incumplimientos bloqueantes antes de liberar la funcionalidad.
