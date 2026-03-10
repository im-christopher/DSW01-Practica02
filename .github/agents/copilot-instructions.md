# DSW01-Practica02 Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-02-25

## Active Technologies
- Java 17 (mandatory) + Spring Boot 3.x, Spring Security (BCryptPasswordEncoder), Spring Data JPA, springdoc-openapi, spring-boot-starter-cache (Caffeine), jjwt (JWT tokens) (002-empleado-auth)
- PostgreSQL (mandatory) - nueva columna `password_hash VARCHAR(255) NULLABLE`, `password_changed_at TIMESTAMP NULLABLE`, tabla audit `auth_events` (002-empleado-auth)

- Java 17 (mandatory) + Spring Boot 3.x, Spring Security, Spring Data JPA, springdoc-openapi (001-crud-empleados)

## Project Structure

```text
src/main/java/
src/main/resources/
src/test/java/
```

## Commands

# Add commands for Java 17 (mandatory)

## Code Style

Java 17 (mandatory): Follow standard conventions

## API Versioning

- Public backend endpoints MUST use path versioning (`/api/v{major}/...`).
- Breaking API changes MUST bump the major path version.

## Recent Changes
- 003-departamentos-crud: Added Java 17 (mandatory) + Spring Boot 3.x, Spring Security, Spring Data JPA, springdoc-openapi
- 002-empleado-auth: Added Java 17 (mandatory) + Spring Boot 3.x, Spring Security (BCryptPasswordEncoder), Spring Data JPA, springdoc-openapi, spring-boot-starter-cache (Caffeine), jjwt (JWT tokens)

- 001-crud-empleados: Added Java 17 (mandatory) + Spring Boot 3.x, Spring Security, Spring Data JPA, springdoc-openapi

<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
