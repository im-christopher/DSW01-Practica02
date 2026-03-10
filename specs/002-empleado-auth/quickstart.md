# Quickstart: Autenticación de Empleados

**Feature**: 002-empleado-auth  
**Date**: 2026-03-08  
**Prerequisites**: Docker, Docker Compose, Java 17, Maven

## Overview

Esta guía proporciona instrucciones rápidas para ejecutar y probar la funcionalidad de autenticación de empleados localmente.

---

## Setup Local

### 1. Iniciar Servicios

```bash
# Desde el directorio raíz del proyecto
docker-compose up -d

# Verificar que PostgreSQL está corriendo
docker-compose ps
```

### 2. Variables de Entorno

Crear archivo `.env` en raíz del proyecto (si no existe):

```bash
# PostgreSQL
POSTGRES_DB=practica02
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# JWT Secret (generar uno nuevo para producción)
JWT_SECRET=your-256-bit-secret-key-change-this-in-production

# Basic Auth (credenciales administrativas)
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
```

**⚠️ IMPORTANTE**: `JWT_SECRET` DEBE ser una cadena fuerte (mínimo 32 caracteres aleatorios). Para producción, usar generador seguro:

```bash
# Linux/Mac
openssl rand -base64 32

# PowerShell (Windows)
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 32 | % {[char]$_})
```

### 3. Compilar y Ejecutar

```bash
# Compilar proyecto
mvn clean package -DskipTests

# Ejecutar aplicación
java -jar target/practica02-0.0.1-SNAPSHOT.jar

# O usar Maven directamente
mvn spring-boot:run
```

Aplicación corriendo en: `http://localhost:8080`

---

## Testing Endpoints

### Prerequisito: Crear Empleado con Contraseña

Antes de autenticar, necesitas un empleado con contraseña establecida.

**Opción 1: Crear empleado nuevo con contraseña**

```bash
curl -X POST http://localhost:8080/api/v1/empleados \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{
    "nombre": "Juan Pérez",
    "direccion": "Calle Principal 123",
    "telefono": "555-1234",
    "password": "Password123"
  }'

# Response: 201 Created
# {
#   "clave": "E-001",
#   "nombre": "Juan Pérez",
#   "direccion": "Calle Principal 123",
#   "telefono": "555-1234",
#   "version": 0
# }
# Nota: password_hash NO se expone en response
```

**Opción 2: Agregar contraseña a empleado existente**

```bash
curl -X PUT http://localhost:8080/api/v1/empleados/E-001 \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{
    "nombre": "Juan Pérez",
    "direccion": "Calle Principal 123",
    "telefono": "555-1234",
    "password": "Password123"
  }'

# Response: 200 OK (empleado actualizado)
```

---

### 1. Login (Autenticación)

```bash
curl -X POST http://localhost:8080/api/v1/empleados/login \
  -H "Content-Type: application/json" \
  -d '{
    "clave": "E-001",
    "password": "Password123"
  }'

# Response: 200 OK
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "expiresIn": 86400,
#   "clave": "E-001"
# }
```

**Guardar el token** para usar en requests autenticados:

```bash
# Bash/Zsh
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# PowerShell
$TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### 2. Cambiar Contraseña

```bash
curl -X PUT http://localhost:8080/api/v1/empleados/E-001/password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "currentPassword": "Password123",
    "newPassword": "NewPassword456"
  }'

# Response: 204 No Content
# (sin body, cambio exitoso)
```

**⚠️ IMPORTANTE**: Después de cambiar contraseña, el token anterior queda invalidado. Debes hacer login nuevamente:

```bash
curl -X POST http://localhost:8080/api/v1/empleados/login \
  -H "Content-Type: application/json" \
  -d '{
    "clave": "E-001",
    "password": "NewPassword456"
  }'
```

---

## Escenarios de Testing

### Escenario 1: Credenciales Inválidas (401)

```bash
curl -X POST http://localhost:8080/api/v1/empleados/login \
  -H "Content-Type: application/json" \
  -d '{
    "clave": "E-001",
    "password": "WrongPassword"
  }'

# Response: 401 Unauthorized
# {
#   "timestamp": "2026-03-08T14:30:00Z",
#   "status": 401,
#   "error": "Unauthorized",
#   "message": "Credenciales inválidas",
#   "path": "/api/v1/empleados/login"
# }
```

### Escenario 2: Rate Limiting (429)

Intenta login fallido 6 veces consecutivas:

```bash
# Intentos 1-5: retornan 401 (credenciales inválidas)
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/v1/empleados/login \
    -H "Content-Type: application/json" \
    -d '{"clave": "E-001", "password": "wrong"}'
  echo "\n--- Intento $i ---"
done

# Intento 6: retorna 429 (rate limit)
curl -X POST http://localhost:8080/api/v1/empleados/login \
  -H "Content-Type: application/json" \
  -d '{"clave": "E-001", "password": "wrong"}'

# Response: 429 Too Many Requests
# {
#   "timestamp": "2026-03-08T14:30:00Z",
#   "status": 429,
#   "error": "Too Many Requests",
#   "message": "Demasiados intentos fallidos. Intente nuevamente en 15 minutos.",
#   "path": "/api/v1/empleados/login"
# }
```

**Reset rate limit**: Esperar 15 minutos o hacer login exitoso.

### Escenario 3: Contraseña Débil (400)

```bash
curl -X POST http://localhost:8080/api/v1/empleados \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{
    "nombre": "Test User",
    "direccion": "Test Address",
    "telefono": "555-0000",
    "password": "weak"
  }'

# Response: 400 Bad Request
# {
#   "timestamp": "2026-03-08T14:30:00Z",
#   "status": 400,
#   "error": "Bad Request",
#   "message": "Contraseña debe tener mínimo 8 caracteres, al menos una letra y un número",
#   "path": "/api/v1/empleados"
# }
```

### Escenario 4: Cambiar Contraseña con Current Password Incorrecta (403)

```bash
curl -X PUT http://localhost:8080/api/v1/empleados/E-001/password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "currentPassword": "WrongCurrentPassword",
    "newPassword": "NewPassword456"
  }'

# Response: 403 Forbidden
# {
#   "timestamp": "2026-03-08T14:30:00Z",
#   "status": 403,
#   "error": "Forbidden",
#   "message": "Contraseña actual incorrecta",
#   "path": "/api/v1/empleados/E-001/password"
# }
```

### Escenario 5: Token Invalidado Después de Cambio de Contraseña

```bash
# 1. Login y guardar token
TOKEN_OLD=$(curl -s -X POST http://localhost:8080/api/v1/empleados/login \
  -H "Content-Type: application/json" \
  -d '{"clave": "E-001", "password": "Password123"}' | jq -r '.token')

# 2. Cambiar contraseña con token válido
curl -X PUT http://localhost:8080/api/v1/empleados/E-001/password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_OLD" \
  -d '{"currentPassword": "Password123", "newPassword": "NewPassword456"}'

# 3. Intentar cambiar contraseña nuevamente con token antiguo
curl -X PUT http://localhost:8080/api/v1/empleados/E-001/password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_OLD" \
  -d '{"currentPassword": "NewPassword456", "newPassword": "AnotherPassword789"}'

# Response: 401 Unauthorized (token invalidado)
```

---

## Verificación de Auditoría

### Ver Eventos de Autenticación

Consultar tabla `auth_events` directamente en PostgreSQL:

```bash
# Conectar a PostgreSQL
docker exec -it <container-name> psql -U postgres -d practica02

# Queries
-- Ver todos los eventos
SELECT * FROM auth_events ORDER BY timestamp DESC;

-- Ver intentos fallidos de un empleado
SELECT * FROM auth_events 
WHERE clave = 'E-001' AND event_type = 'LOGIN_FAILURE' 
ORDER BY timestamp DESC;

-- Contar intentos fallidos en última hora
SELECT clave, COUNT(*) as attempts
FROM auth_events
WHERE event_type = 'LOGIN_FAILURE' 
  AND timestamp > NOW() - INTERVAL '1 hour'
GROUP BY clave;
```

---

## Swagger UI (OpenAPI Documentation)

Ver documentación interactiva de API:

1. Abrir navegador: `http://localhost:8080/swagger-ui/index.html`
2. Buscar sección "Authentication"
3. Probar endpoints directamente desde UI:
   - **POST /api/v1/empleados/login**: No requiere autenticación previa
   - **PUT /api/v1/empleados/{clave}/password**: Click "Authorize", pegar token JWT

---

## Troubleshooting

### Error: "JWT_SECRET environment variable not set"

**Causa**: Variable de entorno `JWT_SECRET` no configurada.

**Solución**:
```bash
# Linux/Mac
export JWT_SECRET="your-secret-key-min-32-chars-long"

# PowerShell
$env:JWT_SECRET = "your-secret-key-min-32-chars-long"

# O agregar a application.properties
jwt.secret=${JWT_SECRET:default-secret-for-development-only}
```

### Error: "Password must be at least 8 characters long"

**Causa**: Contraseña no cumple requisitos (8+ chars, letra + número).

**Solución**: Usar contraseña válida, ej: `Password123`, `MySecret99`, `Test1234`

### Error: "Too Many Requests" sin haber intentado antes

**Causa**: Caffeine cache reteniendo intentos fallidos previos.

**Solución**: 
- Esperar 15 minutos para expiración automática
- O reiniciar aplicación (cache in-memory se limpia)

### Token no válido después de cambio de contraseña

**Causa**: Comportamiento esperado (tokens emitidos antes de cambio quedan invalidados).

**Solución**: Hacer login nuevamente con nueva contraseña.

### Rate limiting no funciona en tests

**Causa**: Tests pueden limpiar cache entre ejecuciones.

**Solución**: Verificar configuración `@CacheConfig` en clase de servicio, asegurar que tests usen mismo application context.

---

## Performance Verification

### Medir Tiempo de Respuesta

```bash
# Login performance (debe ser < 2s según SC-002)
time curl -X POST http://localhost:8080/api/v1/empleados/login \
  -H "Content-Type: application/json" \
  -d '{"clave": "E-001", "password": "Password123"}'

# Password change performance (debe ser < 30s según SC-006)
time curl -X PUT http://localhost:8080/api/v1/empleados/E-001/password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"currentPassword": "Password123", "newPassword": "NewPassword456"}'
```

### Load Testing (Opcional)

```bash
# Usar Apache Bench para test de carga
ab -n 100 -c 10 -p login.json -T application/json \
  http://localhost:8080/api/v1/empleados/login

# login.json:
# {"clave": "E-001", "password": "Password123"}
```

---

## Security Checklist

Antes de deployar a producción, verificar:

- [ ] `JWT_SECRET` es una cadena fuerte (256+ bits), NO usar default
- [ ] `JWT_SECRET` está en variable de entorno, NO en código
- [ ] HTTPS configurado (tokens transmitidos por header Authorization)
- [ ] Basic Auth credentials (`admin:admin123`) cambiadas en producción
- [ ] PostgreSQL password cambiada (no usar `postgres:postgres`)
- [ ] Rate limiting funcionando (verificar con 6 intentos fallidos)
- [ ] Audit logging funcionando (verificar registros en `auth_events`)
- [ ] Contraseñas nunca expuestas en responses (verificar DTOs)
- [ ] Tokens antiguos invalidados después de cambio de contraseña

---

## Next Steps

Una vez verificado funcionamiento local:

1. **Ejecutar tests**: `mvn test` (unit + integration tests)
2. **Verificar cobertura**: `mvn test jacoco:report`
3. **Review security**: Ejecutar OWASP dependency check
4. **Documentar cambios**: Actualizar README.md con instrucciones de autenticación
5. **Deploy staging**: Probar en ambiente staging antes de producción

---

## References

- **OpenAPI Contract**: [contracts/auth-api.yaml](contracts/auth-api.yaml)
- **Data Model**: [data-model.md](data-model.md)
- **Research Report**: [research.md](research.md)
- **Feature Spec**: [spec.md](spec.md)
