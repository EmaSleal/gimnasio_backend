# Sprint 2: Observabilidad Avanzada

**Período**: 1-5 de noviembre de 2025 (Iniciando después de Sprint 1 ✅)  
**Duración Estimada**: 3-4 días (12-16 horas)  
**Objetivo**: Implementar stack completo de métricas y monitoreo

---

## 📊 Estado del Sprint 1 (Completado)

✅ **Sprint 1 - COMPLETADO 100%** (17h/24h estimadas)
- ✅ Admin Service implementado en puerto 9000
- ✅ Actuator securizado en todos los servicios
- ✅ Flyway en user-service y workout-service
- ✅ Variables de entorno configuradas (`.env`)
- ✅ Sin IPs hardcodeadas (configuración portable)

**Servicios Activos**:
1. eureka-server (8761)
2. config-service (8888)
3. authentication (8589)
4. user-service (8588)
5. workout-service (8586)
6. api-gateway (8590)
7. **admin-service (9000)** ⭐ NUEVO en Sprint 1

---

## 🎯 Objetivo del Sprint 2

Complementar el Admin Service con un sistema de observabilidad robusto que permita:
- ✅ Monitorear métricas en tiempo real de todos los servicios (complementando Admin Service)
- ✅ Crear dashboards visuales de rendimiento (Grafana)
- ✅ Configurar alertas proactivas
- ✅ Analizar tendencias históricas (datos persistentes)
- ✅ Correlacionar trazas con métricas (Zipkin)

---

## 📊 Arquitectura de Observabilidad

```
┌─────────────────────────────────────────────────────────────┐
│              OBSERVABILIDAD COMPLETA (Sprint 1 + 2)          │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐        │
│  │ Eureka      │   │ Gateway     │   │ Auth        │        │
│  │ Config      │   │ User        │   │ Workout     │        │
│  └──────┬──────┘   └──────┬──────┘   └──────┬──────┘        │
│         │                 │                 │                │
│         └─────────────────┴─────────────────┘                │
│                          │                                   │
│                    Micrometer                                │
│                          │                                   │
│         ┌────────────────┼────────────────┐                  │
│         │                │                │                  │
│         ▼                ▼                ▼                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │  Zipkin     │  │ Admin Svc   │  │ Prometheus  │          │
│  │  :9411      │  │  :9000 ✅   │  │   :9090     │          │
│  └─────────────┘  └─────────────┘  └──────┬──────┘          │
│                                            │                 │
│  Distributed        Real-time              ▼                 │
│  Tracing           Monitoring       ┌─────────────┐         │
│  - Request flows   - Health checks  │  Grafana    │         │
│  - Latency         - Metrics        │   :3000     │         │
│  - Error tracking  - Logs           └─────────────┘         │
│                    - Endpoints                               │
│                                                               │
│                          SPRINT 1 ✅ | SPRINT 2 🔄          │
│                   Admin Service      | Prometheus/Grafana    │
└─────────────────────────────────────────────────────────────┘
```

**Nota**: El Admin Service (Sprint 1) ya proporciona monitoreo básico en tiempo real.
Sprint 2 añade métricas históricas, dashboards personalizados y alerting avanzado.

---

## 🗂️ Tareas Planificadas

### Tarea 1: 📊 Configurar Prometheus (P1 - 4h)

**Descripción**: Agregar Prometheus al docker-compose y configurar scraping de métricas

**Clasificación**: `📊 OBSERVABILIDAD` - `P1` - `4 horas`

**Pasos**:

1. **Agregar servicio Prometheus a docker-compose.yml**:
```yaml
  prometheus:
    image: prom/prometheus:v2.47.0
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus-data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
    networks:
      - spring-cloud-network
    restart: unless-stopped

volumes:
  prometheus-data:
```

2. **Crear archivo monitoring/prometheus/prometheus.yml**:
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'eureka-server'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['eureka-server:8761']

  - job_name: 'config-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['config-service:8888']

  - job_name: 'api-gateway'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['api-gateway:8590']  # ✅ Puerto correcto

  - job_name: 'authentication-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['authentication-service:8589']  # ✅ Puerto correcto

  - job_name: 'user-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['user-service:8588']

  - job_name: 'workout-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['workout-service:8586']

  - job_name: 'admin-service'  # ⭐ NUEVO - Admin Service del Sprint 1
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['admin-service:9000']
```

3. **Agregar dependencia Micrometer Registry en cada servicio**:
```xml
<!-- pom.xml de cada microservicio -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Nota**: Verificar que Actuator ya expone métricas (configurado en Sprint 1).

4. **Actualizar application.yml si es necesario**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus  # ✅ Ya configurado en Sprint 1
  endpoint:
    health:
      show-details: when-authorized  # ✅ Ya configurado en Sprint 1
  metrics:
    export:
      prometheus:
        enabled: true  # ⭐ NUEVO - Agregar solo esta sección
```

**Validación**:
- [ ] Prometheus accesible en http://localhost:9090
- [ ] Dashboard Prometheus muestra **7 targets UP** (incluyendo admin-service)
- [ ] Endpoint /actuator/prometheus devuelve métricas en formato Prometheus
- [ ] Queries básicas funcionan: `up`, `jvm_memory_used_bytes`
- [ ] Admin Service también visible en Prometheus

---

### Tarea 2: 📈 Configurar Grafana (P1 - 3h)

**Descripción**: Instalar Grafana y conectar con Prometheus

**Clasificación**: `📊 OBSERVABILIDAD` - `P1` - `3 horas`

**Pasos**:

1. **Agregar servicio Grafana a docker-compose.yml**:
```yaml
  grafana:
    image: grafana/grafana:10.1.0
    container_name: grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_PASSWORD:-admin}
      - GF_INSTALL_PLUGINS=grafana-clock-panel,grafana-simple-json-datasource
    volumes:
      - grafana-data:/var/lib/grafana
      - ./monitoring/grafana/provisioning:/etc/grafana/provisioning
    networks:
      - spring-cloud-network
    depends_on:
      - prometheus
    restart: unless-stopped

volumes:
  grafana-data:
```

2. **Crear datasource automático (monitoring/grafana/provisioning/datasources/prometheus.yml)**:
```yaml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: true
```

3. **Agregar GRAFANA_PASSWORD a .env**:
```bash
# Grafana Admin Password (agregar a archivo .env existente)
GRAFANA_PASSWORD=your_secure_grafana_password
```

**Nota**: El archivo `.env` ya existe desde Sprint 1. Solo agregar esta variable.

**Validación**:
- [ ] Grafana accesible en http://localhost:3000
- [ ] Login con admin/admin (cambiar password)
- [ ] Datasource Prometheus conectado exitosamente
- [ ] Puede ejecutar queries contra Prometheus

---

### Tarea 3: 📊 Importar Dashboards Predefinidos (P2 - 2h)

**Descripción**: Importar dashboards de Spring Boot, JVM y PostgreSQL

**Clasificación**: `📊 OBSERVABILIDAD` - `P2` - `2 horas`

**Dashboards Recomendados**:

1. **Spring Boot 2.1 Statistics** (ID: 10280)
   - Métricas generales de Spring Boot
   - HTTP requests, latency, throughput

2. **JVM (Micrometer)** (ID: 4701)
   - Memory usage (Heap, Non-Heap)
   - Garbage Collection
   - Thread states

3. **Spring Cloud Gateway** (ID: 11506)
   - Gateway routing metrics
   - Response times por ruta

**Pasos**:

1. **Crear dashboards provisioning (monitoring/grafana/provisioning/dashboards/dashboards.yml)**:
```yaml
apiVersion: 1

providers:
  - name: 'default'
    orgId: 1
    folder: 'Spring Boot'
    type: file
    disableDeletion: false
    updateIntervalSeconds: 10
    allowUiUpdates: true
    options:
      path: /etc/grafana/provisioning/dashboards
```

2. **Importar dashboards manualmente** (primera vez):
   - Dashboard → Import → ID: 10280 → Select Prometheus
   - Dashboard → Import → ID: 4701 → Select Prometheus
   - Dashboard → Import → ID: 11506 → Select Prometheus

3. **Exportar y guardar JSON** para futuras instalaciones

**Validación**:
- [ ] Al menos 3 dashboards importados
- [ ] Dashboards muestran datos en tiempo real
- [ ] Gráficas de JVM memory funcionan
- [ ] HTTP metrics visibles

---

### Tarea 4: 🔔 Configurar Alertas Básicas (P2 - 3h)

**Descripción**: Crear alertas en Grafana para condiciones críticas

**Clasificación**: `📊 OBSERVABILIDAD` - `P2` - `3 horas`

**Alertas Propuestas**:

1. **Servicio Caído**
```
Condición: up == 0
Duración: > 1 minuto
Severidad: Critical
```

2. **Memoria JVM Alta**
```
Condición: jvm_memory_used_bytes / jvm_memory_max_bytes > 0.9
Duración: > 5 minutos
Severidad: Warning
```

3. **HTTP Error Rate Alto**
```
Condición: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 10
Duración: > 2 minutos
Severidad: Critical
```

4. **Latencia Alta**
```
Condición: histogram_quantile(0.95, http_server_requests_seconds) > 1
Duración: > 3 minutos
Severidad: Warning
```

**Pasos**:

1. Crear Contact Point (Alerting → Contact points)
2. Crear Notification Policy
3. Configurar alertas en dashboards
4. Probar alertas manualmente

**Validación**:
- [ ] Al menos 3 alertas configuradas
- [ ] Alertas se disparan correctamente
- [ ] Notificaciones llegan (email/slack)

---

## � Integración Admin Service + Prometheus/Grafana

### Comparación de Herramientas

| Característica | Admin Service (Sprint 1) | Prometheus + Grafana (Sprint 2) |
|----------------|-------------------------|----------------------------------|
| **Propósito** | Monitoreo en tiempo real | Métricas históricas y alerting |
| **Puerto** | 9000 | 9090 (Prometheus) + 3000 (Grafana) |
| **Descubrimiento** | Vía Eureka (automático) | Configuración estática |
| **Retención** | No persistente (se pierde al reiniciar) | Persistente (TSDB) |
| **Dashboards** | Web UI básico | Dashboards personalizables avanzados |
| **Alertas** | Notificaciones simples | Alerting robusto con múltiples canales |
| **Logs** | Acceso en tiempo real | No incluido (considerar ELK para Sprint 3) |
| **Thread/Heap Dumps** | ✅ Sí | ❌ No |
| **Cambio de Log Levels** | ✅ Sí | ❌ No |
| **Métricas Custom** | ✅ Vía Actuator | ✅ Vía Micrometer |

### Casos de Uso Complementarios

**Usar Admin Service cuando**:
- ✅ Necesitas ver estado actual de servicios rápidamente
- ✅ Quieres cambiar niveles de logging sin reiniciar
- ✅ Necesitas hacer thread dump o heap dump
- ✅ Debugging de problemas en tiempo real

**Usar Prometheus/Grafana cuando**:
- ✅ Necesitas ver tendencias históricas (última semana, mes, etc.)
- ✅ Quieres dashboards personalizados con múltiples métricas
- ✅ Necesitas alertas proactivas (antes de que falle)
- ✅ Análisis de capacidad y planificación

**Estrategia Recomendada**: Usar ambos en conjunto
- Admin Service para operaciones del día a día
- Prometheus/Grafana para análisis y alerting

---

## �📅 Planificación Tentativa

### Día 1 (4h)
- ✅ Tarea 1: Prometheus setup completo

### Día 2 (3h)
- ✅ Tarea 2: Grafana setup y conexión

### Día 3 (3h)
- ✅ Tarea 3: Importar dashboards
- ✅ Inicio Tarea 4: Alertas

### Día 4 (2h)
- ✅ Completar Tarea 4
- ✅ Documentación
- ✅ Retrospectiva

**Total Estimado**: 12-16 horas

---

## ✅ Criterios de Finalización Sprint 2

- [ ] Prometheus scrapeando métricas de **7 servicios** (incluyendo admin-service)
- [ ] Grafana accesible con password seguro (configurado en `.env`)
- [ ] Al menos 3 dashboards funcionando
- [ ] Datos históricos visibles (mínimo 1 día)
- [ ] Al menos 3 alertas configuradas
- [ ] Documentación de acceso y uso
- [ ] README actualizado con nuevos servicios
- [ ] Admin Service (Sprint 1) y Prometheus/Grafana trabajando en conjunto

---

## 🔗 Dependencias

**Pre-requisitos completados en Sprint 1** ✅:
- ✅ Actuator securizado funcionando en todos los servicios
- ✅ Admin Service operativo en puerto 9000
- ✅ Variables de entorno configuradas (`.env` creado)
- ✅ Endpoints `/actuator/health`, `/actuator/info`, `/actuator/metrics` expuestos
- ✅ Sistema portable sin IPs hardcodeadas

**Nuevas dependencias Sprint 2**:
- Agregar `micrometer-registry-prometheus` a cada servicio
- Habilitar exportación Prometheus en `application.yml`
- Configurar Docker Compose con Prometheus y Grafana

**Bloqueantes**:
- ⚠️ Si Actuator no está expuesto, Prometheus no puede scrapear métricas
  - **Mitigación**: Ya configurado en Sprint 1
- ⚠️ Si Admin Service falla, Prometheus/Grafana siguen funcionando como respaldo

---

## 📚 Referencias

- [Prometheus con Spring Boot](https://prometheus.io/docs/prometheus/latest/getting_started/)
- [Grafana Dashboards](https://grafana.com/grafana/dashboards/)
- [Micrometer Prometheus Registry](https://micrometer.io/docs/registry/prometheus)
- [Spring Boot Actuator Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)

---

**Creado**: 1 de noviembre de 2025  
**Actualizado**: 1 de noviembre de 2025 (Adaptado post Sprint 1)  
**Estado**: 📋 Listo para Iniciar (Sprint 1 Completado ✅)

**Cambios respecto a la planificación original**:
- ✅ Admin Service ya implementado en Sprint 1 (puerto 9000)
- ✅ Actuator ya configurado y securizado en Sprint 1
- ✅ Variables de entorno (`.env`) ya configuradas en Sprint 1
- ✅ Puertos de servicios validados y actualizados
- ⭐ Agregado admin-service a configuración de Prometheus
- 📝 Documentación actualizada con estado real del sistema
