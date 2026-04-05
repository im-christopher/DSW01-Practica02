# Feature Specification: Empleados Con Correo Y Password

**Feature Branch**: `001-number`  
**Created**: 2026-03-11  
**Status**: Draft  
**Input**: User description: "que los empleados tengan un correo y una contraseña con la cual puedan iniciar sesion"

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

### User Story 1 - Registrar Empleado Con Correo Y Password (Priority: P1)

Como administrador, necesito registrar empleados con correo y contraseña para que cada empleado tenga credenciales únicas de acceso.

**Why this priority**: Sin correo y contraseña no existe base para autenticación de empleados.

**Independent Test**: Puede validarse creando un empleado con correo y contraseña, verificando que el empleado queda registrado y que la contraseña no se expone en respuestas.

**Acceptance Scenarios**:

1. **Given** un nuevo empleado, **When** el administrador lo crea con correo válido y contraseña válida, **Then** el sistema registra el empleado y devuelve sus datos sin exponer la contraseña.
2. **Given** un correo ya registrado, **When** se intenta crear otro empleado con el mismo correo, **Then** el sistema rechaza la operación con error de conflicto.
3. **Given** una contraseña inválida por política mínima, **When** se intenta crear el empleado, **Then** el sistema rechaza la solicitud con error de validación.

---

### User Story 2 - Iniciar Sesion Con Correo Y Password (Priority: P2)

Como empleado, necesito iniciar sesión con mi correo y contraseña para acceder al sistema de forma segura.

**Why this priority**: Es el objetivo funcional principal visible para el empleado una vez registradas las credenciales.

**Independent Test**: Puede validarse autenticando con credenciales correctas e incorrectas, verificando códigos de respuesta y entrega de token/sesión válida en caso exitoso.

**Acceptance Scenarios**:

1. **Given** un empleado con credenciales válidas, **When** inicia sesión con correo y contraseña correctos, **Then** el sistema autentica y devuelve un token/sesión válida.
2. **Given** un empleado existente, **When** inicia sesión con contraseña incorrecta, **Then** el sistema rechaza con error de autenticación sin revelar datos sensibles.
3. **Given** un correo inexistente, **When** se intenta iniciar sesión, **Then** el sistema rechaza con error de autenticación.

---

### User Story 3 - Gestionar Password De Empleado (Priority: P3)

Como empleado autenticado, necesito cambiar mi contraseña para mantener la seguridad de mi cuenta.

**Why this priority**: Mejora la seguridad operativa, pero depende de que ya exista registro e inicio de sesión.

**Independent Test**: Puede validarse intentando cambiar contraseña con la contraseña actual correcta e incorrecta, y verificando que luego solo la nueva contraseña permite acceso.

**Acceptance Scenarios**:

1. **Given** un empleado autenticado, **When** solicita cambio de contraseña con contraseña actual válida, **Then** el sistema actualiza la contraseña y conserva la cuenta activa.
2. **Given** un empleado autenticado, **When** intenta cambiar contraseña con contraseña actual inválida, **Then** el sistema rechaza la operación.
3. **Given** una contraseña nueva que no cumple política mínima, **When** se intenta guardar, **Then** el sistema responde con validación fallida.

---

### Edge Cases

- ¿Qué ocurre si el correo tiene formato inválido? El sistema debe rechazar la solicitud con error de validación.
- ¿Qué ocurre si el correo cambia de mayúsculas/minúsculas (ej. User@Mail.com vs user@mail.com)? El sistema debe tratarlo como el mismo correo para evitar duplicados funcionales.
- ¿Qué ocurre si un empleado intenta iniciar sesión múltiples veces con credenciales inválidas? El sistema debe aplicar política de protección contra abuso.
- ¿Qué ocurre si un empleado está inactivo? El inicio de sesión debe ser rechazado aunque las credenciales sean correctas.
- ¿Qué ocurre si se intenta reutilizar la contraseña actual al cambiarla? El sistema debe poder rechazar o advertir según política definida por negocio.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE permitir registrar empleados con un campo `correo` obligatorio y único.
- **FR-002**: El sistema DEBE validar que el correo tenga formato válido antes de persistirlo.
- **FR-003**: El sistema DEBE almacenar la contraseña de empleado de forma segura y nunca en texto plano.
- **FR-004**: El sistema DEBE exigir una política mínima de contraseña para alta y cambio (mínimo 8 caracteres, al menos una letra y un número).
- **FR-005**: El sistema DEBE exponer un endpoint de inicio de sesión para empleados usando correo y contraseña.
- **FR-006**: El sistema DEBE devolver token o sesión válida tras autenticación exitosa.
- **FR-007**: El sistema DEBE rechazar autenticaciones inválidas sin revelar si falló correo o contraseña.
- **FR-008**: El sistema DEBE impedir correos duplicados al crear o actualizar empleados.
- **FR-009**: El sistema DEBE permitir cambio de contraseña para empleados autenticados verificando contraseña actual.
- **FR-010**: El sistema DEBE registrar eventos de autenticación relevantes (éxito, fallo, cambio de contraseña) para auditoría.

### Constitution Alignment *(mandatory)*

- **CA-001**: La funcionalidad DEBE mantenerse en alcance backend y no depender de una interfaz de usuario específica.
- **CA-002**: La funcionalidad DEBE respetar las políticas de control de acceso vigentes salvo excepciones aprobadas.
- **CA-003**: La funcionalidad DEBE preservar la integridad de datos y documentar impacto en datos existentes.
- **CA-004**: La funcionalidad DEBE mantener ejecución local reproducible para validación funcional.
- **CA-005**: La funcionalidad DEBE mantener documentación funcional actualizada de los contratos expuestos.
- **CA-006**: La funcionalidad DEBE mantener versionado claro de interfaces externas e identificar impacto de cambios rompientes.

### Key Entities *(include if feature involves data)*

- **Empleado**: Representa al usuario de negocio con datos personales y credenciales. Incluye clave, nombre, dirección, teléfono, correo único, estado activo y metadatos de credencial.
- **CredencialEmpleado**: Representa información de autenticación asociada al empleado (hash de contraseña, fecha de último cambio, estado de validez).
- **EventoAutenticacion**: Representa un registro auditable de intentos y cambios de autenticación (tipo de evento, empleado, fecha/hora, resultado).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de empleados nuevos pueden registrarse con correo único y contraseña válida sin errores de consistencia de datos.
- **SC-002**: El 95% de intentos de inicio de sesión válidos se completan en menos de 2 segundos.
- **SC-003**: El 100% de intentos de inicio de sesión con credenciales inválidas reciben respuesta controlada sin fuga de información sensible.
- **SC-004**: El 100% de respuestas públicas de empleados omiten contraseña o hash de contraseña.
- **SC-005**: Al menos el 99% de los cambios de contraseña válidos se aplican exitosamente en el primer intento.

## Assumptions

- Se asume que la autenticación inicial de empleados coexistirá con el esquema de autenticación administrativa existente.
- Se asume que la recuperación de contraseña por correo queda fuera del alcance inicial.
- Se asume que el correo del empleado es su identificador de login único.
- Se asume que la cuenta de empleado inactiva no puede iniciar sesión.
