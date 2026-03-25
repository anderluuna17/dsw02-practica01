# DSW02-Practica01 Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-02-26

## Active Technologies
- PostgreSQL (002-autenticar-empleados-correo)
- TypeScript 5.9, SCSS, HTML (Angular 21.2.x en este entorno; Angular 22 no disponible en npm al momento) + Angular standalone APIs, `@angular/common/http`, `@angular/forms`, RxJS (004-login-frontend-admin)
- N/A para persistencia nueva (sesion solo en memoria de UI) (004-login-frontend-admin)
- TypeScript 5.9, HTML, SCSS (Angular 21.2.x en este entorno) + Angular standalone APIs, `@angular/common/http`, `@angular/forms`, RxJS (005-crud-empleados-frontend-admin)
- N/A para persistencia nueva; estado de sesion y formularios en memoria de UI (005-crud-empleados-frontend-admin)
- N/A para persistencia nueva; sesion y estado de UI en memoria (005-crud-empleados-frontend-admin)
- Java 17 (backend), TypeScript 5.9 (frontend Angular 21) + Spring Boot 3.3.2, Spring Security (HTTP Basic), Spring Data JPA, PostgreSQL driver, Angular standalone APIs, RxJS (006-fix-admin-auth-role)
- PostgreSQL (sin cambios de modelo persistente para esta feature) (006-fix-admin-auth-role)
- Java 17 (backend), TypeScript 5.9 (frontend Angular 21) + Spring Boot 3.3.x, Spring Security (HTTP Basic), Spring Data JPA, PostgreSQL driver, Angular standalone APIs, RxJS (006-fix-admin-auth-role)
- PostgreSQL (sin cambios de esquema obligatorios para esta feature) (006-fix-admin-auth-role)

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
- Ensure auth profile endpoints (for example `/auth/me`) explicitly distinguish admin vs empleado actor types.
- Keep OpenAPI documentation synchronized with endpoint version and pagination params.

## Security Defaults

- Use HTTP Basic Auth for business endpoints.
- For local/dev defaults, use `APP_BASIC_USER=admin` and `APP_BASIC_PASSWORD=admin123`.
- Allow credentials override via environment variables.

## Recent Changes
- 006-fix-admin-auth-role: Added Java 17 (backend), TypeScript 5.9 (frontend Angular 21) + Spring Boot 3.3.x, Spring Security (HTTP Basic), Spring Data JPA, PostgreSQL driver, Angular standalone APIs, RxJS
- 006-fix-admin-auth-role: Added Java 17 (backend), TypeScript 5.9 (frontend Angular 21) + Spring Boot 3.3.2, Spring Security (HTTP Basic), Spring Data JPA, PostgreSQL driver, Angular standalone APIs, RxJS
- 005-crud-empleados-frontend-admin: Added TypeScript 5.9, HTML, SCSS (Angular 21.2.x en este entorno) + Angular standalone APIs, `@angular/common/http`, `@angular/forms`, RxJS


<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
