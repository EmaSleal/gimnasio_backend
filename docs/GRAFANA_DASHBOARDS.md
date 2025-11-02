# 📊 Guía de Dashboards Grafana - Sistema Gimnasio

**Última Actualización**: 1 de noviembre de 2025  
**Versión Grafana**: Latest (10.x+)  
**Datasource**: Prometheus

---

## 📋 Índice

1. [Dashboards Disponibles](#dashboards-disponibles)
2. [Acceso a Grafana](#acceso-a-grafana)
3. [Dashboard: Spring Boot Statistics (Grafana Labs)](#dashboard-spring-boot-statistics-grafana-labs)
4. [Dashboard: Spring Boot Overview](#dashboard-spring-boot-overview)
5. [Dashboard: JVM Details](#dashboard-jvm-details)
6. [Dashboard: API Gateway Metrics](#dashboard-api-gateway-metrics)
7. [Queries Útiles](#queries-útiles)
8. [Troubleshooting](#troubleshooting)

---

## 📊 Dashboards Disponibles

El sistema cuenta con **4 dashboards principales** pre-configurados:

| Dashboard | UID | Descripción | Servicios Monitoreados |
|-----------|-----|-------------|------------------------|
| **🏆 Spring Boot Statistics** | `gym-spring-boot-stats` | Dashboard profesional completo de Grafana Labs (ID 10280) | Todos (7 servicios) |
| **Spring Boot Overview** | `gym-spring-boot-overview` | Vista general de todos los servicios | Todos (7 servicios) |
| **JVM Details** | `gym-jvm-details` | Métricas detalladas de la JVM | Todos (7 servicios) |
| **API Gateway Metrics** | `gym-api-gateway` | Métricas específicas del gateway | api-gateway |

### 🏆 Dashboard Destacado: Spring Boot Statistics

Este dashboard profesional proviene de **Grafana Labs** (Dashboard ID: 10280) y ha sido adaptado específicamente para nuestro sistema. Ofrece:

- **Variables Template**: Selección dinámica de Application e Instance
- **Métricas Completas de JVM**: Heap, Non-Heap, GC, Threads, Classes
- **Métricas HTTP**: Request rate, duration, status codes
- **Logging**: Métricas de Logback por nivel (ERROR, WARN, INFO, DEBUG, TRACE)
- **HikariCP**: Pool de conexiones de base de datos
- **Tomcat**: Sessions, threads, connectors
- **Compatibilidad**: Spring Boot 3.x + Micrometer + Prometheus

**Recomendación**: Usar este dashboard como principal para monitoreo detallado.

---

## 🔐 Acceso a Grafana

### Credenciales

```
URL: http://localhost:3000
Usuario: admin
Password: Ver archivo .env (GRAFANA_ADMIN_PASSWORD)
```

**⚠️ Seguridad**:
- El password por defecto es: `grafana_gym_2024!`
- Se recomienda cambiarlo después del primer login
- Nunca usar credenciales por defecto en producción

### Primer Login

1. Abrir http://localhost:3000
2. Ingresar credenciales
3. Navegar a **Dashboards** → **Browse**
4. Buscar carpeta **"Gym System"**
5. Seleccionar el dashboard deseado

---

## 1️⃣ Dashboard: Spring Boot Overview

**Archivo**: `gym-spring-boot-overview.json`  
**UID**: `gym-spring-boot-overview`  
**Refresh**: 5 segundos  
**Tags**: `spring-boot`, `micrometer`, `gym-system`

### Descripción

Dashboard principal para monitorear el estado general de todos los microservicios del sistema. Incluye métricas básicas de salud, rendimiento y recursos.

### Paneles Incluidos

#### 1. **Service Status (UP/DOWN)**
- **Tipo**: Time Series
- **Métrica**: `up{job=~".*"}`
- **Descripción**: Estado de cada servicio (1 = UP, 0 = DOWN)
- **Uso**: Identificar servicios caídos rápidamente

#### 2. **Services Status Gauge**
- **Tipo**: Gauge
- **Métrica**: `up{job=~".*"}`
- **Descripción**: Indicador visual del estado de cada servicio
- **Colores**: 
  - Verde (1): Servicio funcionando
  - Rojo (0): Servicio caído

#### 3. **JVM Memory - Heap Usage**
- **Tipo**: Time Series
- **Métricas**: 
  - `jvm_memory_used_bytes{area="heap"}` - Memoria usada
  - `jvm_memory_max_bytes{area="heap"}` - Memoria máxima
- **Descripción**: Uso de memoria heap por servicio
- **Calcs**: Mean, Last, Max

#### 4. **JVM Threads - Live**
- **Tipo**: Time Series
- **Métrica**: `jvm_threads_live_threads`
- **Descripción**: Número de threads activos por servicio
- **Uso**: Detectar thread leaks o picos de concurrencia

#### 5. **CPU Usage**
- **Tipo**: Time Series
- **Métricas**:
  - `system_cpu_usage` - CPU del sistema
  - `process_cpu_usage` - CPU del proceso
- **Descripción**: Uso de CPU del sistema y de cada proceso Java
- **Unidad**: Porcentaje (0-1)

#### 6. **HTTP Requests Rate**
- **Tipo**: Time Series
- **Métrica**: `rate(http_server_requests_seconds_count[1m])`
- **Descripción**: Tasa de requests HTTP por URI y status
- **Unidad**: Requests por segundo
- **Calcs**: Mean, Last, Max

### Uso Recomendado

- **Dashboard principal** para monitoreo diario
- **Ideal para**: Detectar problemas generales en cualquier servicio
- **Frecuencia de revisión**: Cada hora en operación normal
- **Alertas sugeridas**: Service DOWN, Memory > 90%, CPU > 80%

---

## 2️⃣ Dashboard: JVM Details

**Archivo**: `gym-jvm-details.json`  
**UID**: `gym-jvm-details`  
**Refresh**: 10 segundos  
**Tags**: `jvm`, `micrometer`, `gym-system`, `java`

### Descripción

Dashboard especializado para análisis detallado de la JVM (Java Virtual Machine). Útil para debugging de problemas de memoria, garbage collection y performance.

### Paneles Incluidos

#### 1. **JVM Heap Memory Usage (%)**
- **Tipo**: Time Series con Gradiente
- **Métrica**: `jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}`
- **Descripción**: Porcentaje de uso de memoria heap
- **Umbrales**:
  - Verde: < 70%
  - Amarillo: 70-90%
  - Rojo: > 90%
- **Uso**: Identificar memory leaks o necesidad de aumentar heap

#### 2. **JVM Heap - Memory Pools**
- **Tipo**: Stacked Time Series
- **Métricas**:
  - Eden Space: `jvm_memory_used_bytes{id=~".*Eden.*"}`
  - Survivor Space: `jvm_memory_used_bytes{id=~".*Survivor.*"}`
  - Old Generation: `jvm_memory_used_bytes{id=~".*Old.*"}`
- **Descripción**: Distribución de memoria en las diferentes generaciones
- **Uso**: Análisis de garbage collection y promoción de objetos

#### 3. **JVM Non-Heap Memory**
- **Tipo**: Time Series
- **Métrica**: `jvm_memory_used_bytes{area="nonheap"}`
- **Descripción**: Memoria no-heap (Metaspace, Code Cache, etc.)
- **Uso**: Monitorear crecimiento de classes loaded y compiled code

#### 4. **Garbage Collection - Pause Rate**
- **Tipo**: Bar Chart
- **Métrica**: `rate(jvm_gc_pause_seconds_count[1m])`
- **Descripción**: Frecuencia de pausas del GC
- **Uso**: Detectar GC excesivo que impacta performance

#### 5. **Garbage Collection - Average Pause Duration**
- **Tipo**: Time Series
- **Métrica**: `rate(jvm_gc_pause_seconds_sum[1m]) / rate(jvm_gc_pause_seconds_count[1m])`
- **Descripción**: Duración promedio de cada pausa del GC
- **Unidad**: Segundos
- **Alerta**: Si > 1 segundo, investigar

#### 6. **JVM Threads**
- **Tipo**: Time Series
- **Métricas**:
  - Live Threads: `jvm_threads_live_threads`
  - Daemon Threads: `jvm_threads_daemon_threads`
  - Peak Threads: `jvm_threads_peak_threads` (línea punteada)
- **Uso**: Detectar thread leaks o deadlocks

#### 7. **JVM Classes**
- **Tipo**: Time Series
- **Métricas**:
  - Loaded Classes: `jvm_classes_loaded_classes`
  - Unloaded Classes Rate: `rate(jvm_classes_unloaded_classes_total[1m])`
- **Uso**: Monitorear class loading (importante para hot reload)

### Uso Recomendado

- **Para**: Investigación de problemas de performance
- **Cuando**: Memory leaks, GC excesivo, degradación de performance
- **Frecuencia**: Solo cuando hay problemas
- **Combinación**: Usar junto con Admin Service para thread dumps

---

## 3️⃣ Dashboard: API Gateway Metrics

**Archivo**: `gym-api-gateway.json`  
**UID**: `gym-api-gateway`  
**Refresh**: 10 segundos  
**Tags**: `api-gateway`, `spring-cloud`, `gym-system`, `routing`

### Descripción

Dashboard especializado para el API Gateway. Monitorea enrutamiento, latencia, errores y distribución de tráfico entre servicios.

### Paneles Incluidos

#### 1. **Request Rate**
- **Tipo**: Time Series
- **Métrica**: `rate(http_server_requests_seconds_count{application="api-gateway"}[1m])`
- **Descripción**: Tasa de requests por URI, método y status
- **Uso**: Identificar endpoints más usados

#### 2. **Response Time (Percentiles)**
- **Tipo**: Time Series con Thresholds
- **Métricas**:
  - P50: `histogram_quantile(0.50, ...)`
  - P95: `histogram_quantile(0.95, ...)`
  - P99: `histogram_quantile(0.99, ...)`
- **Umbrales**:
  - Verde: < 500ms
  - Amarillo: 500ms - 1s
  - Rojo: > 1s
- **Uso**: SLA monitoring, detectar latencia

#### 3. **HTTP Status Codes (%)**
- **Tipo**: Stacked Percentage Time Series
- **Métricas**:
  - 2xx Success (Verde)
  - 4xx Client Errors (Amarillo)
  - 5xx Server Errors (Rojo)
- **Descripción**: Distribución porcentual de status codes
- **Uso**: Health check rápido del sistema

#### 4. **Error Rate (5xx)**
- **Tipo**: Gauge
- **Métrica**: `sum(rate(...{status=~"5.."} ...)) / sum(rate(...))`
- **Umbrales**:
  - Verde: < 1%
  - Amarillo: 1-5%
  - Rojo: > 5%
- **Uso**: Alerta crítica de errores del servidor

#### 5. **Requests per Route**
- **Tipo**: Time Series
- **Métrica**: `sum by (uri) (rate(http_server_requests_seconds_count[1m]))`
- **Descripción**: Distribución de tráfico entre rutas
- **Uso**: Identificar rutas populares, balanceo de carga

#### 6. **Top Routes (Table)**
- **Tipo**: Table
- **Métrica**: `topk(20, sum by (uri, method, status) (...))`
- **Columnas**: URI, Method, Status, Requests/sec
- **Uso**: Análisis detallado de endpoints más usados
- **Color Coding**: Background por status code

### Uso Recomendado

- **Para**: Monitoreo de tráfico y performance del gateway
- **Ideal para**: DevOps, análisis de tráfico, troubleshooting
- **Frecuencia**: Continuamente en producción
- **Alertas**: Error Rate > 5%, P95 > 1s, Service DOWN

---

## 🔍 Queries Útiles

### Queries PromQL Comunes

#### Estado de Servicios
```promql
# Verificar si todos los servicios están UP
up{job=~".*"}

# Contar servicios UP
count(up{job=~".*"} == 1)

# Servicios DOWN
up{job=~".*"} == 0
```

#### Memoria
```promql
# Porcentaje de uso de heap
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100

# Memoria heap por servicio
sum by (application) (jvm_memory_used_bytes{area="heap"})

# Servicios con memoria > 80%
(jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) > 0.8
```

#### HTTP Requests
```promql
# Total requests por segundo
sum(rate(http_server_requests_seconds_count[1m]))

# Requests con error 5xx
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))

# Latencia p95 por servicio
histogram_quantile(0.95, sum by (application, le) (rate(http_server_requests_seconds_bucket[5m])))
```

#### CPU y Threads
```promql
# CPU usage por servicio
process_cpu_usage * 100

# Threads por servicio
jvm_threads_live_threads
```

---

## 🐛 Troubleshooting

### Dashboard no muestra datos

**Síntoma**: Paneles vacíos o "No Data"

**Soluciones**:
1. Verificar que Prometheus esté scrapeando:
   ```
   http://localhost:9090/targets
   ```
2. Verificar datasource en Grafana:
   - Configuration → Data Sources → Prometheus → Test
3. Verificar que los servicios exponen métricas:
   ```bash
   curl http://localhost:8761/actuator/prometheus
   ```
4. Ajustar rango de tiempo (esquina superior derecha)

### Métricas con nombres diferentes

**Síntoma**: Error "metric not found"

**Soluciones**:
1. Verificar nombres de métricas en Prometheus:
   ```
   http://localhost:9090/graph
   ```
2. Usar Explore en Grafana para descubrir métricas:
   - Metrics browser → Buscar `jvm_` o `http_`
3. Ajustar queries en dashboard según nombres reales

### Dashboards no cargan automáticamente

**Síntoma**: Carpeta "Gym System" vacía

**Soluciones**:
1. Verificar volumen montado correctamente:
   ```bash
   docker-compose exec grafana ls /var/lib/grafana/dashboards
   ```
2. Verificar provisioning:
   ```bash
   docker-compose logs grafana | grep -i provision
   ```
3. Reiniciar Grafana:
   ```bash
   docker-compose restart grafana
   ```

### Performance lento

**Síntoma**: Dashboards cargan lento

**Soluciones**:
1. Aumentar intervalo de refresh (cambiar de 5s a 30s)
2. Reducir rango de tiempo (de 15m a 5m)
3. Usar queries más eficientes (agregar `by` para reducir series)
4. Aumentar recursos de Grafana en docker-compose

---

## 🏆 Dashboard: Spring Boot Statistics (Grafana Labs)

### Descripción General

Dashboard profesional adaptado de **Grafana Labs (ID: 10280)** específicamente optimizado para Spring Boot 3.x con Micrometer y Prometheus.

**Características principales**:
- ✅ 40+ paneles organizados por categorías
- ✅ Variables template para filtrar por Application/Instance
- ✅ Compatible con Spring Boot 3.x + Micrometer
- ✅ Auto-refresh configurable
- ✅ Totalmente provisionado automáticamente

### Secciones del Dashboard

#### 1. **Basic Statistics**
- Uptime del servicio
- Start time
- Heap usado / Max heap
- Non-Heap usado
- Threads activos
- Classes cargadas
- GC Pause time

#### 2. **JVM Memory**
- Heap Memory: Eden Space, Survivor Space, Old Gen
- Non-Heap Memory: Metaspace, Code Cache, Compressed Class Space
- Gráficos de uso y máximo para cada pool

#### 3. **JVM Miscellaneous**
- Thread states (runnable, blocked, waiting, timed-waiting)
- Classes loaded/unloaded
- ClassLoader hierarchy

#### 4. **Garbage Collection**
- GC pause duration por collector (G1 Young/Old Generation)
- GC pause count
- Memory promoted/allocated
- GC overhead percentage

#### 5. **HTTP Requests**
- Request rate (requests/segundo)
- Response time percentiles (p50, p75, p95, p99)
- Status codes distribution (2xx, 3xx, 4xx, 5xx)
- Top endpoints por request count
- Top slow endpoints

#### 6. **Logging (Logback)**
- Log rate por nivel (ERROR, WARN, INFO, DEBUG, TRACE)
- Tendencias de logging
- Detección de error spikes

#### 7. **Database (HikariCP)**
- Active connections
- Idle connections
- Pending threads
- Connection timeout
- Connection acquire time
- Connection usage time
- Pool utilization %

#### 8. **Tomcat Metrics**
- Active sessions
- Session creation rate
- Busy threads
- Total threads
- Max threads
- Connector statistics

### Variables Template

El dashboard usa variables que permiten filtrar dinámicamente:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `$application` | Nombre del servicio Spring Boot | `user-service`, `authentication`, `api-gateway` |
| `$instance` | Instancia específica del servicio | `user-service:8588`, `api-gateway:8590` |
| `$hikaricp` | Pool de HikariCP (si aplica) | `HikariPool-1` |
| `$memory_pool_heap` | Pool de memoria heap | `G1 Old Gen`, `G1 Eden Space` |
| `$memory_pool_nonheap` | Pool de memoria non-heap | `Metaspace`, `CodeCache` |

### Queries Principales

#### Uptime del servicio
```promql
process_uptime_seconds{application="$application", instance="$instance"}
```

#### Heap Memory Usage
```promql
jvm_memory_used_bytes{application="$application", instance="$instance", area="heap"}
```

#### HTTP Request Rate
```promql
rate(http_server_requests_seconds_count{application="$application", instance="$instance"}[1m])
```

#### Response Time p95
```promql
histogram_quantile(0.95, 
  rate(http_server_requests_seconds_bucket{application="$application", instance="$instance"}[1m])
)
```

#### GC Pause Time
```promql
rate(jvm_gc_pause_seconds_sum{application="$application", instance="$instance"}[1m])
```

#### Active DB Connections
```promql
hikaricp_connections_active{application="$application", instance="$instance", pool="$hikaricp"}
```

### Uso Recomendado

1. **Monitoreo General**: Seleccionar `All` en Application para ver todos los servicios
2. **Debug de Servicio Específico**: Seleccionar un `application` particular
3. **Análisis de Instancia**: Seleccionar `instance` específica para troubleshooting
4. **Análisis de Performance**: Enfocarse en secciones HTTP y JVM Memory
5. **Detección de Fugas de Memoria**: Monitorear tendencias en JVM Memory
6. **Database Issues**: Revisar sección HikariCP

### Alertas Sugeridas

Basándose en este dashboard, se recomienda configurar alertas para:

- ✅ Heap Memory > 90%
- ✅ Response Time p95 > 1s
- ✅ Error Rate > 5%
- ✅ GC Pause Time > 500ms
- ✅ DB Connection Pool > 80% utilización
- ✅ ERROR logs > 10/min

---

## 📚 Referencias

### Documentación Oficial
- [Grafana Dashboards](https://grafana.com/docs/grafana/latest/dashboards/)
- [PromQL Cheat Sheet](https://promlabs.com/promql-cheat-sheet/)
- [Micrometer Metrics](https://micrometer.io/docs/concepts#_naming_meters)

### Dashboards Públicos
- [Grafana Dashboard Library](https://grafana.com/grafana/dashboards/)
- [Spring Boot Statistics (10280)](https://grafana.com/grafana/dashboards/10280)
- [Spring Boot Dashboards](https://grafana.com/grafana/dashboards/?search=spring+boot)

### PromQL
- [PromQL Tutorial](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [Query Functions](https://prometheus.io/docs/prometheus/latest/querying/functions/)

---

## 🎯 Próximos Pasos

1. **Personalizar dashboards** según necesidades específicas
2. **Configurar alertas** basadas en umbrales (Fase 3 - Tarea 3.2)
3. **Crear dashboards custom** para métricas de negocio
4. **Documentar SLOs/SLIs** del sistema

---

**Creado**: 1 de noviembre de 2025  
**Mantenedor**: Equipo DevOps  
**Feedback**: Reportar problemas en el repositorio
