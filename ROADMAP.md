# ROADMAP.md — TV Series Engagement Survey

Roadmap de desarrollo del MVP. Cada fase debe dejar una versión funcional antes de avanzar.

## FASE 1: Configuración inicial del proyecto Spring Boot
- [x] Crear proyecto Maven con Java 21 (`pom.xml`, Spring Boot 3.5.16).
- [x] Configurar dependencias: Spring Web, Spring Data JPA, PostgreSQL, Spring Security, JWT (jjwt 0.12.6), Flyway, Lombok.
- [x] Configurar `application.yml` con variables de entorno: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`.
- [x] Crear `.gitignore` (excluye `target/`, `.idea/`, `.env`, secrets).
- [x] Paquete raíz oficial: `com.example.tvseriesengagementsurvey`.

## FASE 2: Base de datos y migraciones Flyway
- [x] Crear base de datos `tv_series_engagement` con usuario dedicado (`tv_series_app`).
- [x] Migraciones versionadas en `src/main/resources/db/migration`:
  - `V1__create_users_table.sql`
  - `V2__create_series_table.sql`
  - `V3__create_ratings_table.sql`
- [x] Restricciones en BD: `UNIQUE (user_id, series_id)`, `CHECK (score 1-5)`, FKs.
- [x] Configurar Flyway en `application.yml` (`ddl-auto: none`).

## FASE 3: Entidades y relaciones JPA
- [x] Entidades: `User`, `Series`, `Rating`, enum `Role` (USER, ADMIN).
- [x] Relaciones: User 1:N Rating, Series 1:N Rating.
- [x] Campos `createdAt` con `@CreationTimestamp`.
- [x] Repositorios: `UserRepository`, `SeriesRepository`, `RatingRepository`.

## FASE 4: Catálogo de series (CRUD admin)
- [x] `GET /api/series` (activas, autenticado) y `GET /api/series/{id}`.
- [x] `POST /api/series`, `PUT /api/series/{id}`, `PATCH /api/series/{id}/status` (ADMIN).
- [x] Desactivación lógica con campo `active` (no se elimina la fila).
- [x] DTOs: `CreateSeriesRequest`, `UpdateSeriesRequest`, `UpdateSeriesStatusRequest`, `SeriesResponse`.

## FASE 5: Autenticación y JWT
- [x] `POST /api/auth/register` (crea usuario con rol USER, email único, 201 Created).
- [x] `POST /api/auth/login` (retorna `{token, tokenType: "Bearer"}`).
- [x] `UserDetailsService` + `DaoAuthenticationProvider` + `BCryptPasswordEncoder`.
- [x] `JwtService` (generación y validación, HS256, expiración 2h por defecto).
- [x] `JwtAuthenticationFilter` (valida Bearer token en cada request protegido).
- [x] `SecurityConfig` stateless; `/api/auth/**` público; resto autenticado.
- [x] Seed opcional de ADMIN vía variables `ADMIN_EMAIL` / `ADMIN_PASSWORD`.
- [x] Verificado: al arrancar se crea el ADMIN en PostgreSQL (rol `ADMIN`), login OK y puede crear series; un USER normal recibe 403.

## FASE 6: Ratings y reglas de negocio
- [x] `POST /api/ratings` (usuario obtenido del JWT, nunca del body).
- [x] Validaciones de `RatingService`:
  - Score 1-5 (Bean Validation + guarda defensiva en service).
  - Serie debe existir (404).
  - Serie debe estar activa (409).
  - Un voto por usuario y serie (409 duplicado).
- [x] Protección final en PostgreSQL con `UNIQUE (user_id, series_id)`.

## FASE 7: Dashboard de métricas
- [x] `GET /api/dashboard` con `AVG(score)` y `COUNT(*)` agrupado por serie.
- [x] Respuesta: `seriesId`, `title`, `averageScore`, `totalVotes`.
- [x] Fuente de verdad: tabla `ratings` (el promedio no se almacena).

## FASE 8: Manejo de errores y validaciones
- [x] `GlobalExceptionHandler` con `@RestControllerAdvice`.
- [x] Excepciones: `ResourceNotFoundException` (404), `DuplicateRatingException` (409), `InactiveSeriesException` (409), `EmailAlreadyExistsException` (409).
- [x] Formato estándar: `{timestamp, status, message}`.
- [x] 401 para no autenticado y 403 para roles insuficientes.
- [x] Validaciones Jakarta en DTOs de entrada.

## FASE 9: Tests básicos
- [x] Unit tests (JUnit 5 + Mockito) en Services:
  - crear rating correctamente
  - rechazar score inválido
  - rechazar rating duplicado
  - rechazar serie inexistente
  - rechazar serie inactiva
  - calcular dashboard
- [x] Integration test (MockMvc + H2 en modo PostgreSQL): registro, login, creación de serie (ADMIN), rating, duplicado 409 y dashboard.
- [x] Verificado: `mvn test` compila y corre 10/10 tests OK.
- [x] Verificado: la app arranca contra PostgreSQL 17.11 (Flyway aplica V1-V3) y los endpoints responden (register 201, login JWT, /api/series 200/401).

## FASE 10: Swagger/OpenAPI
- [x] Agregada dependencia `springdoc-openapi-starter-webmvc-ui:2.8.17`.
- [x] Creado `OpenApiConfig.java` con esquema de seguridad `bearerAuth` (HTTP Bearer JWT).
- [x] Anotaciones `@Tag`, `@Operation`, `@SecurityRequirement` en todos los controllers.
- [x] `SecurityConfig` actualizado: `permitAll` para `/swagger-ui/**`, `/v3/api-docs/**`, `/webjars/**`.
- [x] Verificado: Swagger UI accesible en `http://localhost:8080/swagger-ui.html`.

## FASE 11: Frontend Thymeleaf
- [x] Agregada dependencia `spring-boot-starter-thymeleaf`.
- [x] Creado `WebController.java` con mappings para páginas web.
- [x] `SecurityConfig` actualizado: `permitAll` para `/`, `/login`, `/register`, `/series`, `/rate`, `/dashboard`, `/css/**`, `/js/**`.
- [x] Creados templates Thymeleaf: `index.html`, `login.html`, `register.html`, `series.html`, `rate.html`, `dashboard.html`.
- [x] Creado CSS propio (`style.css`): dark theme, responsive, sin frameworks externos.
- [x] Creado JavaScript vanilla (`app.js`): JWT en localStorage, fetch API, manejo de errores, nav dinámica.
- [x] Manejo de JWT expirado: detecta 401, limpia token, redirige a `/login`.
- [x] Verificado: todas las páginas retornan 200, Swagger funciona, API responde correctamente.
- [x] Pruebas manuales: registro, login, catálogo, rating, voto duplicado, dashboard, logout.

## FASE 12: Documentación y portfolio
- [x] Asegurar que credenciales no estén en Git (env vars).
- [x] Documentar configuración local en README.
- [x] Verificar estructura por capas y uso de DTOs.
- [x] Actualizar README.md, SPECS.md, ROADMAP.md, CODESTYLE.md con estado actual.

## Mejoras futuras (opcionales)

Estas mejoras no son requisitos para el MVP, pero podrían implementarse:

- [ ] Paginación del catálogo de series.
- [ ] Loading indicators / spinners más elaborados.
- [ ] Validaciones client-side más detalladas.
- [ ] Otras mejoras visuales opcionales.
- [ ] Cloud deployment (pendiente).
