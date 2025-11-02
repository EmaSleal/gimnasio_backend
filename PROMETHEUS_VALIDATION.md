# 🎯 Validación de Prometheus - Sistema de Gimnasio Backend

**Fecha**: 2025-02-01  
**Sprint**: Sprint 2 - Fase 3  
**Tarea**: 3.1 Dashboards de Monitoreo

---

## ✅ Estado Final: TODOS LOS SERVICIOS MONITOREADOS CORRECTAMENTE

### 📊 Prometheus Targets Status

| Service | Status | Endpoint | Port |
|---------|--------|----------|------|
| **admin-service** | ✅ UP | `/actuator/prometheus` | 9000 |
| **api-gateway** | ✅ UP | `/actuator/prometheus` | 8590 |
| **authentication** | ✅ UP | `/actuator/prometheus` | 8583 |
| **config-service** | ✅ UP | `/actuator/prometheus` | 8889 |
| **eureka-server** | ✅ UP | `/actuator/prometheus` | 8761 |
| **prometheus** | ✅ UP | `/metrics` | 9090 |
| **user-service** | ✅ UP | `/actuator/prometheus` | 8588 |
| **workout-service** | ✅ UP | `/actuator/prometheus` | 8586 |

**Total**: 8/8 targets UP (100%) 🎉

---

## 🔧 Problemas Encontrados y Soluciones

### Problema 1: api-gateway - 404 Not Found
**Síntoma**: 
- Endpoint `/actuator/prometheus` devolvía 404
- Error en Prometheus: "received unsupported status code 404"

**Causa Raíz**: 
- Faltaba dependencia `spring-boot-starter-actuator` en `api-gateway/pom.xml`
- A pesar de tener configuración de actuator en `application.yml`, los endpoints no estaban disponibles

**Solución Aplicada**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Comando de Aplicación**:
```powershell
docker-compose up -d --build api-gateway
docker-compose restart prometheus
```

**Resultado**: ✅ api-gateway ahora expone métricas correctamente

---

### Problema 2: config-service - Content-Type Incorrecto
**Síntoma**:
- Prometheus mostraba: "received unsupported Content-Type 'application/json'"
- El endpoint devolvía `Content-Type: application/json` en lugar de `text/plain`

**Causa Raíz**:
- Similar a api-gateway: faltaba dependencia `spring-boot-starter-actuator`
- Solo tenía `micrometer-registry-prometheus` que no es suficiente

**Intento Inicial (FALLIDO)**:
```yaml
# monitoring/prometheus/prometheus.yml
- job_name: 'config-service'
  params:
    format: ['prometheus']  # ❌ No funcionó
```

**Solución Correcta**:
```xml
<!-- config-service/pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Comando de Aplicación**:
```powershell
docker-compose up -d --build config-service
docker-compose restart prometheus
```

**Resultado**: ✅ config-service ahora devuelve `Content-Type: text/plain; version=0.0.4`

---

## 📋 Lecciones Aprendidas

### 1. **Actuator es Obligatorio para Métricas**
- `micrometer-registry-prometheus` solo registra métricas
- `spring-boot-starter-actuator` expone los endpoints HTTP
- **Ambos son necesarios** para que Prometheus pueda scrapear

### 2. **Spring Cloud Gateway Necesita Actuator Explícito**
- A pesar de ser un componente Spring Boot, Gateway no incluye actuator automáticamente
- Debe declararse explícitamente en `pom.xml`

### 3. **Reinicio de Prometheus Requerido**
- Cualquier cambio en `prometheus.yml` requiere reinicio del contenedor
- Comando: `docker-compose restart prometheus`

### 4. **Verificación Multi-Nivel**
```powershell
# 1. Verificar endpoint directamente
curl http://localhost:8590/actuator/prometheus

# 2. Verificar Content-Type
curl -I http://localhost:8889/actuator/prometheus

# 3. Verificar targets en Prometheus
curl http://localhost:9090/api/v1/targets | ConvertFrom-Json
```

---

## 🎯 Configuración de Prometheus

### Global Settings
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s
  scrape_timeout: 10s
  external_labels:
    cluster: 'gym-microservices'
    environment: 'development'
```

### Scrape Jobs Configurados

#### Infrastructure Services (3)
1. **prometheus** - Auto-monitoreo (localhost:9090)
2. **eureka-server** - Service Discovery (8761)
3. **config-service** - Configuración Centralizada (8889)

#### Business Services (3)
4. **authentication** - Autenticación JWT (8583) + PostgreSQL
5. **user-service** - Gestión de Usuarios (8588) + PostgreSQL + RabbitMQ
6. **workout-service** - Gestión de Ejercicios (8586) + PostgreSQL + RabbitMQ

#### Edge & Monitoring (2)
7. **api-gateway** - Enrutamiento y Balanceo (8590)
8. **admin-service** - Spring Boot Admin (9000)

---

## 📊 Métricas Disponibles

### JVM Metrics
- `jvm_memory_used_bytes` - Memoria usada por la JVM
- `jvm_threads_live_threads` - Threads activos
- `jvm_gc_pause_seconds` - Tiempo de pausa del GC

### HTTP Metrics
- `http_server_requests_seconds` - Latencia de requests
- `http_server_requests_seconds_count` - Total de requests
- `http_server_requests_seconds_sum` - Suma de tiempos

### System Metrics
- `system_cpu_usage` - Uso de CPU del sistema
- `process_cpu_usage` - Uso de CPU del proceso
- `system_load_average_1m` - Carga promedio

### Database Metrics (servicios con JPA)
- `hikaricp_connections_active` - Conexiones activas
- `hikaricp_connections_idle` - Conexiones inactivas
- `hikaricp_connections_pending` - Conexiones pendientes

### Gateway Metrics (api-gateway)
- `spring_cloud_gateway_requests_seconds` - Latencia de rutas
- `resilience4j_circuitbreaker_state` - Estado del circuit breaker

---

## 🔗 URLs de Acceso

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Prometheus UI** | http://localhost:9090 | - |
| **Prometheus Targets** | http://localhost:9090/targets | - |
| **Prometheus Graph** | http://localhost:9090/graph | - |
| **Grafana** | http://localhost:3000 | admin / admin |
| **Eureka Dashboard** | http://localhost:8761 | - |
| **RabbitMQ Management** | http://localhost:15672 | guest / guest |

---

## 🧪 Comandos de Verificación

### Verificar Todos los Targets
```powershell
curl http://localhost:9090/api/v1/targets | ConvertFrom-Json | `
  Select-Object -ExpandProperty data | `
  Select-Object -ExpandProperty activeTargets | `
  Select-Object scrapePool, health | Format-Table -AutoSize
```

### Verificar Endpoint Específico
```powershell
# Health check
curl http://localhost:8590/actuator/health

# Métricas Prometheus
curl http://localhost:8590/actuator/prometheus

# Content-Type header
curl -I http://localhost:8889/actuator/prometheus
```

### Verificar Grafana
```powershell
curl http://localhost:3000/api/health
```

### Verificar Contenedores
```powershell
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

---

## 🎯 Siguiente Fase: Alertas (Tarea 3.2)

### Alertas a Configurar
1. **Service Down Alert**
   - Condición: Target DOWN por >1 minuto
   - Severidad: Critical

2. **High Memory Usage Alert**
   - Condición: `jvm_memory_used_bytes / jvm_memory_max_bytes > 0.9`
   - Severidad: Warning

3. **High Error Rate Alert**
   - Condición: `rate(http_server_requests_seconds_count{status=~"5.."}[1m]) > 10`
   - Severidad: Critical

4. **High Latency Alert**
   - Condición: `histogram_quantile(0.95, http_server_requests_seconds) > 1s`
   - Severidad: Warning

---

## ✅ Estado del Sprint 2 - Fase 3

- [x] **Tarea 3.1**: Dashboards de Monitoreo (COMPLETO)
  - [x] 8 servicios exponen métricas a Prometheus
  - [x] 4 dashboards configurados en Grafana
  - [x] Todos los targets UP (100%)
  
- [ ] **Tarea 3.2**: Configuración de Alertas (PENDIENTE)
  - [ ] Definir reglas de alertas en Prometheus
  - [ ] Configurar contact points en Grafana
  - [ ] Probar alertas con escenarios reales
  
- [ ] **Tarea 3.3**: Documentación Final (PENDIENTE)
  - [ ] Actualizar README principal
  - [ ] Documentar alertas configuradas
  - [ ] Crear guía de troubleshooting

---

## 📝 Cambios Realizados en el Código

### Archivos Modificados

1. **api-gateway/pom.xml**
   - Agregada dependencia: `spring-boot-starter-actuator`

2. **config-service/pom.xml**
   - Agregada dependencia: `spring-boot-starter-actuator`

3. **monitoring/prometheus/prometheus.yml**
   - Sin cambios permanentes (configuración ya correcta)

### Servicios Reconstruidos
```bash
docker-compose up -d --build api-gateway
docker-compose up -d --build config-service
docker-compose restart prometheus
```

---

## 🏆 Conclusión

**Estado**: ✅ VALIDACIÓN EXITOSA  
**Targets Monitoreados**: 8/8 (100%)  
**Dashboards Disponibles**: 4  
**Prometheus**: Operacional  
**Grafana**: Operacional  

El sistema de monitoreo está **completamente funcional** y listo para la configuración de alertas.

---

*Documento generado automáticamente durante la validación del sistema de monitoreo*  
*Gimnasio Backend - Microservices Architecture*
