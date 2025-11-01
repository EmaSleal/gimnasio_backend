# Análisis de Arquitectura - Sistema de Gimnasio Backend

## 1. Resumen Ejecutivo

Este proyecto implementa un sistema de gestión de gimnasio basado en una **arquitectura de microservicios** usando Spring Boot y Spring Cloud. El sistema está compuesto por 6 servicios principales orquestados mediante Docker Compose.

### Tecnologías Principales
- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **PostgreSQL 16**
- **RabbitMQ 3.9.11**
- **Docker & Docker Compose**

---

## 2. Arquitectura General

### 2.1 Tipo de Arquitectura
**Microservicios con Discovery Pattern y API Gateway**

```
┌─────────────────────────────────────────────────────────────────┐
│                         Cliente (Angular)                        │
│                      http://localhost:4200                       │
└────────────────────────────────┬────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                    API Gateway (Puerto 8590)                     │
│  - Enrutamiento                                                  │
│  - Autenticación JWT                                            │
│  - CORS                                                          │
│  - Circuit Breaker (Resilience4j)                               │
└─────────────────────────────────┬───────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│              Eureka Server (Puerto 8761)                         │
│              Service Discovery & Registry                        │
└─────────────────────────────────────────────────────────────────┘
                                  │
                ┌─────────────────┼─────────────────┬──────────────┐
                ▼                 ▼                 ▼              ▼
    ┌───────────────────┐ ┌──────────────┐ ┌─────────────┐ ┌──────────────┐
    │  Authentication   │ │ User Service │ │   Workout   │ │   Config     │
    │   (Puerto 8583)   │ │ (Puerto 8588)│ │   Service   │ │   Service    │
    │                   │ │              │ │ (Puerto 8586)│ │ (Puerto 8889)│
    └─────────┬─────────┘ └──────┬───────┘ └──────┬──────┘ └──────────────┘
              │                  │                │
              └──────────────────┼────────────────┘
                                 ▼
                    ┌─────────────────────────┐
                    │   PostgreSQL Database   │
                    │     (Puerto 5432)       │
                    │  - gym_authentication   │
                    │  - gym_exercise         │
                    └─────────────────────────┘

              ┌──────────────────────────────┐
              │      RabbitMQ (5672/15672)   │
              │   Message Broker (no usado)  │
              └──────────────────────────────┘
```

---

## 3. Componentes del Sistema

### 3.1 API Gateway
- **Puerto**: 8590
- **Responsabilidades**:
  - Punto de entrada único para todas las peticiones
  - Validación de JWT tokens
  - Control de acceso basado en roles (ADMIN, TRAINER, CLIENT)
  - Configuración CORS para Angular
  - Circuit Breaker para resiliencia
  - Enrutamiento inteligente a microservicios

**Rutas Configuradas**:
```
/Login                                          → authentication
/user/**                                        → user-service
/workout/**, /workoutSpecification/**,          → workout-service
/workoutPlan/**, /dailyRoutine/**,
/muscularGroup/**
```

### 3.2 Eureka Server (Service Discovery)
- **Puerto**: 8761
- **Responsabilidades**:
  - Registro de servicios
  - Descubrimiento dinámico
  - Health checking
  - Load balancing automático

### 3.3 Authentication Service
- **Puerto**: 8583
- **Responsabilidades**:
  - Generación de JWT tokens (access + refresh)
  - Registro de usuarios
  - Login y autenticación
  - Recuperación de contraseña (con integración de email via Resend)
  - **NOTA**: Actúa como proxy hacia user-service usando RestTemplate

**Dependencias Clave**:
- Spring Boot Actuator ✅
- JWT (jjwt 0.11.5)
- RestTemplate para comunicación con user-service
- Resend Java SDK (envío de emails)

### 3.4 User Service
- **Puerto**: 8588
- **Responsabilidades**:
  - CRUD de usuarios
  - Persistencia en PostgreSQL
  - Gestión de credenciales
  - Validación de usuarios

**Base de Datos**: `gym_authentication` (PostgreSQL)

**Dependencias Clave**:
- Spring Data JPA
- PostgreSQL Driver
- Spring Boot Actuator ✅

### 3.5 Workout Service
- **Puerto**: 8586
- **Responsabilidades**:
  - Gestión de ejercicios
  - Planes de entrenamiento
  - Rutinas diarias
  - Grupos musculares
  - Especificaciones de entrenamientos

**Base de Datos**: `gym_exercise` (PostgreSQL)

**Endpoints**:
- `/workout/**` - Ejercicios
- `/workoutPlan/**` - Planes
- `/dailyRoutine/**` - Rutinas
- `/muscularGroup/**` - Grupos musculares
- `/workoutSpecification/**` - Especificaciones

**Dependencias Clave**:
- Spring Data JPA
- PostgreSQL Driver
- Spring Boot Actuator ✅

### 3.6 Config Service
- **Puerto**: 8889
- **Responsabilidades**:
  - Centralización de configuraciones
  - Integración con GitHub para configuración externa
  - Configuración dinámica

**Repositorio Git**: `https://github.com/EmaSleal/config-server.git`

---

## 4. Patrones de Diseño Implementados

### 4.1 Patrones de Arquitectura
1. **API Gateway Pattern**: Punto de entrada único
2. **Service Discovery Pattern**: Eureka para registro dinámico
3. **Circuit Breaker Pattern**: Resilience4j en API Gateway
4. **Centralized Configuration**: Config Service con Git backend
5. **Load Balancing**: A través de Ribbon/Spring Cloud LoadBalancer

### 4.2 Patrones de Comunicación
1. **REST over HTTP**: Comunicación síncrona entre servicios
2. **Service-to-Service Communication**: RestTemplate con `@LoadBalanced`
3. **Token-based Authentication**: JWT para seguridad

### 4.3 Patrones de Observabilidad
1. **Distributed Tracing**: Micrometer Tracing + Brave
2. **Health Checks**: Spring Boot Actuator (parcialmente implementado)
3. **Logging Pattern**: Structured logging con trace IDs

---

## 5. Comunicación entre Servicios

### 5.1 Flujo de Autenticación
```
Cliente → API Gateway → Authentication Service → User Service → PostgreSQL
                            ↓
                        JWT Token
                            ↓
                          Cliente
```

### 5.2 Flujo de Operaciones Protegidas
```
Cliente + JWT → API Gateway (valida JWT) → Servicio correspondiente → PostgreSQL
```

### 5.3 Service-to-Service Communication
**Authentication → User Service**:
```java
// Usando RestTemplate con LoadBalanced
restTemplate.postForObject("http://user-service/user/register", userSecurity, UserDto.class);
restTemplate.postForObject("http://user-service/user/authenticate", request, UserDto.class);
restTemplate.getForObject("http://user-service/user/findByEmail/"+email, UserDto.class);
```

**Característica**: Usa nombres de servicio (no URLs directas) gracias a Eureka Discovery

---

## 6. Seguridad

### 6.1 Autenticación y Autorización
- **Mecanismo**: JWT (JSON Web Tokens)
- **Secret Key**: Compartida entre API Gateway y Authentication Service
- **Token Types**: 
  - Access Token (24h)
  - Refresh Token (24h)
  - Forgot Password Token (tiempo limitado)

### 6.2 Roles Implementados
- `ADMIN`: Acceso total
- `TRAINER`: Acceso a recursos de entrenamiento
- `CLIENT`: Acceso limitado a recursos propios

### 6.3 Filtros de Seguridad en Gateway
```java
AuthenticationFilter:
- Valida presencia de token
- Verifica expiración
- Extrae rol
- Autoriza acceso según ruta y rol
```

### 6.4 CORS
Configurado para permitir peticiones desde:
- `http://localhost:4200` (Angular)
- Métodos: GET, POST, PUT, DELETE
- Headers personalizados para JWT

---

## 7. Persistencia

### 7.1 Base de Datos
**PostgreSQL 16**
- **Container**: db-microservices
- **Puerto**: 5432
- **Usuario**: postgres
- **Password**: password123 (⚠️ Cambiar en producción)

### 7.2 Esquemas
1. **gym_authentication**: Datos de usuarios y credenciales
2. **gym_exercise**: Ejercicios, rutinas, planes de entrenamiento

### 7.3 Configuración JPA
- **DDL Auto**: 
  - user-service: `create` (⚠️ Resetea en cada inicio)
  - workout-service: `create-drop` (⚠️ Resetea en cada inicio/parada)
- **Show SQL**: true (para debugging)
- **Dialect**: PostgreSQLDialect

---

## 8. Observabilidad y Monitoreo

### 8.1 Distributed Tracing
**Stack Configurado**:
- Micrometer Tracing
- Brave (bridge)
- Zipkin Reporter
- Formato: W3C Trace Context
- Trace ID: 128 bits

**Configuración**:
```yaml
logging:
  pattern:
    level: '%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]'
```

### 8.2 Actuator
**Servicios con Actuator**:
- ✅ authentication
- ✅ user-service
- ✅ workout-service

**Problema Detectado**: No hay configuración de endpoints expuestos en `application.yml`

### 8.3 Infraestructura de Mensajería
**RabbitMQ**:
- Puerto: 5672 (AMQP)
- Management UI: 15672
- **Estado**: Configurado pero NO utilizado actualmente
- Dependencia incluida: `spring-boot-starter-amqp`

---

## 9. Infraestructura y Despliegue

### 9.1 Docker Compose
**Redes**:
- `spring`: Para comunicación entre microservicios
- `postgres`: Para acceso a base de datos

**Volúmenes**:
- `postgres`: Persistencia de datos de PostgreSQL

### 9.2 Dependencias de Inicio
```
1. postgres, rabbitmq, eureka-server (independientes)
2. config-service (depende de rabbitmq, eureka)
3. api-gateway (depende de rabbitmq, eureka)
4. user-service (depende de postgres, rabbitmq, eureka, api-gateway)
5. authentication (depende de rabbitmq, eureka, api-gateway, user-service)
```

### 9.3 Configuración de IPs
⚠️ **IP Hardcodeada**: `192.168.100.207`
- Eureka defaultZone
- PostgreSQL datasource URLs

**Problema**: Dificulta portabilidad entre entornos

---

## 10. Configuración Multi-Entorno

### 10.1 Perfiles de Spring
**Evidencia encontrada**:
```yaml
spring:
  profiles:
    active: cors  # En API Gateway
```

### 10.2 Configuración Centralizada
- Config Service conectado a GitHub
- Configuración local en archivos `application.yml` y `bootstrap.yml`
- **Problema**: Configuraciones críticas (secretos JWT, passwords DB) en archivos locales

---

## 11. Estado del Proyecto

### 11.1 ✅ Implementado
- Arquitectura de microservicios funcional
- Service Discovery con Eureka
- API Gateway con autenticación JWT
- CRUD de usuarios y entrenamientos
- Comunicación REST entre servicios
- Containerización con Docker
- Distributed Tracing configurado
- Circuit Breaker en Gateway

### 11.2 ⚠️ Parcialmente Implementado
- Spring Boot Actuator (sin endpoints expuestos)
- Config Service (existe pero configuraciones siguen en local)
- RabbitMQ (dependencia añadida pero sin uso)
- Health Checks (clase comentada)

### 11.3 ❌ No Implementado
- Eventos asíncronos con RabbitMQ
- Configuración externalizada completa
- Secrets management
- API documentation (Swagger/OpenAPI)
- Tests automatizados
- CI/CD pipeline

---

## 12. Versiones de Dependencias

| Componente | Versión |
|-----------|---------|
| Java | 21 |
| Spring Boot | 3.2.0 |
| Spring Cloud | 2023.0.0 |
| PostgreSQL Driver | Runtime (42.7.3 en workout-service) |
| Lombok | 1.18.30 |
| JWT (jjwt) | 0.11.5 |
| Micrometer Tracing | 1.2.0 |
| Zipkin Reporter | 3.2.1 |
| Resilience4j | Incluido en Spring Cloud |

---

## Conclusión

El proyecto presenta una **arquitectura sólida de microservicios** con patrones modernos bien implementados. La comunicación entre servicios está correctamente orquestada, aunque hay oportunidades significativas de mejora en áreas como observabilidad, configuración centralizada y comunicación asíncrona.
