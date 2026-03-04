# Feature Specification: CRUD de Empleados

**Feature Branch**: `001-crud-empleados`  
**Created**: 2026-02-25  
**Status**: Draft  
**Input**: User description: "crear un crud de empleados con los campos clave: nombre, direccion y telefono. Donde clave sea el PK y los demas campos sean de 100 espacios."

## Clarifications

### Session 2026-02-25

- Q: Para `nombre`, `direccion` y `telefono`, ¿quieres que el tamaño sea exactamente 100 caracteres o máximo 100 caracteres? → A: Exactamente 100 caracteres.
- Q: Cuando se elimina un empleado, ¿la baja debe ser física o lógica? → A: Baja física.
- Q: ¿Cómo se resuelven actualizaciones concurrentes sobre el mismo empleado? → A: Rechazar actualización si hubo cambio concurrente (conflicto 409).
- Q: ¿Cuál es el formato definitivo de `clave` y cómo se genera? → A: Formato mixto con prefijo `E-` + autonumérico.
- Q: ¿Qué formato exacto debe usar el autonumérico de `clave`? → A: `E-001` (3 dígitos con ceros a la izquierda).

### Session 2026-02-26

- Q: ¿Qué debe pasar cuando el consecutivo alcance `E-999`? → A: Limitar a 3 dígitos y rechazar nuevas altas al llegar a `E-999`.
- Q: ¿Qué código HTTP debe devolver el sistema cuando ya se alcanzó `E-999`? → A: `409 Conflict`.
- Q: Tras baja física, ¿se deben reutilizar claves liberadas? → A: No reutilizar claves; el consecutivo solo avanza.
- Q: En `POST` de creación, ¿la `clave` se envía o la genera backend? → A: No se envía `clave`; backend la genera.
- Q: ¿Cómo se envía el control de concurrencia en actualización? → A: Con campo `version` en el body de `PUT`.
- Q: ¿Qué código HTTP usar para errores de validación de entrada? → A: `400 Bad Request` para todos los errores de validación de entrada.
- Q: ¿Qué código HTTP usar cuando la `clave` no existe en `GET`, `PUT` o `DELETE`? → A: `404 Not Found` en las tres operaciones.
- Q: ¿Qué debe devolver `POST` en una creación exitosa? → A: `201 Created` con la representación completa del empleado creado.
- Q: ¿Se incluye listado de empleados y con qué alcance? → A: Sí, listado con paginación y filtros.
- Q: ¿Qué filtros se permiten en el listado? → A: `nombre` por coincidencia parcial (contains) y `clave` por coincidencia exacta.
- Q: ¿Qué valores de paginación se usarán por defecto y máximo? → A: `page=0`, `size=20` por defecto y `size` máximo de `100`.
- Q: ¿Cuál es el orden por defecto del listado? → A: Orden por `clave` ascendente.
- Q: ¿Se permite parámetro `sort` en listado? → A: Sí, solo para `clave` con valores `asc` o `desc`.
- Q: ¿Qué formato de respuesta paginada se usará en el listado? → A: Formato estándar tipo Spring Page (`content`, `number`, `size`, `totalElements`, `totalPages`, `first`, `last`).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Alta y consulta de empleados (Priority: P1)

Como usuario de operación, quiero registrar empleados y consultar su información para tener un padrón base confiable.

**Why this priority**: Sin altas y consultas no existe valor funcional mínimo del módulo.

**Independent Test**: Puede probarse creando un empleado válido y consultándolo por su clave; esto entrega valor inmediato de registro y lectura.

**Acceptance Scenarios**:

1. **Given** que no existe conflicto de datos del nuevo registro, **When** se registra un empleado con `nombre`, `direccion` y `telefono` válidos, **Then** el sistema genera la `clave` con formato `E-001` (prefijo `E-` + 3 dígitos con ceros a la izquierda), lo guarda y confirma su creación.
2. **Given** que existe un empleado registrado, **When** se consulta por su `clave`, **Then** el sistema devuelve exactamente sus datos almacenados.

---

### User Story 2 - Actualización de empleados (Priority: P2)

Como usuario de operación, quiero actualizar datos de empleados para mantener el padrón vigente.

**Why this priority**: Mantener datos actualizados reduce errores operativos y retrabajo.

**Independent Test**: Puede probarse actualizando `nombre`, `direccion` y `telefono` de un registro existente y verificando la persistencia del cambio.

**Acceptance Scenarios**:

1. **Given** que existe un empleado con cierta clave, **When** se solicita actualizar sus campos permitidos con valores válidos, **Then** el sistema guarda los cambios y devuelve la versión actualizada.

---

### User Story 3 - Baja de empleados (Priority: P3)

Como usuario de operación, quiero eliminar empleados para depurar registros que ya no aplican.

**Why this priority**: Completa el ciclo CRUD y evita acumulación de datos obsoletos.

**Independent Test**: Puede probarse eliminando un empleado existente y validando que no se encuentre en consultas posteriores.

**Acceptance Scenarios**:

1. **Given** que existe un empleado, **When** se solicita su eliminación por `clave`, **Then** el sistema confirma la baja física y deja de retornarlo en consultas.

---

### User Story 4 - Listado paginado y filtrado de empleados (Priority: P2)

Como usuario de operación, quiero listar empleados con paginación y filtros para localizar registros eficientemente.

**Why this priority**: Mejora la operación diaria y evita consultas por clave cuando se requiere exploración del padrón.

**Independent Test**: Puede probarse listando empleados con diferentes páginas y aplicando filtros sobre datos existentes.

**Acceptance Scenarios**:

1. **Given** que existen múltiples empleados, **When** se consulta el listado con parámetros de paginación válidos, **Then** el sistema devuelve una página consistente de resultados.
2. **Given** que existen empleados que cumplen criterio de búsqueda, **When** se aplica filtro por campos permitidos, **Then** el sistema devuelve solo los registros que cumplan el filtro.

---

### Edge Cases

- ¿Qué pasa cuando se intenta crear un empleado con una `clave` ya existente?
- ¿Qué pasa cuando la `clave` no cumple el formato `E-001` (prefijo `E-` + 3 dígitos)?
- ¿Qué pasa cuando `nombre`, `direccion` o `telefono` no tienen exactamente 100 caracteres?
- ¿Cómo responde el sistema cuando se consulta, actualiza o elimina una `clave` inexistente?
- ¿Qué pasa cuando alguno de los campos requeridos llega vacío o nulo?
- ¿Qué pasa cuando dos usuarios intentan actualizar el mismo empleado al mismo tiempo?
- ¿Qué pasa cuando se intenta crear un empleado después de alcanzar `E-999`?
- ¿Qué pasa con el consecutivo cuando se elimina físicamente un empleado?
- ¿Qué pasa si en `POST` el cliente intenta enviar `clave` manualmente?
- ¿Qué pasa si en `PUT` falta `version` o llega inválida?
- ¿Qué pasa cuando los parámetros de paginación son inválidos (`page`, `size`)?
- ¿Qué pasa cuando un filtro no devuelve resultados?
- ¿Qué pasa cuando `sort` tiene un valor inválido o campo no permitido?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir crear empleados con los campos obligatorios `nombre`, `direccion` y `telefono`, generando automáticamente la `clave`.
- **FR-002**: El sistema MUST usar `clave` como identificador único y llave primaria con formato `E-001` (prefijo `E-` + 3 dígitos con ceros a la izquierda).
- **FR-003**: El sistema MUST permitir consultar empleados por `clave` y devolver sus datos completos.
- **FR-004**: El sistema MUST permitir actualizar `nombre`, `direccion` y `telefono` de un empleado existente identificado por `clave`.
- **FR-005**: El sistema MUST permitir eliminar físicamente un empleado existente por `clave`.
- **FR-006**: El sistema MUST validar que `nombre`, `direccion` y `telefono` tengan exactamente 100 caracteres cada uno.
- **FR-007**: El sistema MUST rechazar operaciones con datos inválidos y devolver mensajes claros de validación.
- **FR-008**: El sistema MUST garantizar unicidad de `clave` generada automáticamente, incluso en creación concurrente.
- **FR-009**: El sistema MUST rechazar la actualización cuando detecte cambio concurrente del mismo registro y responder con conflicto de concurrencia (409).
- **FR-010**: El sistema MUST limitar la generación de `clave` al rango `E-001` a `E-999` y rechazar nuevas altas al agotarse el consecutivo con `409 Conflict`.
- **FR-011**: El sistema MUST NO reutilizar claves de empleados eliminados físicamente; el consecutivo de `clave` MUST avanzar de forma monótona.
- **FR-012**: El sistema MUST rechazar solicitudes de alta que incluyan `clave` en el payload; en creación la `clave` MUST generarse exclusivamente en backend.
- **FR-013**: El sistema MUST exigir el campo `version` en el body de `PUT` para control de concurrencia; si falta o es inválido, MUST rechazar la solicitud como dato inválido.
- **FR-014**: El sistema MUST responder `400 Bad Request` para cualquier error de validación de entrada (longitudes, formato de campos, `version` faltante/inválido, o payload inválido).
- **FR-015**: El sistema MUST responder `404 Not Found` cuando la `clave` no exista en operaciones `GET`, `PUT` y `DELETE`.
- **FR-016**: El sistema MUST responder `201 Created` en altas exitosas e incluir en el body la representación completa del empleado creado (`clave`, `nombre`, `direccion`, `telefono`, `version`).
- **FR-017**: El sistema MUST exponer un endpoint de listado de empleados con paginación.
- **FR-018**: El sistema MUST permitir filtrar el listado por campos permitidos del empleado.
- **FR-021**: El sistema MUST soportar filtros de listado por `nombre` (coincidencia parcial) y por `clave` (coincidencia exacta).
- **FR-022**: El endpoint de listado MUST usar `page=0` y `size=20` por defecto, y MUST rechazar valores de `size` mayores a `100` con `400 Bad Request`.
- **FR-023**: El endpoint de listado MUST ordenar por defecto por `clave` en forma ascendente.
- **FR-024**: El endpoint de listado MUST permitir parámetro `sort` únicamente para `clave` con dirección `asc` o `desc`; valores inválidos MUST responder `400 Bad Request`.
- **FR-025**: El endpoint de listado MUST responder en formato paginado tipo Spring Page con los campos `content`, `number`, `size`, `totalElements`, `totalPages`, `first` y `last`.
- **FR-019**: El sistema MUST validar parámetros de paginación y responder `400 Bad Request` cuando sean inválidos.
- **FR-020**: El sistema MUST responder exitosamente con lista vacía cuando un filtro válido no tenga coincidencias.

### Constitution Alignment *(mandatory)*

- **CA-001**: La feature MUST permanecer dentro del alcance backend definido por el proyecto.
- **CA-002**: La feature MUST respetar el esquema de autenticación definido por la gobernanza vigente.
- **CA-003**: La feature MUST documentar impacto en persistencia, contrato de API y ejecución en contenedor cuando aplique.

### Key Entities *(include if feature involves data)*

- **Empleado**: Representa a una persona registrada en el padrón; atributos clave: `clave` (PK, única, formato `E-001`: prefijo `E-` + 3 dígitos con ceros a la izquierda), `nombre`, `direccion`, `telefono`.

## Assumptions

- La `clave` se genera automáticamente por el sistema en formato `E-001` (prefijo `E-` + 3 dígitos con ceros a la izquierda).
- Los campos `nombre`, `direccion` y `telefono` son obligatorios.
- El módulo opera sobre un único catálogo de empleados sin jerarquías adicionales.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de operaciones de alta con datos válidos se completan exitosamente en la primera ejecución.
- **SC-002**: El 100% de intentos de alta con `clave` duplicada son rechazados con mensaje de negocio claro.
- **SC-003**: El 100% de operaciones sobre `clave` inexistente (consulta, actualización, eliminación) regresan resultado controlado y comprensible.
- **SC-004**: El 100% de validaciones de longitud para `nombre`, `direccion` y `telefono` exigen exactamente 100 caracteres.
- **SC-005**: El 100% de validaciones sobre `clave` exigen formato `E-001` (prefijo `E-` + 3 dígitos con ceros a la izquierda).
- **SC-006**: El 100% de intentos de actualización concurrente detectados son rechazados con resultado de conflicto controlado (409).
- **SC-007**: El 100% de intentos de alta posteriores al agotamiento en `E-999` son rechazados con `409 Conflict` y mensaje de capacidad agotada.
- **SC-008**: El 100% de nuevas altas posteriores a bajas físicas generan una `clave` nueva no reutilizada.
- **SC-009**: El 100% de solicitudes de alta que incluyan `clave` son rechazadas con un mensaje de validación claro.
- **SC-010**: El 100% de solicitudes `PUT` sin `version` válido son rechazadas como inválidas.
- **SC-011**: El 100% de errores de validación de entrada responden con `400 Bad Request` y mensaje claro.
- **SC-012**: El 100% de operaciones `GET`, `PUT` y `DELETE` con `clave` inexistente responden con `404 Not Found`.
- **SC-013**: El 100% de altas exitosas responden con `201 Created` y body completo del recurso creado.
- **SC-014**: El 100% de consultas de listado con paginación válida responden exitosamente con estructura paginada consistente.
- **SC-015**: El 100% de filtros válidos aplicados al listado retornan únicamente resultados que cumplen el criterio.
- **SC-017**: El 100% de filtros por `nombre` usan coincidencia parcial y el 100% de filtros por `clave` usan coincidencia exacta.
- **SC-018**: El 100% de listados sin parámetros usan `page=0` y `size=20`, y el 100% de solicitudes con `size>100` son rechazadas con `400 Bad Request`.
- **SC-019**: El 100% de listados sin parámetro de orden retornan resultados ordenados por `clave` ascendente.
- **SC-020**: El 100% de solicitudes con `sort=asc|desc` sobre `clave` ordenan correctamente, y el 100% de `sort` inválidos responden `400 Bad Request`.
- **SC-021**: El 100% de respuestas de listado incluyen la estructura paginada tipo Spring Page con campos obligatorios completos.
- **SC-016**: El 100% de consultas de listado con paginación inválida responden con `400 Bad Request`.
