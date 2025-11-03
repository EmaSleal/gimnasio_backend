# Sprint 3 - Fases de Implementación

**Sprint**: Sprint 3 - Mejoras Arquitectónicas y Optimización  
**Duración**: 4-6 semanas (estimada)  
**Horas Totales**: 60-80 horas

---

## 📊 Progreso General

```
[░░░░░░░░░░░░░░░░░░░░] 0% - Sprint 3 listo para iniciar
```

| Métrica | Valor |
|---------|-------|
| **Fases Completadas** | 0/5 |
| **Tareas Completadas** | 0/22 |
| **Horas Invertidas** | 0/80 |
| **Semanas Transcurridas** | 0/6 |
| **Velocidad Real** | - |

---

## 🎯 Contexto Pre-Sprint 3

**Estado Sprints Anteriores**:
- ✅ **Sprint 1**: Microservicios + Admin Service (COMPLETADO 100%)
- ✅ **Sprint 2**: Prometheus + Grafana + Alertmanager (COMPLETADO 100%)
- ✅ **Optimización Fase 1**: Límites memoria + JVM (COMPLETADO 100%)

**Baseline Actual (Post-Optimización)**:
- **Memoria total**: 2,888 MB (~2.82 GB)
- **Reducción lograda**: 35.8% desde baseline estimado
- **Servicios**: 7 microservicios + 5 infraestructura = 12 contenedores
- **Estado**: Todos operacionales y monitoreados

**Infraestructura Disponible pero Subutilizada**:
- ❌ **RabbitMQ**: Operacional pero 0% uso
- ❌ **Config Service**: Operacional pero servicios no lo usan
- ❌ **Circuit Breakers**: Incluidos pero sin configurar
- ❌ **Actuator**: Configurado pero sin validación ni health checks custom

---

## 🎯 Fases del Sprint 3

### Fase 1: Comunicación Asíncrona con RabbitMQ (16h) 🔴 CRÍTICO
**Objetivo**: Implementar eventos asíncronos para desacoplar servicios y mejorar latencia

**Estado**: 🏗️ Pendiente  
**Progreso**: `[░░░░░░░░░░] 0%` - No iniciado

**Tareas Incluidas**:

#### [ ] **Tarea 1.1**: Configurar exchanges y queues en RabbitMQ (3h)
**Descripción**: Crear infraestructura de mensajería

**Subtareas**:
- [ ] Crear `RabbitMQConfig.java` en cada servicio que lo requiera
- [ ] Definir exchanges: `user.exchange`, `email.exchange`, `notification.exchange`, `audit.exchange`
- [ ] Definir queues: `user.created.queue`, `email.welcome.queue`, `workout.notification.queue`, `audit.queue`
- [ ] Configurar bindings con routing keys
- [ ] Validar creación en RabbitMQ Management UI (http://localhost:15672)

**Archivos a Crear**:
```
user-service/src/main/java/cr/ac/backend/user/config/RabbitMQConfig.java
authentication/src/main/java/cr/ac/backend/auth/config/RabbitMQConfig.java
workout-service/src/main/java/cr/ac/backend/workout/config/RabbitMQConfig.java
```

**Criterios de Aceptación**:
- ✅ 4 exchanges creados y visibles en RabbitMQ UI
- ✅ 4+ queues creadas con bindings correctos
- ✅ Dead Letter Queues (DLQ) configuradas para retry
- ✅ TTL configurado en mensajes (24 horas)

**Esfuerzo**: 3 horas  
**Riesgo**: Bajo

---

#### [ ] **Tarea 1.2**: Implementar envío de emails asíncrono (4h)
**Descripción**: Desacoplar envío de emails del flujo de registro

**Flujo Actual (síncrono)**:
```
POST /auth/register → Authentication Service → Resend API (bloquea 2-3s) → Response
```

**Flujo Propuesto (asíncrono)**:
```
POST /auth/register → Authentication Service → RabbitMQ (5ms) → Response
                                                    ↓
                                              Email Worker → Resend API
```

**Subtareas**:
- [ ] Crear DTOs de eventos: `WelcomeEmailEvent`, `PasswordResetEmailEvent`
- [ ] Modificar `AuthenticationServiceImpl.register()` para publicar eventos
- [ ] Crear `EmailEventListener` con `@RabbitListener`
- [ ] Implementar retry con backoff exponencial (3 intentos)
- [ ] Agregar logging estructurado de eventos
- [ ] Testing: Registrar usuario y verificar email enviado

**Archivos a Modificar**:
```
authentication/src/main/java/cr/ac/backend/auth/service/AuthenticationServiceImpl.java
```

**Archivos a Crear**:
```
authentication/src/main/java/cr/ac/backend/auth/events/WelcomeEmailEvent.java
authentication/src/main/java/cr/ac/backend/auth/events/PasswordResetEmailEvent.java
authentication/src/main/java/cr/ac/backend/auth/listeners/EmailEventListener.java
```

**Criterios de Aceptación**:
- ✅ Registro de usuario responde en <500ms (vs 2-3s antes)
- ✅ Email se envía correctamente (verificar en Resend)
- ✅ Si Resend falla, mensaje va a DLQ después de 3 reintentos
- ✅ Logs muestran evento publicado y consumido
- ✅ Métricas de RabbitMQ muestran mensajes procesados

**Esfuerzo**: 4 horas  
**Riesgo**: Bajo

---

#### [ ] **Tarea 1.3**: Publicar eventos UserCreated (3h)
**Descripción**: User Service publica evento cuando se crea usuario

**Subtareas**:
- [ ] Crear DTO `UserCreatedEvent` con campos: id, email, username, role, timestamp
- [ ] Modificar `UserServiceImpl.register()` para publicar evento
- [ ] Crear listener en Authentication Service (opcional: pre-generar tokens)
- [ ] Crear listener en futuro Notification Service (para onboarding)
- [ ] Testing: Verificar evento publicado y consumido

**Archivos a Crear**:
```
user-service/src/main/java/cr/ac/backend/user/events/UserCreatedEvent.java
user-service/src/main/java/cr/ac/backend/user/events/UserEventPublisher.java
authentication/src/main/java/cr/ac/backend/auth/listeners/UserEventListener.java
```

**Criterios de Aceptación**:
- ✅ Al crear usuario, evento publicado en `user.exchange`
- ✅ Authentication Service recibe evento correctamente
- ✅ Evento contiene todos los datos necesarios
- ✅ Logs correlacionados con traceId

**Esfuerzo**: 3 horas  
**Riesgo**: Bajo

---

#### [ ] **Tarea 1.4**: Implementar notificaciones de Workouts (4h)
**Descripción**: Notificar cuando se asigna/completa workout

**Subtareas**:
- [ ] Crear eventos: `WorkoutAssignedEvent`, `WorkoutCompletedEvent`
- [ ] Modificar `WorkoutServiceImpl` para publicar eventos
- [ ] Crear `NotificationEventListener` (puede ser en mismo servicio temporalmente)
- [ ] Implementar envío de email/notificación
- [ ] Testing: Asignar workout y verificar notificación

**Archivos a Crear**:
```
workout-service/src/main/java/cr/ac/backend/workout/events/WorkoutAssignedEvent.java
workout-service/src/main/java/cr/ac/backend/workout/events/WorkoutCompletedEvent.java
workout-service/src/main/java/cr/ac/backend/workout/listeners/NotificationEventListener.java
```

**Criterios de Aceptación**:
- ✅ Asignar workout publica evento
- ✅ Completar workout publica evento
- ✅ Usuario recibe email de notificación
- ✅ Mensajes persistidos en RabbitMQ si listener no disponible

**Esfuerzo**: 4 horas  
**Riesgo**: Bajo

---

#### [ ] **Tarea 1.5**: Implementar auditoría con eventos (2h)
**Descripción**: Auditar acciones críticas de forma asíncrona

**Subtareas**:
- [ ] Crear `AuditEvent` DTO con: user, action, resource, timestamp, ip
- [ ] Crear `@Auditable` annotation
- [ ] Crear `AuditAspect` con AOP para capturar eventos
- [ ] Publicar eventos a `audit.exchange`
- [ ] Crear listener que persiste en base de datos (futuro: tabla audit_log)
- [ ] Anotar métodos críticos: register, login, delete, update

**Archivos a Crear**:
```
common/src/main/java/cr/ac/backend/common/audit/Auditable.java
common/src/main/java/cr/ac/backend/common/audit/AuditEvent.java
common/src/main/java/cr/ac/backend/common/audit/AuditAspect.java
common/src/main/java/cr/ac/backend/common/audit/AuditEventListener.java
```

**Criterios de Aceptación**:
- ✅ Métodos anotados publican eventos automáticamente
- ✅ Eventos contienen contexto completo (usuario, IP, timestamp)
- ✅ Listener persiste eventos (tabla audit_log o logs)
- ✅ No impacta performance (<5ms overhead)

**Esfuerzo**: 2 horas  
**Riesgo**: Medio (AOP puede ser complejo)

---

**Criterios de Aceptación Fase 1**:
- ✅ RabbitMQ utilización > 50% (vs 0% actual)
- ✅ Latencia de registro reducida >2s
- ✅ Al menos 4 tipos de eventos implementados
- ✅ Dead Letter Queues funcionando
- ✅ Métricas de RabbitMQ visibles en Grafana
- ✅ Logs estructurados con correlación

**Archivos Totales**:
- A crear: ~15 archivos nuevos
- A modificar: ~5 archivos existentes

**Bloqueadores Potenciales**:
- Configuración incorrecta de bindings
- Serialización/deserialización de eventos
- Dead letter queues no capturan errores

**Fecha Objetivo**: Semana 1-2

---

### Fase 2: Desacoplamiento Architecture (8h) 🔴 CRÍTICO
**Objetivo**: Eliminar dependencia directa Authentication → User Service

**Estado**: 🏗️ Pendiente  
**Progreso**: `[░░░░░░░░░░] 0%` - No iniciado

**Tareas Incluidas**:

#### [ ] **Tarea 2.1**: Refactorizar flujo de registro (4h)
**Descripción**: Eliminar hop Authentication → User Service en registro

**Flujo Actual (acoplado)**:
```
Cliente → Gateway → Authentication → User Service → DB
                         ↓
                    Genera JWT
```

**Flujo Propuesto (desacoplado)**:
```
Cliente → Gateway → User Service → DB
                         ↓
                    Publica UserCreated
                         ↓
                    Authentication → Procesa evento (async)
```

**Subtareas**:
- [ ] Modificar `AuthenticationController.register()` para redirigir a User Service
- [ ] O mejor: Exponer `/user/register` directamente en Gateway
- [ ] Authentication solo escucha evento `UserCreated` para logging/email
- [ ] Eliminar `restTemplate.postForObject()` de Authentication
- [ ] Testing: Verificar registro funciona sin cambios en cliente
- [ ] Medir latencia antes/después

**Archivos a Modificar**:
```
authentication/src/main/java/cr/ac/backend/auth/controller/AuthenticationController.java
authentication/src/main/java/cr/ac/backend/auth/service/AuthenticationServiceImpl.java
api-gateway/src/main/resources/application.yml
```

**Criterios de Aceptación**:
- ✅ Registro funciona sin Authentication como proxy
- ✅ Latencia reducida ~100-200ms
- ✅ Authentication procesa evento UserCreated correctamente
- ✅ Tests de integración pasan
- ✅ Documentación actualizada

**Esfuerzo**: 4 horas  
**Riesgo**: Medio (requiere cambio en flujo)

---

#### [ ] **Tarea 2.2**: Refactorizar flujo de login (4h)
**Descripción**: Login solo consulta User Service para validación básica

**Flujo Actual**:
```
POST /auth/login → Authentication → User Service (GET /user/{email})
                        ↓
                   Valida password (¿dónde?)
                        ↓
                   Genera JWT
```

**Flujo Propuesto**:
```
POST /auth/login → Authentication → Consulta User (solo email, role)
                        ↓
                   Valida password con hash almacenado
                        ↓
                   Genera JWT
```

**Subtareas**:
- [ ] Verificar dónde se almacena password hash (User Service o Authentication)
- [ ] Si está en User Service: Crear endpoint `GET /user/credentials/{email}`
- [ ] Minimizar datos transferidos (solo lo necesario para validación)
- [ ] Implementar cache de credenciales (opcional, con Redis)
- [ ] Testing: Login funcional, JWT válido
- [ ] Benchmarking: Medir latencia

**Archivos a Modificar**:
```
authentication/src/main/java/cr/ac/backend/auth/service/AuthenticationServiceImpl.java
user-service/src/main/java/cr/ac/backend/user/controller/UserController.java
```

**Criterios de Aceptación**:
- ✅ Login funciona correctamente
- ✅ Solo datos necesarios transferidos
- ✅ Latencia < 200ms (percentil 95)
- ✅ Password hash nunca expuesto en logs
- ✅ JWT con claims correctos

**Esfuerzo**: 4 horas  
**Riesgo**: Alto (seguridad crítica)

---

**Criterios de Aceptación Fase 2**:
- ✅ Authentication y User Service desacoplados
- ✅ Registro y login funcionan sin cambios en cliente
- ✅ Latencia total reducida >200ms
- ✅ Tests de integración pasan
- ✅ Métricas muestran reducción de llamadas inter-servicios

**Fecha Objetivo**: Semana 2

---

### Fase 3: Resiliencia y Circuit Breakers (6h) 🔴 CRÍTICO
**Objetivo**: Proteger servicios con circuit breakers y fallbacks

**Estado**: 🏗️ Pendiente  
**Progreso**: `[░░░░░░░░░░] 0%` - No iniciado

**Tareas Incluidas**:

#### [ ] **Tarea 3.1**: Configurar Resilience4j en Gateway (3h)
**Descripción**: Agregar circuit breakers a todas las rutas

**Subtareas**:
- [ ] Agregar configuración `resilience4j` en `application.yml`
- [ ] Configurar circuit breakers para: user-service, workout-service, authentication
- [ ] Definir parámetros: sliding-window-size=10, failure-rate-threshold=50%, wait-duration=10s
- [ ] Configurar time limiter: timeout-duration=3s
- [ ] Testing: Simular caída de servicio y verificar circuit breaker se abre

**Archivos a Modificar**:
```
api-gateway/src/main/resources/application.yml
```

**Configuración**:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      user-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
      workout-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000
      authentication:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000
  timelimiter:
    instances:
      user-service:
        timeout-duration: 3s
      workout-service:
        timeout-duration: 3s
      authentication:
        timeout-duration: 3s
```

**Criterios de Aceptación**:
- ✅ Circuit breakers configurados en 3 servicios
- ✅ Timeouts configurados (3s)
- ✅ Métricas de Resilience4j expuestas en Actuator
- ✅ Prometheus scrapeando métricas de circuit breakers

**Esfuerzo**: 3 horas  
**Riesgo**: Bajo

---

#### [ ] **Tarea 3.2**: Implementar Fallback Controllers (3h)
**Descripción**: Crear respuestas amigables cuando servicios caen

**Subtareas**:
- [ ] Crear `FallbackController` en Gateway
- [ ] Implementar endpoints: `/fallback/user-service`, `/fallback/workout-service`, `/fallback/authentication`
- [ ] Responder con `ApiResponse` estándar (503 Service Unavailable)
- [ ] Incluir mensaje descriptivo y tiempo estimado de recuperación
- [ ] Configurar fallbackUri en rutas de Gateway
- [ ] Testing: Detener servicio y verificar fallback

**Archivos a Crear**:
```
api-gateway/src/main/java/cr/ac/backend/gateway/controller/FallbackController.java
```

**Código Ejemplo**:
```java
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/user-service")
    public ResponseEntity<ApiResponse<?>> userServiceFallback() {
        return ResponseEntity.status(503).body(
            ApiResponse.builder()
                .success(false)
                .message("User service is temporarily unavailable")
                .error("SERVICE_UNAVAILABLE")
                .timestamp(LocalDateTime.now())
                .data(Map.of(
                    "estimated_recovery", "2-5 minutes",
                    "retry_after", "30 seconds"
                ))
                .build()
        );
    }
    
    // Similar para workout-service y authentication
}
```

**Archivos a Modificar**:
```
api-gateway/src/main/java/cr/ac/backend/gateway/config/GatewayConfig.java
```

**Criterios de Aceptación**:
- ✅ Fallbacks implementados para 3 servicios
- ✅ Respuestas con formato `ApiResponse` estándar
- ✅ Mensaje amigable al usuario
- ✅ HTTP 503 (Service Unavailable)
- ✅ Testing: Caída de servicio devuelve fallback correctamente

**Esfuerzo**: 3 horas  
**Riesgo**: Bajo

---

**Criterios de Aceptación Fase 3**:
- ✅ Circuit breakers activos en Gateway
- ✅ Fallbacks funcionando en caso de fallo
- ✅ Timeout < 3s en lugar de 30s
- ✅ Métricas de circuit breaker en Grafana
- ✅ Dashboard Grafana muestra estado de circuit breakers (OPEN/CLOSED/HALF_OPEN)

**Fecha Objetivo**: Semana 3

---

### Fase 4: Calidad de Código y APIs (14h) 🟡 ALTA
**Objetivo**: Estandarizar respuestas, validación y documentación

**Estado**: 🏗️ Pendiente  
**Progreso**: `[░░░░░░░░░░] 0%` - No iniciado

**Tareas Incluidas**:

#### [ ] **Tarea 4.1**: Crear DTOs estándar (2h)
**Descripción**: Clase `ApiResponse<T>` común para todos los servicios

**Subtareas**:
- [ ] Crear módulo `common` o librería compartida
- [ ] Crear clase `ApiResponse<T>` genérica
- [ ] Crear clase `ErrorDetail` para errores estructurados
- [ ] Agregar dependencia en todos los servicios
- [ ] Documentar uso en README

**Archivos a Crear**:
```
common/src/main/java/cr/ac/backend/common/dto/ApiResponse.java
common/src/main/java/cr/ac/backend/common/dto/ErrorDetail.java
common/pom.xml
```

**Código**:
```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<ErrorDetail> errors;
    private LocalDateTime timestamp;
    private String traceId;  // Para correlación con logs/Grafana
    private String path;     // Request path
}

@Data
@Builder
public class ErrorDetail {
    private String field;
    private String code;
    private String message;
}
```

**Criterios de Aceptación**:
- ✅ Clase `ApiResponse<T>` creada y compilable
- ✅ Incluida como dependencia en 3+ servicios
- ✅ Ejemplo de uso documentado
- ✅ Compatible con Jackson (JSON serialization)

**Esfuerzo**: 2 horas  
**Riesgo**: Bajo

---

#### [ ] **Tarea 4.2**: Refactorizar controllers con ApiResponse (4h)
**Descripción**: Migrar todos los controllers a usar `ApiResponse<T>`

**Subtareas**:
- [ ] Refactorizar `UserController` (user-service)
- [ ] Refactorizar `WorkoutController` (workout-service)
- [ ] Refactorizar `AuthenticationController` (authentication)
- [ ] Mantener backward compatibility (opcional: versioning `/api/v1/`, `/api/v2/`)
- [ ] Testing: Verificar respuestas con formato correcto

**Archivos a Modificar**:
```
user-service/src/main/java/cr/ac/backend/user/controller/UserController.java
workout-service/src/main/java/cr/ac/backend/workout/controller/WorkoutController.java
authentication/src/main/java/cr/ac/backend/auth/controller/AuthenticationController.java
```

**Antes**:
```java
@GetMapping("/users")
public ResponseEntity<List<UserDto>> getUsers() {
    return ResponseEntity.ok(userService.findAll());
}
```

**Después**:
```java
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

**Criterios de Aceptación**:
- ✅ Todos los endpoints devuelven `ApiResponse<T>`
- ✅ Formato consistente en todos los servicios
- ✅ TraceId incluido para correlación
- ✅ Tests pasan

**Esfuerzo**: 4 horas  
**Riesgo**: Bajo (pero tedioso)

---

#### [ ] **Tarea 4.3**: Implementar Global Exception Handler (2h)
**Descripción**: Manejo centralizado de excepciones

**Subtareas**:
- [ ] Crear `@RestControllerAdvice` en cada servicio
- [ ] Manejar `MethodArgumentNotValidException` (validación)
- [ ] Manejar `EntityNotFoundException` (404)
- [ ] Manejar excepciones genéricas (500)
- [ ] Devolver `ApiResponse` con errores estructurados
- [ ] Logging de errores con nivel apropiado

**Archivos a Crear**:
```
user-service/src/main/java/cr/ac/backend/user/exception/GlobalExceptionHandler.java
workout-service/src/main/java/cr/ac/backend/workout/exception/GlobalExceptionHandler.java
authentication/src/main/java/cr/ac/backend/auth/exception/GlobalExceptionHandler.java
```

**Código Ejemplo**:
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorDetail> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> ErrorDetail.builder()
                .field(e.getField())
                .code("VALIDATION_ERROR")
                .message(e.getDefaultMessage())
                .build())
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
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(
            ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errors(List.of(ErrorDetail.builder()
                    .code("NOT_FOUND")
                    .message(ex.getMessage())
                    .build()))
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(500).body(
            ApiResponse.builder()
                .success(false)
                .message("Internal server error")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
}
```

**Criterios de Aceptación**:
- ✅ Excepciones devuelven `ApiResponse` consistente
- ✅ Errores de validación con detalles por campo
- ✅ 404 con mensaje descriptivo
- ✅ 500 sin exponer detalles internos
- ✅ Logs apropiados (ERROR para 500, WARN para 404)

**Esfuerzo**: 2 horas  
**Riesgo**: Bajo

---

#### [ ] **Tarea 4.4**: Agregar validación con Bean Validation (3h)
**Descripción**: Validar DTOs de entrada con anotaciones

**Subtareas**:
- [ ] Agregar dependencia `spring-boot-starter-validation`
- [ ] Anotar DTOs con `@NotBlank`, `@Email`, `@Size`, `@Pattern`, etc.
- [ ] Agregar `@Valid` en parámetros de controllers
- [ ] Crear validadores custom si es necesario
- [ ] Testing: Enviar datos inválidos y verificar respuesta 400

**Archivos a Modificar**:
```
user-service/src/main/java/cr/ac/backend/user/dto/UserRegistrationRequest.java
authentication/src/main/java/cr/ac/backend/auth/dto/LoginRequest.java
workout-service/src/main/java/cr/ac/backend/workout/dto/WorkoutPlanRequest.java
```

**Ejemplo**:
```java
@Data
@Builder
public class UserRegistrationRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",
        message = "Password must contain uppercase, lowercase, digit, special char, min 8 chars"
    )
    private String password;
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "USER|ADMIN|TRAINER", message = "Invalid role")
    private String role;
}
```

**Criterios de Aceptación**:
- ✅ DTOs anotados con validaciones
- ✅ Controllers con `@Valid`
- ✅ Datos inválidos devuelven 400 con detalles
- ✅ Mensajes de error descriptivos
- ✅ Global Exception Handler captura errores de validación

**Esfuerzo**: 3 horas  
**Riesgo**: Bajo

---

#### [ ] **Tarea 4.5**: Documentar APIs con Swagger/OpenAPI (3h)
**Descripción**: Generar documentación interactiva de APIs

**Subtareas**:
- [ ] Agregar dependencia `springdoc-openapi-starter-webmvc-ui`
- [ ] Crear `OpenApiConfig` en cada servicio
- [ ] Anotar controllers con `@Operation`, `@ApiResponse`, etc.
- [ ] Configurar seguridad JWT en Swagger
- [ ] Validar Swagger UI accesible: http://localhost:8588/swagger-ui.html
- [ ] Exportar OpenAPI spec JSON

**Archivos a Crear**:
```
user-service/src/main/java/cr/ac/backend/user/config/OpenApiConfig.java
workout-service/src/main/java/cr/ac/backend/workout/config/OpenApiConfig.java
authentication/src/main/java/cr/ac/backend/auth/config/OpenApiConfig.java
```

**pom.xml**:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

**OpenApiConfig.java**:
```java
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "User Service API",
        version = "1.0",
        description = "API for managing gym users",
        contact = @Contact(
            name = "Development Team",
            email = "dev@gym.com"
        ),
        license = @License(
            name = "MIT",
            url = "https://opensource.org/licenses/MIT"
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

**Controller Annotations**:
```java
@Operation(
    summary = "Get all users",
    description = "Retrieve a list of all registered users"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
})
@GetMapping("/users")
public ResponseEntity<ApiResponse<List<UserDto>>> getUsers() {
    // ...
}
```

**Criterios de Aceptación**:
- ✅ Swagger UI accesible en 3 servicios
- ✅ Documentación completa de endpoints
- ✅ Autenticación JWT configurable en UI
- ✅ Schemas de DTOs visibles
- ✅ Ejemplos de request/response
- ✅ Exportable como JSON/YAML

**Esfuerzo**: 3 horas  
**Riesgo**: Bajo

---

**Criterios de Aceptación Fase 4**:
- ✅ Todos los servicios devuelven `ApiResponse<T>`
- ✅ Validación con Bean Validation implementada
- ✅ Global Exception Handler en 3 servicios
- ✅ Swagger UI accesible en 3 servicios
- ✅ Documentación API completa y actualizada

**Fecha Objetivo**: Semana 3-4

---

### Fase 5: Configuración Centralizada (8h) 🟡 ALTA
**Objetivo**: Usar Config Service para centralizar configuración

**Estado**: 🏗️ Pendiente  
**Progreso**: `[░░░░░░░░░░] 0%` - No iniciado

**Tareas Incluidas**:

#### [ ] **Tarea 5.1**: Crear repositorio de configuraciones (2h)
**Descripción**: Repositorio Git para almacenar configs

**Subtareas**:
- [ ] Crear repositorio GitHub: `gym-config-server`
- [ ] Estructura de carpetas: por servicio y por perfil
- [ ] Crear archivos: `application.yml`, `application-docker.yml`, `application-prod.yml`
- [ ] Crear configs específicos: `user-service.yml`, `workout-service.yml`, etc.
- [ ] Documentar estructura en README
- [ ] Hacer público o configurar SSH key

**Estructura**:
```
gym-config-server/
├── README.md
├── application.yml              # Configuración común
├── application-docker.yml       # Específico Docker
├── application-prod.yml         # Producción
├── user-service.yml
├── user-service-docker.yml
├── user-service-prod.yml
├── workout-service.yml
├── workout-service-docker.yml
├── authentication.yml
└── api-gateway.yml
```

**application.yml** (común):
```yaml
# Configuración compartida por TODOS los servicios
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URI:http://eureka-server:8761/eureka/}
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  pattern:
    level: '%5p [${spring.application.name:},%X{traceId:-}]'
  level:
    root: INFO
```

**user-service.yml**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:postgres}:5432/${DB_NAME:gym_authentication}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate  # Nunca create en config centralizado
    show-sql: false
  flyway:
    enabled: true
    baseline-on-migrate: true
```

**Criterios de Aceptación**:
- ✅ Repositorio creado y accesible
- ✅ Estructura de archivos correcta
- ✅ Config Service puede clonar repositorio
- ✅ Archivo README con documentación

**Esfuerzo**: 2 horas  
**Riesgo**: Bajo

---

#### [ ] **Tarea 5.2**: Actualizar Config Service (2h)
**Descripción**: Configurar Config Service para usar repositorio Git

**Subtareas**:
- [ ] Modificar `application.yml` de config-service
- [ ] Configurar URI del repositorio Git
- [ ] Configurar clone-on-start y force-pull
- [ ] Testing: Verificar Config Service arranca y clona repo
- [ ] Verificar endpoints: `http://localhost:8889/user-service/docker`

**Archivos a Modificar**:
```
config-service/src/main/resources/application.yml
```

**Configuración**:
```yaml
server:
  port: 8889

spring:
  application:
    name: config-service
  cloud:
    config:
      server:
        git:
          uri: https://github.com/EmaSleal/gym-config-server.git
          default-label: main
          clone-on-start: true
          force-pull: true
          timeout: 10
        # Opción: Usar sistema de archivos local para desarrollo
        # native:
        #   search-locations: file:///path/to/config-repo

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
```

**Testing**:
```bash
# Verificar config disponible
curl http://localhost:8889/user-service/docker
curl http://localhost:8889/application/docker
```

**Criterios de Aceptación**:
- ✅ Config Service clona repositorio al arrancar
- ✅ Endpoints de config accesibles
- ✅ Responde con configuraciones correctas
- ✅ Force-pull trae cambios al reiniciar

**Esfuerzo**: 2 horas  
**Riesgo**: Bajo

---

#### [ ] **Tarea 5.3**: Migrar servicios a Config Client (4h)
**Descripción**: Modificar servicios para consumir Config Service

**Subtareas**:
- [ ] Agregar dependencias `spring-cloud-starter-config` y `spring-cloud-starter-bootstrap`
- [ ] Crear `bootstrap.yml` en cada servicio (reemplaza parte de application.yml)
- [ ] Mover configuraciones sensibles a repositorio Git
- [ ] Mantener solo config mínimo en application.yml
- [ ] Configurar fail-fast y retry
- [ ] Testing: Arrancar servicio y verificar config cargada desde Config Service

**Archivos a Modificar (por cada servicio)**:
```
user-service/pom.xml
user-service/src/main/resources/bootstrap.yml (CREAR)
user-service/src/main/resources/application.yml (SIMPLIFICAR)
```

**pom.xml**:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

**bootstrap.yml**:
```yaml
spring:
  application:
    name: user-service
  cloud:
    config:
      uri: http://config-service:8889
      fail-fast: true  # Falla si Config Service no disponible
      retry:
        max-attempts: 6
        initial-interval: 1000
        multiplier: 1.5
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:docker}
```

**application.yml** (simplificado):
```yaml
# Solo config local/específico del contenedor
server:
  port: 8588

# TODO lo demás viene de Config Service
```

**Criterios de Aceptación**:
- ✅ 3+ servicios migratos a Config Client
- ✅ Servicios arrancan y cargan config desde Config Service
- ✅ Logs muestran "Fetching config from server"
- ✅ Cambios en repositorio Git se reflejan al reiniciar servicio
- ✅ Fail-fast funciona (no arranca si Config Service caído)

**Esfuerzo**: 4 horas (1h por servicio x 3-4 servicios)  
**Riesgo**: Medio (puede romper servicios si mal configurado)

---

**Criterios de Aceptación Fase 5**:
- ✅ Repositorio Git de configuraciones creado
- ✅ Config Service consumiendo repositorio
- ✅ Al menos 3 servicios usando Config Client
- ✅ Cambios de config sin rebuild de imágenes
- ✅ Documentación de estructura de configs

**Fecha Objetivo**: Semana 4-5

---

## 📈 Métricas por Fase

| Fase | Horas Est. | Horas Real | Tareas | Estado | % Completo |
|------|------------|------------|--------|--------|------------|
| **Fase 1** - RabbitMQ | 16h | - | 0/5 | 🏗️ Pendiente | 0% |
| **Fase 2** - Desacoplamiento | 8h | - | 0/2 | 🏗️ Pendiente | 0% |
| **Fase 3** - Circuit Breakers | 6h | - | 0/2 | 🏗️ Pendiente | 0% |
| **Fase 4** - Calidad Código | 14h | - | 0/5 | 🏗️ Pendiente | 0% |
| **Fase 5** - Config Centralizado | 8h | - | 0/3 | 🏗️ Pendiente | 0% |
| **Documentación** | 4h | - | - | 🏗️ Pendiente | 0% |
| **Testing Final** | 4h | - | - | 🏗️ Pendiente | 0% |
| **TOTAL** | **60h** | **0h** | **0/17** | - | **0%** |

---

## 🎯 Hitos (Milestones)

### Hito 1: RabbitMQ en Producción ⏳
**Fase**: Fase 1  
**Fecha Objetivo**: Semana 2  
**Estado**: 🏗️ **PENDIENTE**

**Definición**:
- ✅ RabbitMQ utilización > 50% (vs 0% actual)
- ✅ Al menos 4 tipos de eventos implementados
- ✅ Emails enviados asíncronamente
- ✅ Dead Letter Queues funcionando
- ✅ Métricas visibles en Grafana

---

### Hito 2: Arquitectura Desacoplada ⏳
**Fase**: Fase 2  
**Fecha Objetivo**: Semana 2  
**Estado**: 🏗️ **PENDIENTE**

**Definición**:
- ✅ Authentication no hace proxy a User Service
- ✅ Latencia reducida >200ms
- ✅ Servicios escalables independientemente
- ✅ Tests de integración pasan

---

### Hito 3: Sistema Resiliente ⏳
**Fase**: Fase 3  
**Fecha Objetivo**: Semana 3  
**Estado**: 🏗️ **PENDIENTE**

**Definición**:
- ✅ Circuit breakers activos
- ✅ Fallbacks implementados
- ✅ Timeout < 3s
- ✅ Dashboard Grafana muestra circuit breakers

---

### Hito 4: APIs Profesionales ⏳
**Fase**: Fase 4  
**Fecha Objetivo**: Semana 4  
**Estado**: 🏗️ **PENDIENTE**

**Definición**:
- ✅ ApiResponse<T> en todos los endpoints
- ✅ Validación implementada
- ✅ Swagger UI accesible
- ✅ Documentación completa

---

### Hito 5: Configuración Centralizada ⏳
**Fase**: Fase 5  
**Fecha Objetivo**: Semana 5  
**Estado**: 🏗️ **PENDIENTE**

**Definición**:
- ✅ Config Service operacional
- ✅ Servicios consumiendo config remota
- ✅ Cambios sin rebuild
- ✅ Configuraciones versionadas en Git

---

## 🚦 Semáforo de Riesgos

### 🟢 Bajo Riesgo
- Tarea 1.1: Configurar RabbitMQ (bien documentado)
- Tarea 1.2: Emails asíncronos (uso común)
- Tarea 3.1: Configurar Resilience4j (solo YAML)
- Tarea 4.1: Crear ApiResponse DTO (simple)
- Tarea 4.5: Swagger (librería madura)

### 🟡 Riesgo Medio
- Tarea 1.5: Auditoría con AOP (AOP puede ser complejo)
- Tarea 2.1: Refactorizar registro (cambio de flujo)
- Tarea 5.3: Migrar a Config Client (puede romper servicios)

### 🔴 Alto Riesgo
- Tarea 2.2: Refactorizar login (seguridad crítica)
  - **Mitigación**: Testing exhaustivo, revisar manejo de passwords
  - **Plan B**: Revertir a flujo anterior si falla

---

## 🔗 Dependencias entre Fases

```
Fase 1: RabbitMQ
    ↓
    ├── Tarea 1.1 → Tarea 1.2, 1.3, 1.4, 1.5 (paralelas)
    └── Habilita Fase 2 (eventos UserCreated)
    ↓
Fase 2: Desacoplamiento
    ↓
    ├── Tarea 2.1 → Tarea 2.2
    └── Puede ejecutarse en paralelo con Fase 3
    ↓
Fase 3: Circuit Breakers
    ↓
    ├── Tarea 3.1 → Tarea 3.2
    └── Independiente de otras fases
    ↓
Fase 4: Calidad Código
    ↓
    ├── Tarea 4.1 → Tarea 4.2, 4.3
    └── Tarea 4.4 y 4.5 paralelas
    ↓
Fase 5: Config Centralizado
    ↓
    ├── Tarea 5.1 → Tarea 5.2 → Tarea 5.3
    └── NO bloquea otras fases (puede ser última)
```

**Bloqueantes Críticos**:
- ⚠️ Fase 2 requiere Tarea 1.3 completada (eventos UserCreated)
- ⚠️ Tarea 4.2 requiere Tarea 4.1 completada (ApiResponse DTO)
- ⚠️ Tarea 5.3 requiere Tarea 5.1 y 5.2 completadas

**Paralelización Posible**:
- ✅ Fase 3 puede ejecutarse en paralelo con Fase 1/2
- ✅ Fase 4 puede iniciarse antes de completar Fase 5
- ✅ Tareas 1.2, 1.3, 1.4, 1.5 pueden hacerse en paralelo después de 1.1

---

## 📋 Checklist Pre-Sprint 3

Validar antes de iniciar:

**Infraestructura**:
- [x] Sprint 1 completado 100%
- [x] Sprint 2 completado 100%
- [x] Optimización Fase 1 completada
- [x] RabbitMQ corriendo (puerto 5672, 15672)
- [x] Config Service corriendo (puerto 8889)
- [x] Prometheus + Grafana operacionales
- [x] Memoria optimizada (~2,888 MB)

**Herramientas**:
- [ ] Cuenta GitHub para crear repositorio config
- [ ] Acceso a RabbitMQ Management UI (guest/guest)
- [ ] IDE configurado (IntelliJ/VS Code)
- [ ] Postman/Insomnia para testing APIs

**Conocimiento**:
- [ ] Familiarizado con RabbitMQ concepts (exchange, queue, binding)
- [ ] Conocimiento de Spring Cloud Config
- [ ] Conocimiento de Resilience4j/Circuit Breakers
- [ ] Bean Validation y Swagger basics

---

## 📚 Referencias y Recursos

### RabbitMQ
- [Spring AMQP Documentation](https://docs.spring.io/spring-amqp/reference/)
- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)
- [Dead Letter Exchanges](https://www.rabbitmq.com/dlx.html)

### Circuit Breakers
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Spring Cloud Circuit Breaker](https://spring.io/projects/spring-cloud-circuitbreaker)

### Config Service
- [Spring Cloud Config](https://spring.io/projects/spring-cloud-config)
- [Config Server Tutorial](https://www.baeldung.com/spring-cloud-configuration)

### API Quality
- [SpringDoc OpenAPI](https://springdoc.org/)
- [Bean Validation](https://beanvalidation.org/)
- [REST API Best Practices](https://restfulapi.net/)

---

## 🎯 Criterios de Finalización Sprint 3

**Técnicos**:
- [ ] RabbitMQ utilización > 50%
- [ ] Al menos 4 tipos de eventos implementados
- [ ] Authentication y User Service desacoplados
- [ ] Circuit breakers activos en Gateway
- [ ] ApiResponse<T> en todos los endpoints
- [ ] Swagger UI accesible en 3+ servicios
- [ ] Config Service consumido por 3+ servicios
- [ ] Latencia de registro < 500ms (vs 2-3s antes)

**Documentación**:
- [ ] README actualizado con nuevas features
- [ ] Guía de RabbitMQ events
- [ ] Guía de Circuit Breakers
- [ ] Swagger endpoints documentados
- [ ] Config repository README

**Calidad**:
- [ ] Sin degradación de performance
- [ ] Tests de integración pasan
- [ ] Sin errores críticos en logs
- [ ] Métricas de circuit breaker en Grafana
- [ ] Dead Letter Queues capturando errores

---

## 🔄 Estrategia de Implementación

### Enfoque Incremental
1. **Empezar pequeño**: Implementar email asíncrono primero (Tarea 1.2)
2. **Validar cada paso**: No pasar a siguiente tarea sin validar anterior
3. **Commits frecuentes**: Commit por tarea completada
4. **Testing continuo**: Probar cada feature antes de integrar

### Rollback Plan
- Mantener branches: `feature/rabbitmq`, `feature/circuit-breakers`, etc.
- Cada fase en PR separado
- Mantener configuración anterior comentada (rollback fácil)
- Monitoring intensivo primeras 48h después de deploy

### Comunicación
- Actualizar `fases.md` después de cada tarea
- Documentar problemas en `TROUBLESHOOTING_GUIDE.md`
- Commits descriptivos con prefijos: `feat:`, `fix:`, `refactor:`, `docs:`

---

## 📊 Tabla de Seguimiento de Tareas

| ID | Tarea | Esfuerzo | Estado | Inicio | Fin | Horas Real |
|----|-------|----------|--------|--------|-----|------------|
| 1.1 | Configurar RabbitMQ | 3h | ⏳ | - | - | - |
| 1.2 | Emails asíncronos | 4h | ⏳ | - | - | - |
| 1.3 | Eventos UserCreated | 3h | ⏳ | - | - | - |
| 1.4 | Notificaciones Workouts | 4h | ⏳ | - | - | - |
| 1.5 | Auditoría con eventos | 2h | ⏳ | - | - | - |
| 2.1 | Refactorizar registro | 4h | ⏳ | - | - | - |
| 2.2 | Refactorizar login | 4h | ⏳ | - | - | - |
| 3.1 | Config Resilience4j | 3h | ⏳ | - | - | - |
| 3.2 | Fallback Controllers | 3h | ⏳ | - | - | - |
| 4.1 | Crear ApiResponse DTO | 2h | ⏳ | - | - | - |
| 4.2 | Refactorizar controllers | 4h | ⏳ | - | - | - |
| 4.3 | Global Exception Handler | 2h | ⏳ | - | - | - |
| 4.4 | Bean Validation | 3h | ⏳ | - | - | - |
| 4.5 | Swagger/OpenAPI | 3h | ⏳ | - | - | - |
| 5.1 | Repo configuraciones | 2h | ⏳ | - | - | - |
| 5.2 | Actualizar Config Service | 2h | ⏳ | - | - | - |
| 5.3 | Migrar a Config Client | 4h | ⏳ | - | - | - |

---

**Creado**: 2 de noviembre de 2025  
**Última Actualización**: 2 de noviembre de 2025  
**Estado**: 📋 Planificado - Listo para iniciar  
**Siguiente Acción**: Iniciar Fase 1 - Tarea 1.1 (Configurar RabbitMQ exchanges y queues)
