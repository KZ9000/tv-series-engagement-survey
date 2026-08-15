# TV Series Engagement Survey

## Descripción general

**TV Series Engagement Survey** es una aplicación web tipo MVP orientada a medir qué tanto conectan los usuarios con determinadas series de televisión. El sistema permite registrar usuarios, iniciar sesión, consultar un catálogo de series y calificar series con un score entre 1 y 5.

## Tecnologías utilizadas

- **Java 21 LTS** - Lenguaje principal
- **Spring Boot** - Framework backend
- **Spring Web** - API REST
- **Spring Data JPA** / **Hibernate** - Persistencia
- **PostgreSQL** - Base de datos relacional
- **Spring Security** - Seguridad y autenticación
- **JWT** - Autenticación basada en tokens
- **Maven** - Gestión del proyecto
- **Jakarta Bean Validation** - Validaciones de datos
- **JUnit 5** / **Mockito** - Testing
- **Flyway** - Migraciones de base de datos

## Arquitectura por capas

```
Cliente
   │
   ▼
Controller → Service → Repository → PostgreSQL
```

## Endpoints API

### Autenticación (`/api/auth`)
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión y obtener JWT

### Series (`/api/series`)
- `GET /api/series` - Listar series activas (todos los usuarios)
- `GET /api/series/{id}` - Consultar una serie en particular
- `POST /api/series` - Crear nueva serie (ADMIN solo)
- `PUT /api/series/{id}` - Actualizar serie (ADMIN solo)
- `PATCH /api/series/{id}/status` - Activar/desactivar serie (ADMIN solo)

### Ratings (`/api/ratings`)
- `POST /api/ratings` - Crear una calificación (usuario autenticado)
  - Body: `{"seriesId": 1, "score": 5}`
  - Score válido: 1, 2, 3, 4, 5
  - No votar serie inactiva (409 Conflict)
  - No votar dos veces la misma serie (409 Conflict)

### Dashboard (`/api/dashboard`)
- `GET /api/dashboard` - Ver promedio y conteo de votos por serie

## Respuesta del Dashboard

```json
[
  {
    "seriesId": 1,
    "title": "Serie A",
    "averageScore": 4.6,
    "totalVotes": 120
  },
  {
    "seriesId": 2,
    "title": "Serie B",
    "averageScore": 4.2,
    "totalVotes": 95
  }
]
```

## Modelo de datos

### Entidades principales

**User**
- id (generado automáticamente)
- email (obligatorio, único)
- password (hashed, nunca en texto plano)
- role (USER o ADMIN)
- createdAt (generado al crear)

**Series**
- id
- title (obligatorio)
- description (opcional)
- releaseDate (fecha de estreno)
- active (determina si recibe votos)

**Rating**
- id
- score (1-5, validado)
- userId (fk → users)
- seriesId (fk → series)
- createdAt

### Restricciones DB
- `UNIQUE (user_id, series_id)` en tabla ratings
- `CHECK (score >= 1 AND score <= 5)`
- Foreign keys a users y series

## Roles y permisos

**USER**
- Consultar catálogo de series
- Consultar una serie
- Calificar una serie
- Consultar dashboard

**ADMIN**
- Consultar catálogo
- Consultar una serie
- Gestionar series (crear, actualizar, activar/desactivar)
- Consultar dashboard

## Validaciones principales

- Score debe estar entre 1 y 5 (@Min, @Max)
- Email debe ser único y tener formato válido (@Email, @NotBlank, @NotNull)
- Password obligatoria y será hasheada
- Título de serie es obligatorio (@NotBlank)
- No se permiten ratings duplicados por usuario y serie

## Seguridad

- Hashing de contraseñas con BCrypt
- Autenticación mediante JWT (tokens Bearer)
- Endpoints protegidos por Spring Security
- Control de roles por cada endpoint
- Contraseñas nunca almacenadas en texto plano
- Entidades JPA no expuestas directamente (usar DTOs)

## Migraciones

Versionadas con Flyway:
- V1__create_users_table.sql
- V2__create_series_table.sql
- V3__create_ratings_table.sql

## Testing

- Unit tests en Services (reglas de negocio principales)
- Integration tests flujos principales de API
- Casos de prueba:
  - crear rating correctamente
  - rechazar score inválido
  - rechazar rating duplicado
  - rechazar serie inexistente
  - rechazar serie inactiva
  - calcular dashboard

## Configuración local (desarrollo)

### Requisitos

- Java 21 LTS
- Maven 3.9+
- PostgreSQL 17 (servicio corriendo)

### Crear la base de datos

Ejecutar como superusuario `postgres` (psql):

```sql
CREATE ROLE tvsurvey WITH LOGIN PASSWORD 'cambiar-contrasena';
CREATE DATABASE netflix_engagement OWNER tvsurvey;
```

### Variables de entorno

Definir a nivel de usuario en PowerShell (una sola vez):

```powershell
[Environment]::SetEnvironmentVariable("DB_URL", "jdbc:postgresql://localhost:5432/netflix_engagement", "User")
[Environment]::SetEnvironmentVariable("DB_USERNAME", "tvsurvey", "User")
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

La API queda en `http://localhost:8080/api`.

### Ejecutar los tests

```powershell
mvn test
```

Los tests de integración usan H2 en modo PostgreSQL (perfil `test`), por lo que no requieren una instancia de PostgreSQL.

## Criterios de aceptación

- [x] Un usuario puede registrarse
- [x] Un usuario puede iniciar sesión
- [x] La contraseña no se almacena en texto plano
- [x] El sistema genera un JWT válido
- [x] Se pueden listar series activas
- [x] Un ADMIN puede crear/actualizar/activar/desactivar series
- [x] El score únicamente puede estar entre 1 y 5
- [x] El usuario no puede votar dos veces la misma serie
- [x] No se puede votar una serie inexistente
- [x] No se puede votar una serie inactiva
- [x] Se muestra el promedio y cantidad de votos por serie
- [x] El proyecto está organizado por capas
- [x] Se utilizan DTOs
- [x] Existe manejo global de errores
- [x] Las credenciales sensibles no forman parte del repositorio