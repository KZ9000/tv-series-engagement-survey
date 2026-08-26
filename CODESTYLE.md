# CODESTYLE.md — TV Series Engagement Survey

## 1. Formato de Código

### 1.1. General
- Todo el código debe seguir el formato **Google Java Style** con ajustes para Spring Boot.
- Longitud máxima de línea: **100 caracteres**.
- Sangría: **4 espacios** (no tabs).
- Final de archivo: debe terminar con un salto de línea.
- Todas las clases deben tener una clase pública principal por archivo, con nombre coincidente.

### 1.2. Organización de Archivos
Estructura obligatoria por capas (definida en SPECS.md):

```
src/main/java/com/example/tvseriesengagementsurvey/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
│   ├── auth/
│   ├── series/
│   ├── rating/
│   └── dashboard/
├── exception/
├── security/
└── config/
```

```
src/main/resources/
├── templates/          (Thymeleaf HTML)
├── static/
│   ├── css/            (estilos propios)
│   └── js/             (JavaScript vanilla)
├── db/migration/       (Flyway)
└── application.yml
```

### 1.3. Importaciones
- Organizar imports en grupos: **Java**, **Spring**, **Proyecto**.
- Usar import estático solo cuando sea absolutamente necesario.
- Nunca importar entidades JPA directamente en DTOs o controllers.

## 2. Convenciones de Nomenclatura

### 2.1. Clases y Archivos
- **Clases**: `PascalCase` (ej: `AuthController`, `UserService`).
- **Archivos**: Mismo nombre que la clase pública (`AuthController.java`).
- **Enums**: `PascalCase` con valores en `UPPER_SNAKE_CASE` (ej: `Role.java` con `USER`, `ADMIN`).

### 2.2. Métodos y Variables
- **Métodos**: `camelCase` (ej: `calculateAverageScore()`).
- **Variables de instancia**: `camelCase` con minúscula inicial (ej: `userId`, `seriesTitle`).
- **Constantes**: `UPPER_SNAKE_CASE` (ej: `MAX_SCORE = 5`).
- **Parámetros**: `camelCase`, nombres descriptivos.

### 2.3. Consultas JPA y Repositorios
- Nombre de métodos de consulta: `findBy*`, `getBy*`, `countBy*`.
- Nombre de eliminación: `deleteBy*`, `removeBy*`.
- Prefijos de booleanos: `isActive`, `hasPermission`.

## 3. Comentarios y Documentación

### 3.1. Directiva General
- **Regla de oro**: "El código debe ser autoexplicativo". Comentar solo cuando el "por qué" no sea obvio.
- **Comentarios vagos prohibidos**: `// TODO`, `// fixme`, `// fix later`, `// soon`... a menos que sean técnicos y accionables.
- **Comentarios técnicos permitidos**: Explicar decisiones de negocio, lógica compleja, interacciones con la base de datos, o trucos de rendimiento justificados.

### 3.2. Estilo de Comentarios
- **Línea completa**: `// Comentario descriptivo técnico`.
- **Bloque multilínea**: Solo para documentación de alto nivel o configuraciones no obvias.
- **Javadoc**: Obligatorio para métodos públicos y classes expuestas en API.

### 3.3. Ejemplos Válidos

```java
// ❌ PROHIBIDO: Comentario vacío o obvio
if (score > 5) { // score no puede ser mayor a 5
    throw exception;
}

// ✅ PERMITIDO: Explicación de decisión de negocio
// Se rechaza el rating si la serie está inactiva, según regla de negocio #735
if (!series.isActive()) {
    throw new InactiveSeriesException("Series not active");
}

// ✅ PERMITIDO: Javadoc público
/**
 * Calcula el promedio de calificaciones para una serie.
 * @param seriesId ID de la serie
 * @return Promedio double entre 1.0 y 5.0
 */
public Double calculateAverage(Long seriesId) { ... }
```

### 3.4. Javadoc Obligatorio
- Clases públicas, métodos públicos, y any cosa expuesta en la API REST.
- Formato `@param`, `@return`, `@throws` cuando corresponda.

## 4. Estructura de Código

### 4.1. Controladores (Controller)
- **Responsabilidad**: Recibir requests, validar entrada via DTOs, invocar servicio, devolver respuesta HTTP.
- **Prohibido**: Lógica de negocio directa en controllers.
- **Estructura obligatoria**:
  - Clase anotada con `@RestController`
  - `@RequestMapping` base `/api/{resource}`
  - Cada endpoint con annotation HTTP específica
  - Manejo de excepciones interno o delegado a GlobalExceptionHandler

### 4.2. Servicios (Service)
- **Responsabilidad**: Regla de negocio, coordinar repositories, operaciones CRUD.
- **Estructura obligatoria**:
  - Clase anotada con `@Service`
  - Métodos públicos con Javadoc cuando es lógico
  - Transaccionales cuando modifican datos (@Transactional)
  - Dependencia de repositories mediante constructor injection

### 4.3. Repositorios (Repository)
- **Responsabilidad**: Acceso a datos, Spring Data JPA queries.
- **Prohibido**: Queries nativas SQL sinjustificadas.
- **Permitido**: `@Query` con JPQL cuando sea necesario para queries complejas.

### 4.4. DTOs (Data Transfer Objects)
- **Regla absoluta**: Las entidades JPA **nunca** se exponen directamente.
- Usar `@Getter`/`@Setter` (o Lombok) o builders manuales.
- Validaciones `@NotNull`, `@NotBlank`, `@Size`, `@Min`, `@Max` en fields.
- Nombre de DTOs seguir patron: `*Request` (entrada) / `*Response` (salida).

### 4.5. Entidades JPA
- **Regla**: No contener lógica de negocio.
- Usar `@Entity`, `@Table`, `@Id`, `@Column` apropiadamente.
- Relaciones `@OneToMany`, `@ManyToOne` con `mappedBy` correcto.
- `@CreationTimestamp`, `@UpdateTimestamp` para `createdAt`/`updatedAt`.

### 4.6. Excepciones
- Excepciones personalizadas en paquete `exception/`.
- Jerarquía: `RuntimeException` o `Exception` personalizado.
- Mensajes técnicos y accionables (no "error genérico").
- Mapeo global en `GlobalExceptionHandler` con `@RestControllerAdvice`.

## 5. Seguridad y Buenas Prácticas

### 5.1. Credenciales y secretos
- **Nunca** commit de credenciales, secretos o configuraciones de producción en Git.
- **Nunca** incluir contraseñas reales, JWT reales ni valores sensibles en el código.
- Utilizar variables de entorno para información sensible (`DB_PASSWORD`, `JWT_SECRET`, `ADMIN_PASSWORD`).
- El archivo `.gitignore` debe excluir `.env`, `*.key`, `*.pem`.

### 5.2. Contraseñas
- **Siempre** usar BCryptPasswordEncoder.
- Nunca almacenar texto plano.
- Nunca logar passwords en ningún caso.

### 5.3. JWT
- Secret key solo via variables de entorno (`JWT_SECRET`).
- Token expiration razonable (1-2 horas).
- `JwtAuthenticationFilter` validar todos los requests protegidos.
- Nunca poner información sensible en el payload del JWT.
- Frontend almacena JWT en `localStorage` (aceptable para MVP stateless).

### 5.4. Frontend
- No incluir credenciales en JavaScript o HTML.
- No incluir JWT reales en el código.
- No exponer variables de entorno del backend en el frontend.
- El frontend únicamente consume la API REST existente.

## 6. Testing

### 6.1. Unit Tests (JUnit 5 + Mockito)
- Enfocarse en reglas de negocio de Services.
- Mock dependencies externos (repositories, services).
- Nombre de tests: `cuandoX_yY_entoncesZ` o `metodo_escenarioResultado`.

### 6.2. Integration Tests
- Flujos principales de API con `TestRestTemplate` o `@SpringBootTest`.
- Base de datos H2 o PostgreSQL en memoria para tests.
- Cubrir: auth, series CRUD, ratings rules, dashboard calculation.

### 6.3. Cobertura Mínima
- Services principales: mínimo 80% coverage en lógica crítica.
- No tests solo por cobertura — tests deben validar comportamiento.

## 7. Frontend (Thymeleaf + CSS + JavaScript)

### 7.1. Templates HTML
- Ubicación: `src/main/resources/templates/`.
- Nombres de archivo: `snake_case.html` (ej: `login.html`, `register.html`).
- Idioma: `lang="es"` en la etiqueta `<html>`.
- Incluir `<meta charset="UTF-8">` y `<meta name="viewport">`.
- Thymeleaf se utiliza únicamente para servir las páginas web.
- No incluir lógica de negocio en templates.

### 7.2. CSS
- Ubicación: `src/main/resources/static/css/style.css`.
- Nombres de clases: `kebab-case` (ej: `card-title`, `btn-primary`, `form-group`).
- Variables CSS en `:root` para colores y espaciado.
- Diseño responsive con `@media` queries.
- No utilizar frameworks CSS externos (Bootstrap, Tailwind, etc.).

### 7.3. JavaScript vanilla
- Ubicación: `src/main/resources/static/js/app.js`.
- Nombres de funciones: `camelCase` (ej: `handleLogin`, `loadSeries`, `updateNav`).
- Nombres de constantes: `UPPER_SNAKE_CASE` (ej: `API`, `TOKEN_KEY`).
- No utilizar frameworks JavaScript (React, Angular, Vue, etc.).
- Consumir la API REST existente mediante `fetch`.
- Almacenar JWT en `localStorage`.
- Enviar `Authorization: Bearer <token>` en requests autenticados.
- Manejar 401: limpiar token y redirigir a `/login`.
- No duplicar lógica de negocio del backend.

### 7.4. Organización de static/
```
src/main/resources/static/
├── css/
│   └── style.css
└── js/
    └── app.js
```

### 7.5. Evitar duplicación
- No copiar reglas de negocio al frontend.
- El frontend consume la API REST existente.
- Las validaciones de seguridad se mantienen en el backend.

## 8. Migraciones (Flyway)

### 8.1. Convenciones
- Versionado: `V1__`, `V2__`, `V3__` etc.
- Nombre descriptivo: `V1__create_users_table.sql`.
- Nunca commitar migrations sin probar en entorno limpio.
- `flyway.locations` en `application.yml` apuntar a `classpath:db/migration/`.

### 8.2. Estructura
```
src/main/resources/db/migration/
├── V1__create_users_table.sql
├── V2__create_series_table.sql
├── V3__create_ratings_table.sql
└── V4__add_unique_constraint.sql (futuras)
```

## 9. Formato de Archivos de Configuración

### 9.1. application.yml
- Usar **formato YAML** (no XML).
- Variables sensibles via `env` o system properties.
- Estructura anidada clara, 2 espacios de indentación.

### 9.2. application.properties (solo si es necesario)
- Exception: usar YAML cuando sea posible.

```yaml
# ❌ MAL
db.url=jdbc:postgresql://localhost:5432/tv_series_engagement
db.user=admin
db.password=secret

# ✅ BIEN
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/tv_series_engagement}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:}
    hikari:
      maximum-pool-size: 10
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
    properties:
      hibernate:
        format_sql: true
```

## 10. Consideraciones Finales (Production Ready)

1. **Nunca** commit de credenciales, secrets o configuraciones de producción en Git.
2. **Siempre** usar `.gitignore` para excluir `target/`, `.env`, `*.key`, `*.pem`.
3. **Code reviews** obligatorios para cada PR — al menos un revisor.
4. **Build limpio**: `mvn clean install` debe ser exitoso sin warnings críticos.
5. **Dependencias**: `mvn dependency:tree` sin conflictos o versiones override innecesarias.
6. **Docker/Container** (futuro): salud check en puerto 8080/actuator.