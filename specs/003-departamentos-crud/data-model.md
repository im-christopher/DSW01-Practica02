# Data Model: Departamentos CRUD

**Feature**: 003-departamentos-crud  
**Generated**: Phase 1 of planning workflow  
**Dependencies**: `research.md` (soft delete, no versioning, pagination decisions)

## Overview

Este documento define las entidades, relaciones, validaciones y esquema de base de datos para la funcionalidad de departamentos. El modelo extiende la tabla existente de empleados con una relación many-to-one hacia departamentos.

---

## Entity Definitions

### Departamento Entity

**Purpose**: Representa una unidad organizacional dentro de la empresa.

**Java Entity** (package: `com.dsw01.practica02.departamentos.domain`):

```java
@Entity
@Table(name = "departamentos")
public class Departamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del departamento es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Standard constructors, getters, setters
}
```

**Field Details**:

| Field | Type | Nullable | Default | Constraint | Purpose |
|-------|------|----------|---------|------------|---------|
| `id` | Long (BIGINT) | No | Auto-generated | PRIMARY KEY | Identificador único |
| `nombre` | String (VARCHAR) | No | - | UNIQUE, max 100 chars | Nombre del departamento |
| `activo` | Boolean | No | `true` | - | Soft delete flag |

**Business Rules**:
- `nombre` debe ser único en toda la tabla (validado por constraint UNIQUE en DB)
- `nombre` no puede estar vacío ni contener solo espacios (validado por `@NotBlank` + trim)
- `activo` siempre es `true` al crear, y se marca `false` solo mediante soft delete
- No tiene campo `version` (decisión de no usar optimistic locking)

---

### Empleado Entity Extension

**Purpose**: Extender la entidad existente `Empleado` para incluir relación opcional con departamento.

**Modificaciones** (package: `com.dsw01.practica02.empleados.domain`):

```java
@Entity
@Table(name = "empleados")
public class Empleado {
    // ... campos existentes (codigo, nombre, direccion, telefono, version, activo)
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "departamento_id", nullable = true, foreignKey = @ForeignKey(name = "fk_empleado_departamento"))
    private Departamento departamento;
    
    // Getter/Setter para departamento
}
```

**New Field Details**:

| Field | Type | Nullable | Constraint | Purpose |
|-------|------|----------|------------|---------|
| `departamento` | Departamento (FK) | Yes | FK to `departamentos.id` with ON DELETE RESTRICT | Relación many-to-one |

**Business Rules**:
- Un empleado puede tener 0 o 1 departamento asignado (nullable FK)
- Múltiples empleados pueden pertenecer al mismo departamento (many-to-one)
- No se puede eliminar un departamento que tenga empleados asignados (ON DELETE RESTRICT enforces this)
- El fetch es LAZY para evitar N+1 queries en listados de empleados

---

## Relationships

### Empleado → Departamento (Many-to-One)

**Cardinality**: N empleados : 1 departamento (o 0 si no asignado)

**Foreign Key**:
- Table: `empleados`
- Column: `departamento_id` (BIGINT)
- References: `departamentos.id`
- On Delete: `RESTRICT` (prevent deletion of departamento with assigned employees)
- On Update: `CASCADE` (automatic ID update if departamento ID changes, unlikely)

**Query Patterns**:
- Obtener empleados de un departamento: `SELECT * FROM empleados WHERE departamento_id = :id AND activo = true`
- Verificar si departamento tiene empleados: `SELECT COUNT(*) FROM empleados WHERE departamento_id = :id AND activo = true`
- Obtener departamento de un empleado: Join automático via JPA `empleado.getDepartamento()`

---

## Database Schema

### Table: `departamentos`

```sql
CREATE TABLE departamentos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_departamentos_nombre ON departamentos(nombre);
CREATE INDEX idx_departamentos_activo ON departamentos(activo);
```

**Indexes**:
- `idx_departamentos_nombre`: Optimiza búsquedas alfabéticas y validación de unicidad
- `idx_departamentos_activo`: Optimiza queries que filtran por departamentos activos

---

### Table: `empleados` (alteración)

```sql
ALTER TABLE empleados 
ADD COLUMN departamento_id BIGINT NULL;

ALTER TABLE empleados
ADD CONSTRAINT fk_empleado_departamento 
FOREIGN KEY (departamento_id) 
REFERENCES departamentos(id) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;

CREATE INDEX idx_empleados_departamento_id ON empleados(departamento_id);
```

**New Column**: `departamento_id` (BIGINT, nullable)

**New Index**: `idx_empleados_departamento_id` optimiza queries de filtrado por departamento

---

## Flyway Migration

**File**: `src/main/resources/db/migration/V3__create_departamentos_table.sql`

```sql
-- V3: Create departamentos table and add relationship to empleados

-- Create departamentos table
CREATE TABLE departamentos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT true
);

-- Create indexes for performance
CREATE INDEX idx_departamentos_nombre ON departamentos(nombre);
CREATE INDEX idx_departamentos_activo ON departamentos(activo);

-- Add departamento_id column to empleados table
ALTER TABLE empleados 
ADD COLUMN departamento_id BIGINT NULL;

-- Add foreign key constraint with restrict on delete
ALTER TABLE empleados
ADD CONSTRAINT fk_empleado_departamento 
FOREIGN KEY (departamento_id) 
REFERENCES departamentos(id) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;

-- Create index for foreign key lookups
CREATE INDEX idx_empleados_departamento_id ON empleados(departamento_id);

-- Optional: Insert sample departamentos for testing
-- INSERT INTO departamentos (nombre, activo) VALUES 
--     ('Tecnología', true),
--     ('Recursos Humanos', true),
--     ('Finanzas', true);
```

**Migration Strategy**:
- Ejecutar durante startup de Spring Boot (Flyway auto-runs)
- La migration es idempotente si se ejecuta múltiples veces en desarrollo (usar Flyway repair si necesario)
- Los empleados existentes quedan con `departamento_id = NULL` (sin departamento asignado)

---

## Validation Rules

### Domain-Level (Entity)

| Field | Validation | Message |
|-------|-----------|---------|
| `nombre` | `@NotBlank` | "El nombre del departamento es obligatorio" |
| `nombre` | `@Size(max=100)` | "El nombre no puede exceder 100 caracteres" |
| `activo` | Non-null | Implícito via `@Column(nullable = false)` |

### Service-Level (Business Logic)

| Operation | Validation | Response on Failure |
|-----------|-----------|---------------------|
| Crear departamento | Nombre único | 400 Bad Request (duplicate) |
| Actualizar departamento | Nombre único (excluyendo el mismo departamento) | 400 Bad Request |
| Eliminar departamento | No tiene empleados asignados | 409 Conflict (`DepartamentoConEmpleadosException`) |
| Eliminar departamento | Departamento existe y está activo | 404 Not Found (`DepartamentoNotFoundException`) |
| Asignar empleado a departamento | Departamento existe y está activo | 404 Not Found |

---

## DTOs (Data Transfer Objects)

### DepartamentoCreateRequest

**Purpose**: Payload para crear un nuevo departamento

```java
public class DepartamentoCreateRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
}
```

---

### DepartamentoUpdateRequest

**Purpose**: Payload para actualizar un departamento existente (sin control de versión)

```java
public class DepartamentoUpdateRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
}
```

---

### DepartamentoResponse

**Purpose**: Respuesta con datos completos de un departamento

```java
public class DepartamentoResponse {
    private Long id;
    private String nombre;
    private Boolean activo;
}
```

---

### DepartamentoPageResponse

**Purpose**: Respuesta paginada para listado de departamentos

```java
public class DepartamentoPageResponse {
    private List<DepartamentoResponse> content;
    private int totalPages;
    private long totalElements;
    private int number;  // current page
    private int size;    // page size
}
```

---

### DepartamentoSummaryDTO

**Purpose**: Subset de campos para incluir en respuestas de empleados (solo id + nombre)

```java
public class DepartamentoSummaryDTO {
    private Long id;
    private String nombre;
}
```

---

### EmpleadoCreateRequest / EmpleadoUpdateRequest (extensión)

**Purpose**: Agregar campo opcional `departamentoId` para asignar empleado a departamento

```java
public class EmpleadoCreateRequest {
    // ... campos existentes (nombre, direccion, telefono)
    
    @Nullable
    private Long departamentoId;  // Optional: assign to departamento
}

public class EmpleadoUpdateRequest {
    // ... campos existentes
    
    @Nullable
    private Long departamentoId;  // Optional: null to unassign
}
```

---

### EmpleadoResponse (extensión)

**Purpose**: Incluir información del departamento en respuestas de empleados

```java
public class EmpleadoResponse {
    // ... campos existentes (codigo, nombre, direccion, telefono, version)
    
    @Nullable
    private DepartamentoSummaryDTO departamento;  // Only id and nombre
}
```

---

## State Transitions

### Departamento Lifecycle

```
[NEW] --create()--> [ACTIVE (activo=true)]
                          |
                          |--update()--> [ACTIVE (activo=true, nombre cambiado)]
                          |
                          |--eliminar()--> [INACTIVE (activo=false)]
                                              (solo si no tiene empleados)
```

**States**:
- **NEW**: No persisted, solo en memoria antes de save()
- **ACTIVE**: `activo = true`, visible en queries, puede tener empleados asignados
- **INACTIVE**: `activo = false`, excluido de queries normales, no puede tener empleados (validado antes de eliminar)

**Transitions**:
- `create()`: NEW → ACTIVE
- `update()`: ACTIVE → ACTIVE (solo cambia nombre)
- `eliminar()`: ACTIVE → INACTIVE (soft delete, validado que no tenga empleados)

**Irreversible Transitions**:
- No hay endpoint para reactivar un departamento eliminado (requeriría feature adicional)

---

## Query Examples

### 1. List Active Departamentos Sorted by Nombre

```java
// Repository
Page<Departamento> findByActivoTrue(Pageable pageable);

// Service
Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
Page<Departamento> departamentos = departamentoRepository.findByActivoTrue(pageable);
```

---

### 2. Count Empleados in Departamento

```java
// Repository method in EmpleadoRepository
long countByDepartamentoIdAndActivoTrue(Long departamentoId);

// Service usage
long count = empleadoRepository.countByDepartamentoIdAndActivoTrue(departamentoId);
if (count > 0) {
    throw new DepartamentoConEmpleadosException(
        "No se puede eliminar el departamento porque tiene " + count + " empleados asignados"
    );
}
```

---

### 3. Get Empleados by Departamento

```java
// Repository method in EmpleadoRepository
Page<Empleado> findByDepartamentoIdAndActivoTrue(Long departamentoId, Pageable pageable);

// Service usage
Page<Empleado> empleados = empleadoRepository.findByDepartamentoIdAndActivoTrue(
    departamentoId, 
    pageable
);
```

---

## Data Integrity Constraints

| Constraint | Type | Enforced By | Impact |
|------------|------|-------------|--------|
| Nombre único | UNIQUE | PostgreSQL constraint | Prevent duplicate departamento names |
| FK Restrict | ON DELETE RESTRICT | PostgreSQL FK | Prevent deletion of departamento with employees |
| Not Null | NOT NULL | PostgreSQL + JPA validation | Ensure required fields always have values |
| Length limit | VARCHAR(100) | PostgreSQL + JPA `@Size` | Prevent excessively long department names |

---

## Performance Considerations

1. **Indexes**:
   - `idx_departamentos_nombre`: Speeds up alphabetical sorting and uniqueness checks
   - `idx_departamentos_activo`: Optimizes filtering of active departments
   - `idx_empleados_departamento_id`: Optimizes joins and filters by departamento

2. **Fetch Strategy**:
   - `@ManyToOne(fetch = FetchType.LAZY)` in Empleado: Prevents N+1 queries when listing employees without needing departamento data
   - Explicit fetch join when departamento info is needed: `JOIN FETCH e.departamento`

3. **Expected Load**:
   - Small number of departamentos (estimated < 100)
   - Queries typically return all active departamentos in one page
   - No complex aggregations or reporting queries initially

---

## Migration Rollback Strategy

If V3 migration needs to be reverted:

```sql
-- Rollback V3 (manual, Flyway doesn't auto-rollback)
ALTER TABLE empleados DROP CONSTRAINT IF EXISTS fk_empleado_departamento;
ALTER TABLE empleados DROP COLUMN IF EXISTS departamento_id;
DROP INDEX IF EXISTS idx_empleados_departamento_id;
DROP TABLE IF EXISTS departamentos;
```

**Note**: This should only be done in development. In production, use forward-only migrations (e.g., V4 to fix V3 issues).
