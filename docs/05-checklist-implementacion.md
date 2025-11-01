# Checklist de Implementación de Mejoras

## 🎯 Cómo Usar Este Checklist

1. Marca con [x] cada tarea completada
2. Sigue el orden propuesto (Crítico → Alto → Medio → Bajo)
3. Estima tiempo antes de empezar cada sección
4. Haz commit después de cada tarea completada
5. Testea cada cambio antes de continuar

---

## 🔴 FASE 1: MEJORAS CRÍTICAS (Semana 1)

### Día 1: Gestión de Secretos (2-3 horas)

**Objetivo**: Eliminar secretos del código fuente

#### Preparación
- [ ] Crear archivo `.env` en la raíz del proyecto
- [ ] Agregar `.env` a `.gitignore`
- [ ] Documentar estructura de `.env.example`

#### Migración de Secretos
- [ ] Crear `.env` con variables:
  ```env
  # Database
  DB_PASSWORD=NUEVO_PASSWORD_SEGURO
  DB_USERNAME=postgres
  
  # JWT
  JWT_SECRET=NUEVO_SECRET_MUY_LARGO_Y_SEGURO
  
  # Email Service
  RESEND_API_KEY=re_NUEVA_KEY
  
  # Admin Service
  ADMIN_PASSWORD=NUEVO_ADMIN_PASSWORD
  ```

- [ ] Actualizar `docker-compose.yml`:
  ```yaml
  services:
    user-service:
      environment:
        - DB_PASSWORD=${DB_PASSWORD}
        - JWT_SECRET=${JWT_SECRET}
      env_file:
        - .env
  ```

- [ ] Actualizar `application.yml` de cada servicio:
  ```yaml
  spring:
    datasource:
      password: ${DB_PASSWORD}
  jwt:
    secret: ${JWT_SECRET}
  ```

#### Rotación de Secretos
- [ ] Generar nuevo JWT secret: `openssl rand -base64 64`
- [ ] Cambiar password de PostgreSQL en Docker
- [ ] Generar nueva API key de Resend
- [ ] Actualizar todos los valores en `.env`

#### Validación
- [ ] Verificar que ningún servicio inicia sin variables de entorno
- [ ] Confirmar que `.env` NO está en Git
- [ ] Testear login con nuevo JWT secret
- [ ] Documentar proceso en README.md

**Commit**: `feat: migrate secrets to environment variables`

---

### Día 2: Persistencia con Flyway - User Service (4 horas)

**Objetivo**: Evitar pérdida de datos en user-service

#### Instalación
- [ ] Agregar dependencia en `user-service/pom.xml`:
  ```xml
  <dependency>
      <groupId>org.flywaydb</groupId>
      <artifactId>flyway-core</artifactId>
  </dependency>
  <dependency>
      <groupId>org.flywaydb</groupId>
      <artifactId>flyway-database-postgresql</artifactId>
  </dependency>
  ```

#### Configuración
- [ ] Actualizar `application.yml`:
  ```yaml
  spring:
    jpa:
      hibernate:
        ddl-auto: validate  # Cambiar de "create" a "validate"
    flyway:
      enabled: true
      baseline-on-migrate: true
      locations: classpath:db/migration
  ```

#### Crear Migraciones
- [ ] Crear directorio `src/main/resources/db/migration`
- [ ] Crear `V1__create_users_table.sql`:
  ```sql
  CREATE TABLE IF NOT EXISTS users (
      id BIGSERIAL PRIMARY KEY,
      username VARCHAR(255) NOT NULL UNIQUE,
      email VARCHAR(255) NOT NULL UNIQUE,
      password VARCHAR(255) NOT NULL,
      role VARCHAR(50) NOT NULL,
      enabled BOOLEAN DEFAULT TRUE,
      account_non_expired BOOLEAN DEFAULT TRUE,
      credentials_non_expired BOOLEAN DEFAULT TRUE,
      account_non_locked BOOLEAN DEFAULT TRUE,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );
  
  CREATE INDEX idx_users_email ON users(email);
  CREATE INDEX idx_users_username ON users(username);
  ```

#### Validación
- [ ] Eliminar volumen existente: `docker-compose down -v`
- [ ] Levantar servicios: `docker-compose up -d`
- [ ] Verificar tabla `flyway_schema_history` creada
- [ ] Registrar un usuario de prueba
- [ ] Reiniciar servicio: `docker-compose restart user-service`
- [ ] Verificar que el usuario sigue existiendo ✅

**Commit**: `feat: add flyway migrations to user-service`

---

### Día 3: Persistencia con Flyway - Workout Service (4 horas)

**Objetivo**: Evitar pérdida de datos en workout-service

#### Instalación y Configuración
- [ ] Repetir proceso de Flyway en `workout-service/pom.xml`
- [ ] Cambiar `ddl-auto: create-drop` → `validate`
- [ ] Habilitar Flyway en `application.yml`

#### Crear Migraciones
- [ ] Crear directorio `src/main/resources/db/migration`
- [ ] `V1__create_muscular_groups_table.sql`
- [ ] `V2__create_workouts_table.sql`
- [ ] `V3__create_workout_plans_table.sql`
- [ ] `V4__create_daily_routines_table.sql`
- [ ] `V5__create_workout_specifications_table.sql`

#### Validación
- [ ] Eliminar volumen: `docker-compose down -v`
- [ ] Levantar servicios
- [ ] Verificar migraciones aplicadas
- [ ] Crear plan de entrenamiento de prueba
- [ ] Reiniciar servicio
- [ ] Verificar datos persistentes ✅

**Commit**: `feat: add flyway migrations to workout-service`

---

### Día 4: Eliminar IPs Hardcodeadas (2 horas)

**Objetivo**: Hacer configuración portable

#### Crear Perfiles
- [ ] Crear `application-docker.yml` en cada servicio:
  ```yaml
  eureka:
    client:
      service-url:
        defaultZone: http://eureka-server:8761/eureka/
  spring:
    datasource:
      url: jdbc:postgresql://postgres:5432/${DB_NAME}
  ```

- [ ] Crear `application-local.yml`:
  ```yaml
  eureka:
    client:
      service-url:
        defaultZone: http://localhost:8761/eureka/
  spring:
    datasource:
      url: jdbc:postgresql://localhost:5432/gym_authentication
  ```

#### Actualizar Docker Compose
- [ ] Agregar a cada servicio:
  ```yaml
  environment:
    - SPRING_PROFILES_ACTIVE=docker
    - DB_NAME=gym_authentication  # o gym_exercise
  ```

#### Validación
- [ ] Levantar con Docker: `docker-compose up -d`
- [ ] Verificar Eureka dashboard muestra todos los servicios
- [ ] Verificar conectividad a PostgreSQL
- [ ] Testear flujo completo (registro + login)

**Commit**: `refactor: remove hardcoded IPs and add profiles`

---

### Día 5: Crear Admin Service (8 horas)

**Objetivo**: Centralizar monitoreo de Actuator

#### Crear Módulo
- [ ] Agregar módulo en `pom.xml` raíz:
  ```xml
  <modules>
      ...
      <module>admin-service</module>
  </modules>
  ```

- [ ] Crear estructura:
  ```
  admin-service/
  ├── pom.xml
  ├── Dockerfile
  └── src/main/
      ├── java/cr/ac/backend/admin/
      │   └── AdminServiceApplication.java
      └── resources/
          └── application.yml
  ```

#### Dependencias
- [ ] Crear `admin-service/pom.xml`:
  ```xml
  <dependencies>
      <dependency>
          <groupId>de.codecentric</groupId>
          <artifactId>spring-boot-admin-starter-server</artifactId>
          <version>3.2.0</version>
      </dependency>
      <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
      </dependency>
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-security</artifactId>
      </dependency>
  </dependencies>
  ```

#### Código
- [ ] Crear `AdminServiceApplication.java`:
  ```java
  @SpringBootApplication
  @EnableAdminServer
  @EnableDiscoveryClient
  public class AdminServiceApplication {
      public static void main(String[] args) {
          SpringApplication.run(AdminServiceApplication.class, args);
      }
  }
  ```

- [ ] Crear `SecurityConfig.java` para proteger dashboard

#### Configuración
- [ ] Crear `application.yml`:
  ```yaml
  server:
    port: 9000
  spring:
    application:
      name: admin-service
    security:
      user:
        name: admin
        password: ${ADMIN_PASSWORD}
  ```

#### Actualizar Servicios Existentes
- [ ] En `authentication/pom.xml`:
  ```xml
  <dependency>
      <groupId>de.codecentric</groupId>
      <artifactId>spring-boot-admin-starter-client</artifactId>
      <version>3.2.0</version>
  </dependency>
  ```

- [ ] Actualizar `application.yml`:
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: "*"
    endpoint:
      health:
        show-details: always
  spring:
    boot:
      admin:
        client:
          url: http://admin-service:9000
  ```

- [ ] Repetir para `user-service` y `workout-service`

#### Docker
- [ ] Crear `admin-service/Dockerfile`
- [ ] Agregar a `docker-compose.yml`:
  ```yaml
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
      - SPRING_PROFILES_ACTIVE=docker
      - ADMIN_PASSWORD=${ADMIN_PASSWORD}
    restart: unless-stopped
  ```

#### Validación
- [ ] Build: `mvn clean install`
- [ ] Levantar: `docker-compose up -d admin-service`
- [ ] Acceder a `http://localhost:9000`
- [ ] Login con credenciales de admin
- [ ] Verificar que muestra 3 servicios registrados
- [ ] Verificar métricas, health checks, logs

**Commit**: `feat: add admin service for centralized monitoring`

---

## 🟡 FASE 2: MEJORAS DE ALTA PRIORIDAD (Semanas 2-3)

### Implementar RabbitMQ para Eventos (8 horas)

#### Configuración Base
- [ ] Agregar configuración en `application-docker.yml`:
  ```yaml
  spring:
    rabbitmq:
      host: rabbitmq
      port: 5672
      username: guest
      password: guest
  ```

#### Crear Email Service
- [ ] Crear nuevo módulo `email-service`
- [ ] Agregar dependencias (AMQP, Resend SDK)
- [ ] Implementar listeners para eventos
- [ ] Agregar al Docker Compose

#### Configurar Exchanges y Queues
- [ ] Crear `RabbitMQConfig.java` en cada servicio
- [ ] Definir exchanges:
  - `user.exchange`
  - `email.exchange`
  - `notification.exchange`
  - `audit.exchange`

#### Implementar Publicadores
- [ ] En `user-service`: Publicar `user.created` después de registro
- [ ] En `authentication`: Publicar `email.password.reset`
- [ ] En `workout-service`: Publicar `workout.assigned`

#### Implementar Consumidores
- [ ] `email-service`: Consumir eventos de email
- [ ] Crear listeners con `@RabbitListener`

#### Validación
- [ ] Registrar usuario
- [ ] Verificar email de bienvenida enviado
- [ ] Revisar RabbitMQ management UI (http://localhost:15672)
- [ ] Verificar mensajes procesados

**Commit**: `feat: implement event-driven architecture with RabbitMQ`

---

### Desacoplar Authentication de User Service (6 horas)

**Objetivo**: Eliminar llamadas síncronas innecesarias

#### Refactorizar Authentication Service
- [ ] Eliminar llamadas a `http://user-service/user/register`
- [ ] Authentication solo genera tokens
- [ ] User Service maneja su propio registro

#### Actualizar Rutas en Gateway
- [ ] `/user/register` → directamente a `user-service`
- [ ] `/Login` → `authentication` (solo autenticación)

#### Comunicación vía Eventos
- [ ] User Service publica `user.created`
- [ ] Authentication escucha y envía email de bienvenida

#### Validación
- [ ] Testear registro directo a user-service
- [ ] Testear login sigue funcionando
- [ ] Verificar eventos publicados correctamente

**Commit**: `refactor: decouple authentication from user-service`

---

### Configurar Circuit Breakers (4 horas)

#### Configuración de Resilience4j
- [ ] Agregar configuración en `api-gateway/application.yml`:
  ```yaml
  resilience4j:
    circuitbreaker:
      instances:
        user-service:
          sliding-window-size: 10
          failure-rate-threshold: 50
          wait-duration-in-open-state: 10s
  ```

#### Implementar Fallbacks
- [ ] Crear `FallbackController` en api-gateway
- [ ] Implementar respuestas de error amigables
- [ ] Configurar fallback por servicio

#### Validación
- [ ] Detener user-service
- [ ] Hacer petición a `/user/**`
- [ ] Verificar respuesta de fallback
- [ ] Levantar servicio
- [ ] Verificar recuperación automática

**Commit**: `feat: configure circuit breakers with fallbacks`

---

### Centralizar Configuración con Config Service (6 horas)

#### Organizar Repositorio de Configuraciones
- [ ] Crear repositorio Git: `config-server-repo`
- [ ] Estructura:
  ```
  config-server-repo/
  ├── application.yml
  ├── application-dev.yml
  ├── application-prod.yml
  ├── user-service.yml
  ├── authentication.yml
  └── ...
  ```

#### Actualizar Config Service
- [ ] Configurar Git backend
- [ ] Habilitar encryption para secretos
- [ ] Configurar perfiles

#### Migrar Servicios
- [ ] Agregar `spring-cloud-starter-config` a cada servicio
- [ ] Crear `bootstrap.yml`:
  ```yaml
  spring:
    application:
      name: user-service
    cloud:
      config:
        uri: http://config-service:8889
  ```

- [ ] Mover configuraciones comunes a repositorio Git

#### Validación
- [ ] Iniciar servicios
- [ ] Verificar que obtienen configuración de config-service
- [ ] Cambiar configuración en Git
- [ ] Refrescar servicio con `/actuator/refresh`
- [ ] Verificar cambio aplicado sin rebuild

**Commit**: `feat: centralize configuration with config service`

---

## 🟢 FASE 3: MEJORAS DE PRIORIDAD MEDIA (Semanas 4-5)

### Estandarizar Respuestas de API (3 horas)

- [ ] Crear DTO `ApiResponse<T>` común
- [ ] Implementar en todos los controllers
- [ ] Crear `@ControllerAdvice` para excepciones
- [ ] Estandarizar códigos de error

**Commit**: `refactor: standardize API responses`

---

### Validación de DTOs (2 horas)

- [ ] Agregar dependencia `spring-boot-starter-validation`
- [ ] Agregar anotaciones `@Valid`, `@NotNull`, `@Email`, etc.
- [ ] Crear DTOs de request separados de entidades
- [ ] Implementar validaciones custom

**Commit**: `feat: add DTO validation`

---

### Global Exception Handler (2 horas)

- [ ] Crear `@RestControllerAdvice`
- [ ] Manejar `MethodArgumentNotValidException`
- [ ] Manejar `EntityNotFoundException`
- [ ] Manejar errores de autenticación
- [ ] Logging estructurado de errores

**Commit**: `feat: implement global exception handler`

---

### Documentación con Swagger (4 horas)

- [ ] Agregar `springdoc-openapi-starter-webmvc-ui`
- [ ] Configurar `OpenAPIDefinition`
- [ ] Agregar `@Operation`, `@ApiResponse` a endpoints
- [ ] Configurar seguridad JWT en Swagger UI
- [ ] Documentar todos los DTOs

**Commit**: `feat: add API documentation with Swagger`

---

### Health Checks Personalizados (3 horas)

- [ ] Implementar `HealthIndicator` para cada servicio
- [ ] Verificar conectividad a DB
- [ ] Verificar conectividad a servicios downstream
- [ ] Agregar métricas custom
- [ ] Exponer en actuator

**Commit**: `feat: add custom health indicators`

---

### Rate Limiting (4 horas)

- [ ] Agregar Redis al Docker Compose
- [ ] Configurar `RequestRateLimiter` en Gateway
- [ ] Implementar `KeyResolver` por usuario
- [ ] Configurar límites por endpoint
- [ ] Mensajes de error personalizados

**Commit**: `feat: implement rate limiting`

---

## ⚪ FASE 4: MEJORAS DE PRIORIDAD BAJA (Opcional)

### Optimizar Dockerfiles (2 horas)

- [ ] Implementar multi-stage builds
- [ ] Usar imagen Alpine (menor tamaño)
- [ ] Cachear dependencias de Maven
- [ ] Optimizar layers

**Commit**: `chore: optimize Docker images`

---

### Logging Estructurado (3 horas)

- [ ] Agregar `logstash-logback-encoder`
- [ ] Configurar `logback-spring.xml`
- [ ] JSON logging para todos los servicios
- [ ] Integrar con ELK stack (opcional)

**Commit**: `feat: implement structured logging`

---

### Tests Automatizados (2 semanas)

- [ ] Tests unitarios para servicios (>70% coverage)
- [ ] Tests de integración con `@SpringBootTest`
- [ ] Tests de controllers con MockMvc
- [ ] Tests de seguridad
- [ ] Tests de eventos RabbitMQ
- [ ] Contract testing entre servicios

**Commit**: `test: add comprehensive test suite`

---

### CI/CD Pipeline (1 semana)

- [ ] Crear `.github/workflows/ci.yml`
- [ ] Build automático en push
- [ ] Ejecutar tests
- [ ] Build de imágenes Docker
- [ ] Push a Docker Hub
- [ ] Deploy automático a staging
- [ ] Notificaciones de build

**Commit**: `ci: add GitHub Actions pipeline`

---

## 📊 Seguimiento de Progreso

### Métricas

- **Fase 1 (Crítico)**: ☐☐☐☐☐ (0/5 días completados)
- **Fase 2 (Alto)**: ☐☐☐☐ (0/4 tareas completadas)
- **Fase 3 (Medio)**: ☐☐☐☐☐☐ (0/6 tareas completadas)
- **Fase 4 (Bajo)**: ☐☐☐☐ (0/4 tareas completadas)

### Estado General

- [ ] Seguridad: Secretos protegidos
- [ ] Persistencia: Datos seguros
- [ ] Observabilidad: Dashboard operativo
- [ ] Arquitectura: Servicios desacoplados
- [ ] Calidad: API documentada
- [ ] Testing: Coverage >70%
- [ ] DevOps: CI/CD funcional

---

## 🎯 Criterios de Éxito

### Al Completar Fase 1 (Crítico)
✅ No hay secretos en el código  
✅ Los datos persisten entre reinicios  
✅ Dashboard de monitoreo funcional  
✅ Configuración portable entre entornos  

### Al Completar Fase 2 (Alto)
✅ Comunicación asíncrona implementada  
✅ Servicios desacoplados  
✅ Circuit breakers protegiendo el sistema  
✅ Configuración centralizada  

### Al Completar Fase 3 (Medio)
✅ API consistente y documentada  
✅ Validaciones robustas  
✅ Manejo de errores estandarizado  
✅ Health checks completos  

### Al Completar Fase 4 (Bajo)
✅ Imágenes Docker optimizadas  
✅ Logs estructurados  
✅ Tests automatizados  
✅ Pipeline CI/CD funcional  

---

## 📝 Notas

- Haz commit después de cada checkbox marcado
- Testea manualmente después de cada cambio
- Documenta problemas encontrados
- Actualiza README.md con cambios importantes
- Pide code review antes de merge a main

**¡Éxito con la implementación! 🚀**
