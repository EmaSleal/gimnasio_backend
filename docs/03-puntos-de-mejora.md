# Puntos de Mejora - Sistema de Gimnasio Backend

## 1. Categorización de Mejoras

### Prioridad CRÍTICA 🔴
Afectan seguridad, pérdida de datos o funcionamiento básico

### Prioridad ALTA 🟡
Mejoran significativamente mantenibilidad y escalabilidad

### Prioridad MEDIA 🟢
Optimizaciones y mejores prácticas

### Prioridad BAJA ⚪
Nice to have, mejoras cosméticas

---

## 2. Mejoras Críticas 🔴

### 2.1 Gestión de Secretos y Configuración Sensible

**Problema Actual**:
```yaml
# application.yml - EXPUESTO EN REPOSITORIO
spring:
  datasource:
    password: Chismosear01  # ⚠️ Contraseña en texto plano
    
jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970  # ⚠️ Secreto compartido expuesto

sender:
  key: re_X7qY3NFp_ETffUyjtLJpgTMcrzdhvdB4c  # ⚠️ API Key de Resend expuesta
```

**Riesgos**:
- Cualquiera con acceso al repo puede generar JWTs válidos
- Acceso no autorizado a la base de datos
- Compromiso de cuenta de email (Resend)

**Solución Propuesta**:

#### Opción 1: Variables de Entorno (Recomendado para Docker)
```yaml
# application.yml
spring:
  datasource:
    password: ${DB_PASSWORD}
    
jwt:
  secret: ${JWT_SECRET}
  
sender:
  key: ${RESEND_API_KEY}
```

```yaml
# docker-compose.yml
services:
  user-service:
    environment:
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    env_file:
      - .env  # ← No commitear este archivo
```

```bash
# .env (agregar a .gitignore)
DB_PASSWORD=SecurePassword123!
JWT_SECRET=YourVeryLongAndSecureJWTSecret
RESEND_API_KEY=re_YourActualKey
```

#### Opción 2: Spring Cloud Config + Vault
```yaml
# application.yml
spring:
  cloud:
    config:
      uri: http://config-service:8889
      fail-fast: true
```

```yaml
# config-server (en GitHub privado o Vault)
spring:
  datasource:
    password: '{cipher}ENCRYPTED_VALUE'
```

**Acción Inmediata**:
1. ✅ Rotar TODOS los secretos actuales
2. ✅ Implementar variables de entorno
3. ✅ Agregar `.env` a `.gitignore`
4. ✅ Eliminar secretos del historial de Git (git filter-branch o BFG)

---

### 2.2 DDL Auto - Pérdida de Datos

**Problema Actual**:
```yaml
# user-service/application.yml
spring:
  jpa:
    hibernate:
      ddl-auto: create  # 💀 DESTRUYE Y RECREA SCHEMA EN CADA INICIO

# workout-service/application.yml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # 💀 DESTRUYE AL PARAR EL SERVICIO
```

**Impacto**:
- Pérdida total de datos de usuarios
- Pérdida total de workouts, planes, rutinas
- Imposible ejecutar en producción
- Reiniciar servicio = borrar base de datos

**Solución**:

#### Fase 1: Desarrollo Local
```yaml
spring:
  profiles: dev
  jpa:
    hibernate:
      ddl-auto: update  # Solo agrega columnas/tablas, no destruye
```

#### Fase 2: Migraciones con Flyway (RECOMENDADO)
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # Solo valida, no modifica
  flyway:
    enabled: true
    baseline-on-migrate: true
```

```sql
-- src/main/resources/db/migration/V1__create_users_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Beneficios**:
- Historial versionado de cambios de schema
- Rollback posible
- Mismo schema en dev, staging, producción
- Auditoría de cambios

---

### 2.3 IPs Hardcodeadas - Portabilidad

**Problema Actual**:
```yaml
# Múltiples servicios
eureka:
  client:
    service-url:
      defaultZone: http://192.168.100.207:8761/eureka/  # ❌

spring:
  datasource:
    url: jdbc:postgresql://192.168.100.207:5432/gym_authentication  # ❌
```

**Impacto**:
- No funciona en otros entornos
- Rompe Docker Compose networking
- Dificulta CI/CD
- Requiere edición manual por entorno

**Solución**:
```yaml
# application-docker.yml
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/  # ✅ Nombre del servicio

spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/${DB_NAME}  # ✅ Nombre del servicio
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

```yaml
# application-local.yml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/gym_authentication
```

```yaml
# docker-compose.yml
services:
  user-service:
    environment:
      - SPRING_PROFILES_ACTIVE=docker
```

---

### 2.4 Actuator Sin Configuración de Seguridad

**Problema Actual**:
Actuator incluido en 3 servicios pero:
- ❌ No hay configuración de endpoints expuestos
- ❌ No hay seguridad configurada
- ❌ Potencialmente expone información sensible

**Riesgo**:
Si `/actuator/env` está expuesto, puede revelar:
- Variables de entorno
- Propiedades de configuración
- Secretos (si no están protegidos)

**Solución**:

#### Configuración Segura de Actuator
```yaml
# application.yml (todos los servicios con actuator)
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus  # Solo lo necesario
  endpoint:
    health:
      show-details: when-authorized  # Oculta detalles sin autenticación
      probes:
        enabled: true  # Para Kubernetes liveness/readiness
  metrics:
    export:
      prometheus:
        enabled: true

# Información segura
info:
  app:
    name: @project.name@
    version: @project.version@
```

#### Securizar con Spring Security (si se implementa)
```java
@Configuration
public class ActuatorSecurityConfig {
    @Bean
    public SecurityFilterChain actuatorSecurity(HttpSecurity http) {
        return http
            .requestMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeRequests(auth -> auth
                .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                .anyRequest().hasRole("ADMIN")
            )
            .build();
    }
}
```

---

## 3. Mejoras de Alta Prioridad 🟡

### 3.1 ACTUATOR: Crear Servicio Dedicado de Monitoreo

**Problema Identificado por el Usuario**:
> "se que hay uno y es pasar a un servicio el actuator"

**Análisis**:
Actualmente, Actuator está embebido en:
- authentication (puerto 8583)
- user-service (puerto 8588)
- workout-service (puerto 8586)

**Problemas**:
1. **Exposición de endpoints sensibles** en servicios de negocio
2. **Duplicación de configuración** de actuator
3. **Sin centralización** de métricas y health checks
4. **Dificulta monitoreo** holístico del sistema

**Solución Propuesta: Admin Service**

#### Paso 1: Crear Admin Service
```
admin-service/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── cr/ac/backend/admin/
│       │       └── AdminServiceApplication.java
│       └── resources/
│           └── application.yml
```

```xml
<!-- admin-service/pom.xml -->
<dependencies>
    <!-- Spring Boot Admin Server -->
    <dependency>
        <groupId>de.codecentric</groupId>
        <artifactId>spring-boot-admin-starter-server</artifactId>
        <version>3.2.0</version>
    </dependency>
    
    <!-- Eureka Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <!-- Security para proteger el dashboard -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
</dependencies>
```

```java
// AdminServiceApplication.java
@SpringBootApplication
@EnableAdminServer  // ← Habilita Spring Boot Admin
@EnableDiscoveryClient
public class AdminServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }
}
```

```yaml
# admin-service/application.yml
server:
  port: 9000

spring:
  application:
    name: admin-service
  
  # Seguridad básica para el dashboard
  security:
    user:
      name: admin
      password: ${ADMIN_PASSWORD:admin123}  # ⚠️ Cambiar en producción

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
```

#### Paso 2: Modificar Servicios Existentes
Convertirlos en **Clientes de Spring Boot Admin**:

```xml
<!-- authentication, user-service, workout-service pom.xml -->
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>3.2.0</version>
</dependency>

<!-- Mantener actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
# application.yml de cada servicio
management:
  endpoints:
    web:
      exposure:
        include: "*"  # Admin Service consumirá estos endpoints
  endpoint:
    health:
      show-details: always

spring:
  boot:
    admin:
      client:
        url: http://admin-service:9000  # URL del Admin Service
        instance:
          prefer-ip: true
```

#### Paso 3: Agregar al Docker Compose
```yaml
# docker-compose.yml
services:
  admin-service:
    container_name: admin-service
    build:
      context: .
      dockerfile: ./admin-service/Dockerfile
    ports:
      - "9000:9000"
    networks:
      - spring
    depends_on:
      - eureka-server
    environment:
      - ADMIN_PASSWORD=${ADMIN_PASSWORD}
    restart: unless-stopped
```

#### Beneficios
✅ **Dashboard centralizado** con:
- Estado de todos los servicios en tiempo real
- Métricas (CPU, memoria, threads)
- Logs en vivo
- Health checks agregados
- Notificaciones de caídas

✅ **Seguridad mejorada**:
- Endpoints de actuator NO expuestos públicamente
- Acceso controlado con autenticación

✅ **Observabilidad**:
- Vista holística del sistema
- Histórico de eventos
- Integración con Prometheus/Grafana

**Captura de Pantalla del Dashboard**:
```
http://localhost:9000
```
<img src="https://codecentric.github.io/spring-boot-admin/current/images/screenshot.png" alt="Spring Boot Admin Dashboard" width="600"/>

---

### 3.2 Desacoplar Authentication de User Service

**Problema**:
```java
// AuthenticationServiceImpl.java
// Authentication Service actúa como PROXY completo de User Service
public UserDto register(User request) {
    var userSecurity = User.builder()...build();
    var UserDto = restTemplate.postForObject(
        "http://user-service/user/register", 
        userSecurity, 
        UserDto.class
    );  // ← Hop innecesario
    // ... generar JWT
}
```

**Flujo Actual (ineficiente)**:
```
Cliente → Gateway → Authentication → User Service → DB
                         ↓
                    Genera JWT
```

**Flujo Propuesto (eficiente)**:
```
// Registro
Cliente → Gateway → User Service → DB
                         ↓
                    Publica evento "UserCreated"
                         ↓
                    Authentication → Genera JWT

// Login
Cliente → Gateway → Authentication → Valida credenciales
                         ↓
                    Consulta User Service (solo info básica)
                         ↓
                    Genera JWT
```

**Implementación con Eventos (RabbitMQ)**:

```java
// user-service
@Service
public class UserService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public UserDto register(User user) {
        UserDto saved = userRepository.save(user);
        
        // Publicar evento
        UserCreatedEvent event = new UserCreatedEvent(
            saved.getId(),
            saved.getEmail(),
            saved.getRole()
        );
        rabbitTemplate.convertAndSend(
            "user.exchange",
            "user.created",
            event
        );
        
        return saved;
    }
}
```

```java
// authentication-service
@Service
public class AuthenticationEventListener {
    @RabbitListener(queues = "user.created.queue")
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("User created: {}, generating welcome email", event.getEmail());
        emailService.sendWelcomeEmail(event.getEmail());
    }
}
```

**Beneficios**:
- ✅ Reduce latencia (elimina hop)
- ✅ Mejor separación de responsabilidades
- ✅ Permite escalado independiente
- ✅ Usa infraestructura existente (RabbitMQ)

---

### 3.3 Implementar Comunicación Asíncrona con RabbitMQ

**Estado Actual**: RabbitMQ está corriendo pero CERO uso

**Casos de Uso Ideales**:

#### 1. Envío de Emails Asíncrono
**Problema**: Email de bienvenida/recuperación bloquea el flujo

**Solución**:
```java
// authentication-service
public UserDto register(User request) {
    var userDto = userService.register(request);
    
    // Publicar evento en lugar de bloquear
    rabbitTemplate.convertAndSend(
        "email.exchange",
        "email.welcome",
        new WelcomeEmailEvent(userDto.getEmail(), userDto.getUserName())
    );
    
    return userDto;  // Respuesta inmediata
}
```

```java
// email-service (nuevo microservicio)
@RabbitListener(queues = "email.welcome.queue")
public void sendWelcomeEmail(WelcomeEmailEvent event) {
    resendClient.sendEmail(
        event.getEmail(),
        "Bienvenido a Gym App",
        emailTemplate.render(event)
    );
}
```

#### 2. Notificaciones de Rutinas
```java
// workout-service
public void assignWorkoutPlan(Long userId, WorkoutPlan plan) {
    // ... guardar plan
    
    rabbitTemplate.convertAndSend(
        "notification.exchange",
        "notification.workout.assigned",
        new WorkoutAssignedEvent(userId, plan.getId())
    );
}
```

```java
// notification-service
@RabbitListener(queues = "notification.workout.queue")
public void notifyWorkoutAssigned(WorkoutAssignedEvent event) {
    // Enviar push notification, email, etc.
}
```

#### 3. Auditoría de Acciones
```java
// Todos los servicios
@Aspect
public class AuditAspect {
    @AfterReturning("@annotation(Auditable)")
    public void audit(JoinPoint joinPoint) {
        AuditEvent event = new AuditEvent(
            getCurrentUser(),
            joinPoint.getSignature().getName(),
            LocalDateTime.now()
        );
        rabbitTemplate.convertAndSend("audit.exchange", "audit.log", event);
    }
}
```

**Configuración**:
```yaml
# application.yml
spring:
  rabbitmq:
    host: rabbitmq
    port: 5672
    username: guest
    password: guest
```

```java
@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue userCreatedQueue() {
        return new Queue("user.created.queue", true);  // durable
    }
    
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange("user.exchange");
    }
    
    @Bean
    public Binding binding(Queue userCreatedQueue, TopicExchange userExchange) {
        return BindingBuilder
            .bind(userCreatedQueue)
            .to(userExchange)
            .with("user.created");
    }
}
```

---

### 3.4 Centralizar Configuración con Config Service

**Problema**: Config Service existe pero servicios no lo usan

**Implementación**:

#### Paso 1: Organizar Repositorio de Configuraciones
```
config-server-repo/
├── application.yml              # Configuración común
├── application-dev.yml          # Desarrollo
├── application-prod.yml         # Producción
├── api-gateway.yml              # Específico de gateway
├── api-gateway-dev.yml
├── api-gateway-prod.yml
├── user-service.yml
├── user-service-dev.yml
├── user-service-prod.yml
└── ...
```

```yaml
# application.yml (común a todos)
spring:
  sleuth:
    propagation:
      type: w3c
  zipkin:
    base-url: http://zipkin:9411

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/

logging:
  pattern:
    level: '%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]'
```

```yaml
# user-service-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://prod-db-host:5432/gym_authentication
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate  # NUNCA create en producción
```

#### Paso 2: Modificar Servicios para Consumir Config
```xml
<!-- pom.xml de cada servicio -->
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
# bootstrap.yml (reemplaza parte de application.yml)
spring:
  application:
    name: user-service
  cloud:
    config:
      uri: http://config-service:8889
      fail-fast: true
      retry:
        max-attempts: 6
        initial-interval: 1000
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
```

#### Paso 3: Actualizar Config Service
```yaml
# config-service/application.yml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/EmaSleal/config-server.git
          default-label: main
          clone-on-start: true
          force-pull: true
        encrypt:
          enabled: true  # Para secretos
```

**Beneficios**:
- ✅ Cambios de configuración sin rebuild
- ✅ Configuración por entorno centralizada
- ✅ Secretos encriptados con Spring Cloud Config Encryption
- ✅ Auditoría de cambios (Git)

---

### 3.5 Implementar Zipkin para Tracing Distribuido

**Estado Actual**: 
- Dependencias de Zipkin incluidas en api-gateway
- Configuración deshabilitada (servidor Zipkin no disponible)
- Errores de conexión solucionados temporalmente

**¿Es Necesario?**

**NO para funcionamiento básico**:
- ✅ Los microservicios funcionan sin Zipkin
- ✅ Circuit breakers operan correctamente
- ✅ Prometheus/Grafana monitorean métricas

**SÍ para observabilidad avanzada**:
- 🔍 **Tracing distribuido**: Rastrear peticiones a través de múltiples servicios
- 📊 **Análisis de latencia**: Identificar cuellos de botella entre microservicios
- 🐛 **Debugging complejo**: Saber exactamente dónde falló una petición en la cadena
- ⏱️ **Medición de performance**: Ver tiempos de respuesta por cada hop
- 🔗 **Correlación de logs**: Conectar logs de diferentes servicios con trace ID

**Ejemplo de Uso Real**:
```
Usuario reporta: "El login es muy lento"

Sin Zipkin:
- Solo ves: "Login tardó 2.5s"
- No sabes dónde está el problema

Con Zipkin:
Request /auth/login → 2,500ms total
  ├─ API Gateway (8ms)
  ├─ Authentication Service (180ms)
  │   ├─ GET /user/credentials → User Service (1,950ms) ← AQUÍ EL PROBLEMA
  │   │   └─ Database query (1,900ms) ← Query lenta sin índice
  │   └─ Password validation (20ms)
  └─ JWT generation (12ms)

Ahora sabes: Optimizar query de User Service
```

**Implementación Propuesta**:

#### Paso 1: Agregar Zipkin al Docker Compose
```yaml
# docker-compose.yml
services:
  zipkin:
    image: openzipkin/zipkin:latest
    container_name: zipkin
    ports:
      - "9411:9411"
    networks:
      - gym-network
    environment:
      - STORAGE_TYPE=mem  # Para desarrollo, usar postgres/cassandra en producción
    restart: unless-stopped
    mem_limit: 512m
    mem_reservation: 256m
```

#### Paso 2: Habilitar Zipkin en Servicios
```yaml
# api-gateway/application.yml (descomentar)
spring:
  zipkin:
    base-url: http://zipkin:9411
    enabled: true

management:
  tracing:
    enabled: true
    sampling:
      probability: 1.0  # 100% en dev, 0.1 (10%) en producción
  zipkin:
    tracing:
      endpoint: http://zipkin:9411/api/v2/spans
```

#### Paso 3: Configurar en Todos los Servicios
```yaml
# user-service, workout-service, authentication/application.yml
spring:
  application:
    name: user-service  # Nombre que aparecerá en Zipkin
  zipkin:
    base-url: http://zipkin:9411
    enabled: true

management:
  tracing:
    enabled: true
    sampling:
      probability: 0.1  # 10% de las peticiones en producción (menos overhead)
```

#### Paso 4: Configuración de Trazas
```yaml
# Logging pattern con trace ID (ya configurado)
logging:
  pattern:
    level: '%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]'
```

**Acceso al Dashboard**:
```
http://localhost:9411/zipkin
```

**Características del Dashboard**:
- 🔎 **Search**: Buscar trazas por servicio, operación, tags
- 📈 **Dependencies**: Mapa de dependencias entre servicios
- 🕐 **Timeline**: Visualización temporal de spans
- 📊 **Metrics**: Latencias p50, p95, p99

**Integración con Prometheus/Grafana**:
```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'zipkin'
    static_configs:
      - targets: ['zipkin:9411']
```

**Ventajas**:
- ✅ Identifica servicios lentos en la cadena
- ✅ Detecta errores en cascada
- ✅ Visualiza arquitectura real (quién llama a quién)
- ✅ Debugging en producción sin logs invasivos
- ✅ Análisis de SLA por endpoint

**Desventajas**:
- ⚠️ Overhead de performance (~1-5ms por request con sampling 100%)
- ⚠️ Consumo de memoria (512MB para Zipkin + storage)
- ⚠️ Complejidad adicional en el stack

**Recomendación**:
- 🟢 **Desarrollo**: Habilitar con sampling 100% para debugging
- 🟡 **Staging**: Habilitar con sampling 50%
- 🟢 **Producción**: Habilitar con sampling 10% (balance entre observabilidad y overhead)

**Alternativas**:
- **Jaeger**: Similar a Zipkin, más features (CNCF project)
- **AWS X-Ray**: Si estás en AWS
- **Google Cloud Trace**: Si estás en GCP
- **New Relic / DataDog**: Soluciones comerciales con más features

**Estado**: ⏳ PENDIENTE - Prioridad MEDIA-ALTA  
**Esfuerzo**: 2-3 horas (configuración + testing)  
**Sprint**: Sprint 3 - Fase de Observabilidad Avanzada

---

### 3.6 Implementar Circuit Breaker Correctamente

**Estado Actual**: ✅ Resilience4j configurado y funcionando en API Gateway

**Implementación Completada** (Sprint 3 - Fase 3):
- ✅ Circuit breakers para user-service, workout-service, authentication
- ✅ Configuración: sliding-window=10, failure-rate=50%, timeout=3s
- ✅ FallbackController con respuestas HTTP 503 amigables
- ✅ Métricas expuestas en /actuator (circuitbreaker.state, calls, failure.rate)
- ✅ Dependencia reactor-resilience4j correcta para Gateway reactivo

**Configuración Implementada**:

```yaml
# api-gateway/application.yml
resilience4j:
  circuitbreaker:
    instances:
      user-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000
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
        timeout-duration: 5s
      workout-service:
        timeout-duration: 5s
```

```java
// GatewayConfig.java
@Bean
public RouteLocator myRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("user-service", r -> r
            .path("/user/**")
            .filters(f -> f
                .filter(authenticationFilter)
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
    public ResponseEntity<Map<String, String>> userServiceFallback() {
        return ResponseEntity.status(503).body(
            Map.of(
                "error", "User service temporarily unavailable",
                "message", "Please try again later"
            )
        );
    }
}
```

---

## 4. Mejoras de Prioridad Media 🟢

### 4.1 Estandarizar Respuestas de API

**Problema**: Respuestas inconsistentes entre servicios

**Solución**: DTO común de respuesta

```java
@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private LocalDateTime timestamp;
    private String traceId;
}

// Uso
@GetMapping("/users")
public ApiResponse<List<UserDto>> getUsers() {
    return ApiResponse.<List<UserDto>>builder()
        .success(true)
        .message("Users retrieved successfully")
        .data(userService.findAll())
        .timestamp(LocalDateTime.now())
        .build();
}
```

### 4.2 Validación de DTOs

```java
@Data
public class UserRegistrationRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;
    
    @Email(message = "Invalid email format")
    @NotBlank
    private String email;
    
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
             message = "Password must contain uppercase, lowercase, digit, min 8 chars")
    private String password;
}

@RestController
public class UserController {
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest request) {
        // ...
    }
}
```

### 4.3 Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
            
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
}
```

### 4.4 Documentación con OpenAPI/Swagger

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
        contact = @Contact(name = "Your Team", email = "team@gym.com")
    )
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
                )
            );
    }
}
```

Acceso: `http://localhost:8590/swagger-ui.html`

### 4.5 Health Checks Personalizados

```java
// Implementar el health indicator comentado
@Component
public class DownstreamServiceHealthIndicator implements ReactiveHealthIndicator {
    
    @Autowired
    private WebClient webClient;
    
    @Override
    public Mono<Health> health() {
        return checkDownstreamServiceHealth()
            .onErrorResume(ex -> Mono.just(
                Health.down()
                    .withDetail("error", ex.getMessage())
                    .build()
            ));
    }

    private Mono<Health> checkDownstreamServiceHealth() {
        return webClient
            .get()
            .uri("http://user-service/actuator/health")
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> Health.up()
                .withDetail("user-service", "available")
                .build()
            )
            .timeout(Duration.ofSeconds(2));
    }
}
```

### 4.6 Rate Limiting en Gateway

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway-redis</artifactId>
</dependency>
```

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
                redis-rate-limiter.replenishRate: 10  # requests per second
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@userKeyResolver}"
```

```java
@Bean
public KeyResolver userKeyResolver() {
    return exchange -> {
        String userId = exchange.getRequest()
            .getHeaders()
            .getFirst("X-User-Id");
        return Mono.just(userId != null ? userId : "anonymous");
    };
}
```

---

## 5. Mejoras de Prioridad Baja ⚪

### 5.1 Containerización Optimizada

**Multi-stage Dockerfile**:
```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8588
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 5.2 Logging Estructurado

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
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
    <root level="INFO">
        <appender-ref ref="JSON"/>
    </root>
</configuration>
```

### 5.3 Tests Automatizados

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
        UserDto mockUser = new UserDto(/* ... */);
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

### 5.4 CI/CD Pipeline

```yaml
# .github/workflows/ci.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Build with Maven
        run: mvn clean install
      
      - name: Run Tests
        run: mvn test
      
      - name: Build Docker Images
        run: docker-compose build
      
      - name: Push to Docker Hub
        if: github.ref == 'refs/heads/main'
        run: |
          echo "${{ secrets.DOCKER_PASSWORD }}" | docker login -u "${{ secrets.DOCKER_USERNAME }}" --password-stdin
          docker-compose push
```

---

## 6. Roadmap de Implementación

> **⚠️ IMPORTANTE**: Las mejoras pendientes han sido catalogadas y priorizadas en **Sprint 3**.  
> Ver: [`docs/sprints/sprint-3/MEJORAS_PENDIENTES.md`](sprints/sprint-3/MEJORAS_PENDIENTES.md)

### ✅ Fase 1: Estabilización (COMPLETADA) 
1. ✅ Migrar secretos a variables de entorno
2. ✅ Cambiar DDL auto a `validate` + Flyway
3. ✅ Eliminar IPs hardcodeadas
4. ✅ Configurar actuator correctamente

### ✅ Fase 2: Observabilidad (COMPLETADA - Sprint 1 & 2)
1. ✅ Implementar Admin Service
2. ✅ Integrar con Prometheus/Grafana
3. ✅ Configurar Alertmanager
4. ✅ Dashboards de monitoreo

### ✅ Optimización Fase 1 (COMPLETADA)
1. ✅ Optimización JVM (MaxRAMPercentage, G1GC, lazy-init)
2. ✅ Límites de memoria (mem_limit, mem_reservation)
3. ✅ OOM score adjustment
4. ✅ Reducción de memoria: 35.8% (~1,611 MB)

### 🏗️ Fase 3: Arquitectura Asíncrona (Sprint 3 - EN PROGRESO)
1. ✅ Implementar RabbitMQ para eventos (email, notificaciones, auditoría) - COMPLETADO
2. ✅ Desacoplar Authentication de User Service - COMPLETADO
3. ✅ Configurar Circuit Breakers correctamente - COMPLETADO
4. ⏳ Centralizar configuración con Config Service - PENDIENTE
5. ⏳ Implementar Zipkin para tracing distribuido - PENDIENTE (Prioridad MEDIA-ALTA)

**Progreso**: `[████████░░] 60%` (3/5 tareas completadas)

### 🏗️ Fase 4: Calidad de Código (Sprint 3 - PENDIENTE)
1. ⏳ Estandarizar respuestas de API (`ApiResponse<T>`)
2. ⏳ Validación de DTOs con Bean Validation
3. ⏳ Global exception handler
4. ⏳ Documentación con Swagger/OpenAPI

### 🏗️ Optimización Fase 2 (Sprint 3 - PENDIENTE)
1. ⏳ Migrar a imágenes Alpine Linux
2. ⏳ Evaluar OpenJ9 vs Temurin
3. ⏳ Ajuste fino de límites de memoria
4. ⏳ Health checks personalizados

### 🔮 Fase 5: DevOps y Testing (Sprint 3 - FUTURO)
1. ⏳ Logging estructurado (JSON/Logstash)
2. ⏳ Tests automatizados (>50% coverage)
3. ⏳ Rate limiting en Gateway
4. ⏳ CI/CD pipeline (GitHub Actions)

---

## 7. Métricas de Éxito

### Antes de Mejoras
- ❌ Secretos expuestos en repositorio
- ❌ Pérdida de datos en cada reinicio
- ❌ Configuración no portable
- ❌ Sin observabilidad centralizada
- ❌ RabbitMQ sin usar (desperdicio)
- ❌ Acoplamiento alto entre servicios
- ❌ Sin documentación de API
- ❌ Sin tests automatizados

### Después de Mejoras
- ✅ Secretos en variables de entorno/Vault
- ✅ Persistencia de datos garantizada (Flyway)
- ✅ Configuración por entorno (Config Service)
- ✅ Dashboard de monitoreo (Admin Service)
- ✅ Comunicación asíncrona implementada
- ✅ Servicios desacoplados
- ✅ Swagger UI disponible
- ✅ Coverage de tests >70%
- ✅ CI/CD funcional

---

## Conclusión

El proyecto tiene fundamentos sólidos pero requiere mejoras críticas antes de producción. Priorizar:

1. **Seguridad** (secretos, actuator)
2. **Persistencia** (DDL auto, Flyway)
3. **Observabilidad** (Admin Service)
4. **Arquitectura** (desacoplamiento, eventos)

Con estas mejoras, el sistema será:
- 🔒 **Seguro**: Sin secretos expuestos
- 📊 **Observable**: Monitoreo centralizado
- 🚀 **Escalable**: Eventos asíncronos
- 🛡️ **Resiliente**: Circuit breakers y fallbacks
- 📖 **Documentado**: Swagger + README
- 🧪 **Testeado**: Cobertura adecuada

**La mejora sobre Actuator es CLAVE y debería ser prioridad junto con la gestión de secretos.**
