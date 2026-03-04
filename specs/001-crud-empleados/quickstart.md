# Quickstart: CRUD de Empleados

## Prerrequisitos
- Java 17
- Maven 3.9+
- PostgreSQL
- Variables de entorno para credenciales de app y DB

## Configuración mínima
1. Configurar base de datos PostgreSQL.
2. Definir variables:
   - `APP_SECURITY_USER`
   - `APP_SECURITY_PASSWORD`
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
3. Compilar y arrancar:
   - `mvn clean spring-boot:run`

## Endpoints esperados
- `POST /api/empleados`
- `GET /api/empleados`
- `GET /api/empleados/{clave}`
- `PUT /api/empleados/{clave}`
- `DELETE /api/empleados/{clave}`

## Verificación rápida
1. Crear empleado enviando solo `nombre`, `direccion`, `telefono` (sin `clave`) y validar `201` con `clave` generada tipo `E-001`.
2. Consultar por `clave` y validar payload completo (`version` incluida).
3. Listar con paginación por defecto y validar estructura tipo Spring Page.
4. Listar con filtros (`nombre` contains, `clave` exact) y validar resultados.
5. Listar con `sort=desc` y validar orden por `clave`.
6. Actualizar con `version` vigente y validar éxito; reintentar con versión obsoleta y validar `409`.
7. Eliminar empleado y validar que consulta posterior indique `404`.

## Docker (ejecución local)
- Construir imagen:
  - `docker build -t empleados-service:local .`
- Levantar PostgreSQL y app (configuración mínima validada):
  - `docker network create dsw-net`
  - `docker run -d --name pg-dsw --network dsw-net -e POSTGRES_DB=empleados_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:15-alpine`
  - `docker run -d --name empleados-app --network dsw-net -p 8080:8080 -e DB_URL=jdbc:postgresql://pg-dsw:5432/empleados_db -e DB_USERNAME=postgres -e DB_PASSWORD=postgres -e APP_SECURITY_USER=admin -e APP_SECURITY_PASSWORD=admin123 empleados-service:local`
- Validar documentación con auth básica:
  - `GET /swagger-ui/index.html` -> `200`
  - `GET /v3/api-docs` -> `200`
- Limpieza:
  - `docker rm -f empleados-app pg-dsw && docker network rm dsw-net`

## Rendimiento básico (local)
- Medición realizada sobre `GET /api/empleados?page=0&size=20&sort=asc` con autenticación básica.
- Resultado: `p95 = 155.55 ms` (objetivo: `< 300 ms`, cumple).

## Documentación API
- OpenAPI contrato: `specs/001-crud-empleados/contracts/empleados-api.yaml`
- Swagger UI (cuando la app esté arriba): `/swagger-ui.html`
