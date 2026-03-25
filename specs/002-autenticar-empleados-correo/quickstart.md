# Quickstart - Autenticacion de empleados por correo y contrasena

## Prerrequisitos

- Java 17
- Docker y Docker Compose
- Maven 3.9+

## Variables de entorno

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `APP_BASIC_USER` (default desarrollo: `admin`)
- `APP_BASIC_PASSWORD` (default desarrollo: `admin123`)

Ejemplo:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=empleados_db
export DB_USER=empleados_user
export DB_PASSWORD=empleados_pass
export APP_BASIC_USER=admin
export APP_BASIC_PASSWORD=admin123
```

## Levantar stack con Docker

```bash
docker compose up --build
```

## Ejecucion local

1. Levantar postgres:

```bash
docker compose up -d postgres
```

2. Iniciar backend:

```bash
cd backend
mvn spring-boot:run
```

## Flujo de validacion funcional

### 1) Comprobar endpoint publico

```bash
curl http://localhost:8080/actuator/health
```

### 2) Comprobar que ruta de negocio exige autenticacion

```bash
curl -i http://localhost:8080/api/v1/empleados/auth/me
```

Esperado: `401 Unauthorized` sin credenciales.

### 3) Autenticar con empleado (correo/contrasena)

```bash
curl -u "empleado@empresa.com:MiPassword123" \
  http://localhost:8080/api/v1/empleados/auth/me
```

Esperado: `200 OK` con perfil del empleado autenticado.

### 4) Probar credenciales invalidas

```bash
curl -i -u "empleado@empresa.com:incorrecta" \
  http://localhost:8080/api/v1/empleados/auth/me
```

Esperado: rechazo de autenticacion con respuesta generica.

### 5) Verificar endpoints existentes con paginacion default

```bash
curl -u "empleado@empresa.com:MiPassword123" \
  "http://localhost:8080/api/v1/empleados?page=0"
```

Esperado: listado paginado con `size=5` por defecto cuando se omite.

### 6) Verificar que correo no se expone en response general de empleado

```bash
curl -u "empleado@empresa.com:MiPassword123" \
  "http://localhost:8080/api/v1/empleados?page=0&size=5"
```

Esperado: cada item de listado no incluye campo `correo`.

### 7) Verificar politica de contrasena en alta

```bash
curl -i -u "$APP_BASIC_USER:$APP_BASIC_PASSWORD" \
  -H "Content-Type: application/json" \
  -X POST http://localhost:8080/api/v1/empleados \
  -d '{
    "nombre":"Empleado Demo",
    "direccion":"Calle Demo 1",
    "telefono":"5551231234",
    "correo":"demo@empresa.com",
    "contrasena":"123"
  }'
```

Esperado: rechazo por politica minima (longitud < 8).

### 8) Verificar control de autorizacion en cambio de estado

```bash
curl -i -u "empleado@empresa.com:MiPassword123" \
  -H "Content-Type: application/json" \
  -X PATCH http://localhost:8080/api/v1/empleados/EMP-1001/estado \
  -d '{"activo":false}'
```

Esperado: `403 Forbidden` para actor autenticado que no es `admin` bootstrap.

### 9) Verificar idempotencia de cambio de estado

```bash
curl -i -u "$APP_BASIC_USER:$APP_BASIC_PASSWORD" \
  -H "Content-Type: application/json" \
  -X PATCH http://localhost:8080/api/v1/empleados/EMP-1001/estado \
  -d '{"activo":true}'
```

Esperado: `200 OK` con payload consistente (`clave`, `activo`) cuando el estado solicitado coincide con el actual.

### 10) Verificar objetivo de performance (SC-001)

Ejecutar prueba de carga baja/moderada (hasta 50 rps) sobre:

- `GET /api/v1/empleados/auth/me`
- endpoint de autenticacion equivalente en flujo Basic Auth

Esperado: p95 <= 200 ms en ambiente de integracion.

## Pruebas automatizadas

```bash
cd backend
mvn test
```

## Swagger/OpenAPI

- UI: `http://localhost:8080/swagger-ui.html`
- Contrato de esta feature: `specs/002-autenticar-empleados-correo/contracts/empleados-auth-openapi.yaml`

## Alcance explicito

- Este feature no incluye cambio de contrasena.
- El correo solo se expone en `GET /api/v1/empleados/auth/me`.
- El endpoint `PATCH /api/v1/empleados/{clave}/estado` solo permite gestion por el usuario bootstrap `admin`.