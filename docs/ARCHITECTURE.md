# Arquitectura del Sistema - Gimnasio Backend

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Arquitectura de Microservicios](#arquitectura-de-microservicios)
3. [Componentes del Sistema](#componentes-del-sistema)
4. [Flujos de Comunicación](#flujos-de-comunicación)
5. [Stack Tecnológico](#stack-tecnológico)
6. [Infraestructura](#infraestructura)
7. [Seguridad](#seguridad)
8. [Monitoreo y Observabilidad](#monitoreo-y-observabilidad)

---

## Visión General

### Objetivo del Sistema
Sistema backend para gestión integral de un gimnasio, incluyendo usuarios, entrenadores, ejercicios, rutinas y planes de entrenamiento.

### Arquitectura Base
```
┌─────────────────────────────────────────────────────────────────┐
│                     Cliente (Angular)                            │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP/REST
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                       API Gateway (8590)                         │
│              JWT Authentication, CORS, Routing                   │
└────────────────────────────┬────────────────────────────────────┘
                             │
                ┌────────────┼────────────┐
                │            │            │
                ▼            ▼            ▼
    ┌────────────────┐ ┌──────────┐ ┌──────────────┐
    │ Authentication │ │   User   │ │   Workout    │
    │   Service      │ │ Service  │ │   Service    │
    │    (8583)      │ │  (8588)  │ │    (8586)    │
    └────────┬───────┘ └─────┬────┘ └───────┬──────┘
             │               │              │
             └───────────────┼──────────────┘
                             ▼
                    ┌─────────────────┐
                    │   PostgreSQL    │
                    │      (5432)     │
                    └─────────────────┘
```

### Principios de Diseño

1. **Separación de Responsabilidades**: Cada microservicio maneja un dominio específico
2. **Descubrimiento de Servicios**: Eureka Server para registro dinámico
3. **API Gateway**: Punto único de entrada, manejo de autenticación
4. **Configuración Centralizada**: Config Service para gestión de configuraciones
5. **Resiliencia**: Health checks, circuit breakers, retry patterns
6. **Observabilidad**: Monitoreo completo con Prometheus, Grafana y Alertmanager

---

## Arquitectura de Microservicios

### Diagrama Completo

```
                                 ┌─────────────────┐
                                 │  Config Service │
                                 │     (8889)      │
                                 │  GitHub Config  │
                                 └────────┬────────┘
                                          │
         ┌────────────────────────────────┼─────────────────────────┐
         │                                │                         │
         ▼                                ▼                         ▼
┌─────────────────┐           ┌────────────────────┐      ┌────────────────┐
│  Eureka Server  │◄──────────┤   API Gateway      │      │ Authentication │
│     (8761)      │   Register│     (8590)         │      │   Service      │
│Service Discovery│           │ JWT, CORS, Routes  │      │    (8583)      │
└─────────┬───────┘           └──────────┬─────────┘      └────────┬───────┘
          │                              │                         │
          │ Register                     │ Forward                 │
          │                              ▼                         │
          │                   ┌───────────────────┐               │
          ├───────────────────┤   User Service    │◄──────────────┤
          │                   │      (8588)       │   Validate     │
          │                   │  Users, Profiles  │   JWT Token    │
          │                   └─────────┬─────────┘                │
          │                             │                          │
          │                             │ Database                 │
          │                             ▼                          │
          │                   ┌───────────────────┐               │
          ├───────────────────┤ Workout Service   │               │
          │                   │      (8586)       │               │
          │                   │ Exercises, Plans  │               │
          │                   └─────────┬─────────┘               │
          │                             │                         │
          │                             ▼                         ▼
          │                   ┌──────────────────────────────────────┐
          │                   │        PostgreSQL (5432)             │
          │                   │  ┌──────────────┬─────────────────┐ │
          │                   │  │ gym_auth     │ gym_exercise    │ │
          │                   │  └──────────────┴─────────────────┘ │
          │                   └──────────────────────────────────────┘
          │
          │ Register
          ▼
┌───────────────────┐         ┌──────────────────┐
│ Spring Boot Admin │◄────────┤    RabbitMQ      │
│      (9595)       │         │      (5672)      │
│  Monitoring UI    │         │ Message Broker   │
└───────────────────┘         └──────────────────┘
          │
          │ Metrics
          ▼
┌───────────────────────────────────────────────────────┐
│           Observability Stack                         │
│  ┌────────────┐  ┌──────────┐  ┌────────────────┐   │
│  │ Prometheus │──│ Grafana  │  │ Alertmanager   │   │
│  │   (9090)   │  │  (3000)  │  │     (9093)     │   │
│  └────────────┘  └──────────┘  └────────────────┘   │
└───────────────────────────────────────────────────────┘
```

### Patrones Implementados

#### 1. API Gateway Pattern
- **Implementación**: Spring Cloud Gateway
- **Responsabilidades**:
  - Routing dinámico a microservicios
  - Autenticación JWT centralizada
  - CORS handling
  - Rate limiting (futuro)
  - Request/Response logging

#### 2. Service Discovery Pattern
- **Implementación**: Netflix Eureka
- **Beneficios**:
  - Registro dinámico de servicios
  - Health checking automático
  - Load balancing del lado del cliente
  - Failover automático

#### 3. Externalized Configuration
- **Implementación**: Spring Cloud Config
- **Estado**: Configurado, pendiente activación
- **Plan**: Migrar configuraciones a repositorio Git externo

#### 4. Database per Service
- **Implementación**: PostgreSQL con 2 bases de datos aisladas
  - `gym_authentication`: Authentication & User services
  - `gym_exercise`: Workout service
- **Beneficio**: Aislamiento y escalabilidad independiente

#### 5. Asynchronous Messaging (Configurado)
- **Implementación**: RabbitMQ
- **Estado**: Infraestructura lista, eventos pendientes
- **Uso futuro**: Eventos de dominio, notificaciones, procesamiento asíncrono

---

## Componentes del Sistema

### Core Services

#### 1. Eureka Server
```yaml
Puerto: 8761
Función: Service Discovery
Dependencias: Ninguna (primer servicio en iniciar)
Métricas: ✅ Monitoreado
Health Check: /actuator/health
```

**Responsabilidades:**
- Registro de microservicios
- Health checking periódico
- Proveer lista de instancias disponibles
- Dashboard de estado de servicios

**Configuración Clave:**
```yaml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: true
```

#### 2. Config Service
```yaml
Puerto: 8889
Función: Configuración Centralizada
Estado: Configurado, no en uso activo
Métricas: ✅ Monitoreado
Health Check: /actuator/health
```

**Responsabilidades:**
- Gestión centralizada de configuraciones
- Versionado de configuraciones con Git
- Refresh dinámico sin reinicios
- Cifrado de secretos (futuro)

**Plan de Activación:**
1. Crear repositorio Git de configuraciones
2. Migrar application.yml de cada servicio
3. Configurar refresh automático
4. Implementar encriptación de secretos

#### 3. API Gateway
```yaml
Puerto: 8590
Función: API Gateway / Edge Service
Dependencias: Eureka, Config, Authentication
Métricas: ✅ Monitoreado
RabbitMQ: ✅ Conectado
```

**Responsabilidades:**
- Punto único de entrada para clientes
- Validación de JWT tokens
- CORS configuration
- Routing a microservicios via Eureka
- Request/Response transformation

**Rutas Configuradas:**
```yaml
- id: authentication
  uri: lb://AUTHENTICATION
  predicates: [Path=/Login, /Login/**]

- id: user
  uri: lb://USER-SERVICE
  predicates: [Path=/user/**, /users/**]

- id: workout
  uri: lb://WORKOUT-SERVICE
  predicates: [Path=/workout/**, /exercises/**]
```

### Business Services

#### 4. Authentication Service
```yaml
Puerto: 8583
Función: Autenticación y JWT
Base de Datos: gym_authentication
Métricas: ✅ Monitoreado
RabbitMQ: ✅ Conectado
Flyway: ✅ Configurado
```

**Responsabilidades:**
- Generación de JWT tokens
- Validación de tokens
- Refresh token mechanism
- Password recovery
- Email notifications

**Endpoints:**
- `POST /Login` - Autenticación de usuario
- `POST /refresh` - Renovar token
- `POST /recover-password` - Recuperar contraseña

**Schemas Flyway:**
- V1__Create_users_table.sql
- V2__Add_password_reset_tokens.sql

#### 5. User Service
```yaml
Puerto: 8588
Función: Gestión de Usuarios
Base de Datos: gym_authentication (compartida con Auth)
Métricas: ✅ Monitoreado
RabbitMQ: ✅ Conectado
Flyway: ✅ Configurado
```

**Responsabilidades:**
- CRUD de usuarios
- Gestión de perfiles
- Roles y permisos
- Información de clientes/entrenadores

**Endpoints:**
- `POST /user/register` - Registrar usuario
- `GET /user/{id}` - Obtener usuario
- `PUT /user/{id}` - Actualizar usuario
- `DELETE /user/{id}` - Eliminar usuario
- `GET /users` - Listar usuarios

**Entidades:**
- User (id, userName, email, role)
- UserCredentials (password, salt)
- Profile (additional info)

#### 6. Workout Service
```yaml
Puerto: 8586
Función: Gestión de Ejercicios y Rutinas
Base de Datos: gym_exercise
Métricas: ✅ Monitoreado
RabbitMQ: ✅ Conectado
```

**Responsabilidades:**
- CRUD de ejercicios
- Gestión de planes de entrenamiento
- Rutinas personalizadas
- Seguimiento de progreso

**Endpoints:**
- `GET /workout/all` - Listar ejercicios
- `POST /exercises` - Crear ejercicio
- `GET /exercises/{id}` - Obtener ejercicio
- `PUT /exercises/{id}` - Actualizar ejercicio
- `DELETE /exercises/{id}` - Eliminar ejercicio

**Entidades:**
- Exercise (id, name, description, muscleGroup)
- WorkoutPlan (id, name, duration)
- Routine (exercises, sets, reps)

### Admin & Infrastructure Services

#### 7. Spring Boot Admin
```yaml
Puerto: 9595
Función: Monitoreo Centralizado de Microservicios
Métricas: ✅ Auto-monitoreado
```

**Responsabilidades:**
- Dashboard de salud de servicios
- Visualización de métricas JVM
- Logs en tiempo real
- Thread dumps
- Heap dumps
- Environment properties

**Features:**
- Auto-registration via Eureka
- Email notifications (configurable)
- Wallboard de estado general
- Detalles por instancia

#### 8. PostgreSQL
```yaml
Puerto: 5432
Versión: 16
Persistencia: Volume docker
Red: postgres (aislada)
```

**Bases de Datos:**
```
gym_authentication:
  - users
  - user_credentials
  - password_reset_tokens
  - flyway_schema_history

gym_exercise:
  - exercises
  - workout_plans
  - routines
```

**Configuración:**
- Connection pooling via HikariCP
- Transacciones ACID
- Migraciones versionadas con Flyway

#### 9. RabbitMQ
```yaml
Puerto AMQP: 5672
Puerto Management: 15672
Usuarios: guest/guest (dev)
Red: spring
```

**Estado Actual:**
- ✅ Infraestructura configurada
- ✅ 4 servicios conectados
- ⏳ Eventos de negocio pendientes de implementación

**Uso Planificado:**
```
Eventos a Implementar:
- UserRegisteredEvent → Email welcome
- WorkoutPlanCreatedEvent → Notifications
- PasswordResetRequestedEvent → Email sending
- UserDeletedEvent → Cleanup tasks
```

---

## Flujos de Comunicación

### 1. Flujo de Autenticación

```
┌────────┐          ┌──────────┐       ┌──────────┐       ┌──────────┐
│ Client │          │   API    │       │   Auth   │       │   User   │
│        │          │ Gateway  │       │ Service  │       │ Service  │
└───┬────┘          └────┬─────┘       └────┬─────┘       └────┬─────┘
    │                    │                   │                   │
    │ POST /Login        │                   │                   │
    ├───────────────────>│                   │                   │
    │ {email, password}  │                   │                   │
    │                    │ Forward           │                   │
    │                    ├──────────────────>│                   │
    │                    │                   │ Validate          │
    │                    │                   ├──────────────────>│
    │                    │                   │ User exists?      │
    │                    │                   │<──────────────────┤
    │                    │                   │ User data         │
    │                    │                   │                   │
    │                    │                   │ Check password    │
    │                    │                   │ Generate JWT      │
    │                    │                   │                   │
    │                    │<──────────────────┤                   │
    │<───────────────────┤ {user, token}     │                   │
    │ 200 OK             │                   │                   │
```

### 2. Flujo de Request Autenticado

```
┌────────┐          ┌──────────┐       ┌──────────┐       ┌──────────┐
│ Client │          │   API    │       │   Auth   │       │ Workout  │
│        │          │ Gateway  │       │ Service  │       │ Service  │
└───┬────┘          └────┬─────┘       └────┬─────┘       └────┬─────┘
    │                    │                   │                   │
    │ GET /workout/all   │                   │                   │
    │ + Bearer Token     │                   │                   │
    ├───────────────────>│                   │                   │
    │                    │                   │                   │
    │                    │ Validate Token    │                   │
    │                    ├──────────────────>│                   │
    │                    │<──────────────────┤                   │
    │                    │ Token valid       │                   │
    │                    │                   │                   │
    │                    │ Forward request   │                   │
    │                    │ (lb://WORKOUT-SERVICE)               │
    │                    ├──────────────────────────────────────>│
    │                    │                   │                   │
    │                    │                   │                   │ Query DB
    │                    │<──────────────────────────────────────┤
    │<───────────────────┤ Workouts list     │                   │
    │ 200 OK             │                   │                   │
```

### 3. Flujo de Service Discovery

```
┌──────────────┐         ┌─────────────┐         ┌─────────────┐
│  User Service│         │   Eureka    │         │ API Gateway │
│   Startup    │         │   Server    │         │             │
└──────┬───────┘         └──────┬──────┘         └──────┬──────┘
       │                        │                       │
       │ Register               │                       │
       │ USER-SERVICE:8588      │                       │
       ├───────────────────────>│                       │
       │<───────────────────────┤                       │
       │ Registered OK          │                       │
       │                        │                       │
       │ Heartbeat (every 30s)  │                       │
       ├───────────────────────>│                       │
       │                        │                       │
       │                        │ Fetch Registry        │
       │                        │<──────────────────────┤
       │                        │ List of services      │
       │                        ├──────────────────────>│
       │                        │ [USER-SERVICE:8588]   │
       │                        │ [WORKOUT-SERVICE:8586]│
       │                        │ [AUTHENTICATION:8583] │
```

### 4. Flujo de Monitoreo

```
┌──────────┐     ┌────────────┐     ┌─────────┐     ┌─────────────┐
│ Services │     │ Prometheus │     │ Grafana │     │Alertmanager │
└────┬─────┘     └─────┬──────┘     └────┬────┘     └──────┬──────┘
     │                 │                  │                 │
     │ Expose          │                  │                 │
     │ /actuator/      │                  │                 │
     │ prometheus      │                  │                 │
     │<────────────────┤ Scrape           │                 │
     │                 │ (every 15s)      │                 │
     │ Metrics         │                  │                 │
     ├────────────────>│                  │                 │
     │                 │                  │                 │
     │                 │ Store TSDB       │                 │
     │                 │                  │                 │
     │                 │                  │ Query           │
     │                 │<─────────────────┤ PromQL          │
     │                 │ Metrics Data     │                 │
     │                 ├─────────────────>│                 │
     │                 │                  │ Render          │
     │                 │                  │ Dashboards      │
     │                 │                  │                 │
     │                 │ Evaluate Rules   │                 │
     │                 │ (every 15s)      │                 │
     │                 │                  │                 │
     │                 │ Alert Triggered  │                 │
     │                 ├─────────────────────────────────────>│
     │                 │                  │                 │
     │                 │                  │                 │ Send
     │                 │                  │                 │ Notification
     │                 │                  │                 │ (Email/Slack)
```

---

## Stack Tecnológico

### Backend Framework
```
Java 21 (LTS)
├── Spring Boot 3.2.0
│   ├── Spring Web (REST APIs)
│   ├── Spring Data JPA (ORM)
│   ├── Spring Security (Authentication)
│   └── Spring Boot Actuator (Monitoring)
│
├── Spring Cloud 2023.0.0
│   ├── Spring Cloud Netflix Eureka (Service Discovery)
│   ├── Spring Cloud Gateway (API Gateway)
│   ├── Spring Cloud Config (Configuration)
│   └── Spring Cloud LoadBalancer (Client-side LB)
│
└── Spring Boot Admin 3.2.0 (Monitoring UI)
```

### Persistence
```
PostgreSQL 16
├── HikariCP (Connection Pooling)
├── Flyway (Schema Migrations)
└── JPA/Hibernate (ORM)
```

### Messaging
```
RabbitMQ 3.9.11
└── Spring AMQP (Client)
```

### Security
```
JWT (JSON Web Tokens)
├── jjwt 0.11.5 (JWT library)
├── BCrypt (Password hashing)
└── Spring Security (Framework)
```

### Observability
```
Monitoring Stack
├── Prometheus 2.x (Metrics Collection)
│   ├── Micrometer (Metrics API)
│   └── Micrometer Registry Prometheus
│
├── Grafana 11.x (Visualization)
│   ├── Dashboards
│   └── Alerting
│
├── Alertmanager (Alert Management)
│   ├── Email notifications
│   ├── Slack integration
│   └── Webhook support
│
└── Spring Boot Admin (Microservices Monitoring)
    ├── Health checks
    ├── Metrics
    └── Log streaming
```

### DevOps
```
Containerization
├── Docker 24.x
├── Docker Compose 2.x
└── Multi-stage builds

Build Tools
├── Maven 3.8+
└── OpenJDK 21

Version Control
└── Git / GitHub
```

---

## Infraestructura

### Docker Compose Services

```yaml
version: '3.8'

services:
  # Service Discovery
  eureka-server:      # Port 8761
  
  # Configuration
  config-service:     # Port 8889
  
  # API Gateway
  api-gateway:        # Port 8590
  
  # Business Services
  authentication:     # Port 8583
  user-service:       # Port 8588
  workout-service:    # Port 8586
  
  # Admin
  admin:              # Port 9595
  
  # Data Layer
  postgres:           # Port 5432
  rabbitmq:           # Port 5672, 15672
  
  # Observability
  prometheus:         # Port 9090
  grafana:            # Port 3000
  alertmanager:       # Port 9093
```

### Docker Networks

```
gimnasio_backend_spring (bridge)
├── eureka-server
├── config-service
├── api-gateway
├── authentication
├── user-service
├── workout-service
├── admin
├── rabbitmq
├── prometheus
├── grafana
└── alertmanager

gimnasio_backend_postgres (bridge)
├── postgres
├── authentication
├── user-service
└── workout-service
```

**Diseño de Redes:**
- `spring`: Comunicación entre microservicios
- `postgres`: Aislamiento de base de datos

### Volumes

```yaml
volumes:
  postgres-data:        # PostgreSQL persistence
  prometheus-data:      # Prometheus TSDB
  grafana-data:         # Grafana dashboards & config
```

### Health Checks

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

**Servicios con Health Check:**
- ✅ Eureka Server
- ✅ Config Service
- ✅ API Gateway
- ✅ Authentication
- ✅ User Service
- ✅ Workout Service
- ✅ PostgreSQL
- ✅ RabbitMQ

### Orden de Inicio

```
Fase 1 (Independientes):
  - postgres
  - rabbitmq
  - eureka-server

Fase 2 (Dependen de Eureka):
  - config-service

Fase 3 (Core Services):
  - api-gateway
  - user-service

Fase 4 (Business Services):
  - authentication
  - workout-service

Fase 5 (Monitoring):
  - admin
  - prometheus
  - grafana
  - alertmanager
```

---

## Seguridad

### Autenticación y Autorización

#### JWT Token Structure
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user@example.com",
    "role": "CLIENT",
    "userId": 123,
    "iat": 1698876543,
    "exp": 1698880143
  },
  "signature": "..."
}
```

**Token Lifecycle:**
1. User logs in → Authentication Service generates JWT
2. Client stores token (localStorage/sessionStorage)
3. Client includes token in Authorization header
4. API Gateway validates token via Authentication Service
5. Request forwarded to business service with user context

**Token Types:**
- **Access Token**: Short-lived (1 hour), used for API requests
- **Refresh Token**: Long-lived (7 days), used to obtain new access tokens

#### Roles y Permisos

```
ADMIN:
  - Full system access
  - User management
  - Workout management
  - Reports and analytics

TRAINER:
  - Client management (assigned clients)
  - Workout plan creation
  - Progress tracking
  - Exercise library access

CLIENT:
  - View own profile
  - View assigned workout plans
  - Track own progress
  - View exercise library (read-only)
```

### CORS Configuration

```yaml
# API Gateway
spring:
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "http://localhost:4200"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowedHeaders: "*"
            allowCredentials: true
            maxAge: 3600
```

**Configuración Actual:**
- Origen permitido: `http://localhost:4200` (Angular dev server)
- Credenciales: Permitidas
- Headers: Todos permitidos (dev)

**Mejoras de Seguridad Pendientes:**
1. ❌ Migrar secretos JWT a variables de entorno cifradas
2. ❌ Implementar rotación de secretos
3. ❌ Añadir rate limiting en API Gateway
4. ❌ Implementar HTTPS en producción
5. ❌ Restricción de headers permitidos en CORS
6. ❌ Agregar security headers (CSP, HSTS, etc.)

### Password Security

**Hashing:**
- Algoritmo: BCrypt
- Salt rounds: 10
- One-way hash (no reversible)

**Password Requirements:**
- Mínimo 8 caracteres
- Al menos 1 mayúscula
- Al menos 1 minúscula
- Al menos 1 número
- Caracteres especiales (recomendado)

---

## Monitoreo y Observabilidad

### Arquitectura de Monitoreo

```
┌──────────────────────────────────────────────────────────┐
│                    Observability Stack                   │
│                                                          │
│  ┌────────────────┐       ┌─────────────────────────┐  │
│  │  Microservices │──────>│   Spring Boot Admin     │  │
│  │   /actuator    │ HTTP  │        (9595)           │  │
│  └────────────────┘       └─────────────────────────┘  │
│          │                                              │
│          │ Expose /actuator/prometheus                 │
│          │                                              │
│          ▼                                              │
│  ┌────────────────┐                                    │
│  │  Prometheus    │                                    │
│  │    (9090)      │                                    │
│  │  - Scraping    │                                    │
│  │  - TSDB        │                                    │
│  │  - Rules       │                                    │
│  └───────┬────────┘                                    │
│          │                                              │
│          ├───────────────┬──────────────────────┐      │
│          ▼               ▼                      ▼      │
│  ┌──────────────┐ ┌─────────────┐  ┌─────────────────┐│
│  │   Grafana    │ │Alertmanager │  │  Alert Rules    ││
│  │    (3000)    │ │   (9093)    │  │  (15 rules)     ││
│  │ - Dashboards │ │ - Routing   │  │  - 7 groups     ││
│  │ - Alerting   │ │ - Grouping  │  │  - Thresholds   ││
│  └──────────────┘ └─────────────┘  └─────────────────┘│
└──────────────────────────────────────────────────────────┘
```

### Métricas Monitoreadas

#### JVM Metrics
- **Memoria**: Heap, Non-heap, Pools (Eden, Survivor, Old Gen)
- **GC**: Collections, pause time, throughput
- **Threads**: Count, daemon count, peak count
- **Classes**: Loaded, unloaded

#### Application Metrics
- **HTTP**: Requests/second, latency, status codes
- **Database**: Connections active, idle, wait time
- **Circuit Breaker**: State, calls, failures
- **Custom**: Business metrics específicas

#### System Metrics
- **CPU**: Usage percentage
- **Disk**: I/O, free space
- **Network**: Bytes sent/received

### Dashboards

**1. Spring Boot Statistics (ID: 11378)**
- Overview de todos los microservicios
- Métricas JVM consolidadas
- HTTP request/response stats

**2. JVM Micrometer (ID: 4701)**
- Análisis detallado de memoria
- Garbage Collection performance
- Thread analysis

**3. Spring Cloud Gateway (ID: 11506)**
- Routes performance
- Request routing stats
- Gateway-specific metrics

**4. Custom Microservices Dashboard**
- Business metrics
- Service-specific KPIs
- Custom panels por servicio

### Sistema de Alertas

#### Prometheus Rules (15 reglas)

**Service Availability (Critical)**
- ServiceDown: Servicio no responde por > 1 minuto
- EurekaServerDown: Eureka caído
- DatabaseDown: PostgreSQL no accesible
- RabbitMQDown: RabbitMQ no accesible

**Memory Monitoring (Warning/Critical)**
- HighMemoryUsage: > 90% uso de memoria
- MemoryCritical: > 95% uso de memoria
- HighHeapUsage: > 85% heap usado

**Performance Monitoring (Warning)**
- HighLatency: Response time > 1 segundo
- SlowDatabaseQueries: Queries > 500ms

**Error Monitoring (Warning/Critical)**
- HighErrorRate: > 5% error rate
- DatabaseConnectionErrors: Connection pool exhausted

**Garbage Collection (Warning)**
- FrequentGC: > 5 GC/min

**Circuit Breaker (Warning)**
- CircuitBreakerOpen: Circuit abierto

#### Alertmanager Configuration

**Receivers:**
- Email (SMTP)
- Slack (Webhook)
- Discord (Webhook)
- Telegram (Bot API)
- PagerDuty (Integration)

**Routing:**
```yaml
route:
  group_by: ['alertname', 'severity']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 4h
  
  routes:
    - match:
        severity: critical
      receiver: pagerduty
    
    - match:
        severity: warning
      receiver: slack
    
    - match_re:
        service: .*database.*
      receiver: dba-team
```

**Inhibit Rules:**
- Critical alerts suppress warnings
- ServiceDown suppresses other alerts for that service

### Testing de Alertas

**Script Automatizado:** `scripts/test-alerts.ps1`

```powershell
# Menú interactivo
1. Test ServiceDown Alert
2. Test Memory Alert
3. Test Latency Alert
4. Test Error Rate Alert
5. Query Active Alerts

# Ejemplo: Simular ServiceDown
docker-compose stop user-service
# Esperar 1 minuto
# Alert se dispara
# Verificar en Alertmanager UI
```

---

## Mejores Prácticas Implementadas

### 1. Health Checks
- Todos los servicios exponen `/actuator/health`
- Docker healthchecks configurados
- Eureka usa health checks para service discovery

### 2. Graceful Shutdown
```yaml
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

### 3. Connection Pooling
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
```

### 4. Resilience Patterns
- Service discovery para failover
- Circuit breaker configurado (monitoreado, no implementado)
- Retry logic en comunicación entre servicios

### 5. Configuration Management
- Externalized configuration con environment variables
- Profiles para diferentes entornos (dev, prod)
- Config Server para gestión centralizada (configurado)

### 6. Observability
- Structured logging
- Distributed tracing con Micrometer
- Comprehensive metrics
- Alerting automatizado

---

## Roadmap Técnico

### Sprint 3 (Próximo)
1. Implementar eventos RabbitMQ
2. Activar Config Server
3. Circuit Breaker con Resilience4j
4. Rate Limiting en API Gateway

### Sprint 4
1. Tests automatizados (unit + integration)
2. Documentación OpenAPI/Swagger
3. CI/CD con GitHub Actions
4. Security scanning

### Futuro
1. Logging centralizado (ELK Stack)
2. Service Mesh (Istio/Linkerd)
3. Kubernetes deployment
4. Multi-region support

---

**Versión del Documento**: 1.0.0
**Última Actualización**: 2 de noviembre de 2025
**Mantenido por**: EmaSleal
