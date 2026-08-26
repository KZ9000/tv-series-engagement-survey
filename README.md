# TV Series Engagement Survey

## Descripción general

**TV Series Engagement Survey** es una aplicación web tipo MVP orientada a medir qué tanto conectan los usuarios con determinadas series de televisión. El sistema permite registrar usuarios, iniciar sesión, consultar un catálogo de series y calificar series con un score entre 1 y 5.

## Flujo principal

```
Registro
   ↓
Login
   ↓
Catálogo de series
   ↓
Seleccionar serie
   ↓
Calificar de 1 a 5
   ↓
Evitar voto duplicado
   ↓
Dashboard
```

## Stack tecnológico

### Backend
- **Java 21 LTS** - Lenguaje principal
- **Spring Boot 3.5.16** - Framework backend
- **Spring Web** - API REST
- **Spring Data JPA** / **Hibernate** - Persistencia
- **PostgreSQL 17.11** - Base de datos relacional
- **Spring Security** - Seguridad y autenticación
- **JWT** - Autenticación basada en tokens
- **Flyway** - Migraciones de base de datos
- **Jakarta Bean Validation** - Validaciones de datos
- **Lombok** - Reducción de boilerplate
- **Swagger/OpenAPI** - Documentación de la API

### Testing
- **JUnit 5** - Framework de tests
- **Mockito** - Tests unitarios
- **MockMvc** - Tests de integración de API
- **H2** - Base de datos en memoria para tests

### Frontend
- **Thymeleaf** - Motor de templates server-side
- **HTML5** - Estructura de páginas
- **CSS** - Estilos propios (sin frameworks externos)
- **JavaScript vanilla** - Lógica del frontend (sin frameworks)

### Herramientas
- **Maven 3.9.9** - Gestión del proyecto
- **Git / GitHub** - Control de versiones

## Arquitectura

```
Browser
   │
   ▼
Thymeleaf + HTML/CSS/JavaScript
   │
   ▼
REST API
   │
   ▼
Controller → Service → Repository → PostgreSQL
```

Thymeleaf sirve las páginas web. JavaScript consume la API REST existente mediante `fetch`. No se duplica lógica de negocio en el frontend.

## Páginas

| Ruta | Descripción |
|------|-------------|
| `/` | Página de inicio. Muestra opciones de login/registro para invitados, o enlaces a series/dashboard para usuarios autenticados. |
| `/login` | Formulario de inicio de sesión. Consume `POST /api/auth/login`. |
| `/register` | Formulario de registro. Consume `POST /api/auth/register`. |
| `/series` | Catálogo de series activas. Consume `GET /api/series`. Muestra tarjetas con título, descripción, fecha de estreno, estado y botón para calificar. |
| `/rate` | Formulario de calificación para una serie específica. Selector de score 1-5. Consume `POST /api/ratings`. |
| `/dashboard` | Métricas de engagement: promedio de calificación y cantidad de votos por serie. Consume `GET /api/dashboard`. |

## Endpoints API

### Autenticación (`/api/auth`) — Públicos

- `POST /api/auth/register` - Registrar nuevo usuario (201 Created)
- `POST /api/auth/login` - Iniciar sesión y obtener JWT

### Series (`/api/series`) — Requiere autenticación

- `GET /api/series` - Listar series activas (USER o ADMIN)
- `GET /api/series/{id}` - Consultar una serie (USER o ADMIN)
- `POST /api/series` - Crear nueva serie (ADMIN solo)
- `PUT /api/series/{id}` - Actualizar serie (ADMIN solo)
- `PATCH /api/series/{id}/status` - Activar/desactivar serie (ADMIN solo)

### Ratings (`/api/ratings`) — Requiere autenticación

- `POST /api/ratings` - Crear una calificación
  - Body: `{"seriesId": 1, "score": 5}`
  - Score válido: 1, 2, 3, 4, 5
  - No votar serie inactiva (409 Conflict)
  - No votar dos veces la misma serie (409 Conflict)

### Dashboard (`/api/dashboard`) — Requiere autenticación

- `GET /api/dashboard` - Ver promedio y conteo de votos por serie

## Autenticación

- Backend **stateless** con JWT (no hay sesiones server-side).
- El cliente envía `Authorization: Bearer <token>` en cada request protegido.
- Roles: `ROLE_USER` y `ROLE_ADMIN`.
- `/api/auth/**` es público. El resto requiere autenticación.
- POST/PUT/PATCH a `/api/series/**` requiere rol ADMIN.

### Comportamiento del frontend ante JWT expirado

```
API devuelve 401
      ↓
Frontend detecta 401 (distinto de /auth/login y /auth/register)
      ↓
Elimina jwt_token de localStorage
      ↓
Redirige a /login
```

## Validaciones y reglas

- Score debe estar entre 1 y 5 (@Min, @Max).
- La serie debe existir (404 Not Found).
- La serie debe estar activa (409 Conflict).
- Un usuario no puede calificar dos veces la misma serie (409 Conflict).
- Usuario no autenticado recibe 401 Unauthorized.
- Usuario sin permisos administrativos recibe 403 Forbidden.

## Swagger/OpenAPI

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Tests

```
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Tests existentes

- Unit tests en RatingService (5 tests): crear rating, score inválido, rating duplicado, serie inexistente, serie inactiva.
- Unit test en DashboardService (1 test): calcular dashboard.
- Integration test (4 tests): flujo completo registro-login-serie-rating-dashboard, usuario sin rol admin no puede crear serie, email duplicado devuelve conflict, score fuera de rango devuelve bad request.

Los tests de integración usan H2 en modo PostgreSQL (perfil `test`), por lo que no requieren una instancia de PostgreSQL.

## Ejecución local

### Requisitos

- Java 21 LTS
- Maven 3.9+
- PostgreSQL 17 (servicio corriendo)

### Crear la base de datos

Ejecutar como superusuario `postgres` (psql):

```sql
CREATE ROLE tv_series_app WITH LOGIN PASSWORD 'cambiar-contrasena';
CREATE DATABASE tv_series_engagement OWNER tv_series_app;
```

### Variables de entorno

Definir a nivel de usuario en PowerShell (una sola vez):

```powershell
[Environment]::SetEnvironmentVariable("DB_URL", "jdbc:postgresql://localhost:5432/tv_series_engagement", "User")
[Environment]::SetEnvironmentVariable("DB_USERNAME", "tv_series_app", "User")
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "cambiar-contrasena", "User")
[Environment]::SetEnvironmentVariable("JWT_SECRET", "cadena-aleatoria-de-al-menos-32-caracteres", "User")
```

Opcional, para crear un ADMIN inicial al arrancar:

```powershell
[Environment]::SetEnvironmentVariable("ADMIN_EMAIL", "admin@email.com", "User")
[Environment]::SetEnvironmentVariable("ADMIN_PASSWORD", "Admin1234", "User")
```

Abrir una terminal nueva y comprobar con `echo $env:DB_URL`.

### Ejecutar

```powershell
mvn spring-boot:run
```

La aplicación queda en `http://localhost:8080`.

### Ejecutar los tests

```powershell
mvn test
```

## Estado del proyecto

El MVP actual está **funcional y probado localmente**.

### Funcionalidades completadas

- [x] Registro de usuarios
- [x] Inicio de sesión con JWT
- [x] Catálogo de series activas
- [x] Calificación de series (score 1-5)
- [x] Prevención de voto duplicado
- [x] Dashboard con métricas
- [x] Frontend con Thymeleaf
- [x] Manejo de JWT expirado
- [x] Diseño responsive
- [x] Swagger/OpenAPI
- [x] Tests (10/10 OK)

### Pruebas manuales realizadas

- [x] Registro exitoso
- [x] Login con credenciales correctas
- [x] Login con credenciales incorrectas (muestra error)
- [x] Catálogo de series
- [x] Calificar una serie
- [x] Intentar calificar dos veces (muestra 409)
- [x] Dashboard con métricas
- [x] Logout
- [x] Acceso sin autenticación (redirige a login)
- [x] JWT expirado (redirige a login)
- [x] Diseño responsive en móvil
