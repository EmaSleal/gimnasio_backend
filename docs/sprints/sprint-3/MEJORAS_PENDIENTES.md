# Sprint 3 - Mejoras Pendientes

**Sprint**: Sprint 3 - Optimización y Mejoras Arquitectónicas  
**Prioridad**: Post-Observabilidad  
**Estado**: 📋 Planificado

---

## 📊 Contexto

Este documento recoge las mejoras identificadas después de completar:
- ✅ **Sprint 1**: Microservicios básicos + Admin Service
- ✅ **Sprint 2**: Prometheus + Grafana + Alertmanager  
- ✅ **Optimización Fase 1**: Límites de memoria JVM optimizados

**Baseline Actual**:
- 7 microservicios operativos (Eureka, Config, Gateway, Auth, User, Workout, Admin)
- Infraestructura de monitoreo completa (Prometheus, Grafana, Alertmanager)
- PostgreSQL + RabbitMQ (RabbitMQ infrautilizado)
- Memoria optimizada: ~2,888 MB total (reducción de 35.8%)

---

## 🎯 Mejoras Priorizadas para Sprint 3

### Categoría 1: Optimización de Recursos 🟢

#### 1.1 Fase 2 - Optimización de Imágenes Docker
**Objetivo**: Reducir tamaño de imágenes y mejorar tiempos de startup

**Estado Actual**:
- Usando `eclipse-temurin:21` (base Ubuntu/Debian pesada)
- Imágenes ~500-700 MB cada una
- Startup time: 30-45 segundos promedio

**Propuesta**:
```dockerfile
# Opción A: Alpine Linux
FROM eclipse-temurin:21-jre-alpine
# Reduce imágenes a ~200-300 MB

# Opción B: OpenJ9 (menor uso de memoria)
FROM ibm-semeru-runtimes:open-21-jre
# Reduce heap footprint 30-40%
```

**Impacto Estimado**:
- 📦 Reducción de tamaño: 40-50% por imagen
- ⚡ Startup time: 20-30 segundos
- 💾 Memoria adicional: 10-15% de reducción con OpenJ9

**Esfuerzo**: 4-6 horas
**Riesgo**: Medio (compatibilidad de librerías nativas en Alpine)

---

#### 1.2 Ajustes Finos de Memoria Post-Optimización
**Objetivo**: Refinar límites de memoria basados en uso real

**Servicios con Oportunidad de Reducción**:
| Servicio | Uso Actual | Límite Actual | Límite Propuesto | Ahorro |
|----------|------------|---------------|------------------|--------|
| authentication | 311 MB (40.5%) | 768 MB | 512 MB | 256 MB |
| user-service | 228 MB (29.7%) | 768 MB | 512 MB | 256 MB |
| postgres | 51 MB (5.0%) | 1024 MB | 512 MB | 512 MB |

**Ahorro Total Potencial**: ~1,024 MB adicionales

**Estrategia**:
1. Monitorear 7 días con límites actuales
2. Identificar picos de uso (percentil 95)
3. Ajustar límites con margen 30% sobre el pico
4. Implementar gradualmente (un servicio por día)

**Esfuerzo**: 2-3 horas implementación + 1 semana monitoreo
**Riesgo**: Bajo (tenemos métricas de Grafana)

---

#### 1.3 Fase 3 - Spring Native (GraalVM) [Largo Plazo]
**Objetivo**: Compilación nativa para máximo rendimiento

**Beneficios**:
- ⚡ Startup instantáneo (<100ms vs 30s)
- 💾 Memoria: 50-70% menos uso
- 📦 Imágenes: 50-80 MB por servicio

**Limitaciones**:
- ⚠️ Reflection y proxies requieren configuración manual
- ⚠️ No todas las librerías compatibles
- ⚠️ Build time aumenta significativamente

**Estado**: Investigación requerida
**Esfuerzo**: 2-3 semanas (experimental)
**Riesgo**: Alto (cambio arquitectónico significativo)

---

### Categoría 2: Mejoras de Arquitectura 🟡

#### 2.1 Implementar Comunicación Asíncrona con RabbitMQ
**Problema**: RabbitMQ está corriendo pero **CERO uso actual**

**Casos de Uso Propuestos**:

##### A. Envío de Emails Asíncrono
**Flujo Actual (síncrono)**:
```
Cliente → Authentication → Resend API (bloquea 2-3s)
```

**Flujo Propuesto**:
```
Cliente → Authentication → RabbitMQ → Email Worker
         ↓
      Respuesta inmediata
```

**Implementación**:
```java
// authentication-service
@Service
public class AuthenticationService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public UserDto register(User user) {
        UserDto saved = userService.register(user);
        
        // Enviar evento asíncrono
        rabbitTemplate.convertAndSend(
            "email.exchange",
            "email.welcome",
            new WelcomeEmailEvent(saved.getEmail(), saved.getUserName())
        );
        
        return saved;  // Sin esperar email
    }
}
```

**Beneficios**:
- ✅ Respuesta 2-3s más rápida
- ✅ Retry automático si Resend falla
- ✅ Escalabilidad horizontal (múltiples workers)

---

##### B. Notificaciones de Workouts
```java
// workout-service
public void assignWorkoutPlan(Long userId, WorkoutPlan plan) {
    workoutRepository.save(plan);
    
    rabbitTemplate.convertAndSend(
        "notification.exchange",
        "workout.assigned",
        new WorkoutAssignedEvent(userId, plan.getId())
    );
}

// notification-service (NUEVO)
@RabbitListener(queues = "workout.notification.queue")
public void notifyWorkoutAssigned(WorkoutAssignedEvent event) {
    User user = userService.findById(event.getUserId());
    emailService.send(user.getEmail(), "Nueva rutina asignada", ...);
    // Futuro: push notifications, SMS, etc.
}
```

---

##### C. Auditoría de Acciones Críticas
```java
@Aspect
@Component
public class AuditAspect {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @AfterReturning(pointcut = "@annotation(Auditable)")
    public void logAuditEvent(JoinPoint joinPoint) {
        AuditEvent event = AuditEvent.builder()
            .user(SecurityContextHolder.getContext().getAuthentication().getName())
            .action(joinPoint.getSignature().getName())
            .timestamp(LocalDateTime.now())
            .build();
            
        rabbitTemplate.convertAndSend("audit.exchange", "audit.log", event);
    }
}

// audit-service (NUEVO)
@RabbitListener(queues = "audit.queue")
public void storeAuditLog(AuditEvent event) {
    auditRepository.save(event);  // Persistir para compliance
}
```

**Esfuerzo**: 
- Email asíncrono: 3-4 horas
- Notificaciones: 6-8 horas (incluye crear notification-service)
- Auditoría: 4-6 horas

**Riesgo**: Bajo (infraestructura RabbitMQ ya funcional)

---

#### 2.2 Desacoplar Authentication de User Service
**Problema Actual**: Authentication actúa como proxy de User Service

**Flujo Problemático**:
```
Cliente → Gateway → Authentication → User Service → DB
                         ↓
                    Genera JWT
```

**Código Actual**:
```java
// AuthenticationServiceImpl.java
public UserDto register(User request) {
    // Authentication hace FORWARD completo a User Service
    var userDto = restTemplate.postForObject(
        "http://user-service/user/register",  // ← Hop innecesario
        userSecurity,
        UserDto.class
    );
    // Luego genera JWT
}
```

**Problemas**:
- 🐌 Latencia adicional (hop extra)
- 🔗 Acoplamiento alto
- 📈 Dificulta escalado independiente

**Solución Propuesta**:
```java
// user-service
@Service
public class UserService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public UserDto register(User user) {
        UserDto saved = userRepository.save(user);
        
        // Publicar evento "UserCreated"
        rabbitTemplate.convertAndSend(
            "user.exchange",
            "user.created",
            UserCreatedEvent.of(saved)
        );
        
        return saved;
    }
}

// authentication-service
@RabbitListener(queues = "user.created.queue")
public void onUserCreated(UserCreatedEvent event) {
    log.info("New user registered: {}", event.getEmail());
    // Opcional: pre-generar refresh token, enviar email, etc.
}
```

**Nuevo Flujo de Registro**:
```
Cliente → Gateway → User Service → DB
                         ↓
                    Publica evento
                         ↓
                    Authentication → Procesa (async)
```

**Nuevo Flujo de Login**:
```
Cliente → Gateway → Authentication
                         ↓
                    Consulta User (solo validación)
                         ↓
                    Genera JWT
```

**Beneficios**:
- ⚡ Reducción de latencia: ~100-200ms menos
- 🔓 Servicios desacoplados
- 📊 Cada servicio escala independientemente

**Esfuerzo**: 6-8 horas
**Riesgo**: Medio (requiere refactoring de Authentication Service)

---

#### 2.3 Centralizar Configuración con Config Service
**Problema**: Config Service existe pero **servicios NO lo usan**

**Estado Actual**:
- Cada servicio tiene `application.yml` duplicado
- Cambios requieren rebuild de imágenes
- Secretos hardcodeados en cada servicio

**Implementación**:

##### Paso 1: Crear Repositorio de Configuraciones
```
config-server-repo/  (Nuevo repo Git)
├── application.yml              # Configuración común
├── application-docker.yml       # Específico de Docker
├── application-prod.yml         # Producción
├── api-gateway.yml
├── user-service.yml
├── workout-service.yml
└── authentication.yml
```

**application.yml** (común):
```yaml
# Configuración compartida por TODOS los servicios
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URI:http://eureka-server:8761/eureka/}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  pattern:
    level: '%5p [${spring.application.name:},%X{traceId:-}]'
```

**user-service.yml** (específico):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:postgres}:5432/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate  # Nunca create
  flyway:
    enabled: true
```

##### Paso 2: Modificar Servicios
```xml
<!-- pom.xml de CADA servicio -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

```yaml
# bootstrap.yml (REEMPLAZA application.yml)
spring:
  application:
    name: user-service
  cloud:
    config:
      uri: http://config-service:8889
      fail-fast: true
      retry:
        max-attempts: 6
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:docker}
```

##### Paso 3: Actualizar Config Service
```yaml
# config-service/application.yml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/EmaSleal/gym-config-server.git
          default-label: main
          clone-on-start: true
          force-pull: true
```

**Beneficios**:
- ✅ Cambios de config SIN rebuild
- ✅ Configuración versionada (Git)
- ✅ Secretos centralizados
- ✅ Refresh dinámico (`/actuator/refresh`)

**Esfuerzo**: 8-12 horas
**Riesgo**: Medio (requiere refactoring de todos los servicios)

---

#### 2.4 Implementar Circuit Breaker Correctamente
**Problema**: Resilience4j incluido en Gateway pero **SIN configuración**

**Riesgo Actual**:
```
Si user-service cae → Gateway sigue enviando requests
                   → Timeout de 30s por request
                   → Cascada de errores
```

**Solución**:
```yaml
# api-gateway/application.yml
resilience4j:
  circuitbreaker:
    instances:
      user-service:
        sliding-window-size: 10
        failure-rate-threshold: 50  # Abre después de 50% fallos
        wait-duration-in-open-state: 10000  # 10s antes de retry
        permitted-number-of-calls-in-half-open-state: 3
      workout-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
      authentication:
        sliding-window-size: 10
        failure-rate-threshold: 50
  
  timelimiter:
    instances:
      user-service:
        timeout-duration: 3s  # Falla rápido
      workout-service:
        timeout-duration: 3s
```

```java
// GatewayConfig.java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("user-service", r -> r
            .path("/user/**")
            .filters(f -> f
                .circuitBreaker(config -> config
                    .setName("user-service")
                    .setFallbackUri("forward:/fallback/user-service")
                )
            )
            .uri("lb://user-service")
        )
        .build();
}

@RestController
public class FallbackController {
    @GetMapping("/fallback/user-service")
    public ResponseEntity<ApiResponse> userServiceFallback() {
        return ResponseEntity.status(503).body(
            ApiResponse.builder()
                .success(false)
                .message("User service temporarily unavailable")
                .error("SERVICE_UNAVAILABLE")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
}
```

**Beneficios**:
- ✅ Falla rápido (3s vs 30s)
- ✅ Protege servicios downstream
- ✅ Fallback con mensaje amigable
- ✅ Métricas en Grafana

**Esfuerzo**: 4-6 horas
**Riesgo**: Bajo

---

### Categoría 3: Mejoras de Código 🟢

#### 3.1 Estandarizar Respuestas de API
**Problema**: Respuestas inconsistentes entre servicios

**Ejemplo Actual**:
```java
// user-service
return ResponseEntity.ok(userDto);

// workout-service
return new ResponseEntity<>(workout, HttpStatus.CREATED);

// authentication
return ResponseEntity.ok().body(Map.of("token", token));
```

**Solución: DTO Común**:
```java
@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private LocalDateTime timestamp;
    private String traceId;  // Para correlación con logs
}

// Uso consistente
@GetMapping("/users")
public ResponseEntity<ApiResponse<List<UserDto>>> getUsers() {
    return ResponseEntity.ok(
        ApiResponse.<List<UserDto>>builder()
            .success(true)
            .message("Users retrieved successfully")
            .data(userService.findAll())
            .timestamp(LocalDateTime.now())
            .traceId(MDC.get("traceId"))
            .build()
    );
}
```

**Esfuerzo**: 6-8 horas (refactorizar todos los controllers)
**Riesgo**: Bajo

---

#### 3.2 Validación de DTOs con Bean Validation
**Problema**: Validación inconsistente o ausente

**Implementación**:
```java
@Data
@Builder
public class UserRegistrationRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
        message = "Password must contain uppercase, lowercase, digit, min 8 chars"
    )
    private String password;
}

@RestController
public class UserController {
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(
            @Valid @RequestBody UserRegistrationRequest request) {
        // Validación automática antes de entrar al método
    }
}
```

**Esfuerzo**: 4-6 horas
**Riesgo**: Bajo

---

#### 3.3 Global Exception Handler
**Problema**: Errores devueltos inconsistentemente

**Implementación**:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(
            MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .toList();
            
        return ResponseEntity.badRequest().body(
            ApiResponse.builder()
                .success(false)
                .message("Validation failed")
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(
            ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(500).body(
            ApiResponse.builder()
                .success(false)
                .message("Internal server error")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
}
```

**Esfuerzo**: 3-4 horas
**Riesgo**: Bajo

---

#### 3.4 Documentación con OpenAPI/Swagger
**Problema**: No hay documentación de API

**Implementación**:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

```java
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Gym Management API",
        version = "1.0",
        description = "API for managing gym operations",
        contact = @Contact(
            name = "Development Team",
            email = "dev@gym.com"
        )
    ),
    security = @SecurityRequirement(name = "bearer-jwt")
)
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token authentication")
                )
            );
    }
}
```

**Acceso**: `http://localhost:8590/swagger-ui.html`

**Esfuerzo**: 2-3 horas por servicio
**Riesgo**: Bajo

---

### Categoría 4: Infraestructura y DevOps ⚪

#### 4.1 Health Checks Personalizados
**Objetivo**: Mejor visibilidad del estado real de servicios

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(1)) {
                return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("status", "connected")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
        return Health.down().build();
    }
}

@Component
public class RabbitMQHealthIndicator implements HealthIndicator {
    @Autowired
    private ConnectionFactory connectionFactory;
    
    @Override
    public Health health() {
        try {
            Connection conn = connectionFactory.createConnection();
            conn.close();
            return Health.up()
                .withDetail("rabbitmq", "connected")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

**Esfuerzo**: 4-6 horas
**Riesgo**: Bajo

---

#### 4.2 Rate Limiting en Gateway
**Objetivo**: Proteger servicios de abuso

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10  # req/sec
                redis-rate-limiter.burstCapacity: 20
```

**Requiere**: Redis (nuevo contenedor)

**Esfuerzo**: 6-8 horas
**Riesgo**: Medio (nueva dependencia)

---

#### 4.3 Logging Estructurado (JSON)
**Objetivo**: Logs parseables para ELK Stack

```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeContext>true</includeContext>
            <includeMdc>true</includeMdc>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="JSON"/>
    </root>
</configuration>
```

**Salida**:
```json
{
  "timestamp": "2025-11-02T15:30:45.123Z",
  "level": "INFO",
  "logger": "cr.ac.backend.user.UserService",
  "message": "User registered successfully",
  "thread": "http-nio-8588-exec-1",
  "traceId": "abc123",
  "userId": "12345"
}
```

**Esfuerzo**: 3-4 horas
**Riesgo**: Bajo

---

#### 4.4 Tests Automatizados
**Estado Actual**: Cero tests

**Propuesta Mínima**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void shouldRegisterUser() throws Exception {
        UserDto mockUser = UserDto.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .build();
            
        when(userService.register(any())).thenReturn(mockUser);
        
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "testuser",
                        "email": "test@example.com",
                        "password": "Password123"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"));
    }
}
```

**Meta**: 50% coverage mínimo

**Esfuerzo**: 16-20 horas (para todos los servicios)
**Riesgo**: Bajo

---

## 📋 Roadmap Propuesto

### Sprint 3.1 - Arquitectura Asíncrona (2 semanas)
**Objetivo**: Aprovechar RabbitMQ

- [ ] **Semana 1**: Implementar email asíncrono
  - Crear queue `email.welcome.queue`
  - Refactorizar `AuthenticationService`
  - Crear `EmailWorker` (consumer)
  - Testing y validación
  
- [ ] **Semana 2**: Desacoplar Authentication de User
  - Implementar eventos `UserCreated`
  - Refactorizar flujo de registro
  - Refactorizar flujo de login
  - Testing y validación

**Entregables**:
- ✅ RabbitMQ en uso activo
- ✅ Latencia de registro reducida >2s
- ✅ Servicios desacoplados

---

### Sprint 3.2 - Calidad de Código (1 semana)
**Objetivo**: Estandarizar y documentar APIs

- [ ] Implementar `ApiResponse<T>` común
- [ ] Agregar validación con `@Valid`
- [ ] Crear `GlobalExceptionHandler`
- [ ] Documentar con Swagger/OpenAPI

**Entregables**:
- ✅ Respuestas API estandarizadas
- ✅ Swagger UI accesible
- ✅ Validación consistente

---

### Sprint 3.3 - Config Centralizado (1 semana)
**Objetivo**: Usar Config Service

- [ ] Crear repositorio `gym-config-server`
- [ ] Migrar configuraciones
- [ ] Agregar `spring-cloud-config-client` a servicios
- [ ] Crear `bootstrap.yml` en cada servicio
- [ ] Validar refresh dinámico

**Entregables**:
- ✅ Config Service funcional
- ✅ Zero config en código
- ✅ Refresh sin rebuild

---

### Sprint 3.4 - Resiliencia (1 semana)
**Objetivo**: Implementar Circuit Breakers

- [ ] Configurar Resilience4j en Gateway
- [ ] Crear fallback controllers
- [ ] Health checks personalizados
- [ ] Testing de fallos

**Entregables**:
- ✅ Circuit breakers activos
- ✅ Fallbacks implementados
- ✅ Métricas en Grafana

---

### Sprint 3.5 - Optimización Fase 2 (1 semana)
**Objetivo**: Reducir recursos

- [ ] Migrar a Alpine Linux
- [ ] Ajustar límites de memoria
- [ ] Monitorear impacto
- [ ] Documentar resultados

**Entregables**:
- ✅ Imágenes 40% más pequeñas
- ✅ Memoria reducida ~1GB adicional
- ✅ Startup time mejorado

---

## 📊 Métricas de Éxito

### Antes (Post-Sprint 2)
- Memoria total: 2,888 MB
- RabbitMQ: 0% utilización
- Config Service: No usado
- Circuit Breakers: No configurados
- Documentación API: No existe
- Tests: 0%
- Respuestas API: Inconsistentes
- Logging: Texto plano

### Después (Post-Sprint 3)
- Memoria total: <2,000 MB (objetivo)
- RabbitMQ: 100% integrado (emails, eventos, auditoría)
- Config Service: Centralizado
- Circuit Breakers: Activos con fallbacks
- Documentación API: Swagger en todos los servicios
- Tests: >50% coverage
- Respuestas API: Estandarizadas con `ApiResponse<T>`
- Logging: JSON estructurado

---

## 🚦 Priorización Sugerida

### 🔴 Alta Prioridad (Hacer PRIMERO)
1. **Implementar comunicación asíncrona con RabbitMQ**
   - Impacto: Alto (reduce latencia, desacopla servicios)
   - Esfuerzo: Medio (12-16 horas)
   
2. **Desacoplar Authentication de User Service**
   - Impacto: Alto (arquitectura más limpia)
   - Esfuerzo: Medio (6-8 horas)

3. **Configurar Circuit Breakers**
   - Impacto: Alto (resiliencia)
   - Esfuerzo: Bajo (4-6 horas)

### 🟡 Media Prioridad
4. **Estandarizar respuestas API**
5. **Global Exception Handler**
6. **Documentación Swagger**
7. **Centralizar configuración**

### 🟢 Baja Prioridad (Nice to Have)
8. **Optimización Fase 2 (Alpine)**
9. **Health checks personalizados**
10. **Logging estructurado**
11. **Tests automatizados**
12. **Rate limiting**

---

## 🎯 Quick Wins (Hacer YA si tienes 2-4 horas)

1. **Global Exception Handler** (3h)
   - Crea clase `GlobalExceptionHandler`
   - Beneficio inmediato: Errores consistentes
   
2. **ApiResponse DTO** (2h)
   - Crea clase `ApiResponse<T>`
   - Refactoriza 1-2 controllers como ejemplo
   
3. **Swagger en un servicio** (2h)
   - Agrega dependencia
   - Configura OpenAPI
   - Valida en http://localhost:8588/swagger-ui.html

---

## 🔗 Referencias

### Documentación Spring
- [Spring Cloud Config](https://spring.io/projects/spring-cloud-config)
- [Spring AMQP (RabbitMQ)](https://spring.io/projects/spring-amqp)
- [Resilience4j](https://resilience4j.readme.io/docs/circuitbreaker)

### Mejores Prácticas
- [12 Factor App](https://12factor.net/)
- [Microservices Patterns](https://microservices.io/patterns/index.html)
- [Spring Boot Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.best-practices)

---

**Creado**: 2 de noviembre de 2025  
**Estado**: 📋 Planificado  
**Siguiente Acción**: Priorizar mejoras según disponibilidad de tiempo

