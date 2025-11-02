# Optimización de Rendimiento - Fase 1

## 📊 Resumen

**Fecha**: 2 de noviembre de 2025
**Objetivo**: Reducir huella de memoria del stack completo en 40-60%
**Fase**: 1 - Optimización JVM + Límites de Memoria

## 🎯 Cambios Implementados

### 1. Configuración JVM Centralizada

Se creó un **anchor YAML** (`x-jvm-opts`) en `docker-compose.yml` para centralizar la configuración JVM de todos los servicios Java:

```yaml
x-jvm-opts: &jvm_opts
  JAVA_TOOL_OPTIONS: >-
    -XX:+UseContainerSupport
    -XX:InitialRAMPercentage=10
    -XX:MaxRAMPercentage=70
    -XX:MaxMetaspaceSize=128m
    -XX:+UseG1GC
    -XX:+ExitOnOutOfMemoryError
    -Dspring.main.lazy-initialization=true
    -Dspring.jmx.enabled=false
    -Dlogging.level.root=INFO
```

**Beneficios**:
- ✅ JVM auto-detecta límites del contenedor
- ✅ Heap inicial pequeño (10%) para arranque rápido
- ✅ Heap máximo limitado a 70% del contenedor
- ✅ Metaspace fijo en 128MB (evita crecimiento silencioso)
- ✅ G1GC para mejor manejo de pausas
- ✅ Lazy initialization reduce beans cargados
- ✅ JMX deshabilitado (reduce overhead)
- ✅ Logs en nivel INFO (reduce I/O)

### 2. Límites de Memoria por Servicio

#### Servicios Core (Discovery y Config)
```yaml
eureka-server:    
  mem_limit: 512m
  mem_reservation: 384m
  oom_score_adj: 500

config-service:   
  mem_limit: 512m
  mem_reservation: 384m
  oom_score_adj: 500
```

#### API Gateway
```yaml
api-gateway:      
  mem_limit: 768m
  mem_reservation: 512m
  oom_score_adj: 300
```

#### Servicios de Negocio
```yaml
authentication:   
  mem_limit: 768m
  mem_reservation: 512m
  oom_score_adj: 300

user-service:     
  mem_limit: 768m
  mem_reservation: 512m
  oom_score_adj: 300

workout-service:  
  mem_limit: 768m
  mem_reservation: 512m
  oom_score_adj: 300
```

#### Admin Service
```yaml
admin-service:    
  mem_limit: 512m
  mem_reservation: 384m
  oom_score_adj: 500
```

#### Infraestructura
```yaml
postgres:         
  mem_limit: 1024m
  mem_reservation: 768m
  oom_score_adj: 100

rabbitmq:         
  mem_limit: 384m
  mem_reservation: 256m
  oom_score_adj: 400
  RABBITMQ_VM_MEMORY_HIGH_WATERMARK: "0.3"

prometheus:       
  mem_limit: 512m
  mem_reservation: 384m
  oom_score_adj: 600

grafana:          
  mem_limit: 384m
  mem_reservation: 256m
  oom_score_adj: 600

alertmanager:     
  mem_limit: 256m
  mem_reservation: 192m
  oom_score_adj: 700
```

### 3. OOM Score Adjustment

El `oom_score_adj` determina qué servicio mata primero el kernel en caso de falta de memoria:

**Prioridad de Sacrificio** (mayor valor = más probable de ser matado):
1. **700**: Alertmanager (menos crítico)
2. **600**: Prometheus, Grafana (monitoreo, recreables)
3. **500**: Eureka, Config, Admin (core pero reiniciables)
4. **400**: RabbitMQ (mensajería, con persistencia)
5. **300**: Gateway, Business Services (crítico, pero stateless)
6. **100**: PostgreSQL (MÁS CRÍTICO - datos persistentes)

### 4. Optimización de RabbitMQ

```yaml
RABBITMQ_VM_MEMORY_HIGH_WATERMARK: "0.3"
```

- Limita RabbitMQ a usar máximo 30% de la memoria asignada al contenedor
- En contenedor de 384MB → ~115MB máximo para RabbitMQ
- Previene que consuma toda la RAM disponible

### 5. Dockerfiles Simplificados

Se eliminó `ENV JAVA_TOOL_OPTIONS` de todos los Dockerfiles, ya que ahora se maneja centralmente vía docker-compose.

**Antes**:
```dockerfile
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC"
```

**Después**: *(línea eliminada, configuración viene de compose)*

## 📈 Consumo de Memoria Estimado

### Antes de Optimización
```
Servicios Java (7):        ~2800 MB  (400 MB c/u promedio)
PostgreSQL:                ~600 MB
RabbitMQ:                  ~250 MB
Prometheus:                ~400 MB
Grafana:                   ~300 MB
Alertmanager:              ~150 MB
────────────────────────────────────
TOTAL:                     ~4500 MB
```

### Después de Optimización (Objetivo)
```
Servicios Java (7):        ~1400-1750 MB  (200-250 MB c/u)
PostgreSQL:                ~500 MB
RabbitMQ:                  ~120 MB
Prometheus:                ~300 MB
Grafana:                   ~200 MB
Alertmanager:              ~80 MB
────────────────────────────────────
TOTAL:                     ~2600-2950 MB
```

**Reducción esperada: 35-42%** 🎯

## 🔧 Parámetros Técnicos Explicados

### JVM Flags

| Flag | Valor | Descripción |
|------|-------|-------------|
| `UseContainerSupport` | enabled | JVM detecta límites de cgroup |
| `InitialRAMPercentage` | 10% | Heap inicial pequeño |
| `MaxRAMPercentage` | 70% | Heap máximo del contenedor |
| `MaxMetaspaceSize` | 128m | Límite de metaspace |
| `UseG1GC` | enabled | Garbage Collector optimizado |
| `ExitOnOutOfMemoryError` | enabled | Falla rápido en OOM |

### Spring Boot Flags

| Flag | Valor | Descripción |
|------|-------|-------------|
| `spring.main.lazy-initialization` | true | Beans se crean solo cuando se usan |
| `spring.jmx.enabled` | false | Deshabilita JMX (reduce overhead) |
| `logging.level.root` | INFO | Reduce I/O de logs |

### Docker Memory

| Parámetro | Descripción |
|-----------|-------------|
| `mem_limit` | Límite hard (OOM kill si se excede) |
| `mem_reservation` | "Soft limit" (preferido por scheduler) |
| `oom_score_adj` | Prioridad para OOM killer (-1000 a 1000) |

## 🚀 Aplicación de Cambios

### 1. Rebuild de Servicios

```powershell
# Reconstruir todos los servicios
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### 2. Verificación

```powershell
# Ver uso de memoria
docker stats

# Ver límites configurados
docker inspect [contenedor] | Select-String -Pattern "Memory"

# Verificar JVM options en logs
docker-compose logs [servicio] | Select-String -Pattern "JAVA_TOOL_OPTIONS"
```

### 3. Monitoreo en Grafana

- Dashboard: **JVM (Micrometer)** → Ver heap usage
- Métricas clave:
  - `jvm_memory_used_bytes`
  - `jvm_memory_max_bytes`
  - `process_resident_memory_bytes`

## ⚠️ Consideraciones

### Valores Conservadores

Los límites establecidos son **conservadores** (más altos que el mínimo):
- Permiten picos de tráfico
- Evitan OOM en operaciones pesadas
- Margen para crecimiento

### Ajuste Fino

Después de 48h de operación:
1. Revisar dashboards de Grafana
2. Identificar servicios con uso < 50% de límite
3. Reducir gradualmente en incrementos de 128MB
4. Monitorear por 24h antes de próximo ajuste

### Alertas Recomendadas

Agregar alertas de Prometheus para:
```yaml
- alert: HighMemoryUsage
  expr: (container_memory_usage_bytes / container_spec_memory_limit_bytes) > 0.85
  for: 5m

- alert: MemoryThrottling
  expr: rate(container_memory_throttled_total[5m]) > 0
  for: 2m
```

## 📋 Checklist de Validación

- [x] docker-compose.yml actualizado con x-jvm-opts
- [x] Límites mem_limit/mem_reservation en todos los servicios
- [x] oom_score_adj configurado según criticidad
- [x] RabbitMQ con RABBITMQ_VM_MEMORY_HIGH_WATERMARK
- [x] Dockerfiles sin ENV JAVA_TOOL_OPTIONS duplicado
- [ ] Rebuild y restart de servicios
- [ ] Verificación de métricas en Grafana
- [ ] Documentación de baseline de memoria
- [ ] Monitoreo 48h para ajuste fino

## 🔮 Próximas Fases

### Fase 2: Imágenes Optimizadas (Futuro)
- Cambiar a `eclipse-temurin:21-jre-alpine` (musl, más liviana)
- O evaluar `ibm-semeru-runtimes:open-21-jre` (OpenJ9)
- Reducción adicional: 15-25%

### Fase 3: Spring Native (Futuro)
- Compilar servicios CRUD a native con GraalVM
- Reducción dramática: 60-80% en servicios compilados
- Inicio en milisegundos vs segundos

## 📊 Métricas de Éxito

**KPIs a monitorear**:
1. Memoria total usada (docker stats)
2. Heap usage por servicio (Grafana)
3. Tiempo de arranque (logs de Spring Boot)
4. P99 latency (Prometheus)
5. Rate de GC pauses (JVM metrics)

**Objetivos**:
- ✅ Reducción 35-42% memoria total
- ✅ Ningún servicio con OOM errors
- ✅ P99 latency < 500ms
- ✅ GC pauses < 100ms

---

**Última actualización**: 2 de noviembre de 2025
**Autor**: EmaSleal
**Versión**: 1.0 - Fase 1
