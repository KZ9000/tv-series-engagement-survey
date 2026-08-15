# ROADMAP.md — TV Series Engagement Survey

## Development Phases

### FASE 1: Configuración inicial del proyecto Spring Boot
- Crear proyecto Maven con Java 21
- Configurar dependencias: Spring Web, Spring Data JPA, PostgreSQL, Spring Security, JWT
- Configurar application.yml con variables de entorno

### FASE 2: Base de datos y migraciones Flyway
- Configurar PostgreSQL database `netflix_engagement`
- Crear migraciones iniciales: V1__users, V2__series, V3__ratings
- Configurar Flyway en application.yml

### FASE 3: Entidades y relaciones JPA
- Implementar entidades: User, Series, Rating, Role
- Definir relaciones: User 1:N Rating, Series 1:N Rating
- Agregar restricción UNIQUE (user_id, series_id) en Rating
- Configurar hash de contraseñas (BCrypt)

### FASE 4: Catálogo de series (CRUD admin)
- SeriesController: GET /api/series, GET /api/series/{id}
- SeriesService y SeriesRepository
- POST /api/series (ADMIN), PUT /api/series/{id} (ADMIN), PATCH /api/series/{id}/status (ADMIN)
- Lógica: series activas/inactivas con campo `active`

### FASE 5: Autenticación y JWT
- AuthController: POST /api/auth/register, POST /api/auth/login
- UserDetailsService y usuarioDetails
- JwtService para generación y validación de tokens
- JwtAuthenticationFilter para seguridad de endpoints

### FASE 6: Ratings y reglas de negocio
- RatingController: POST /api/ratings
- RatingService con validaciones principales:
  - Score debe ser 1-5 (Jakarta Validation)
  - Serie debe existir (404) y estar activa (409)
  - Usuario solo puede votar una vez por serie (409 duplicado)
- RatingRepository con restricción UNIQUE (user_id, series_id) en PostgreSQL

### FASE 7: Dashboard de métricas
- DashboardController: GET /api/dashboard
- DashboardService con query SQL agregado:
  - AVG(score) y COUNT(*) GROUP BY series_id
- Retornar estructura: seriesId, title, averageScore, totalVotes

### FASE 8: Manejo de errores y validaciones
- GlobalExceptionHandler con @RestControllerAdvice
- Excepciones personalizadas: ResourceNotFoundException, DuplicateRatingException, InactiveSeriesException
- Formato estándar de error: timestamp, status, message
- Validaciones Jakarta Bean Validation en DTOs

### FASE 9: Tests básicos
- Unit tests en Service layer:
  - crear rating correctamente
  - rechazar score inválido
  - rechazar rating duplicado
  - rechazar serie inexistente
  - rechazar serie inactiva
  - calcular dashboard
- Integration tests para flujos principales de API

### FASE 10: Documentación y portfolio
- Revisar endpoints con Postman
- Asegurar que credenciales no estén en Git
- Preparar descripción para portfolio
- Verificar estructura por capas y uso de DTOs