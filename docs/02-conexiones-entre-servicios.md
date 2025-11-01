# Conexiones Entre Servicios - Sistema de Gimnasio

## 1. Diagrama de Dependencias

```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│                          Cliente Angular                            │
│                         (localhost:4200)                            │
│                                                                     │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ HTTP/REST
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        API Gateway (8590)                           │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ AuthenticationFilter                                        │   │
│  │  - Valida JWT                                               │   │
│  │  - Verifica roles (ADMIN/TRAINER/CLIENT)                    │   │
│  │  - Control de acceso por ruta                               │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  Rutas:                                                             │
│    /Login → authentication                                          │
│    /user/** → user-service                                          │
│    /workout/** → workout-service                                    │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               │ Service Discovery
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     Eureka Server (8761)                            │
│                                                                     │
│  Servicios Registrados:                                             │
│  - authentication (8583)                                            │
│  - user-service (8588)                                              │
│  - workout-service (8586)                                           │
│  - api-gateway (8590)                                               │
│  - config-service (8889)                                            │
└─────────────────────────────────────────────────────────────────────┘
                               │
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
          ▼                    ▼                    ▼
┌───────────────────┐  ┌──────────────────┐  ┌────────────────┐
│  Authentication   │  │   User Service   │  │ Workout Service│
│     Service       │  │                  │  │                │
│    (8583)         │  │     (8588)       │  │    (8586)      │
│                   │  │                  │  │                │
│ ┌───────────────┐ │  │ ┌──────────────┐ │  │ ┌────────────┐ │
│ │RestTemplate   │─┼──┼→│ Controllers  │ │  │ │Controllers │ │
│ │@LoadBalanced  │ │  │ │              │ │  │ │            │ │
│ └───────────────┘ │  │ └──────┬───────┘ │  │ └─────┬──────┘ │
│                   │  │        │         │  │       │        │
│ ┌───────────────┐ │  │        ▼         │  │       ▼        │
│ │ JWT Service   │ │  │ ┌──────────────┐ │  │ ┌────────────┐ │
│ │ - Generate    │ │  │ │  Services    │ │  │ │  Services  │ │
│ │ - Validate    │ │  │ │  - CRUD User │ │  │ │  - Workout │ │
│ └───────────────┘ │  │ │  - Auth      │ │  │ │  - Plans   │ │
│                   │  │ └──────┬───────┘ │  │ └─────┬──────┘ │
│ ┌───────────────┐ │  │        │         │  │       │        │
│ │Email Service  │ │  │        ▼         │  │       ▼        │
│ │(Resend SDK)   │ │  │ ┌──────────────┐ │  │ ┌────────────┐ │
│ └───────────────┘ │  │ │ JPA Repos    │ │  │ │ JPA Repos  │ │
└───────────────────┘  │ └──────┬───────┘ │  │ └─────┬──────┘ │
                       └────────┼──────────┘  └───────┼────────┘
                                │                     │
                                │                     │
                                ▼                     ▼
                ┌─────────────────────────────────────────────┐
                │         PostgreSQL (5432)                   │
                │  ┌────────────────┐  ┌──────────────────┐  │
                │  │ gym_auth..      │  │ gym_exercise     │  │
                │  │ - users         │  │ - workouts       │  │
                │  │ - credentials   │  │ - plans          │  │
                │  │                 │  │ - routines       │  │
                │  └────────────────┘  └──────────────────┘  │
                └─────────────────────────────────────────────┘

        ┌───────────────────────────────────────────┐
        │   RabbitMQ (5672/15672)                   │
        │   - Declarado pero NO utilizado           │
        │   - Sin consumers ni producers            │
        └───────────────────────────────────────────┘
```

---

## 2. Tipos de Conexiones

### 2.1 Conexiones HTTP Síncronas

#### A. Cliente → API Gateway
**Protocolo**: HTTP/REST  
**Puerto**: 8590  
**Seguridad**: 
- CORS configurado para `http://localhost:4200`
- JWT en header `Authorization: Bearer <token>`

**Ejemplo de Request**:
```http
POST http://localhost:8590/Login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### B. API Gateway → Servicios Backend
**Mecanismo**: Spring Cloud Gateway  
**Load Balancing**: Ribbon/Spring Cloud LoadBalancer  
**Service Discovery**: Eureka

**Rutas Configuradas**:
```java
/Login                    → lb://authentication
/user/**                  → lb://user-service
/workout/**               → lb://workout-service
/workoutSpecification/**  → lb://workout-service
/workoutPlan/**           → lb://workout-service
/dailyRoutine/**          → lb://workout-service
/muscularGroup/**         → lb://workout-service
```

**Características**:
- Prefijo `lb://` indica load balancing via Eureka
- AuthenticationFilter aplicado a todas las rutas
- Resolución dinámica de hosts

#### C. Authentication → User Service
**Protocolo**: REST over HTTP  
**Mecanismo**: RestTemplate con `@LoadBalanced`  
**URLs Lógicas**: Usa nombre de servicio en lugar de IP/puerto

**Endpoints Consumidos**:
```java
// Registro de usuario
POST http://user-service/user/register

// Autenticación
POST http://user-service/user/authenticate

// Búsqueda por email
GET http://user-service/user/findByEmail/{email}

// Búsqueda por ID
GET http://user-service/user/id/{id}

// Búsqueda por username
GET http://user-service/user/findByUserName/{username}

// Reset de contraseña
POST http://user-service/user/resetPassword

// Obtener todos los usuarios
GET http://user-service/user/all
```

**Implementación**:
```java
@Configuration
public class RestTemplateConfig {
    @Bean
    @LoadBalanced  // ← Crucial para service discovery
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// Uso en AuthenticationServiceImpl
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl {
    private final RestTemplate restTemplate;
    
    public UserDto register(User request) {
        var UserDto = restTemplate.postForObject(
            "http://user-service/user/register",  // ← Nombre lógico
            userSecurity, 
            UserDto.class
        );
        // ...
    }
}
```

---

### 2.2 Conexiones a Base de Datos

#### PostgreSQL (User Service)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://192.168.100.207:5432/gym_authentication
    username: postgres
    password: Chismosear01
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: create  # ⚠️ Resetea en cada inicio
```

#### PostgreSQL (Workout Service)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://192.168.100.207:5432/gym_exercise
    username: postgres
    password: Chismosear01
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop  # ⚠️ Destruye al parar
```

**Problemas Identificados**:
- IP hardcodeada (`192.168.100.207`)
- DDL auto destructivo en ambos servicios
- Credenciales en texto plano

---

### 2.3 Conexiones a Infraestructura

#### Eureka Discovery
Todos los servicios (excepto eureka-server) se registran:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://192.168.100.207:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
```

**Servicios Registrados**:
- api-gateway
- authentication
- user-service
- workout-service
- config-service

**Flujo de Registro**:
1. Servicio inicia
2. Se conecta a Eureka
3. Envía heartbeat cada 30s
4. Eureka mantiene registro actualizado
5. Otros servicios consultan Eureka para resolver nombres

#### Config Service
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/EmaSleal/config-server.git
          clone-on-start: true
```

**Problema**: Servicios no están consumiendo configuraciones de este servicio

#### RabbitMQ
**Estado**: Dependencia incluida pero NO utilizada

```yaml
# Dependencia en pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

**Evidencia de NO uso**:
- Sin configuración de RabbitMQ en application.yml
- Sin clases con `@RabbitListener`
- Sin `RabbitTemplate` inyectado
- Sin exchanges, queues, o bindings definidos

---

## 3. Flujos de Datos Completos

### 3.1 Flujo de Registro de Usuario

```
┌─────────┐      ┌───────────┐      ┌──────────────┐      ┌──────────┐      ┌──────────┐
│ Cliente │      │    API    │      │Authentication│      │   User   │      │PostgreSQL│
│ Angular │      │  Gateway  │      │   Service    │      │ Service  │      │          │
└────┬────┘      └─────┬─────┘      └──────┬───────┘      └────┬─────┘      └────┬─────┘
     │                 │                   │                   │                 │
     │ POST /user/     │                   │                   │                 │
     │ register        │                   │                   │                 │
     ├────────────────>│                   │                   │                 │
     │                 │                   │                   │                 │
     │                 │ No JWT required   │                   │                 │
     │                 │ (public route)    │                   │                 │
     │                 ├──────────────────>│                   │                 │
     │                 │                   │                   │                 │
     │                 │                   │ Hash password     │                 │
     │                 │                   │ with BCrypt       │                 │
     │                 │                   ├──────────┐        │                 │
     │                 │                   │          │        │                 │
     │                 │                   │<─────────┘        │                 │
     │                 │                   │                   │                 │
     │                 │                   │ POST              │                 │
     │                 │                   │ user-service/     │                 │
     │                 │                   │ user/register     │                 │
     │                 │                   ├──────────────────>│                 │
     │                 │                   │                   │                 │
     │                 │                   │                   │ INSERT INTO     │
     │                 │                   │                   │ users           │
     │                 │                   │                   ├────────────────>│
     │                 │                   │                   │                 │
     │                 │                   │                   │    User ID      │
     │                 │                   │                   │<────────────────┤
     │                 │                   │                   │                 │
     │                 │                   │    UserDto        │                 │
     │                 │                   │<──────────────────┤                 │
     │                 │                   │                   │                 │
     │                 │                   │ Generate JWT      │                 │
     │                 │                   │ - access token    │                 │
     │                 │                   │ - refresh token   │                 │
     │                 │                   ├──────────┐        │                 │
     │                 │                   │          │        │                 │
     │                 │                   │<─────────┘        │                 │
     │                 │                   │                   │                 │
     │                 │  UserDto + Tokens │                   │                 │
     │                 │<──────────────────┤                   │                 │
     │                 │                   │                   │                 │
     │  UserDto +      │                   │                   │                 │
     │  Tokens         │                   │                   │                 │
     │<────────────────┤                   │                   │                 │
     │                 │                   │                   │                 │
```

**Puntos Clave**:
1. Cliente envía datos sin encriptar (usuario/contraseña)
2. API Gateway NO valida JWT (ruta pública)
3. Authentication Service hashea la contraseña con BCrypt
4. RestTemplate llama a User Service con `@LoadBalanced`
5. User Service persiste en PostgreSQL
6. Authentication Service genera JWT tokens
7. Cliente recibe UserDto + tokens para futuras peticiones

---

### 3.2 Flujo de Login

```
┌─────────┐      ┌───────────┐      ┌──────────────┐      ┌──────────┐      ┌──────────┐
│ Cliente │      │    API    │      │Authentication│      │   User   │      │PostgreSQL│
│ Angular │      │  Gateway  │      │   Service    │      │ Service  │      │          │
└────┬────┘      └─────┬─────┘      └──────┬───────┘      └────┬─────┘      └────┬─────┘
     │                 │                   │                   │                 │
     │ POST /Login     │                   │                   │                 │
     │ {email, pwd}    │                   │                   │                 │
     ├────────────────>│                   │                   │                 │
     │                 │                   │                   │                 │
     │                 │ AuthFilter        │                   │                 │
     │                 │ (public route)    │                   │                 │
     │                 ├──────────────────>│                   │                 │
     │                 │                   │                   │                 │
     │                 │                   │ POST user-service │                 │
     │                 │                   │ /user/authenticate│                 │
     │                 │                   ├──────────────────>│                 │
     │                 │                   │                   │                 │
     │                 │                   │                   │ SELECT * FROM   │
     │                 │                   │                   │ users WHERE     │
     │                 │                   │                   │ email=?         │
     │                 │                   │                   ├────────────────>│
     │                 │                   │                   │                 │
     │                 │                   │                   │  User record    │
     │                 │                   │                   │<────────────────┤
     │                 │                   │                   │                 │
     │                 │                   │                   │ Validate pwd    │
     │                 │                   │                   │ with BCrypt     │
     │                 │                   │                   ├──────────┐      │
     │                 │                   │                   │          │      │
     │                 │                   │                   │<─────────┘      │
     │                 │                   │                   │                 │
     │                 │                   │    UserDto        │                 │
     │                 │                   │<──────────────────┤                 │
     │                 │                   │                   │                 │
     │                 │                   │ Generate JWT      │                 │
     │                 │                   │ - id: UserDto.id  │                 │
     │                 │                   │ - role: UserDto.  │                 │
     │                 │                   │   role            │                 │
     │                 │                   ├──────────┐        │                 │
     │                 │                   │          │        │                 │
     │                 │                   │<─────────┘        │                 │
     │                 │                   │                   │                 │
     │                 │  UserDto + Tokens │                   │                 │
     │                 │<──────────────────┤                   │                 │
     │                 │                   │                   │                 │
     │  UserDto +      │                   │                   │                 │
     │  Tokens         │                   │                   │                 │
     │<────────────────┤                   │                   │                 │
     │                 │                   │                   │                 │
     │ Store token     │                   │                   │                 │
     │ in localStorage │                   │                   │                 │
     ├──────────┐      │                   │                   │                 │
     │          │      │                   │                   │                 │
     │<─────────┘      │                   │                   │                 │
```

---

### 3.3 Flujo de Petición Protegida (ejemplo: obtener workouts)

```
┌─────────┐      ┌───────────┐      ┌──────────┐      ┌──────────┐
│ Cliente │      │    API    │      │ Workout  │      │PostgreSQL│
│ Angular │      │  Gateway  │      │ Service  │      │          │
└────┬────┘      └─────┬─────┘      └────┬─────┘      └────┬─────┘
     │                 │                 │                 │
     │ GET /workout    │                 │                 │
     │ Authorization:  │                 │                 │
     │ Bearer <JWT>    │                 │                 │
     ├────────────────>│                 │                 │
     │                 │                 │                 │
     │                 │ AuthFilter      │                 │
     │                 │ - Check header  │                 │
     │                 │ - Extract token │                 │
     │                 │ - Verify exp    │                 │
     │                 │ - Extract role  │                 │
     │                 │ - Check authz   │                 │
     │                 ├──────────┐      │                 │
     │                 │          │      │                 │
     │                 │<─────────┘      │                 │
     │                 │                 │                 │
     │                 │ Route to        │                 │
     │                 │ lb://workout-   │                 │
     │                 │ service         │                 │
     │                 ├────────────────>│                 │
     │                 │                 │                 │
     │                 │                 │ SELECT * FROM   │
     │                 │                 │ workouts        │
     │                 │                 ├────────────────>│
     │                 │                 │                 │
     │                 │                 │   Workout list  │
     │                 │                 │<────────────────┤
     │                 │                 │                 │
     │                 │  Workout DTOs   │                 │
     │                 │<────────────────┤                 │
     │                 │                 │                 │
     │  Workout DTOs   │                 │                 │
     │<────────────────┤                 │                 │
     │                 │                 │                 │
```

**Validaciones en AuthenticationFilter**:
1. ¿Header `Authorization` presente? → Si no: 401
2. ¿Token expirado? → Si sí: 401
3. ¿Rol adecuado para la ruta? → Si no: 401
4. ✅ Todas las validaciones pasadas → Continua al servicio

---

## 4. Tabla de Dependencias

| Servicio Origen | Servicio Destino | Tipo | Protocolo | Propósito |
|----------------|------------------|------|-----------|-----------|
| Cliente Angular | API Gateway | Síncrono | HTTP/REST | Todas las operaciones |
| API Gateway | Eureka Server | Síncrono | HTTP | Service Discovery |
| API Gateway | Authentication | Síncrono | HTTP | Login, registro |
| API Gateway | User Service | Síncrono | HTTP | Operaciones de usuario |
| API Gateway | Workout Service | Síncrono | HTTP | Operaciones de entrenamiento |
| Authentication | Eureka Server | Síncrono | HTTP | Registro de servicio |
| Authentication | User Service | Síncrono | HTTP/REST | Validación de usuarios |
| User Service | PostgreSQL | Síncrono | JDBC | Persistencia de usuarios |
| User Service | Eureka Server | Síncrono | HTTP | Registro de servicio |
| Workout Service | PostgreSQL | Síncrono | JDBC | Persistencia de entrenamientos |
| Workout Service | Eureka Server | Síncrono | HTTP | Registro de servicio |
| Config Service | GitHub | Síncrono | HTTPS/Git | Obtener configuraciones |
| Config Service | Eureka Server | Síncrono | HTTP | Registro de servicio |

---

## 5. Puertos y Endpoints

### API Gateway (8590)
| Ruta | Servicio Destino | Autenticación | Roles Permitidos |
|------|------------------|---------------|------------------|
| `/Login` | authentication | No (público) | Todos |
| `/user/register` | user-service | No (público) | Todos |
| `/user/**` | user-service | Sí | ADMIN, TRAINER, CLIENT |
| `/workout/**` | workout-service | Sí | ADMIN, TRAINER |
| `/workoutPlan/**` | workout-service | Sí | ADMIN, TRAINER |
| `/dailyRoutine/**` | workout-service | Sí | ADMIN, TRAINER, CLIENT |
| `/muscularGroup/**` | workout-service | Sí | ADMIN, TRAINER |
| `/workoutSpecification/**` | workout-service | Sí | ADMIN, TRAINER |

### Authentication Service (8583)
Expuesto a través de API Gateway únicamente.

### User Service (8588)
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/user/register` | POST | Crear nuevo usuario |
| `/user/authenticate` | POST | Validar credenciales |
| `/user/findByEmail/{email}` | GET | Buscar por email |
| `/user/id/{id}` | GET | Buscar por ID |
| `/user/findByUserName/{username}` | GET | Buscar por username |
| `/user/resetPassword` | POST | Actualizar contraseña |
| `/user/all` | GET | Listar todos |

### Workout Service (8586)
**Controladores**:
- WorkoutController
- WorkoutPlanController
- DailyRoutineController
- MuscularGroupController
- WorkoutSpecificationController

### Eureka Server (8761)
| Endpoint | Descripción |
|----------|-------------|
| `/eureka/apps` | Lista de servicios registrados |
| `/` | Dashboard web |

### Config Service (8889)
**Configurado pero no consumido por otros servicios**

---

## 6. Problemas y Riesgos en las Conexiones

### 6.1 Alta Dependencia de Authentication → User Service
**Problema**: Authentication Service actúa como proxy completo de User Service

**Riesgo**:
- Duplicación de lógica
- Latencia adicional (hop extra)
- Authentication se convierte en punto único de fallo

**Recomendación**: 
- Mover operaciones CRUD de usuarios al User Service directamente
- Authentication solo debería generar/validar tokens

### 6.2 IP Hardcodeada
**Problema**: `192.168.100.207` en múltiples configuraciones

**Impacto**:
- No funciona en otros entornos
- Dificulta Docker Compose (debería usar nombres de servicio)

**Solución**:
```yaml
# En lugar de:
defaultZone: http://192.168.100.207:8761/eureka/

# Usar:
defaultZone: http://eureka-server:8761/eureka/
```

### 6.3 RabbitMQ Sin Usar
**Problema**: Dependencia añadida, infraestructura levantada, pero CERO uso

**Desperdicio de recursos**:
- Container ejecutándose sin propósito
- Dependencias innecesarias en pom.xml

**Opciones**:
1. Implementar comunicación asíncrona (recomendado)
2. Remover RabbitMQ completamente

### 6.4 Configuración No Centralizada
**Problema**: Config Service existe pero servicios no lo consumen

**Evidencia**:
- application.yml contiene todas las configuraciones
- No hay bootstrap.yml consumiendo config-service
- Secretos (JWT, DB passwords) en archivos locales

### 6.5 DDL Auto Destructivo
**Problema**: 
- user-service: `ddl-auto: create`
- workout-service: `ddl-auto: create-drop`

**Impacto**: 
- Pérdida de datos en cada reinicio
- Imposible ejecutar en producción

---

## Conclusión

El sistema tiene conexiones bien diseñadas usando patrones modernos (Service Discovery, Load Balancing), pero presenta problemas críticos:

1. **Alta acoplamiento** entre Authentication y User Service
2. **Configuración no portable** (IPs hardcodeadas)
3. **Infraestructura subutilizada** (RabbitMQ, Config Service)
4. **Riesgos de pérdida de datos** (DDL auto)

Las mejoras propuestas en el siguiente documento abordarán estos puntos.
