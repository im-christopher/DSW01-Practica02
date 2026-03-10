# Quickstart Guide: Departamentos CRUD

**Feature**: 003-departamentos-crud  
**Target Audience**: Developers setting up local environment  
**Prerequisites**: Docker, Docker Compose, curl or PowerShell

---

## Local Setup

### 1. Start Services with Docker Compose

```bash
cd DSW01-Practica02
docker-compose up -d
```

**Expected output**:
```
Creating network "dsw01-practica02_default" with the default driver
Creating pg-dsw ... done
Creating empleados-app ... done
```

**Verification**:
```bash
docker ps
```

You should see two containers running:
- `pg-dsw` (PostgreSQL on port 5432)
- `empleados-app` (Spring Boot on port 8080)

---

### 2. Verify Database Migration

Check Flyway logs to confirm V3 migration ran successfully:

```bash
docker logs empleados-app | grep -i flyway
```

**Expected output**:
```
Flyway Community Edition 9.x.x ...
Successfully validated 3 migrations
Current version of schema "public": 3
Migrating schema "public" to version "3 - create departamentos table"
Successfully applied 1 migration to schema "public"
```

---

### 3. Access Swagger UI

Open browser to `http://localhost:8080/swagger-ui.html`

**Expected**: Swagger UI should display:
- `Departamentos` tag with 5 endpoints (POST, GET, GET /{id}, PUT /{id}, DELETE /{id})
- `Empleados` tag with extended GET endpoint (now supports `departamentoId` filter)

---

## Testing Scenarios

### Authentication

All endpoints require HTTP Basic Auth with credentials:
- **Username**: `admin`
- **Password**: `admin123`

**PowerShell**:
```powershell
$auth = [System.Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes("admin:admin123"))
$headers = @{
    "Authorization" = "Basic $auth"
    "Content-Type" = "application/json"
}
```

**curl**:
```bash
curl -u admin:admin123 -H "Content-Type: application/json" ...
```

---

### Scenario 1: CRUD Departamentos (US1)

#### 1.1 Crear Departamento

**PowerShell**:
```powershell
$body = @{nombre = "Tecnología"} | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/departamentos" `
    -Method POST `
    -Headers $headers `
    -Body $body
```

**curl**:
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/departamentos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Tecnología"}'
```

**Expected Response** (201 Created):
```json
{
  "id": 1,
  "nombre": "Tecnología",
  "activo": true
}
```

---

#### 1.2 Obtener Departamento por ID

**PowerShell**:
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/departamentos/1" `
    -Method GET `
    -Headers $headers
```

**curl**:
```bash
curl -u admin:admin123 http://localhost:8080/api/v1/departamentos/1
```

**Expected Response** (200 OK):
```json
{
  "id": 1,
  "nombre": "Tecnología",
  "activo": true
}
```

---

#### 1.3 Listar Departamentos (Paginado, Alfabético)

**PowerShell**:
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/departamentos?page=0&size=20&sort=nombre,asc" `
    -Method GET `
    -Headers $headers
```

**curl**:
```bash
curl -u admin:admin123 "http://localhost:8080/api/v1/departamentos?page=0&size=20&sort=nombre,asc"
```

**Expected Response** (200 OK):
```json
{
  "content": [
    {
      "id": 3,
      "nombre": "Finanzas",
      "activo": true
    },
    {
      "id": 2,
      "nombre": "Recursos Humanos",
      "activo": true
    },
    {
      "id": 1,
      "nombre": "Tecnología",
      "activo": true
    }
  ],
  "totalElements": 3,
  "totalPages": 1,
  "number": 0,
  "size": 20
}
```

**Note**: Ordered alphabetically by `nombre` (Finanzas, Recursos Humanos, Tecnología)

---

#### 1.4 Actualizar Departamento

**PowerShell**:
```powershell
$body = @{nombre = "TI y Sistemas"} | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/departamentos/1" `
    -Method PUT `
    -Headers $headers `
    -Body $body
```

**curl**:
```bash
curl -u admin:admin123 -X PUT http://localhost:8080/api/v1/departamentos/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"TI y Sistemas"}'
```

**Expected Response** (200 OK):
```json
{
  "id": 1,
  "nombre": "TI y Sistemas",
  "activo": true
}
```

**Note**: No `version` field required (no optimistic locking)

---

#### 1.5 Eliminar Departamento (Soft Delete)

**PowerShell**:
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/departamentos/1" `
    -Method DELETE `
    -Headers $headers
```

**curl**:
```bash
curl -u admin:admin123 -X DELETE http://localhost:8080/api/v1/departamentos/1
```

**Expected Response** (204 No Content)

**Verification**: List departamentos again - ID 1 should not appear (filtered as `activo=false`)

---

### Scenario 2: Asignar Empleados a Departamentos (US2)

#### 2.1 Crear Empleado con Departamento

**PowerShell**:
```powershell
$body = @{
    nombre = "Juan Pérez"
    direccion = "Calle 123"
    telefono = "1234567890"
    departamentoId = 1
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/empleados" `
    -Method POST `
    -Headers $headers `
    -Body $body
```

**curl**:
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/empleados \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Juan Pérez",
    "direccion":"Calle 123",
    "telefono":"1234567890",
    "departamentoId":1
  }'
```

**Expected Response** (201 Created):
```json
{
  "codigo": "E-001",
  "nombre": "Juan Pérez",
  "direccion": "Calle 123",
  "telefono": "1234567890",
  "version": 0,
  "departamento": {
    "id": 1,
    "nombre": "Tecnología"
  }
}
```

**Note**: Response includes nested `departamento` object with only `id` and `nombre`

---

#### 2.2 Actualizar Empleado para Asignar Departamento

**PowerShell**:
```powershell
$body = @{
    nombre = "Juan Pérez"
    direccion = "Calle 123"
    telefono = "1234567890"
    departamentoId = 2
    version = 0
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/empleados/E-001" `
    -Method PUT `
    -Headers $headers `
    -Body $body
```

**curl**:
```bash
curl -u admin:admin123 -X PUT http://localhost:8080/api/v1/empleados/E-001 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Juan Pérez",
    "direccion":"Calle 123",
    "telefono":"1234567890",
    "departamentoId":2,
    "version":0
  }'
```

**Expected Response** (200 OK):
```json
{
  "codigo": "E-001",
  "nombre": "Juan Pérez",
  "direccion": "Calle 123",
  "telefono": "1234567890",
  "version": 1,
  "departamento": {
    "id": 2,
    "nombre": "Recursos Humanos"
  }
}
```

**Note**: `departamento` changed from ID 1 to ID 2, `version` incremented to 1

---

#### 2.3 Desasignar Empleado de Departamento

**PowerShell**:
```powershell
$body = @{
    nombre = "Juan Pérez"
    direccion = "Calle 123"
    telefono = "1234567890"
    departamentoId = $null
    version = 1
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/empleados/E-001" `
    -Method PUT `
    -Headers $headers `
    -Body $body
```

**curl**:
```bash
curl -u admin:admin123 -X PUT http://localhost:8080/api/v1/empleados/E-001 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Juan Pérez",
    "direccion":"Calle 123",
    "telefono":"1234567890",
    "departamentoId":null,
    "version":1
  }'
```

**Expected Response** (200 OK):
```json
{
  "codigo": "E-001",
  "nombre": "Juan Pérez",
  "direccion": "Calle 123",
  "telefono": "1234567890",
  "version": 2,
  "departamento": null
}
```

**Note**: `departamento` is now `null`, employee is unassigned

---

### Scenario 3: Listar Empleados por Departamento (US3)

#### 3.1 Listar Empleados de un Departamento Específico

**PowerShell**:
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/departamentos/1/empleados?page=0&size=20" `
    -Method GET `
    -Headers $headers
```

**curl**:
```bash
curl -u admin:admin123 "http://localhost:8080/api/v1/departamentos/1/empleados?page=0&size=20"
```

**Expected Response** (200 OK):
```json
{
  "content": [
    {
      "codigo": "E-001",
      "nombre": "Juan Pérez",
      "direccion": "Calle 123",
      "telefono": "1234567890",
      "version": 0,
      "departamento": {
        "id": 1,
        "nombre": "Tecnología"
      }
    },
    {
      "codigo": "E-002",
      "nombre": "María García",
      "direccion": "Av. Principal 456",
      "telefono": "0987654321",
      "version": 0,
      "departamento": {
        "id": 1,
        "nombre": "Tecnología"
      }
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "number": 0,
  "size": 20
}
```

---

#### 3.2 Filtrar Empleados por Departamento en Listado General

**PowerShell**:
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/empleados?departamentoId=1&page=0&size=20" `
    -Method GET `
    -Headers $headers
```

**curl**:
```bash
curl -u admin:admin123 "http://localhost:8080/api/v1/empleados?departamentoId=1&page=0&size=20"
```

**Expected Response**: Same as 3.1, filtered by `departamentoId=1`

---

### Scenario 4: Edge Cases

#### 4.1 Intento de Crear Departamento Duplicado

**Command**:
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/departamentos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Tecnología"}'
```

**Expected Response** (400 Bad Request):
```json
{
  "mensaje": "Ya existe un departamento con el nombre 'Tecnología'"
}
```

---

#### 4.2 Intento de Eliminar Departamento con Empleados Asignados

**Precondition**: Assign at least 1 employee to departamento ID 1

**Command**:
```bash
curl -u admin:admin123 -X DELETE http://localhost:8080/api/v1/departamentos/1
```

**Expected Response** (409 Conflict):
```json
{
  "mensaje": "No se puede eliminar el departamento porque tiene 3 empleados asignados"
}
```

---

#### 4.3 Intento de Asignar Empleado a Departamento Inexistente

**Command**:
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/empleados \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Test User",
    "direccion":"Test Address",
    "telefono":"0000000000",
    "departamentoId":999
  }'
```

**Expected Response** (404 Not Found):
```json
{
  "mensaje": "Departamento con ID 999 no encontrado"
}
```

---

## Troubleshooting

### Issue: 401 Unauthorized on All Requests

**Cause**: Missing or incorrect Basic Auth credentials

**Solution**: Verify credentials are `admin:admin123` (case-sensitive)

**PowerShell verification**:
```powershell
$auth = [System.Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes("admin:admin123"))
Write-Host $auth  # Should output: YWRtaW46YWRtaW4xMjM=
```

---

### Issue: 500 Internal Server Error on Departamento Creation

**Cause**: V3 Flyway migration did not run

**Solution**:
1. Check Flyway logs: `docker logs empleados-app | grep -i flyway`
2. Verify migration file exists: `ls src/main/resources/db/migration/V3__create_departamentos_table.sql`
3. Restart app: `docker-compose restart empleados-app`
4. If still failing, check PostgreSQL logs: `docker logs pg-dsw`

---

### Issue: Departamento Not Appearing in List After Creation

**Cause**: Departamento was soft-deleted (`activo=false`)

**Solution**: Check database directly:
```bash
docker exec -it pg-dsw psql -U postgres -d empleados_db -c "SELECT * FROM departamentos;"
```

If `activo=false`, either:
- Create a new departamento with different name
- Manually reactivate: `UPDATE departamentos SET activo=true WHERE id=X;`

---

### Issue: Nested `departamento` Object is NULL in Employee Responses

**Cause**: Employee has no departamento assigned (`departamento_id` is NULL)

**Solution**: Assign departamento via PUT `/api/v1/empleados/{codigo}` with `departamentoId` field

**Verification**:
```bash
docker exec -it pg-dsw psql -U postgres -d empleados_db -c "SELECT codigo, nombre, departamento_id FROM empleados;"
```

---

## Database Inspection

### Connect to PostgreSQL

```bash
docker exec -it pg-dsw psql -U postgres -d empleados_db
```

### Useful Queries

**List all departamentos** (including inactive):
```sql
SELECT * FROM departamentos;
```

**Count empleados per departamento**:
```sql
SELECT d.id, d.nombre, COUNT(e.codigo) as empleados_count
FROM departamentos d
LEFT JOIN empleados e ON e.departamento_id = d.id AND e.activo = true
WHERE d.activo = true
GROUP BY d.id, d.nombre
ORDER BY d.nombre;
```

**Find empleados without departamento**:
```sql
SELECT codigo, nombre FROM empleados WHERE departamento_id IS NULL AND activo = true;
```

**Verify FK constraint**:
```sql
SELECT
    tc.constraint_name,
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
  ON tc.constraint_name = kcu.constraint_name
  AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage AS ccu
  ON ccu.constraint_name = tc.constraint_name
  AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_name = 'empleados'
  AND kcu.column_name = 'departamento_id';
```

Expected output:
```
 constraint_name         | table_name | column_name      | foreign_table_name | foreign_column_name
-------------------------+------------+------------------+--------------------+---------------------
 fk_empleado_departamento| empleados  | departamento_id  | departamentos      | id
```

---

## Next Steps

After verifying all scenarios work:

1. **Run Integration Tests**: `mvn test` (verify no regressions)
2. **Update OpenAPI Docs**: Verify Swagger UI reflects all endpoints correctly
3. **Manual E2E Test**:
   - Create departamento
   - Assign 3 employees to it
   - Try to delete departamento (expect 409 Conflict)
   - Unassign all employees
   - Delete departamento (expect 204 No Content)
   - Verify departamento no longer appears in list (soft deleted)

4. **Code Review Checklist**:
   - [ ] All endpoints require Basic Auth
   - [ ] Soft delete is working (activo=false, not DELETE SQL)
   - [ ] FK constraint prevents orphaned references
   - [ ] Pagination defaults to page=0, size=20, sort=nombre,asc
   - [ ] Employee responses include nested departamento with only id+nombre
   - [ ] No version field in DepartamentoUpdateRequest
