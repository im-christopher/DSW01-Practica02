# Feature Specification: Departamentos CRUD

**Feature Branch**: `003-departamentos-crud`  
**Created**: March 9, 2026  
**Status**: Draft  
**Input**: User description: "Implementar una nueva tabla departamentos (id, nombre) relacionada a la tabla empleados"

## Clarifications

### Session 2026-03-09

- Q: Estrategia de eliminación de departamentos - ¿Hard delete físico o soft delete? → A: Soft delete con campo `activo` - Marca departamento como inactivo pero mantiene el registro en BD
- Q: Campos del departamento en respuestas de empleados - ¿Qué información incluir? → A: Solo ID y nombre del departamento
- Q: Patrón de paginación para listado de departamentos - ¿Seguir mismo patrón que empleados? → A: Mismo patrón que empleados (page=0, size=20, sort=asc)
- Q: Campo de ordenamiento por defecto en listado de departamentos - ¿Por ID o nombre? → A: Ordenar por nombre alfabéticamente
- Q: Control de versión optimista en departamentos - ¿Usar campo `version` como empleados? → A: Sin control de versión - Last-write-wins, departamentos cambian raramente

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Crear y Gestionar Departamentos (Priority: P1)

Como administrador del sistema, necesito crear y gestionar departamentos para poder organizar la estructura de la empresa.

**Why this priority**: Fundamental para establecer la estructura organizacional base. Sin departamentos, no es posible asignar empleados a ellos.

**Independent Test**: Puede ser completamente testeado creando un departamento vía API POST, verificando que se persiste correctamente con ID autogenerado y nombre único, y recuperándolo con GET por ID.

**Acceptance Scenarios**:

1. **Given** el sistema está corriendo, **When** envío POST a `/api/v1/departamentos` con `{"nombre": "Tecnología"}`, **Then** recibo 201 con el departamento creado incluyendo un ID autogenerado
2. **Given** existe un departamento con ID 1, **When** envío GET a `/api/v1/departamentos/1`, **Then** recibo 200 con los datos completos del departamento
3. **Given** existe un departamento, **When** envío PUT a `/api/v1/departamentos/1` con `{"nombre": "TI"}`, **Then** el nombre se actualiza correctamente sin necesidad de campo version
4. **Given** existe un departamento activo sin empleados, **When** envío DELETE a `/api/v1/departamentos/1`, **Then** recibo 204 y el departamento se marca como inactivo (soft delete)
5. **Given** existen departamentos activos, **When** envío GET a `/api/v1/departamentos?page=0&size=20&sort=asc`, **Then** recibo una lista paginada ordenada alfabéticamente por nombre

---

### User Story 2 - Asignar Empleados a Departamentos (Priority: P2)

Como administrador, necesito asignar empleados a departamentos específicos para reflejar la estructura organizacional real.

**Why this priority**: Permite vincular empleados con departamentos, completando la relación necesaria mencionada en los requerimientos.

**Independent Test**: Puede ser testeado creando un departamento, luego creando/actualizando un empleado con el campo `departamentoId`, y verificando que el empleado queda correctamente asociado.

**Acceptance Scenarios**:

1. **Given** existe un departamento con ID 1, **When** creo un empleado con `{"nombre": "Juan", "direccion": "Calle 1", "telefono": "1234567890", "departamentoId": 1}`, **Then** el empleado se crea asociado al departamento
2. **Given** un empleado E-001 sin departamento, **When** envío PUT con `{"departamentoId": 1}`, **Then** el empleado se asocia al departamento
3. **Given** un empleado E-001 en el departamento 1, **When** consulto GET `/api/v1/empleados/E-001`, **Then** los datos incluyen campo departamento con id y nombre únicamente
4. **Given** un empleado asociado a departamento, **When** envío PUT con `{"departamentoId": null}`, **Then** el empleado queda sin departamento asignado

---

### User Story 3 - Listar Empleados por Departamento (Priority: P3)

Como administrador, necesito ver todos los empleados que pertenecen a un departamento específico para tener visibilidad de la estructura.

**Why this priority**: Funcionalidad de consulta útil pero no crítica. Puede implementarse después de tener la relación establecida.

**Independent Test**: Puede ser testeado consultando GET `/api/v1/departamentos/1/empleados` y verificando que retorna solo los empleados asociados a ese departamento.

**Acceptance Scenarios**:

1. **Given** un departamento con 3 empleados asignados, **When** consulto GET `/api/v1/departamentos/1/empleados`, **Then** recibo una lista paginada con exactamente esos 3 empleados
2. **Given** un departamento sin empleados, **When** consulto GET `/api/v1/departamentos/1/empleados`, **Then** recibo una lista vacía con status 200
3. **Given** múltiples departamentos con empleados, **When** consulto GET `/api/v1/empleados?departamentoId=1`, **Then** recibo solo empleados del departamento 1

---

### Edge Cases

- ¿Qué pasa cuando intento crear un departamento con nombre duplicado? → Sistema debe rechazar con 400 Bad Request indicando que el nombre debe ser único
- ¿Qué pasa cuando intento eliminar un departamento que tiene empleados asignados? → Sistema debe rechazar con 409 Conflict explicando que primero se deben reasignar los empleados
- ¿Qué pasa cuando intento eliminar un departamento ya marcado como inactivo? → Sistema debe rechazar con 404 Not Found
- ¿Qué pasa cuando listo departamentos sin especificar filtro? → Sistema retorna solo departamentos activos (inactivos excluidos por defecto)
- ¿Qué pasa cuando intento asignar un empleado a un departamento que no existe? → Sistema debe rechazar con 404 Not Found
- ¿Qué pasa cuando el nombre del departamento está vacío o tiene solo espacios? → Sistema debe rechazar con 400 Bad Request por validación
- ¿Qué pasa cuando intento actualizar un departamento a un nombre que ya existe? → Sistema debe rechazar con 400 Bad Request

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Sistema DEBE crear una tabla `departamentos` con columnas `id` (PRIMARY KEY auto-incremental), `nombre` (VARCHAR(100) NOT NULL UNIQUE), y `activo` (BOOLEAN NOT NULL DEFAULT true)
- **FR-002**: Sistema DEBE exponer endpoints REST para operaciones CRUD sobre departamentos bajo `/api/v1/departamentos` con paginación estándar (page=0, size=20, sort=asc) y ordenamiento por nombre alfabéticamente
- **FR-003**: Sistema DEBE agregar columna `departamento_id` (BIGINT nullable FK) a la tabla `empleados` referenciando `departamentos.id`
- **FR-004**: Sistema DEBE validar que el nombre del departamento sea obligatorio, tenga máximo 100 caracteres y sea único
- **FR-005**: Sistema DEBE implementar soft delete marcando departamentos como inactivos (activo=false) y DEBE prevenir eliminación de departamentos que tengan empleados asignados (retornar 409 Conflict)
- **FR-006**: Sistema DEBE permitir crear/actualizar empleados con departamento opcional
- **FR-007**: Sistema DEBE incluir información del departamento en las respuestas de empleados cuando estén asociados, exponiendo únicamente los campos `id` y `nombre` del departamento en un objeto anidado
- **FR-008**: Sistema DEBE permitir filtrar empleados por departamento en el endpoint de listado
- **FR-009**: Sistema DEBE permitir listar empleados de un departamento específico mediante endpoint dedicado
- **FR-010**: Sistema DEBE mantener integridad referencial con ON DELETE RESTRICT en la FK departamento_id

### Constitution Alignment *(mandatory)*

- **CA-001**: Feature DEBE permanecer backend-only y compatible con Spring Boot 3.x + Java 17.
- **CA-002**: Feature DEBE aplicar HTTP Basic Authentication en todos los endpoints expuestos (`/api/v1/departamentos/**`).
- **CA-003**: Feature DEBE definir migración Flyway que cree tabla `departamentos` y altere tabla `empleados` con columna FK `departamento_id` con constraint de integridad referencial.
- **CA-004**: Feature DEBE documentar que no requiere cambios en Dockerfile ni docker-compose.yml (usa la misma base de datos PostgreSQL existente).
- **CA-005**: Feature DEBE actualizar OpenAPI/Swagger con los nuevos endpoints de departamentos y modificar schemas de empleados para incluir campo departamento.
- **CA-006**: Feature DEBE exponer endpoints bajo `/api/v1/departamentos` manteniendo el versionamiento existente sin introducir cambios breaking.

### Key Entities *(include if feature involves data)*

- **Departamento**: Representa una unidad organizacional de la empresa. Atributos: `id` (identificador único autogenerado), `nombre` (nombre descriptivo único del departamento, máximo 100 caracteres), `activo` (booleano indicando si el departamento está activo o fue eliminado mediante soft delete)
- **Empleado (extendido)**: Se extiende para incluir relación opcional many-to-one con Departamento mediante `departamentoId`. Un empleado puede pertenecer a un departamento o a ninguno (null)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Administradores pueden crear un departamento en menos de 5 segundos incluyendo validación de unicidad
- **SC-002**: Sistema puede manejar al menos 1000 departamentos sin degradación de performance en consultas de listado
- **SC-003**: 100% de las operaciones de eliminación de departamentos con empleados son rechazadas correctamente antes de corromper datos
- **SC-004**: Consultas de empleados con información de departamento se completan en menos de 500ms para datasets de hasta 10,000 empleados
- **SC-005**: Todas las operaciones CRUD de departamentos son exitosas al primer intento cuando los datos son válidos (tasa de error 0% con datos correctos)

## Assumptions

- Se asume que la aplicación ya tiene la infraestructura de PostgreSQL funcionando con Flyway habilitado
- Se asume que el sistema existente de empleados ya funciona correctamente y no será afectado negativamente
- Se asume que los usuarios tienen conocimientos básicos de REST APIs y autenticación Basic
- Se asume que un empleado solo puede pertenecer a un departamento a la vez (relación many-to-one, no many-to-many)
- Se asume que los departamentos son entidades relativamente estáticas (no cambian frecuentemente)

## Constraints

- La columna `departamento_id` en `empleados` debe ser nullable para permitir empleados sin departamento asignado
- El nombre de departamento está limitado a 100 caracteres por restricción de base de datos
- La eliminación de departamentos usa soft delete (marca como inactivo) y está bloqueada si hay empleados asociados (retorna 409 Conflict)
- Los departamentos no requieren control de versión optimista debido a su naturaleza estática (actualizaciones infrecuentes)
- El listado de departamentos retorna solo activos por defecto; departamentos inactivos excluidos de consultas normales
- La feature debe usar el mismo pool de conexiones de base de datos existente (no crear nuevas conexiones)

## Dependencies

- **Flyway**: Para migración de schema (V3__create_departamentos.sql)
- **Spring Data JPA**: Para persistencia de entidad Departamento
- **PostgreSQL**: Para almacenamiento con constraints de FK
- **OpenAPI/Swagger**: Para documentación de nuevos endpoints
- **Existing Empleado API**: Los endpoints de empleados deben modificarse para soportar el nuevo campo departamento
