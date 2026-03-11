<!--
Sync Impact Report
- Version change: 1.1.0 -> 1.2.0
- Modified principles:
	- II. Seguridad por Defecto (Basic Auth) -> II. Seguridad por Defecto (Basic Auth) [credenciales por defecto de desarrollo]
- Added sections: Ninguna (se amplía contenido en secciones existentes)
- Removed sections: Bloque de plantilla con placeholders iniciales
- Templates requiring updates:
	- ✅ .specify/templates/plan-template.md
	- ✅ .specify/templates/spec-template.md
	- ✅ .specify/templates/tasks-template.md
	- ✅ .specify/templates/commands/*.md (no aplica: ruta no existe en este workspace)
- Runtime guidance updates:
	- ✅ .github/agents/copilot-instructions.md
	- ✅ specs/001-crud-empleado/quickstart.md (revisado, ya alineado; sin cambios)
	- ✅ specs/001-crud-empleado/contracts/empleados-openapi.yaml (revisado, ya alineado; sin cambios)
- Deferred TODOs: Ninguno
-->

# DSW02-Practica01 Constitution

## Core Principles

### I. Stack Tecnológico Obligatorio
El backend se implementa exclusivamente con Spring Boot 3 y Java 17. No se permite introducir frameworks alternativos para el núcleo de API, seguridad o persistencia sin una enmienda explícita a esta constitución.

### II. Seguridad por Defecto (Basic Auth)
Todas las rutas de negocio deben estar protegidas mediante autenticación HTTP Basic usando Spring Security. Solo podrán existir endpoints públicos cuando estén justificados (por ejemplo, health checks) y documentados de forma explícita.
En entornos local/desarrollo, las credenciales por defecto DEBEN ser `admin` (usuario) y `admin123` (contraseña), con posibilidad de sobreescritura por variables de entorno.

### III. Persistencia en PostgreSQL
La base de datos oficial del proyecto es PostgreSQL. Toda funcionalidad persistente debe modelarse para PostgreSQL y ejecutarse mediante configuración parametrizable por variables de entorno.

### IV. Entorno Reproducible con Docker
El proyecto debe poder levantarse en local y en integración mediante Docker y Docker Compose, incluyendo al menos el servicio de aplicación y PostgreSQL. Ningún flujo crítico debe depender de instalaciones manuales fuera de contenedores.

### V. Contrato API Versionado, Paginación y Documentación Viva
Toda API REST DEBE estar versionada en ruta (`/api/v{major}/...`) y reflejar esa versión en OpenAPI.
Todo endpoint de listado DEBE soportar paginación y DEBE usar por defecto `size=5` cuando no se especifique.
No se aceptan endpoints nuevos o cambios de contrato sin actualización de Swagger/OpenAPI y ejemplos de uso.

## Restricciones Técnicas y de Arquitectura

- Java: versión 17 (LTS), con compilación y ejecución alineadas a esta versión.
- Framework base: Spring Boot 3.x.
- Seguridad: Spring Security con HTTP Basic Auth.
- Credenciales por defecto (local/dev): `APP_BASIC_USER=admin` y `APP_BASIC_PASSWORD=admin123`.
- Base de datos: PostgreSQL, con configuración por variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`).
- Contenerización: `Dockerfile` para la aplicación y `docker-compose.yml` para la orquestación local.
- API: Swagger/OpenAPI habilitado en entorno de desarrollo y accesible para validación funcional.
- Versionado API: prefijo obligatorio por versión mayor en rutas públicas (`/api/v1/...`).
- Paginación: listados públicos con parámetros de paginación (`page`, `size`) y `size=5` por defecto.
- Configuración: no hardcodear secretos; usar variables de entorno y/o archivos `.env` fuera de control de versiones.
- En no-producción se permite valor por defecto de Basic Auth (`admin`/`admin123`) únicamente como baseline de arranque; en producción DEBE sobreescribirse.

## Flujo de Desarrollo y Quality Gates

- Cada nueva historia backend debe incluir:
	- Endpoint(s) implementados.
	- Endpoint(s) versionados (`/api/v{major}/...`).
	- Paginación en listados con valor por defecto `size=5`.
	- Seguridad aplicada con Basic Auth.
	- Credenciales default de desarrollo configuradas (`admin`/`admin123`) o justificadas si difieren.
	- Persistencia en PostgreSQL (si corresponde).
	- Documentación Swagger actualizada.
- Antes de integrar cambios, se debe verificar:
	- Compilación correcta con Java 17.
	- Arranque del stack con Docker Compose.
	- Pruebas unitarias e integración relevantes en verde.
	- Validación manual mínima de endpoints en Swagger UI.
	- Validación de acceso con credenciales Basic Auth por defecto en entorno local.
	- Validación explícita de versionado de ruta y comportamiento de paginación por defecto.
- Cualquier desviación del stack definido debe registrarse como excepción técnica con motivo, alcance y plan de reversión.

## Governance

Esta constitución prevalece sobre decisiones ad-hoc de implementación. Toda revisión de código debe validar cumplimiento explícito de estos principios.

Las enmiendas requieren:
- Justificación técnica por escrito.
- Evaluación del impacto en seguridad, despliegue y documentación.
- Plan de migración cuando haya cambios de stack, autenticación o base de datos.

Política de versionado de esta constitución:
- **MAJOR**: redefinición o eliminación de principios/gobernanza con impacto incompatible.
- **MINOR**: adición o expansión material de principios o reglas obligatorias.
- **PATCH**: aclaraciones editoriales sin cambio normativo.

Expectativa de cumplimiento:
- Toda PR DEBE declarar cumplimiento constitucional en el `plan.md` y en revisión técnica.
- Cualquier excepción DEBE registrarse con alcance temporal y plan de remediación.

**Version**: 1.2.0 | **Ratified**: 2026-02-25 | **Last Amended**: 2026-03-04
