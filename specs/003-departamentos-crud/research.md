# Research Decisions: Departamentos CRUD

**Feature**: 003-departamentos-crud  
**Generated**: Phase 0 of planning workflow  
**Input**: Clarifications from `spec.md` (session 2026-03-09)

## Overview

Este documento consolida las decisiones técnicas tomadas durante la fase de clarificación de requerimientos. Cada decisión incluye la justificación, alternativas consideradas, y el impacto esperado.

---

## Decision 1: Soft Delete Strategy

**Chosen**: Usar soft delete con campo `activo` (BOOLEAN) en la tabla `departamentos`

**Rationale**:
- **Preservación de historia**: Mantener registro histórico de departamentos eliminados es importante para auditoría y análisis temporal
- **Reversibilidad**: Los departamentos pueden ser reactivados si se eliminaron por error sin perder asociaciones históricas
- **Integridad referencial**: Evita problemas de FK con empleados que fueron asignados a departamentos eliminados
- **Compatibilidad**: La tabla empleados ya usa el mismo patrón (`activo` boolean), mantiene consistencia del modelo de datos

**Alternatives Considered**:
- **Hard delete físico**: Rechazado porque causaría pérdida de información histórica y problemas con FKs nullables en empleados
- **Tabla de histórico separada**: Rechazado porque agrega complejidad innecesaria para entidades que cambian raramente
- **Timestamp de eliminación**: Rechazado porque el booleano es más simple y suficiente dado que no necesitamos la fecha exacta de eliminación

**Implementation Impact**:
- Schema: `departamentos` incluye columna `activo BOOLEAN NOT NULL DEFAULT true`
- Queries: Todas las consultas públicas deben filtrar `WHERE activo = true` por defecto
- Endpoints: DELETE marca `activo = false` en lugar de ejecutar SQL DELETE
- Validación: El endpoint DELETE debe verificar que no haya empleados asignados antes de marcar como inactivo (409 Conflict)

---

## Decision 2: No Optimistic Locking (Version Control)

**Chosen**: Departamentos NO usan control de versión optimista (sin campo `version`)

**Rationale**:
- **Naturaleza estática**: Los departamentos son entidades organizacionales que cambian muy infrecuentemente (creación inicial y renombres ocasionales)
- **Bajo riesgo de concurrencia**: Es extremadamente raro que dos administradores intenten actualizar el mismo departamento simultáneamente
- **Simplicidad**: Eliminar el control de versión reduce complejidad en DTOs, service layer, y contratos API
- **Last-write-wins suficiente**: Para actualizaciones infrecuentes, el patrón last-write-wins es aceptable y no introduce riesgos significativos

**Alternatives Considered**:
- **Control de versión optimista (con `version`)**: Rechazado porque agrega complejidad innecesaria dado el bajo riesgo de conflictos concurrentes
- **Locking pesimista (SELECT FOR UPDATE)**: Rechazado porque introduce overhead de base de datos sin beneficio real para este caso de uso
- **Timestamp de última modificación**: Considerado pero rechazado porque no aporta valor funcional significativo para entidades estáticas

**Implementation Impact**:
- Schema: `departamentos` NO incluye columna `version`
- DTOs: `DepartamentoUpdateRequest` no requiere campo `version`
- Service: Método `actualizar()` no necesita validación de versión
- Controller: Endpoint PUT más simple sin necesidad de manejar 409 Conflict por versión desactualizada

---

## Decision 3: Pagination Pattern Alignment

**Chosen**: Usar el mismo patrón de paginación que empleados (`page=0`, `size=20`, `sort=asc`)

**Rationale**:
- **Consistencia de API**: Mantener el mismo patrón en todos los endpoints de listado hace la API más predecible y fácil de usar
- **Reutilización de configuración**: Spring Data JPA Pageable se comporta de manera uniforme en toda la aplicación
- **Documentación simplificada**: No hay necesidad de documentar diferentes patrones de paginación por recurso
- **Experiencia de usuario**: Los clientes de la API ya conocen el patrón y pueden reutilizar código

**Alternatives Considered**:
- **Paginación diferente (offset/limit)**: Rechazado porque rompe consistencia con empleados y requiere más código custom
- **Sin paginación (lista completa)**: Rechazado porque aunque los departamentos son pocas entidades, sigue siendo una mala práctica y no escala
- **Cursor-based pagination**: Rechazado porque es más complejo y no hay necesidad de performance extremo para este caso de uso

**Implementation Impact**:
- Controller: Endpoint GET `/api/v1/departamentos` acepta mismos query params: `page`, `size`, `sort`
- Service: Usa `Pageable` de Spring Data con configuración idéntica a empleados
- Response: `DepartamentoPageResponse` sigue misma estructura que `EmpleadoPageResponse` (content, totalElements, totalPages, number, size)
- Defaults: `page=0`, `size=20` si no se especifican

---

## Decision 4: Departamento Nested Object Format in Employee Responses

**Chosen**: Respuestas de empleados incluyen `departamento` como objeto con **solo `id` y `nombre`**

**Rationale**:
- **Minimización de payload**: Incluir solo los campos esenciales reduce el tamaño de las respuestas, especialmente en listas paginadas de empleados
- **Información suficiente**: `id` permite navegación/enlace, `nombre` proporciona contexto legible sin necesidad de llamada adicional
- **Evita exposición de campos internos**: No exponemos `activo` en respuestas de empleados porque no es relevante en ese contexto (empleados ya filtran por departamentos activos)
- **Simplifica contratos**: El subset de campos es claro y explícito, no hay ambiguedad sobre qué se incluye

**Alternatives Considered**:
- **Solo ID**: Rechazado porque requiere llamada adicional a `/api/v1/departamentos/{id}` para obtener el nombre, degrada UX
- **Objeto completo** (id, nombre, activo): Rechazado porque `activo` es campo de implementación interna no relevante para consultas de empleados
- **Solo nombre**: Rechazado porque sin ID los clientes no pueden navegar o filtrar por departamento eficientemente
- **HATEOAS links**: Rechazado porque agrega complejidad sin beneficio claro en este contexto

**Implementation Impact**:
- DTOs: Crear `DepartamentoSummaryDTO` con solo `id` y `nombre`
- EmpleadoResponse: Agregar campo `DepartamentoSummaryDTO departamento` (nullable)
- Service Mapping: Al construir `EmpleadoResponse`, si el empleado tiene departamento asignado, mapear solo id+nombre
- OpenAPI: Documentar el objeto anidado con esquema explícito de 2 campos

---

## Decision 5: Sort Departamentos by Nombre Alphabetically

**Chosen**: Listado de departamentos ordenado por `nombre` en orden alfabético ascendente por defecto

**Rationale**:
- **Usabilidad**: El orden alfabético es el más intuitivo y útil para administradores buscando un departamento específico
- **Consistencia semántica**: Ordenar por ID no tiene significado funcional (es solo un valor autogenerado), mientras que `nombre` es el atributo clave de búsqueda
- **Compatibilidad con UI futura**: Si eventualmente se agrega un frontend, el orden alfabético es el más apropiado para dropdowns/listas
- **Previsibilidad**: Cualquier usuario puede predecir dónde aparecerá un departamento en la lista sin conocer el orden de creación

**Alternatives Considered**:
- **Ordenar por ID**: Rechazado porque el ID es arbitrario y no tiene significado funcional para los usuarios
- **Ordenar por fecha de creación**: No aplicable porque no tenemos campo `createdAt` y agregarlo solo para ordenamiento es over-engineering
- **Permitir sort dinámico por cualquier campo**: Rechazado porque agrega complejidad innecesaria para un caso de uso simple con pocos departamentos

**Implementation Impact**:
- Service: En método `listarDepartamentos()`, configurar `Pageable` con `Sort.by("nombre").ascending()` si el cliente no especifica otro orden
- Repository: La query debe permitir sort dinámico pero defaultear a nombre
- Controller: Documentar en OpenAPI que el sort por defecto es `nombre,asc`
- Tests: Verificar que sin parámetro `sort`, los resultados vienen ordenados alfabéticamente

---

## Summary of Technical Choices

| Aspecto | Decisión | Impacto Principal |
|---------|----------|------------------|
| Eliminación | Soft delete (`activo` boolean) | Schema + Queries filtran activo |
| Concurrencia | Sin control de versión | DTOs más simples, last-write-wins |
| Paginación | Mismo patrón que empleados | Consistencia de API |
| Empleado Response | Nested object (`id`, `nombre` solo) | Payload mínimo, info suficiente |
| Ordenamiento | Por `nombre` alfabético | Usabilidad óptima |

Todas estas decisiones priorizan **simplicidad**, **consistencia con código existente**, y **usabilidad** sobre abstracción excesiva o preparación para casos hipotéticos.
