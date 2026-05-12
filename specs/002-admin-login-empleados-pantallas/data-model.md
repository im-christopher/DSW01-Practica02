# Data Model - 002-admin-login-empleados-pantallas

## 1) SesionAdmin
- Purpose: Represents authenticated admin state in frontend runtime.
- Fields:
  - username: string (required)
  - authenticated: boolean (required)
  - authMode: enum(`basic`) (required)
  - createdAt: datetime (required)
- Validation rules:
  - `authenticated=true` requires non-empty `username`.
  - Persisted storage is not allowed; lifecycle is runtime memory only.
- State transitions:
  - anonymous -> authenticated (successful login check)
  - authenticated -> anonymous (logout, tab close, page reload)

## 2) CredencialAdminInput
- Purpose: Login input payload.
- Fields:
  - username: string (required)
  - password: string (required)
- Validation rules:
  - username and password cannot be blank.
  - Failed auth must not disclose whether username exists.

## 3) EmpleadoRecord
- Purpose: Backend employee aggregate exposed to UI.
- Fields:
  - clave: string (`E-###`, primary identifier)
  - nombre: string (required, max 100)
  - direccion: string (required, max 100)
  - telefono: string (required, min 10)
  - version: long (required, optimistic locking)
  - activo: boolean (required for logical delete support)
- Validation rules:
  - `clave` must satisfy format `E-\\d{3}`.
  - `activo=false` records are excluded from default listing.

## 4) EmpleadoFormInput
- Purpose: UI payload for create/edit operations.
- Fields:
  - nombre: string
  - direccion: string
  - telefono: string
  - version: long (edit only)
- Validation rules:
  - create: `version` omitted.
  - edit: `version` required.

## 5) EmpleadoListQuery
- Purpose: Filter and pagination query model for employee listing.
- Fields:
  - page: int (>=0)
  - size: int (1..100)
  - nombre: string (optional)
  - clave: string (`E-###`, optional)
  - sort: enum(`asc`,`desc`)
  - includeInactive: boolean (optional, default false)
- Validation rules:
  - invalid query values return validation error response.

## 6) ConflictoEdicion
- Purpose: Represents optimistic locking conflict details for UI recovery flow.
- Fields:
  - code: string (`VERSION_CONFLICT`)
  - message: string
  - current: EmpleadoRecord (server latest)
- State transitions:
  - edit_submitted -> conflict_detected (HTTP 409)
  - conflict_detected -> editing_with_latest (after reload current)
  - editing_with_latest -> edit_submitted (admin reapplies and retries)
