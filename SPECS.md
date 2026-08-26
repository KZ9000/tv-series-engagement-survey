# SPECS.md — TV Series Engagement Survey

## 1. Descripción del proyecto

**TV Series Engagement Survey** es una aplicación web tipo MVP orientada a medir qué tanto conectan los usuarios con determinadas series.

El sistema permitirá:

* Registrar usuarios mediante email y contraseña.
* Iniciar sesión.
* Consultar un catálogo de series.
* Calificar una serie con un score entre 1 y 5.
* Evitar que un usuario califique dos veces la misma serie.
* Administrar las series mediante un usuario administrador.
* Consultar un dashboard con el promedio y la cantidad de votos por serie.

El proyecto está pensado como una **demo de portfolio para practicar y demostrar conocimientos de Java y Spring Boot**.

El objetivo no es construir una plataforma comercial de streaming, sino desarrollar un backend pequeño, coherente y técnicamente defendible.

---

# 2. Objetivos del MVP

El proyecto debe permitir demostrar conocimientos de:

* Java 21 LTS.
* Spring Boot.
* API REST.
* Spring Data JPA.
* Hibernate.
* PostgreSQL.
* Spring Security.
* JWT.
* Validaciones.
* Manejo de excepciones.
* Relaciones entre entidades.
* Consultas SQL/JPA.
* Arquitectura por capas.
* Tests básicos.
* Git y GitHub.

La prioridad será:

> **Simplicidad + buenas prácticas + funcionalidad completa.**

No se implementarán tecnologías o patrones que no sean necesarios para el alcance del MVP.

---

# 3. Alcance

## 3.1 Funcionalidades incluidas

### Usuarios

* Registro.
* Login.
* Autenticación mediante JWT.
* Roles `USER` y `ADMIN`.

### Series

* Listar series activas.
* Consultar una serie.
* Crear series.
* Actualizar series.
* Activar/desactivar series.

### Calificaciones

* Crear una calificación.
* Score de 1 a 5.
* Un usuario solo puede calificar una vez cada serie.
* No se permiten nuevas calificaciones para series inactivas.

### Dashboard

Por cada serie:

* Promedio de calificación.
* Cantidad de votos.

---

# 4. Funcionalidades fuera del MVP

No se implementarán:

* Comentarios.
* Likes/dislikes.
* Recomendaciones.
* Algoritmos de personalización.
* Seguimiento de reproducciones.
* Perfiles avanzados.
* Notificaciones.
* Chat.
* Microservicios.
* Redis.
* Kafka.
* Docker.
* Kubernetes.
* CI/CD.
* Load balancers.
* Réplicas de PostgreSQL.
* Materialized Views.
* Caché distribuida.
* Machine Learning.
* Analytics avanzados.

Estas funcionalidades pueden mencionarse como futuras evoluciones, pero no forman parte del desarrollo inicial.

---

# 5. Stack tecnológico

| Tecnología              | Uso                          |
| ----------------------- | ---------------------------- |
| Java 21 LTS             | Lenguaje principal           |
| Spring Boot 3.5.16      | Framework backend            |
| Spring Web              | API REST                     |
| Spring Data JPA         | Persistencia                 |
| Hibernate               | ORM                          |
| PostgreSQL 17.11        | Base de datos                |
| Spring Security         | Seguridad                    |
| JWT                     | Autenticación                |
| Maven 3.9.9             | Gestión del proyecto         |
| Jakarta Bean Validation | Validaciones                 |
| Flyway                  | Migraciones de BD            |
| Lombok                  | Reducción de boilerplate     |
| Swagger/OpenAPI         | Documentación de la API      |
| JUnit 5                 | Tests                        |
| Mockito                 | Tests unitarios              |
| MockMvc                 | Tests de integración         |
| H2                      | BD en memoria para tests     |
| Thymeleaf               | Templates server-side        |
| HTML5                   | Estructura de páginas        |
| CSS                     | Estilos propios              |
| JavaScript vanilla      | Lógica del frontend          |
| Git                     | Control de versiones         |
| GitHub                  | Repositorio                  |

Las dependencias utilizarán versiones estables y compatibles con Java 21.

No se utilizan frameworks frontend como React, Angular, Vue ni Vite.

---

# 6. Arquitectura

Se utilizará una arquitectura monolítica sencilla basada en capas.

```text
Browser
   │
   ▼
Thymeleaf + HTML/CSS/JavaScript
   │
   ▼
REST API
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
PostgreSQL
```

## Frontend (Thymeleaf)

Responsabilidades:

* Servir las páginas web (HTML).
* JavaScript consume la API REST existente mediante `fetch`.
* Almacenar el JWT en `localStorage` para requests autenticados.
* Redirigir a `/login` cuando el JWT expira (401).
* No duplicar lógica de negocio del backend.

## Controller (REST API)

Responsabilidades:

* Recibir requests HTTP.
* Validar la entrada mediante DTOs.
* Invocar los servicios.
* Devolver respuestas HTTP (JSON).

No deberá contener lógica de negocio.

## Controller (Web)

Responsabilidades:

* Servir las páginas Thymeleaf.
* Mapear rutas del frontend (`/`, `/login`, `/register`, `/series`, `/rate`, `/dashboard`).
* No contener lógica de negocio.

## Service

Responsabilidades:

* Implementar reglas de negocio.
* Coordinar repositories.
* Gestionar operaciones relacionadas con usuarios, series y calificaciones.

## Repository

Responsabilidades:

* Acceso a la base de datos.
* Consultas mediante Spring Data JPA.
* Consultas personalizadas cuando sean necesarias.

## Entity

Representación de las tablas de PostgreSQL mediante JPA.

## DTO

Objetos utilizados para entrada y salida de información de la API.

Las entidades JPA no se expondrán directamente.

---

# 7. Modelo de datos

El MVP tendrá tres entidades principales:

```text
User
  │
  │ 1
  │
  │ N
  ▼
Rating
  ▲
  │ N
  │
  │ 1
  │
Series
```

---

# 8. Entidad User

Representa un usuario registrado.

## Campos

```text
id
email
password
role
createdAt
```

## Reglas

* `id` será generado automáticamente.
* `email` será obligatorio.
* `email` será único.
* La contraseña será obligatoria.
* La contraseña no se almacenará en texto plano.
* `role` tendrá inicialmente los valores:

  * `USER`
  * `ADMIN`
* `createdAt` será generado al crear el usuario.

---

# 9. Entidad Series

Representa una serie disponible en el catálogo.

## Campos

```text
id
title
description
releaseDate
active
```

## Reglas

* `title` es obligatorio.
* `description` es opcional.
* `releaseDate` representa la fecha de estreno.
* `active` determina si la serie está disponible para recibir votos.
* Una serie inactiva no podrá recibir nuevas calificaciones.

Las series se desactivarán mediante `active = false` en lugar de eliminarse físicamente.

---

# 10. Entidad Rating

Representa la calificación de un usuario sobre una serie.

## Campos

```text
id
score
userId
seriesId
createdAt
```

## Reglas

* `score` debe estar entre 1 y 5.
* Cada rating pertenece a un usuario.
* Cada rating pertenece a una serie.
* Un usuario solamente puede tener un rating por serie.
* No se permitirá modificar un rating en el MVP.
* No se permitirá eliminar un rating desde la API pública.

---

# 11. Relaciones

## User → Rating

Un usuario puede realizar múltiples calificaciones.

```text
User 1 ─────── N Rating
```

## Series → Rating

Una serie puede recibir múltiples calificaciones.

```text
Series 1 ─────── N Rating
```

---

# 12. Restricción contra votos duplicados

La regla principal será:

> Un usuario puede votar varias series, pero solamente una vez cada serie.

Ejemplo válido:

```text
Usuario 1 → Serie A → 5
Usuario 1 → Serie B → 4
Usuario 1 → Serie C → 3
```

Ejemplo inválido:

```text
Usuario 1 → Serie A → 5
Usuario 1 → Serie A → 4
```

La segunda operación deberá ser rechazada.

---

# 13. Integridad de la base de datos

La prevención de duplicados tendrá dos niveles.

## Aplicación

Antes de guardar un rating se comprobará si ya existe:

```text
userId + seriesId
```

## PostgreSQL

La tabla `ratings` tendrá:

```sql
UNIQUE (user_id, series_id)
```

La restricción de PostgreSQL será la protección definitiva frente a solicitudes concurrentes.

---

# 14. PostgreSQL

La base de datos será:

```text
PostgreSQL
```

Nombre sugerido:

```text
tv_series_engagement
```

PostgreSQL se utilizará como base de datos relacional principal.

La elección se realiza por:

* Buen soporte para aplicaciones Java/Spring.
* Integridad referencial.
* Constraints.
* Índices.
* Consultas agregadas.
* Buen manejo de transacciones.
* Facilidad para evolucionar el proyecto en caso de aumentar su tamaño.

No se implementarán características de escalabilidad avanzada dentro del MVP.

---

# 15. Tablas

## users

```text
id
email
password
role
created_at
```

Restricciones:

```text
PRIMARY KEY (id)
UNIQUE (email)
NOT NULL (email)
NOT NULL (password)
NOT NULL (role)
```

---

## series

```text
id
title
description
release_date
active
```

Restricciones:

```text
PRIMARY KEY (id)
NOT NULL (title)
NOT NULL (active)
```

---

## ratings

```text
id
score
user_id
series_id
created_at
```

Restricciones:

```text
PRIMARY KEY (id)

FOREIGN KEY (user_id)
REFERENCES users(id)

FOREIGN KEY (series_id)
REFERENCES series(id)

CHECK (score >= 1 AND score <= 5)

UNIQUE (user_id, series_id)
```

---

# 16. Índices

PostgreSQL generará índices para las restricciones necesarias.

Además, se podrán agregar índices sobre columnas utilizadas frecuentemente en consultas.

No se crearán índices de forma indiscriminada.

La estrategia será mantener únicamente los índices necesarios para el MVP.

---

# 17. Autenticación

Se utilizará:

```text
Spring Security
+
JWT
```

Flujo:

```text
Registro
   ↓
Usuario almacenado
   ↓
Login
   ↓
Spring Security valida credenciales
   ↓
JWT
   ↓
Requests protegidos
```

Las contraseñas serán almacenadas utilizando hashing seguro.

Nunca se almacenarán contraseñas en texto plano.

---

# 18. Roles

Existirán dos roles:

## USER

Puede:

* Consultar el catálogo.
* Consultar una serie.
* Calificar una serie.
* Consultar el dashboard.

## ADMIN

Puede:

* Consultar el catálogo.
* Consultar una serie.
* Gestionar series.
* Consultar el dashboard.

No se implementará un sistema avanzado de permisos.

---

# 19. API REST

Base URL:

```text
/api
```

---

# 20. Autenticación

## Registro

```http
POST /api/auth/register
```

Request:

```json
{
  "email": "usuario@email.com",
  "password": "Password123"
}
```

Respuesta esperada:

```text
201 Created
```

---

## Login

```http
POST /api/auth/login
```

Request:

```json
{
  "email": "usuario@email.com",
  "password": "Password123"
}
```

Respuesta:

```json
{
  "token": "...",
  "tokenType": "Bearer"
}
```

---

# 21. API de Series

## Listar series

```http
GET /api/series
```

Obtendrá las series activas.

---

## Obtener una serie

```http
GET /api/series/{id}
```

---

## Crear una serie

```http
POST /api/series
```

Requiere:

```text
ADMIN
```

Request:

```json
{
  "title": "Nueva serie",
  "description": "Descripción",
  "releaseDate": "2026-08-15"
}
```

---

## Actualizar una serie

```http
PUT /api/series/{id}
```

Requiere:

```text
ADMIN
```

---

## Activar/desactivar serie

```http
PATCH /api/series/{id}/status
```

Requiere:

```text
ADMIN
```

El estado será representado mediante:

```text
active = true
active = false
```

---

# 22. API de Ratings

## Crear rating

```http
POST /api/ratings
```

Requiere autenticación.

El usuario será obtenido a partir del JWT.

El cliente no enviará `userId`.

Request:

```json
{
  "seriesId": 1,
  "score": 5
}
```

---

# 23. Reglas de Rating

## Score válido

```text
1
2
3
4
5
```

Valores fuera del rango serán rechazados.

---

## Serie inexistente

Si la serie no existe:

```text
404 Not Found
```

---

## Serie inactiva

Si la serie está inactiva:

```text
409 Conflict
```

No se permitirá crear nuevos ratings.

---

## Rating duplicado

Si el usuario ya calificó esa serie:

```text
409 Conflict
```

---

# 24. Dashboard

El dashboard será deliberadamente sencillo.

Mostrará únicamente:

```text
Serie
Promedio
Cantidad de votos
```

Ejemplo:

```text
Serie A
Promedio: 4.6
Votos: 120

Serie B
Promedio: 4.2
Votos: 95
```

---

# 25. Endpoint del Dashboard

```http
GET /api/dashboard
```

Respuesta:

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

---

# 26. Cálculo del Dashboard

Las métricas se calcularán a partir de la tabla `ratings`.

Conceptualmente:

```sql
SELECT
    series_id,
    AVG(score),
    COUNT(*)
FROM ratings
GROUP BY series_id;
```

No se almacenará inicialmente el promedio dentro de `series`.

Las calificaciones serán la fuente de verdad.

---

# 27. Validación

Se utilizará:

```text
Jakarta Bean Validation
```

Ejemplos:

```text
@NotBlank
@NotNull
@Email
@Size
@Min
@Max
```

Las validaciones se realizarán principalmente sobre DTOs.

---

# 28. DTOs principales

Se crearán DTOs para evitar exponer las entidades JPA directamente.

```text
RegisterRequest
LoginRequest
LoginResponse

CreateSeriesRequest
UpdateSeriesRequest
SeriesResponse

CreateRatingRequest
RatingResponse

DashboardResponse

ErrorResponse
```

---

# 29. Manejo de errores

Se utilizará un manejador global mediante:

```text
@RestControllerAdvice
```

Formato estándar:

```json
{
  "timestamp": "2026-08-12T10:30:00",
  "status": 404,
  "message": "Series not found"
}
```

Errores principales:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
500 Internal Server Error
```

---

# 30. Excepciones de negocio

Se podrán utilizar excepciones específicas como:

```text
ResourceNotFoundException
DuplicateRatingException
InactiveSeriesException
```

Estas excepciones serán convertidas a respuestas HTTP apropiadas mediante el manejador global.

---

# 31. Transacciones

Las operaciones que modifiquen datos utilizarán transacciones cuando sea necesario.

Particularmente:

* Creación de usuarios.
* Creación de series.
* Actualización de series.
* Creación de ratings.

La finalidad es garantizar consistencia en las operaciones de persistencia.

---

# 32. Migraciones

Se utilizará:

```text
Flyway
```

Las migraciones estarán versionadas.

Ejemplo:

```text
V1__create_users_table.sql
V2__create_series_table.sql
V3__create_ratings_table.sql
```

El esquema de PostgreSQL no dependerá exclusivamente de modificaciones automáticas de Hibernate.

---

# 33. Estructura del proyecto

```text
src/
└── main/
    ├── java/
    │   └── com.example.tvseriesengagementsurvey/
    │
    │       ├── controller/
    │       │   ├── AuthController.java
    │       │   ├── SeriesController.java
    │       │   ├── RatingController.java
    │       │   ├── DashboardController.java
    │       │   └── WebController.java
    │       │
    │       ├── service/
    │       │   ├── AuthService.java
    │       │   ├── SeriesService.java
    │       │   ├── RatingService.java
    │       │   └── DashboardService.java
    │       │
    │       ├── repository/
    │       │   ├── UserRepository.java
    │       │   ├── SeriesRepository.java
    │       │   └── RatingRepository.java
    │       │
    │       ├── entity/
    │       │   ├── User.java
    │       │   ├── Series.java
    │       │   ├── Rating.java
    │       │   └── Role.java
    │       │
    │       ├── dto/
    │       │   ├── auth/
    │       │   ├── series/
    │       │   ├── rating/
    │       │   └── dashboard/
    │       │
    │       ├── exception/
    │       │   ├── ResourceNotFoundException.java
    │       │   ├── DuplicateRatingException.java
    │       │   ├── InactiveSeriesException.java
    │       │   ├── EmailAlreadyExistsException.java
    │       │   └── GlobalExceptionHandler.java
    │       │
    │       ├── security/
    │       │   ├── SecurityConfig.java
    │       │   ├── SecurityBeansConfig.java
    │       │   ├── JwtService.java
    │       │   └── JwtAuthenticationFilter.java
    │       │
    │       └── config/
    │           └── OpenApiConfig.java
    │
    └── resources/
        ├── application.yml
        ├── templates/
        │   ├── index.html
        │   ├── login.html
        │   ├── register.html
        │   ├── series.html
        │   ├── rate.html
        │   └── dashboard.html
        ├── static/
        │   ├── css/
        │   │   └── style.css
        │   └── js/
        │       └── app.js
        └── db/
            └── migration/
```

---

# 34. Testing

El MVP tendrá pruebas básicas.

## Unit tests

Se priorizarán las reglas de negocio de los Services.

Casos principales:

```text
crear rating correctamente
rechazar score inválido
rechazar rating duplicado
rechazar serie inexistente
rechazar serie inactiva
calcular dashboard
```

## Integration tests

Se realizarán únicamente los necesarios para comprobar los flujos principales de la API.

No se implementará infraestructura de testing avanzada en el MVP.

---

# 35. Configuración

Las credenciales de PostgreSQL y secretos de JWT no se almacenarán directamente en Git.

Se utilizarán variables de entorno para valores sensibles.

Ejemplo:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

El archivo `application.yml` utilizará dichas variables.

---

# 36. Seguridad básica

El MVP deberá cumplir:

* Hashing de contraseñas.
* JWT.
* Protección de endpoints.
* Control de roles.
* Validación de datos.
* No exponer contraseñas.
* No guardar secretos en Git.
* No exponer entidades JPA directamente.

No se implementarán mecanismos avanzados de seguridad fuera del alcance del MVP.

---

# 37. Escalabilidad futura

La escalabilidad no será implementada como parte del MVP.

Sin embargo, las decisiones de diseño deberán evitar dificultar una evolución posterior.

Ejemplos de posibles mejoras futuras:

```text
MVP
 │
 ├── Optimización de queries
 ├── Índices adicionales
 ├── Caché
 ├── Réplicas de lectura
 └── Escalamiento horizontal
```

Si el proyecto creciera significativamente, podría evolucionar desde un monolito hacia una arquitectura más distribuida.

La decisión se tomaría según necesidades reales y no únicamente por introducir tecnología adicional.

---

# 38. Principios de desarrollo

Durante la implementación se seguirán estos principios:

* Mantener el proyecto pequeño.
* Evitar sobreingeniería.
* Separar responsabilidades.
* Mantener la lógica de negocio en Services.
* Utilizar DTOs.
* Utilizar restricciones de PostgreSQL para proteger la integridad.
* Validar los datos recibidos.
* Escribir código fácil de entender.
* Priorizar funcionalidad sobre complejidad.
* Agregar nuevas tecnologías únicamente cuando resuelvan una necesidad real.

---

# 39. Criterios de aceptación

El MVP estará terminado cuando se cumpla lo siguiente:

### Usuarios

* Un usuario puede registrarse.
* Un usuario puede iniciar sesión.
* La contraseña no se almacena en texto plano.
* El sistema genera un JWT válido.

### Series

* Se pueden listar las series activas.
* Se puede consultar una serie.
* Un ADMIN puede crear una serie.
* Un ADMIN puede actualizar una serie.
* Un ADMIN puede activar/desactivar una serie.

### Ratings

* Un usuario autenticado puede votar.
* El score únicamente puede estar entre 1 y 5.
* El usuario no puede votar dos veces la misma serie.
* No se puede votar una serie inexistente.
* No se puede votar una serie inactiva.

### Dashboard

* Se muestra el promedio de cada serie.
* Se muestra la cantidad de votos de cada serie.
* Los valores corresponden a los datos almacenados en PostgreSQL.

### Frontend

* Existen páginas HTML para el flujo completo.
* Thymeleaf sirve las páginas web.
* JavaScript consume la API REST existente.
* JWT se almacena en `localStorage`.
* Se envía `Authorization: Bearer` en requests autenticados.
* Si el JWT expira, se redirige a `/login`.
* El diseño es responsive.
* No se utilizan frameworks frontend externos.

### Código

* El proyecto está organizado por capas.
* Se utilizan DTOs.
* Existe manejo global de errores.
* Las reglas principales tienen tests.
* Las credenciales sensibles no forman parte del repositorio.

---

# 40. Roadmap de desarrollo

El desarrollo seguirá este orden:

```text
FASE 1
Crear proyecto Spring Boot
        ↓
FASE 2
Configurar PostgreSQL y Flyway
        ↓
FASE 3
Crear entidades y relaciones
        ↓
FASE 4
Implementar catálogo de series
        ↓
FASE 5
Implementar registro y login
        ↓
FASE 6
Configurar Spring Security + JWT
        ↓
FASE 7
Implementar ratings
        ↓
FASE 8
Implementar reglas de negocio
        ↓
FASE 9
Implementar dashboard
        ↓
FASE 10
Swagger/OpenAPI
        ↓
FASE 11
Frontend Thymeleaf
        ↓
FASE 12
Documentación y preparación del portfolio
```

Cada fase deberá producir una versión funcional antes de avanzar a la siguiente.

---

# 41. Decisión arquitectónica final

La aplicación será desarrollada como un:

> **Monolito MVP con Java 21 + Spring Boot + PostgreSQL.**

La arquitectura por capas permitirá mantener el proyecto organizado sin introducir complejidad innecesaria.

El sistema será suficientemente pequeño para construirlo y explicarlo con claridad, pero incorporará prácticas importantes de backend:

* API REST.
* Persistencia relacional.
* Autenticación.
* Autorización.
* DTOs.
* Validación.
* Reglas de negocio.
* Integridad de datos.
* Consultas agregadas.
* Testing.

El objetivo final es que el proyecto pueda presentarse en un portfolio como una aplicación **pequeña pero técnicamente sólida**, donde cada decisión tenga una razón y pueda explicarse fácilmente durante una entrevista.
