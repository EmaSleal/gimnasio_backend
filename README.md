# Sistema de Gestión de Gimnasio - Microservicios

Sistema backend basado en arquitectura de microservicios usando Spring Boot y Spring Cloud para la gestión integral de un gimnasio.

## 🏗️ Arquitectura

Este proyecto implementa una arquitectura de microservicios con los siguientes componentes:

```
Cliente (Angular) → API Gateway → Eureka → Microservicios → PostgreSQL
                         ↓
                    RabbitMQ (para eventos)
```

### Servicios Principales

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| **Eureka Server** | 8761 | Service Discovery y registro de microservicios |
| **Config Service** | 8889 | Configuración centralizada desde GitHub |
| **API Gateway** | 8590 | Punto de entrada único, autenticación JWT, CORS |
| **Authentication** | 8583 | Generación de tokens JWT, recuperación de contraseña |
| **User Service** | 8588 | Gestión de usuarios y credenciales |
| **Workout Service** | 8586 | Gestión de ejercicios, planes y rutinas |

### Infraestructura

- **PostgreSQL** (5432): Base de datos relacional
  - `gym_authentication`: Usuarios y credenciales
  - `gym_exercise`: Ejercicios y rutinas
- **RabbitMQ** (5672/15672): Message broker para eventos asíncronos
- **Spring Boot Admin** (9595): Monitoreo centralizado de microservicios

### Stack de Monitoreo (Sprint 2) 📊

| Servicio | Puerto | Descripción | Estado |
|----------|--------|-------------|--------|
| **Prometheus** | 9090 | Sistema de métricas y monitoreo | ✅ 8/8 targets UP |
| **Grafana** | 3000 | Visualización de dashboards | ✅ 4 dashboards |
| **Alertmanager** | 9093 | Gestión de alertas | ✅ 15 reglas activas |
| **Spring Boot Admin** | 9595 | Panel de administración | ✅ 6/6 servicios |

**Dashboards Configurados:**
- Spring Boot Statistics (ID: 11378)
- JVM (Micrometer) (ID: 4701)
- Spring Cloud Gateway (ID: 11506)
- Custom Microservices Dashboard

**Sistema de Alertas:**
- 15 reglas Prometheus (7 grupos)
- 5 reglas Grafana
- 7 contact points configurados
- Scripts de testing automatizados

## 📚 Documentación Completa

**🔍 Para un análisis detallado del sistema, consulta la carpeta [`/docs`](./docs/)**

### Índice de Documentos - Sprint 1

0. **[Resumen Ejecutivo](./docs/00-resumen-ejecutivo.md)** ⭐ **EMPIEZA AQUÍ**
   - Respuesta sobre Actuator y Admin Service
   - Hallazgos críticos del análisis
   - Plan de acción inmediato

1. **[Análisis de Arquitectura](./docs/01-analisis-arquitectura.md)**
   - Componentes del sistema
   - Patrones de diseño implementados
   - Stack tecnológico

2. **[Conexiones entre Servicios](./docs/02-conexiones-entre-servicios.md)**
   - Flujos de datos completos
   - Diagramas de comunicación
   - Dependencias entre servicios

3. **[Puntos de Mejora](./docs/03-puntos-de-mejora.md)**
   - Mejoras críticas, altas, medias y bajas
   - Implementación de Admin Service (Actuator centralizado)
   - Código de ejemplo para cada mejora

4. **[Diagramas](./docs/04-diagramas.md)**
   - Arquitectura visual
   - Flujos de autenticación
   - Service Discovery

5. **[Checklist de Implementación](./docs/05-checklist-implementacion.md)** ✅
   - Tareas detalladas por fase
   - Estimaciones de tiempo
   - Criterios de validación

### Documentación Sprint 2 - Monitoreo 📊

- **[Fases del Sprint 2](./docs/fases.md)**
  - Fase 1: Prometheus (métricas)
  - Fase 2: Grafana (visualización)
  - Fase 3: Dashboards y Alertas

- **[Dashboards de Grafana](./docs/monitoring/GRAFANA_DASHBOARDS.md)**
  - Configuración de 4 dashboards
  - Métricas clave por servicio
  - Guía de uso y troubleshooting

- **[Sistema de Alertas](./docs/monitoring/ALERTAS.md)**
  - 15 reglas Prometheus
  - Configuración Alertmanager
  - 5 reglas Grafana
  - Scripts de testing
  - Guías de troubleshooting

- **[Adaptaciones Sprint 2](./docs/ADAPTACIONES_SPRINT_2.md)**
  - Decisiones técnicas tomadas
  - Cambios en la implementación
  - Justificaciones

## 🚀 Inicio Rápido

### Prerrequisitos

- Java 21
- Maven 3.8+
- Docker & Docker Compose

### Ejecución con Docker Compose (Recomendado)

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/EmaSleal/gimnasio_backend.git
   cd gimnasio_backend
   ```

2. **Configurar variables de entorno** ⚠️ IMPORTANTE
   ```bash
   # Crear archivo .env en la raíz
   cp .env.example .env
   # Editar .env con tus secretos
   ```

3. **Construir y ejecutar**
   ```bash
   # Construir los JARs
   mvn clean package -DskipTests

   # Levantar todos los servicios
   docker-compose up -d
   ```

4. **Verificar servicios**
   
   **Microservicios:**
   - Eureka Dashboard: http://localhost:8761
   - API Gateway: http://localhost:8590
   - Spring Boot Admin: http://localhost:9595
   - RabbitMQ Management: http://localhost:15672 (guest/guest)
   
   **Stack de Monitoreo:**
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:3000 (admin/admin)
   - Alertmanager: http://localhost:9093

5. **Estado de servicios**
   ```bash
   # Ver estado de todos los contenedores
   docker-compose ps
   
   # Ver logs de un servicio específico
   docker-compose logs -f [servicio]
   
   # Verificar targets de Prometheus
   curl http://localhost:9090/api/v1/targets
   ```

### Orden de Inicio de Servicios

> ⚠️ **IMPORTANTE**: Los servicios deben iniciarse en este orden:

1. `postgres`, `rabbitmq`, `eureka-server` (independientes)
2. `config-service`
3. `api-gateway`
4. `user-service`
5. `authentication`, `workout-service`

Docker Compose maneja esto automáticamente con `depends_on`.

## 🧪 Pruebas

### Registro de Usuario

```bash
POST http://localhost:8590/user/register
Content-Type: application/json

{
  "userName": "testuser",
  "email": "test@example.com",
  "password": "Password123",
  "role": "CLIENT"
}
```

### Login

```bash
POST http://localhost:8590/Login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "Password123"
}
```

Respuesta:
```json
{
  "id": 1,
  "userName": "testuser",
  "email": "test@example.com",
  "role": "CLIENT",
  "token": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### Peticiones Autenticadas

```bash
GET http://localhost:8590/workout/all
Authorization: Bearer {token}
```

## 🔒 Seguridad

- **Autenticación**: JWT (JSON Web Tokens)
- **Roles**: ADMIN, TRAINER, CLIENT
- **CORS**: Configurado para http://localhost:4200

> ⚠️ **ADVERTENCIA DE SEGURIDAD**: 
> - Los secretos actualmente están en archivos YAML (NO SEGURO)
> - Ver [03-puntos-de-mejora.md](./docs/03-puntos-de-mejora.md) para migrar a variables de entorno

## 🛠️ Stack Tecnológico

### Microservicios
- **Java**: 21
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0 (Eureka, Gateway, Config)
- **Spring Boot Admin**: 3.2.0
- **PostgreSQL**: 16
- **RabbitMQ**: 3.9.11
- **JWT**: jjwt 0.11.5
- **Flyway**: Migraciones de base de datos
- **Micrometer**: Métricas y tracing

### Monitoreo y Observabilidad (Sprint 2)
- **Prometheus**: 2.x (Time-series database)
- **Grafana**: 11.x (Visualización)
- **Alertmanager**: Gestión de alertas
- **Micrometer Tracing + Brave**: Distributed tracing
- **Spring Boot Actuator**: Health checks y métricas

### DevOps
- **Docker & Docker Compose**: Containerización
- **Maven**: Build automation
- **Git**: Control de versiones

## ⚠️ Problemas Conocidos y Soluciones

### Issues Resueltos ✅

1. **✅ RESUELTO**: Admin Service para monitoreo centralizado
   - **Implementado**: Spring Boot Admin en puerto 9595
   - Todos los servicios registrados y monitoreados
   - Health checks y métricas centralizadas

2. **✅ RESUELTO**: Migraciones de base de datos con Flyway
   - **Implementado**: Flyway en authentication y user-service
   - Scripts versionados para gym_authentication
   - Ya no se usa `ddl-auto: create-drop`

3. **✅ RESUELTO**: Conexión RabbitMQ en todos los servicios
   - **Implementado**: Configuración en authentication, api-gateway, user-service, workout-service
   - Variables de entorno configuradas (RABBITMQ_HOST, PORT, USERNAME, PASSWORD)
   - Todos los servicios conectados exitosamente

4. **✅ RESUELTO**: Monitoreo y observabilidad completo
   - **Implementado**: Stack completo Prometheus + Grafana + Alertmanager
   - 8/8 targets monitoreados
   - 4 dashboards configurados
   - 15 reglas de alerta activas
   - Scripts de testing automatizados

### Issues Pendientes 🔄

1. **🔴 CRÍTICO**: Secretos expuestos en `application.yml`
   - **Solución**: Migrar a variables de entorno con cifrado
   - Ver [checklist](./docs/05-checklist-implementacion.md) - Día 1

2. **🟡 ALTO**: IPs hardcodeadas en algunas configuraciones
   - **Solución**: Usar nombres de servicio Docker
   - Ver [checklist](./docs/05-checklist-implementacion.md) - Día 4

3. **� MEDIO**: RabbitMQ configurado pero eventos asíncronos no implementados
   - **Solución**: Implementar eventos para registro de usuarios, creación de rutinas
   - Ver [docs/03-puntos-de-mejora.md](./docs/03-puntos-de-mejora.md#33-implementar-comunicación-asíncrona-con-rabbitmq)

4. **� MEDIO**: Config Server sin uso activo
   - **Solución**: Migrar configuraciones a repositorio Git
   - Ver documentación de Spring Cloud Config

Ver análisis completo en [/docs](./docs/).

## � Estado Actual del Sistema

### Resumen de Servicios (12/12 Contenedores Activos)

| Categoría | Servicio | Estado | Métricas | Notas |
|-----------|----------|--------|----------|-------|
| **Core** | Eureka Server | 🟢 UP | ✅ | 6/6 servicios registrados |
| **Core** | Config Service | 🟢 UP | ✅ | Configurado, pendiente uso activo |
| **Core** | API Gateway | 🟢 UP | ✅ | JWT, CORS, RabbitMQ conectado |
| **Business** | Authentication | 🟢 UP | ✅ | Flyway, RabbitMQ, PostgreSQL |
| **Business** | User Service | 🟢 UP | ✅ | Flyway, RabbitMQ, PostgreSQL |
| **Business** | Workout Service | 🟢 UP | ✅ | RabbitMQ conectado |
| **Admin** | Spring Boot Admin | 🟢 UP | ✅ | 6/6 servicios monitoreados |
| **Data** | PostgreSQL | 🟢 UP | ✅ | 2 bases de datos |
| **Messaging** | RabbitMQ | 🟢 UP | ✅ | 4 servicios conectados |
| **Monitoring** | Prometheus | 🟢 UP | ✅ | 8/8 targets UP |
| **Monitoring** | Grafana | 🟢 UP | ✅ | 4 dashboards activos |
| **Alerting** | Alertmanager | 🟢 UP | ✅ | 15 reglas configuradas |

### Métricas Clave

**Disponibilidad:**
- 🟢 Uptime de servicios: 100%
- 🟢 Prometheus targets: 8/8 (100%)
- 🟢 Eureka registrations: 6/6 (100%)
- 🟢 Health checks: Todos passing

**Monitoreo:**
- 📊 Dashboards: 4 activos
- 🔔 Reglas de alerta: 20 totales (15 Prometheus + 5 Grafana)
- 📧 Contact points: 7 configurados
- 🧪 Scripts de testing: Disponibles

**Conectividad:**
- 🐰 RabbitMQ: 4/4 servicios conectados
- 🗄️ PostgreSQL: 2/2 bases de datos activas
- 🌐 Redes Docker: 2 (spring, postgres)

### Últimas Mejoras Implementadas

**Sprint 2 (Fase 3 - Completado):**
1. ✅ Sistema de alertas Prometheus (15 reglas en 7 grupos)
2. ✅ Alertmanager con múltiples canales de notificación
3. ✅ 5 reglas de alerta Grafana con routing inteligente
4. ✅ Dashboards de monitoreo completos
5. ✅ Scripts de testing automatizados (`test-alerts.ps1`)
6. ✅ Documentación exhaustiva de alertas

**Fixes Recientes:**
1. ✅ Configuración RabbitMQ en authentication y api-gateway
2. ✅ Resolución de problema de red en user-service
3. ✅ Variables de entorno para RabbitMQ en docker-compose

## �📊 Roadmap

### ✅ Sprint 1 - Completado (100%)
- ✅ Arquitectura de microservicios básica
- ✅ Service Discovery con Eureka
- ✅ API Gateway con autenticación JWT
- ✅ CRUD de usuarios y ejercicios
- ✅ Containerización con Docker
- ✅ Spring Boot Admin Service
- ✅ Flyway para migraciones
- ✅ Variables de entorno para secretos
- ✅ Actuator endpoints en todos los servicios

### ✅ Sprint 2 - Completado (100%)
- ✅ **Fase 1**: Prometheus configurado (8 servicios monitoreados)
- ✅ **Fase 2**: Grafana con datasources y plugins
- ✅ **Fase 3**: Dashboards importados (4 dashboards)
- ✅ **Fase 3**: Sistema de alertas completo
  - 15 reglas Prometheus (7 grupos)
  - Alertmanager configurado con 7 contact points
  - 5 reglas Grafana
  - Scripts de testing automatizados
  - Documentación completa
- ✅ Configuración RabbitMQ en todos los servicios
- ✅ Resolución de issues de red y conectividad

### � Sprint 3 - Planificado
- 📋 Implementación de eventos asíncronos con RabbitMQ
- 📋 Configuración centralizada activa con Config Server
- 📋 Documentación con Swagger/OpenAPI
- 📋 Tests automatizados (unit + integration)
- 📋 Circuit breaker con Resilience4j
- 📋 Rate limiting en API Gateway

### 📋 Backlog
- CI/CD pipeline con GitHub Actions
- Métricas de negocio personalizadas
- Dashboards de negocio en Grafana
- Logging centralizado con ELK Stack
- Performance testing con JMeter
- Security scanning automatizado

Ver [roadmap completo](./docs/03-puntos-de-mejora.md#6-roadmap-de-implementación).

## 👥 Contribución

1. Fork del repositorio
2. Crear rama para feature (`git checkout -b feature/amazing-feature`)
3. Commit cambios (`git commit -m 'feat: add amazing feature'`)
4. Push a la rama (`git push origin feature/amazing-feature`)
5. Abrir Pull Request

**Antes de contribuir**, revisa:
- [Checklist de Implementación](./docs/05-checklist-implementacion.md)
- [Puntos de Mejora](./docs/03-puntos-de-mejora.md)

## 📄 Licencia

Este proyecto está bajo licencia privada - ver archivo `LICENSE` para detalles.

## 📧 Contacto

- **Repositorio**: https://github.com/EmaSleal/gimnasio_backend
- **Autor**: EmaSleal

---

## 🎯 Para Nuevos Desarrolladores

### Primeros Pasos

1. **Lee primero** (orden recomendado):
   - [docs/00-resumen-ejecutivo.md](./docs/00-resumen-ejecutivo.md) - Visión general
   - [docs/01-analisis-arquitectura.md](./docs/01-analisis-arquitectura.md) - Arquitectura
   - [docs/fases.md](./docs/fases.md) - Sprints y fases

2. **Configura el entorno**:
   - Sigue la sección "Inicio Rápido" arriba
   - Verifica que todos los 12 contenedores estén UP
   - Accede a Grafana (localhost:3000) y explora los dashboards

3. **Entiende el monitoreo**:
   - [docs/monitoring/GRAFANA_DASHBOARDS.md](./docs/monitoring/GRAFANA_DASHBOARDS.md)
   - [docs/monitoring/ALERTAS.md](./docs/monitoring/ALERTAS.md)
   - Ejecuta `./scripts/test-alerts.ps1` para ver alertas en acción

4. **Implementa mejoras**:
   - Usa [docs/05-checklist-implementacion.md](./docs/05-checklist-implementacion.md)
   - Revisa [docs/03-puntos-de-mejora.md](./docs/03-puntos-de-mejora.md)

### Comandos Útiles

```powershell
# Ver estado de todos los servicios
docker-compose ps

# Logs de un servicio específico
docker-compose logs -f [servicio]

# Reiniciar un servicio
docker-compose restart [servicio]

# Reconstruir un servicio
docker-compose up -d --build [servicio]

# Ver métricas de Prometheus
curl http://localhost:9090/api/v1/targets

# Probar alertas
./scripts/test-alerts.ps1
```

### Recursos de Aprendizaje

**Microservicios:**
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Cloud: https://spring.io/projects/spring-cloud
- Eureka: https://spring.io/guides/gs/service-registration-and-discovery/

**Monitoreo:**
- Prometheus: https://prometheus.io/docs/
- Grafana: https://grafana.com/docs/
- Spring Boot Admin: https://codecentric.github.io/spring-boot-admin/

**Arquitectura:**
- Microservices Patterns: https://microservices.io/patterns/
- 12 Factor App: https://12factor.net/

**¡Bienvenido al equipo! 🚀**