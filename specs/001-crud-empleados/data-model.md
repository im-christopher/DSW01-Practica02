# Data Model: CRUD de Empleados

## Entidad: Empleado

### Campos
- `clave` (string, PK)
  - Formato: `E-001` (`E-` + 3 dígitos)
  - Longitud exacta: 5
  - Restricciones: no nulo, único
- `nombre` (string)
  - Longitud exacta: 100
  - Restricciones: no nulo
- `direccion` (string)
  - Longitud exacta: 100
  - Restricciones: no nulo
- `telefono` (string)
  - Longitud exacta: 100
  - Restricciones: no nulo
- `version` (integer/long)
  - Uso: control de concurrencia optimista
  - Restricciones: no nulo

## Reglas de Validación
- `clave` MUST cumplir regex `^E-\d{3}$`.
- En altas, `clave` MUST generarse solo en backend y no aceptarse en payload.
- El consecutivo de `clave` MUST ser monótono, sin reutilización tras bajas físicas.
- Al llegar a `E-999`, nuevas altas MUST fallar con `409`.
- `nombre`, `direccion` y `telefono` MUST tener longitud exacta de 100 caracteres.
- Filtros de listado permitidos: `nombre` (contains) y `clave` (exact).

## Relaciones
- No existen relaciones con otras entidades en este alcance.

## Transiciones de Estado
- `NoExiste` -> `Activo` (alta)
- `Activo` -> `Activo` (actualización válida)
- `Activo` -> `Eliminado` (baja física; el registro deja de existir)

## Reglas de Concurrencia
- La actualización MUST validar `version`.
- Si la versión enviada no coincide con la persistida, la operación MUST fallar con conflicto (`409`).

## Reglas de Listado
- Paginación por defecto: `page=0`, `size=20`; `size` máximo permitido: `100`.
- Orden por defecto: `clave` ascendente.
- `sort` permitido únicamente sobre `clave` con dirección `asc|desc`.
