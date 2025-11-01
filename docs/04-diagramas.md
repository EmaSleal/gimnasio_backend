# Diagramas de Arquitectura - Sistema de Gimnasio

## 1. Arquitectura de Alto Nivel

```
┌────────────────────────────────────────────────────────────────────────┐
│                          CAPA DE CLIENTE                               │
│                    Angular Frontend (Port 4200)                        │
└───────────────────────────────┬────────────────────────────────────────┘
                                │ HTTP/HTTPS + JWT
                                ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      CAPA DE GATEWAY (Port 8590)                       │
│  ┌──────────────────────────────────────────────────────────────┐     │
│  │  API Gateway                                                  │     │
│  │  - Enrutamiento dinámico                                      │     │
│  │  - Autenticación JWT                                         │     │
│  │  - CORS Configuration                                        │     │
│  │  - Circuit Breaker (Resilience4j)                           │     │
│  │  - Rate Limiting                                             │     │
│  └──────────────────────────────────────────────────────────────┘     │
└───────────────────────────────┬────────────────────────────────────────┘
                                │
                                ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   CAPA DE SERVICE DISCOVERY (Port 8761)                │
│  ┌──────────────────────────────────────────────────────────────┐     │
│  │  Eureka Server                                                │     │
│  │  - Registro de servicios                                      │     │
│  │  - Health checking                                            │     │
│  │  - Load balancing                                             │     │
│  └──────────────────────────────────────────────────────────────┘     │
└───────────────────────────────┬────────────────────────────────────────┘
                                │
                ┌───────────────┼───────────────┐
                ▼               ▼               ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      CAPA DE MICROSERVICIOS                            │
│                                                                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐      │
│  │ Authentication  │  │  User Service   │  │ Workout Service │      │
│  │   (Port 8583)   │  │  (Port 8588)    │  │  (Port 8586)    │      │
│  │                 │  │                 │  │                 │      │
│  │ - JWT Gen/Val   │  │ - User CRUD     │  │ - Workout CRUD  │      │
│  │ - Password Rec  │  │ - Auth          │  │ - Plans         │      │
│  │ - Email Send    │  │ - Profiles      │  │ - Routines      │      │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘      │
│           │                    │                    │                │
│           │  RestTemplate      │                    │                │
│           │  @LoadBalanced     │                    │                │
│           └────────────────────┘                    │                │
└────────────────────────┬───────────────────────────┬─────────────────┘
                         │                           │
                         ▼                           ▼
┌────────────────────────────────────────────────────────────────────────┐
│                       CAPA DE PERSISTENCIA                             │
│  ┌──────────────────────────────────────────────────────────────┐     │
│  │  PostgreSQL (Port 5432)                                      │     │
│  │  - gym_authentication (Users, Credentials)                   │     │
│  │  - gym_exercise (Workouts, Plans, Routines)                  │     │
│  └──────────────────────────────────────────────────────────────┘     │
└────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────┐
│                    CAPA DE INFRAESTRUCTURA                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐       │
│  │  Config Service │  │    RabbitMQ     │  │ Admin Service   │       │
│  │  (Port 8889)    │  │  (Port 5672)    │  │ (Port 9000)     │       │
│  │                 │  │                 │  │ [PROPUESTO]     │       │
│  │ - Git Config    │  │ - Message Queue │  │ - Monitoring    │       │
│  │ [NO USADO]      │  │ [NO USADO]      │  │ - Health Checks │       │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘       │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Flujo de Autenticación (Login)

```
┌─────────┐
│ Cliente │
│ Angular │
└────┬────┘
     │
     │ 1. POST /Login
     │    {email, password}
     ▼
┌─────────────────┐
│   API Gateway   │
│   (Port 8590)   │
└────┬────────────┘
     │
     │ 2. AuthFilter valida ruta
     │    (Login es público)
     ▼
┌──────────────────┐
│ Authentication   │
│    Service       │
│  (Port 8583)     │
└────┬─────────────┘
     │
     │ 3. RestTemplate POST
     │    http://user-service/user/authenticate
     ▼
┌──────────────────┐
│   User Service   │
│  (Port 8588)     │
└────┬─────────────┘
     │
     │ 4. SELECT * FROM users
     │    WHERE email = ?
     ▼
┌──────────────────┐
│   PostgreSQL     │
│   (Port 5432)    │
│ gym_authentication│
└────┬─────────────┘
     │
     │ 5. User record
     ▼
┌──────────────────┐
│   User Service   │
│                  │
│ BCrypt.compare() │
└────┬─────────────┘
     │
     │ 6. UserDto
     ▼
┌──────────────────┐
│ Authentication   │
│    Service       │
│                  │
│ JWT.generate()   │
│  - access_token  │
│  - refresh_token │
└────┬─────────────┘
     │
     │ 7. UserDto + Tokens
     ▼
┌─────────────────┐
│   API Gateway   │
└────┬────────────┘
     │
     │ 8. Response
     ▼
┌─────────┐
│ Cliente │
│ (Store  │
│  JWT)   │
└─────────┘
```

---

## 3. Flujo de Petición Protegida

```
┌─────────┐
│ Cliente │
│ Angular │
└────┬────┘
     │
     │ 1. GET /workout/all
     │    Headers: Authorization: Bearer <JWT>
     ▼
┌─────────────────────────────────────────┐
│           API Gateway                   │
│          (Port 8590)                    │
│                                         │
│  ┌───────────────────────────────┐     │
│  │   AuthenticationFilter        │     │
│  │                               │     │
│  │  ✓ Header exists?             │     │
│  │  ✓ Token valid?               │     │
│  │  ✓ Token not expired?         │     │
│  │  ✓ Extract role               │     │
│  │  ✓ Role authorized for route? │     │
│  └───────────────────────────────┘     │
│           │ PASS                        │
└───────────┼─────────────────────────────┘
            │
            │ 2. Resolve service via Eureka
            │    lb://workout-service
            ▼
┌─────────────────────────────────────────┐
│         Eureka Server                   │
│        (Port 8761)                      │
│                                         │
│  workout-service → 192.168.1.10:8586   │
└───────────┬─────────────────────────────┘
            │
            │ 3. HTTP GET to resolved IP
            ▼
┌─────────────────────────────────────────┐
│       Workout Service                   │
│        (Port 8586)                      │
│                                         │
│  @GetMapping("/workout/all")           │
└───────────┬─────────────────────────────┘
            │
            │ 4. SELECT * FROM workouts
            ▼
┌─────────────────────────────────────────┐
│         PostgreSQL                      │
│        (Port 5432)                      │
│       gym_exercise                      │
└───────────┬─────────────────────────────┘
            │
            │ 5. List<Workout>
            ▼
┌─────────────────────────────────────────┐
│       Workout Service                   │
│                                         │
│  Map to List<WorkoutDto>               │
└───────────┬─────────────────────────────┘
            │
            │ 6. Response
            ▼
┌─────────────────────────────────────────┐
│         API Gateway                     │
└───────────┬─────────────────────────────┘
            │
            │ 7. Forward response
            ▼
┌─────────┐
│ Cliente │
│ Angular │
└─────────┘
```

---

## 4. Arquitectura Propuesta con Mejoras

```
┌────────────────────────────────────────────────────────────────────────┐
│                          CLIENTE                                       │
│                    Angular (Port 4200)                                 │
└───────────────────────────────┬────────────────────────────────────────┘
                                │
                                ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   API GATEWAY (Port 8590)                              │
│  - JWT Validation              - Rate Limiting                         │
│  - Circuit Breaker             - CORS                                  │
└───────────────────────────────┬────────────────────────────────────────┘
                                │
                                ▼
┌────────────────────────────────────────────────────────────────────────┐
│               EUREKA SERVER (Port 8761)                                │
└────────┬───────────┬──────────────┬──────────────┬──────────────┬──────┘
         │           │              │              │              │
         ▼           ▼              ▼              ▼              ▼
┌────────────┐ ┌───────────┐ ┌────────────┐ ┌───────────┐ ┌──────────────┐
│   Auth     │ │   User    │ │  Workout   │ │   Email   │ │    Admin     │
│  Service   │ │  Service  │ │  Service   │ │  Service  │ │   Service    │
│  (8583)    │ │  (8588)   │ │  (8586)    │ │  [NUEVO]  │ │   (9000)     │
│            │ │           │ │            │ │  (8585)   │ │   [NUEVO]    │
└─────┬──────┘ └─────┬─────┘ └─────┬──────┘ └─────┬─────┘ └──────┬───────┘
      │              │              │              │              │
      │              │              │              │              │
      └──────────────┴──────────────┴──────────────┴──────────────┘
                                    │
                                    ▼
      ┌──────────────────────────────────────────────────────────┐
      │                    RabbitMQ (5672)                       │
      │  Exchanges:                                              │
      │  - user.exchange → user.created, user.updated            │
      │  - email.exchange → email.welcome, email.reset           │
      │  - notification.exchange → notification.*                │
      │  - audit.exchange → audit.log                            │
      └──────────────────────────────────────────────────────────┘
                                    │
      ┌─────────────────────────────┴──────────────────────────┐
      │                                                         │
      ▼                                                         ▼
┌─────────────────────────┐                     ┌─────────────────────────┐
│  PostgreSQL (5432)      │                     │  Config Service (8889)  │
│  - gym_authentication   │                     │  - GitHub Backend       │
│  - gym_exercise         │                     │  - Encryption           │
└─────────────────────────┘                     └─────────────────────────┘
```

**Nuevos Componentes**:
1. **Email Service**: Envío asíncrono de emails (consume eventos)
2. **Admin Service**: Monitoreo centralizado con Spring Boot Admin
3. **RabbitMQ**: Comunicación asíncrona entre servicios

---

## 5. Diagrama de Eventos (Propuesto con RabbitMQ)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         EVENT-DRIVEN FLOW                               │
└─────────────────────────────────────────────────────────────────────────┘

Registro de Usuario:
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│ User Service │────>│  RabbitMQ    │────>│Email Service │
│              │     │              │     │              │
│ 1. Save user │     │ 2. Publish   │     │ 3. Send      │
│              │     │ user.created │     │ welcome email│
└──────────────┘     └──────────────┘     └──────────────┘

Asignación de Workout:
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│Workout Svc   │────>│  RabbitMQ    │────>│Email Service │
│              │     │              │     │              │
│ 1. Assign    │     │ 2. Publish   │     │ 3. Notify    │
│    plan      │     │ workout.     │     │    user      │
│              │     │ assigned     │     │              │
└──────────────┘     └──────────────┘     └──────────────┘

Auditoría:
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│ ANY Service  │────>│  RabbitMQ    │────>│ Audit Service│
│              │     │              │     │              │
│ 1. Action    │     │ 2. Publish   │     │ 3. Log to    │
│              │     │ audit.log    │     │    DB/File   │
└──────────────┘     └──────────────┘     └──────────────┘
```

---

## 6. Service Discovery con Eureka

```
┌─────────────────────────────────────────────────────────────────┐
│                  EUREKA SERVER (8761)                           │
│                                                                 │
│  Service Registry:                                              │
│  ┌──────────────────────────────────────────────────────┐      │
│  │ Service Name       │ Instances │ Status    │ Port    │      │
│  ├──────────────────────────────────────────────────────┤      │
│  │ API-GATEWAY        │     1     │ UP        │ 8590    │      │
│  │ AUTHENTICATION     │     1     │ UP        │ 8583    │      │
│  │ USER-SERVICE       │     1     │ UP        │ 8588    │      │
│  │ WORKOUT-SERVICE    │     1     │ UP        │ 8586    │      │
│  │ CONFIG-SERVICE     │     1     │ UP        │ 8889    │      │
│  │ ADMIN-SERVICE      │     1     │ UP        │ 9000    │      │
│  └──────────────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────────┘
              ▲                          │
              │                          │
              │ Heartbeat (30s)          │ Service Lookup
              │                          │
        ┌─────┴───────┐           ┌──────▼──────┐
        │   Service   │           │ API Gateway │
        │  Instance   │           │             │
        │             │           │ Needs to    │
        │ Register on │           │ call        │
        │   startup   │           │ user-service│
        └─────────────┘           └─────────────┘
```

---

## 7. Admin Service Dashboard (Propuesto)

```
┌────────────────────────────────────────────────────────────────────┐
│  Spring Boot Admin - http://localhost:9000                         │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  Services Overview:                                                │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │ Service          Status   Uptime   Memory   Threads      │    │
│  ├──────────────────────────────────────────────────────────┤    │
│  │ authentication   🟢 UP    2h 34m   245 MB   24          │    │
│  │ user-service     🟢 UP    2h 34m   312 MB   32          │    │
│  │ workout-service  🟢 UP    2h 34m   289 MB   28          │    │
│  │ api-gateway      🟢 UP    2h 35m   198 MB   18          │    │
│  └──────────────────────────────────────────────────────────┘    │
│                                                                    │
│  Metrics:                                                          │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │ Total Requests: 15,234                                   │    │
│  │ Errors (5xx): 12 (0.08%)                                 │    │
│  │ Avg Response Time: 145ms                                 │    │
│  │ DB Connections: 45/100                                   │    │
│  └──────────────────────────────────────────────────────────┘    │
│                                                                    │
│  Health Checks:                                                    │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │ Service          DB      Disk    Eureka   Custom         │    │
│  ├──────────────────────────────────────────────────────────┤    │
│  │ user-service     🟢       🟢       🟢        🟢           │    │
│  │ workout-service  🟢       🟢       🟢        🟢           │    │
│  └──────────────────────────────────────────────────────────┘    │
│                                                                    │
│  Recent Events:                                                    │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │ 14:32 - user-service - Instance registered               │    │
│  │ 14:30 - workout-service - Health check passed            │    │
│  │ 14:28 - authentication - Memory usage: 85% (warning)     │    │
│  └──────────────────────────────────────────────────────────┘    │
└────────────────────────────────────────────────────────────────────┘
```

---

## 8. Docker Compose Network Diagram

```
Docker Networks:
┌────────────────────────────────────────────────────────────────────┐
│  Network: spring (bridge)                                          │
│  ┌──────────────────────────────────────────────────────────┐     │
│  │ - eureka-server (eureka-server:8761)                     │     │
│  │ - config-service (config-service:8889)                   │     │
│  │ - api-gateway (api-gateway:8590)                         │     │
│  │ - authentication (authentication:8583)                   │     │
│  │ - user-service (user-service:8588)                       │     │
│  │ - workout-service (workout-service:8586)                 │     │
│  │ - rabbitmq (rabbitmq:5672)                               │     │
│  │ - admin-service (admin-service:9000) [PROPUESTO]         │     │
│  └──────────────────────────────────────────────────────────┘     │
└────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────┐
│  Network: postgres (bridge)                                        │
│  ┌──────────────────────────────────────────────────────────┐     │
│  │ - postgres (postgres:5432)                               │     │
│  │ - user-service                                           │     │
│  │ - authentication                                         │     │
│  │ - workout-service                                        │     │
│  └──────────────────────────────────────────────────────────┘     │
└────────────────────────────────────────────────────────────────────┘

External Ports Exposed:
- 8590 → API Gateway
- 8761 → Eureka Dashboard
- 5432 → PostgreSQL (⚠️ Solo dev)
- 5672, 15672 → RabbitMQ
- 9000 → Admin Service [PROPUESTO]
```

---

## 9. Circuit Breaker States

```
┌──────────────────────────────────────────────────────────────┐
│           Circuit Breaker Pattern (Resilience4j)             │
└──────────────────────────────────────────────────────────────┘

State Transitions:

        ┌─────────────┐
        │   CLOSED    │ ◄──────────────────┐
        │ (Normal)    │                    │
        └──────┬──────┘                    │
               │                           │
               │ Failure rate > 50%        │ Success rate > 60%
               │                           │
               ▼                           │
        ┌─────────────┐            ┌──────┴──────┐
        │    OPEN     │───────────>│ HALF-OPEN   │
        │ (Blocking)  │            │ (Testing)   │
        └─────────────┘            └─────────────┘
               │                           │
               │ After wait duration       │
               └───────────────────────────┘

CLOSED State:
- All requests pass through
- Monitor failures
- If threshold exceeded → OPEN

OPEN State:
- All requests rejected immediately
- Return fallback response
- After wait period (10s) → HALF-OPEN

HALF-OPEN State:
- Limited requests allowed
- If succeed → CLOSED
- If fail → OPEN
```

---

## 10. Seguridad - JWT Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    JWT GENERATION & VALIDATION                  │
└─────────────────────────────────────────────────────────────────┘

Token Structure:
┌──────────────────────────────────────────────────────────────┐
│ Header                                                       │
│ {                                                            │
│   "alg": "HS256",                                            │
│   "typ": "JWT"                                               │
│ }                                                            │
├──────────────────────────────────────────────────────────────┤
│ Payload                                                      │
│ {                                                            │
│   "sub": "user_id_123",                                      │
│   "role": "TRAINER",                                         │
│   "type": "AUTHORIZATION",                                   │
│   "iat": 1698840000,                                         │
│   "exp": 1698926400  // 24h                                  │
│ }                                                            │
├──────────────────────────────────────────────────────────────┤
│ Signature                                                    │
│ HMACSHA256(                                                  │
│   base64UrlEncode(header) + "." +                            │
│   base64UrlEncode(payload),                                  │
│   SECRET_KEY                                                 │
│ )                                                            │
└──────────────────────────────────────────────────────────────┘

Validation in API Gateway:
┌──────────────────────────────────────────────────────────────┐
│ 1. Extract token from Authorization header                  │
│    ↓                                                         │
│ 2. Verify signature with shared SECRET_KEY                  │
│    ↓                                                         │
│ 3. Check expiration (exp claim)                             │
│    ↓                                                         │
│ 4. Extract role from payload                                │
│    ↓                                                         │
│ 5. Authorize based on route requirements                    │
│    - Admin routes: role == "ADMIN"                          │
│    - Trainer routes: role in ["TRAINER", "ADMIN"]           │
│    - Client routes: role in ["CLIENT", "TRAINER", "ADMIN"]  │
└──────────────────────────────────────────────────────────────┘
```

---

## Conclusión de Diagramas

Estos diagramas visualizan:
1. **Arquitectura actual** y sus flujos
2. **Arquitectura propuesta** con mejoras
3. **Componentes de infraestructura** (Eureka, RabbitMQ, Admin)
4. **Patrones de resiliencia** (Circuit Breaker)
5. **Seguridad** (JWT)

Para implementar las mejoras, consultar:
- **03-puntos-de-mejora.md** para detalles técnicos
- **02-conexiones-entre-servicios.md** para flujos completos
