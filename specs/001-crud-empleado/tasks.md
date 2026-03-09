# Tasks: CRUD de empleado

**Input**: Design documents from `/specs/001-crud-empleado/`
**Prerequisites**: `plan.md` (required), `spec.md` (required for user stories), `research.md`, `data-model.md`, `contracts/`

**Tests**: No se generan tareas de pruebas como TDD estricto porque no fueron solicitadas explícitamente en la especificación; se incluye validación funcional e integración en fases de historia y polish.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Inicializar proyecto backend Spring Boot y estructura base.

- [X] T001 Crear esqueleto del backend y estructura de paquetes en `backend/src/main/java/com/dsw02/empleado/` y `backend/src/test/java/com/dsw02/empleado/`
- [X] T002 Inicializar proyecto Maven Spring Boot en `backend/pom.xml` con dependencias Web, Validation, Data JPA, Security, springdoc-openapi y PostgreSQL
- [X] T003 [P] Configurar propiedades base y perfiles en `backend/src/main/resources/application.yml` y `backend/src/main/resources/application-dev.yml`
- [X] T004 [P] Crear clase de arranque `EmpleadoApplication` en `backend/src/main/java/com/dsw02/empleado/EmpleadoApplication.java`
- [X] T005 [P] Preparar `.env.example` y variables requeridas para DB y Basic Auth en `.env.example`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura obligatoria previa a cualquier historia.

**⚠️ CRITICAL**: Ninguna historia se implementa antes de completar esta fase.

- [X] T006 Configurar seguridad HTTP Basic y rutas públicas mínimas en `backend/src/main/java/com/dsw02/empleado/infrastructure/security/SecurityConfig.java`
- [X] T007 [P] Implementar entidad JPA `EmpleadoEntity` con PK compuesta (`prefijo`, `consecutivo`) y longitudes de columnas en `backend/src/main/java/com/dsw02/empleado/infrastructure/persistence/EmpleadoEntity.java`
- [X] T008 [P] Implementar repositorio `EmpleadoRepository` en `backend/src/main/java/com/dsw02/empleado/infrastructure/persistence/EmpleadoRepository.java`
- [X] T009 [P] Definir DTOs base y validaciones compartidas en `backend/src/main/java/com/dsw02/empleado/api/dto/EmpleadoCreateRequest.java`, `backend/src/main/java/com/dsw02/empleado/api/dto/EmpleadoUpdateRequest.java` y `backend/src/main/java/com/dsw02/empleado/api/dto/EmpleadoResponse.java`
- [X] T010 [P] Crear catálogo de errores de dominio en `backend/src/main/java/com/dsw02/empleado/domain/ErrorCode.java` y excepciones en `backend/src/main/java/com/dsw02/empleado/domain/exception/`
- [X] T011 Implementar manejador global de errores para contrato uniforme en `backend/src/main/java/com/dsw02/empleado/api/GlobalExceptionHandler.java`
- [X] T012 [P] Implementar parser/validador de `clave` con patrón `EMP-<autonumérico>` en `backend/src/main/java/com/dsw02/empleado/domain/ClaveParser.java`
- [X] T013 [P] Configurar documentación OpenAPI/Swagger en `backend/src/main/java/com/dsw02/empleado/infrastructure/openapi/OpenApiConfig.java`
- [X] T014 Configurar `docker-compose.yml` con servicios `app` y `postgres` y variables de entorno
- [X] T015 [P] Crear `backend/Dockerfile` para build/run de la aplicación

**Checkpoint**: Base técnica lista; historias pueden implementarse de forma incremental.

---

## Phase 3: User Story 1 - Registrar empleado (Priority: P1) 🎯 MVP

**Goal**: Permitir alta de empleado con `clave` autogenerada (`EMP-<autonumérico>`) y validaciones de longitud.

**Independent Test**: Crear empleado válido (201) verificando `clave` generada en respuesta, y probar campo >100 caracteres o envío manual de `clave` en alta (400).

### Implementation for User Story 1

- [X] T016 [P] [US1] Implementar modelo de dominio `Empleado` en `backend/src/main/java/com/dsw02/empleado/domain/Empleado.java`
- [X] T017 [US1] Implementar servicio de creación con generación automática de `clave` (`EMP-<autonumérico>`) en `backend/src/main/java/com/dsw02/empleado/application/CrearEmpleadoService.java`
- [X] T018 [US1] Implementar endpoint `POST /api/v1/empleados` en `backend/src/main/java/com/dsw02/empleado/api/EmpleadoController.java`
- [X] T019 [US1] Mapear error de alta con `clave` enviada por cliente a HTTP 400 en `backend/src/main/java/com/dsw02/empleado/api/GlobalExceptionHandler.java`
- [X] T020 [US1] Actualizar documentación Swagger del alta en anotaciones de `backend/src/main/java/com/dsw02/empleado/api/EmpleadoController.java`

**Checkpoint**: US1 funcional y demostrable de forma independiente (MVP).

---

## Phase 4: User Story 2 - Consultar empleados (Priority: P2)

**Goal**: Consultar empleado por clave y listar todos los empleados.

**Independent Test**: Consultar `GET /api/v1/empleados/{clave}` con formato `EMP-<autonumérico>` para clave existente/no existente y `GET /api/v1/empleados` con registros cargados y `size=5` por defecto.

### Implementation for User Story 2

- [X] T021 [P] [US2] Implementar servicio de consulta por clave en `backend/src/main/java/com/dsw02/empleado/application/ObtenerEmpleadoService.java`
- [X] T022 [P] [US2] Implementar servicio de listado en `backend/src/main/java/com/dsw02/empleado/application/ListarEmpleadosService.java`
- [X] T023 [US2] Implementar endpoint `GET /api/v1/empleados/{clave}` en `backend/src/main/java/com/dsw02/empleado/api/EmpleadoController.java`
- [X] T024 [US2] Implementar endpoint `GET /api/v1/empleados` en `backend/src/main/java/com/dsw02/empleado/api/EmpleadoController.java`
- [X] T025 [US2] Mapear no encontrado a HTTP 404 en `backend/src/main/java/com/dsw02/empleado/api/GlobalExceptionHandler.java`
- [X] T026 [US2] Actualizar documentación Swagger de endpoints de consulta en `backend/src/main/java/com/dsw02/empleado/api/EmpleadoController.java`

**Checkpoint**: US1 y US2 operativas de manera independiente.

---

## Phase 5: User Story 3 - Actualizar y eliminar empleado (Priority: P3)

**Goal**: Actualizar `nombre`, `direccion`, `telefono` y eliminar empleado por `clave`.

**Independent Test**: Actualizar empleado existente (200), eliminar empleado (204), y validar que update/delete sobre inexistente responde 404.

### Implementation for User Story 3

- [X] T027 [P] [US3] Implementar servicio de actualización sin modificación de `clave` en `backend/src/main/java/com/dsw02/empleado/application/ActualizarEmpleadoService.java`
- [X] T028 [P] [US3] Implementar servicio de eliminación en `backend/src/main/java/com/dsw02/empleado/application/EliminarEmpleadoService.java`
- [X] T029 [US3] Implementar endpoint `PUT /api/v1/empleados/{clave}` en `backend/src/main/java/com/dsw02/empleado/api/EmpleadoController.java`
- [X] T030 [US3] Implementar endpoint `DELETE /api/v1/empleados/{clave}` en `backend/src/main/java/com/dsw02/empleado/api/EmpleadoController.java`
- [X] T031 [US3] Reforzar validación de inmutabilidad de `clave` y formato `EMP-<autonumérico>` en `backend/src/main/java/com/dsw02/empleado/application/ActualizarEmpleadoService.java`
- [X] T032 [US3] Actualizar documentación Swagger de update/delete en `backend/src/main/java/com/dsw02/empleado/api/EmpleadoController.java`

**Checkpoint**: CRUD completo con las tres historias funcionales.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Endurecimiento final y validación transversal.

- [X] T033 [P] Alinear contrato OpenAPI implementado con `specs/001-crud-empleado/contracts/empleados-openapi.yaml`
- [X] T034 [P] Crear script de verificación rápida de endpoints en `scripts/smoke/empleados-smoke.sh`
- [X] T035 Añadir pruebas de integración CRUD con PostgreSQL en `backend/src/test/java/com/dsw02/empleado/integration/EmpleadoCrudIntegrationTest.java`
- [X] T036 [P] Añadir pruebas de seguridad Basic Auth en `backend/src/test/java/com/dsw02/empleado/integration/SecurityIntegrationTest.java`
- [ ] T037 Ejecutar validación de quickstart y actualizar notas en `specs/001-crud-empleado/quickstart.md`
- [X] T038 [P] Documentar decisiones finales y alcance de release en `specs/001-crud-empleado/plan.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: inicia de inmediato.
- **Phase 2 (Foundational)**: depende de Phase 1 y bloquea todas las historias.
- **Phase 3-5 (User Stories)**: dependen de Phase 2; pueden ejecutarse por prioridad o en paralelo por equipo.
- **Phase 6 (Polish)**: depende de completar las historias objetivo.

### User Story Dependencies

- **US1 (P1)**: depende solo de Foundational.
- **US2 (P2)**: depende solo de Foundational; no requiere bloquear US1 para desarrollarse, pero sí para demo incremental.
- **US3 (P3)**: depende solo de Foundational; usa componentes compartidos de controlador/servicios.

### Within Each User Story

- Modelos/validadores antes de servicios.
- Servicios antes de endpoints.
- Endpoints antes de hardening/documentación de la historia.

---

## Parallel Opportunities

- **Setup**: `T003`, `T004`, `T005` en paralelo tras `T002`.
- **Foundational**: `T007`, `T008`, `T009`, `T010`, `T012`, `T013`, `T015` en paralelo tras `T006`.
- **US1**: `T016` puede correr en paralelo con preparación de mapeos DTO.
- **US2**: `T021` y `T022` en paralelo.
- **US3**: `T027` y `T028` en paralelo.
- **Polish**: `T033`, `T034`, `T036`, `T038` en paralelo; `T035` y `T037` al final.

---

## Parallel Example: User Story 2

```bash
# Ejecutar en paralelo por dos desarrolladores:
Task: "T021 [US2] Implementar servicio de consulta por clave en backend/src/main/java/com/dsw02/empleado/application/ObtenerEmpleadoService.java"
Task: "T022 [US2] Implementar servicio de listado en backend/src/main/java/com/dsw02/empleado/application/ListarEmpleadosService.java"

# Luego integrar ambos en el controlador:
Task: "T023 [US2] Implementar endpoint GET /api/v1/empleados/{clave} en backend/src/main/java/com/dsw02/empleado/api/EmpleadoController.java"
Task: "T024 [US2] Implementar endpoint GET /api/v1/empleados en backend/src/main/java/com/dsw02/empleado/api/EmpleadoController.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1 (Setup).
2. Completar Phase 2 (Foundational).
3. Completar Phase 3 (US1).
4. Validar alta de empleado y errores de duplicado/validación.
5. Demo/release interno del MVP.

### Incremental Delivery

1. Setup + Foundational.
2. Entregar US1 (alta).
3. Entregar US2 (consulta/listado).
4. Entregar US3 (actualizar/eliminar).
5. Cerrar con Phase 6 (polish y validación final).

### Suggested MVP Scope

- MVP recomendado: **US1 solamente** (T001-T020).

---

## Notes

- Cada tarea sigue formato obligatorio `- [ ] T### [P?] [US?] Descripción con ruta de archivo`.
- Las tareas `[P]` están marcadas solo cuando no comparten archivo dependiente directo.
- Las historias son comprobables de forma independiente usando sus criterios de prueba.