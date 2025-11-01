# Sprint 2: Observabilidad Avanzada

**Período**: Por definir (Después de Sprint 1)  
**Duración Estimada**: 3-4 días (12-16 horas)  
**Objetivo**: Implementar stack completo de métricas y monitoreo

---

## 🎯 Objetivo del Sprint

Implementar un sistema de observabilidad robusto que permita:
- ✅ Monitorear métricas en tiempo real de todos los servicios
- ✅ Crear dashboards visuales de rendimiento
- ✅ Configurar alertas proactivas
- ✅ Analizar tendencias históricas
- ✅ Correlacionar trazas con métricas

---

## 📊 Arquitectura de Observabilidad

```
┌─────────────────────────────────────────────────────────────┐
│                    OBSERVABILIDAD COMPLETA                   │
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
│         ┌────────────────┴────────────────┐                  │
│         │                                 │                  │
│         ▼                                 ▼                  │
│  ┌─────────────┐                   ┌─────────────┐          │
│  │  Zipkin     │                   │ Prometheus  │          │
│  │  :9411      │                   │   :9090     │          │
│  └─────────────┘                   └──────┬──────┘          │
│                                            │                 │
│  Distributed Tracing                       ▼                 │
│  - Request flows                    ┌─────────────┐         │
│  - Latency analysis                 │  Grafana    │         │
│  - Error tracking                   │   :3000     │         │
│                                     └─────────────┘         │
│                                                               │
│                                  Dashboards & Alerts         │
│                                  - JVM Metrics               │
│                                  - HTTP Metrics              │
│                                  - DB Connection Pools       │
│                                  - Custom Business Metrics   │
└─────────────────────────────────────────────────────────────┘
```

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
      - targets: ['api-gateway:8080']

  - job_name: 'authentication-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['authentication-service:8583']

  - job_name: 'user-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['user-service:8588']

  - job_name: 'workout-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['workout-service:8586']
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

4. **Exponer endpoint Prometheus en Actuator (application.yml)**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

**Validación**:
- [ ] Prometheus accesible en http://localhost:9090
- [ ] Dashboard Prometheus muestra 6 targets UP
- [ ] Endpoint /actuator/prometheus devuelve métricas en formato Prometheus
- [ ] Queries básicas funcionan: `up`, `jvm_memory_used_bytes`

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

3. **Agregar GRAFANA_PASSWORD a .env.example**:
```bash
# Grafana Admin Password
GRAFANA_PASSWORD=your_secure_grafana_password
```

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

## 📅 Planificación Tentativa

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

- [ ] Prometheus scrapeando métricas de 6 servicios
- [ ] Grafana accesible con password seguro
- [ ] Al menos 3 dashboards funcionando
- [ ] Datos históricos visibles (mínimo 1 día)
- [ ] Al menos 3 alertas configuradas
- [ ] Documentación de acceso y uso
- [ ] README actualizado con nuevos servicios

---

## 🔗 Dependencias

**Pre-requisitos** (de Sprint 1):
- ✅ Actuator securizado funcionando
- ✅ Admin Service operativo
- ✅ Variables de entorno configuradas

**Bloqueantes**:
- Si Actuator no está expuesto, Prometheus no puede scrapear métricas
- Si Admin Service falla, tener Prometheus como respaldo

---

## 📚 Referencias

- [Prometheus con Spring Boot](https://prometheus.io/docs/prometheus/latest/getting_started/)
- [Grafana Dashboards](https://grafana.com/grafana/dashboards/)
- [Micrometer Prometheus Registry](https://micrometer.io/docs/registry/prometheus)
- [Spring Boot Actuator Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)

---

**Creado**: 1 de noviembre de 2025  
**Estado**: 📋 Planificado (Pendiente Sprint 1)
