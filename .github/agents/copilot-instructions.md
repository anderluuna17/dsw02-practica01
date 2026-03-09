# DSW02-Practica01 Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-02-26

## Active Technologies

- Java 17 + Spring Boot 3 (Web, Validation, Data JPA, Security), springdoc-openapi (001-crud-empleado)

## Project Structure

```text
src/
tests/
```

## Commands

# Add commands for Java 17

## Code Style

Java 17: Follow standard conventions

## API Governance

- Version public REST endpoints using path prefix `/api/v{major}/...`.
- Implement pagination for list endpoints with default `size=5`.
- Keep OpenAPI documentation synchronized with endpoint version and pagination params.

## Security Defaults

- Use HTTP Basic Auth for business endpoints.
- For local/dev defaults, use `APP_BASIC_USER=admin` and `APP_BASIC_PASSWORD=admin123`.
- Allow credentials override via environment variables.

## Recent Changes

- 001-crud-empleado: Added Java 17 + Spring Boot 3 (Web, Validation, Data JPA, Security), springdoc-openapi

<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
