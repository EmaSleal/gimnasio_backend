# Guía de Monitoreo Post-Optimización

## 🎯 Objetivo

Verificar el impacto de las optimizaciones de Fase 1 y establecer un baseline de memoria para futuros ajustes.

## 📊 Comandos de Verificación

### 1. Ver Uso de Memoria en Tiempo Real

```powershell
# Ver stats de todos los contenedores
docker stats

# Ver stats formateados (solo memoria)
docker stats --format "table {{.Name}}\t{{.MemUsage}}\t{{.MemPerc}}"

# Ver stats de un servicio específico
docker stats eureka-server --no-stream
```

### 2. Verificar Límites Configurados

```powershell
# Ver límites de memoria de un contenedor
docker inspect eureka-server | Select-String -Pattern "Memory"

# Ver todos los límites
docker-compose config | Select-String -Pattern "mem_"
```

### 3. Verificar Configuración JVM

```powershell
# Ver variables de entorno (incluye JAVA_TOOL_OPTIONS)
docker exec eureka-server env | Select-String -Pattern "JAVA"

# Ver logs de inicio para confirmar opciones JVM
docker-compose logs eureka-server | Select-String -Pattern "JAVA_TOOL_OPTIONS"
```

### 4. Verificar en Grafana

1. Ir a http://localhost:3000
2. Dashboard: **JVM (Micrometer)**
3. Métricas clave a revisar:
   - `jvm_memory_used_bytes{area="heap"}` → Heap usado
   - `jvm_memory_max_bytes{area="heap"}` → Heap máximo
   - `process_resident_memory_bytes` → RSS total
   - `jvm_gc_pause_seconds_sum` → Tiempo en GC

## 📈 Tabla de Comparación

### Antes de Optimización (Baseline Original)

| Servicio | Límite Anterior | Uso Típico | % Utilizado |
|----------|----------------|------------|-------------|
| eureka-server | Sin límite | ~350-400 MB | N/A |
| config-service | Sin límite | ~300-350 MB | N/A |
| api-gateway | Sin límite | ~400-450 MB | N/A |
| authentication | Sin límite | ~400-450 MB | N/A |
| user-service | Sin límite | ~400-450 MB | N/A |
| workout-service | Sin límite | ~400-450 MB | N/A |
| admin-service | Sin límite | ~300-350 MB | N/A |
| **TOTAL JAVA** | **Sin límite** | **~2550-2900 MB** | **N/A** |
| postgres | Sin límite | ~500-600 MB | N/A |
| rabbitmq | Sin límite | ~200-250 MB | N/A |
| prometheus | Sin límite | ~350-400 MB | N/A |
| grafana | Sin límite | ~250-300 MB | N/A |
| alertmanager | Sin límite | ~120-150 MB | N/A |
| **TOTAL INFRA** | **Sin límite** | **~1420-1700 MB** | **N/A** |
| **GRAN TOTAL** | **Sin límite** | **~3970-4600 MB** | **N/A** |

### Después de Optimización Fase 1 (Objetivo)

| Servicio | Límite Nuevo | Uso Objetivo | % Esperado |
|----------|--------------|--------------|------------|
| eureka-server | 512 MB | ~200-250 MB | 40-50% |
| config-service | 512 MB | ~200-250 MB | 40-50% |
| api-gateway | 768 MB | ~300-350 MB | 40-45% |
| authentication | 768 MB | ~300-350 MB | 40-45% |
| user-service | 768 MB | ~300-350 MB | 40-45% |
| workout-service | 768 MB | ~300-350 MB | 40-45% |
| admin-service | 512 MB | ~200-250 MB | 40-50% |
| **TOTAL JAVA** | **4608 MB** | **~1800-2100 MB** | **39-46%** |
| postgres | 1024 MB | ~400-500 MB | 40-50% |
| rabbitmq | 384 MB | ~100-120 MB | 26-31% |
| prometheus | 512 MB | ~250-300 MB | 49-59% |
| grafana | 384 MB | ~180-220 MB | 47-57% |
| alertmanager | 256 MB | ~70-90 MB | 27-35% |
| **TOTAL INFRA** | **2560 MB** | **~1000-1230 MB** | **39-48%** |
| **GRAN TOTAL** | **7168 MB** | **~2800-3330 MB** | **39-46%** |

**Reducción esperada: 1140-1270 MB (24-33% del total anterior)**

## 🔍 Qué Buscar

### ✅ Signos de Éxito

1. **Heap Usage < 70% del límite**
   - Indica buen margen para picos
   - JVM tiene espacio para operar

2. **GC Pauses < 100ms**
   - G1GC funcionando correctamente
   - No hay presión extrema de memoria

3. **No OOM Errors**
   - Verificar logs: `docker-compose logs | Select-String -Pattern "OutOfMemoryError"`
   - Todos los servicios arrancan correctamente

4. **Tiempo de Arranque Similar**
   - Lazy initialization no debe incrementar tiempo significativamente
   - Verificar logs de "Started ... in X seconds"

### ⚠️ Signos de Alerta

1. **Uso > 85% del límite**
   - Servicio necesita más memoria
   - Incrementar límite en 128-256 MB

2. **GC Pauses > 200ms**
   - Heap demasiado pequeño
   - Considerar incrementar MaxRAMPercentage a 75-80%

3. **Reinicios Frecuentes**
   - OOM killing por el kernel
   - Verificar: `docker inspect [contenedor] | Select-String -Pattern "OOMKilled"`

4. **Errores 503/504 en Gateway**
   - Timeouts por GC pauses largos
   - Incrementar memoria

## 📋 Checklist Post-Deployment

### Inmediatamente Después del Deploy

- [ ] Todos los contenedores en estado "Up" y "healthy"
  ```powershell
  docker-compose ps
  ```

- [ ] Verificar logs de arranque sin errores
  ```powershell
  docker-compose logs | Select-String -Pattern "error|exception|failed" -CaseSensitive
  ```

- [ ] Servicios registrados en Eureka (6/6)
  ```powershell
  curl http://localhost:8761/eureka/apps
  ```

- [ ] Prometheus targets UP (8/8)
  ```powershell
  curl http://localhost:9090/api/v1/targets | ConvertFrom-Json
  ```

### Después de 1 Hora

- [ ] Verificar uso de memoria estabilizado
  ```powershell
  docker stats --no-stream
  ```

- [ ] Revisar Grafana JVM dashboard
  - Heap no crece indefinidamente
  - GC pauses razonables

- [ ] Revisar logs para warnings
  ```powershell
  docker-compose logs --since 1h | Select-String -Pattern "warn|memory" -CaseSensitive
  ```

### Después de 24 Horas

- [ ] Documentar baseline real de memoria
  - Capturar screenshot de `docker stats`
  - Exportar métricas de Grafana

- [ ] Revisar alertas disparadas
  ```powershell
  curl http://localhost:9093/api/v1/alerts | ConvertFrom-Json
  ```

- [ ] Verificar métricas de latencia no degradadas
  - P99 latency en dashboards
  - Comparar con baseline anterior

### Después de 1 Semana

- [ ] Análisis de ajuste fino
  - Identificar servicios con uso < 40% → reducir límite
  - Identificar servicios con uso > 80% → incrementar límite

- [ ] Documentar ajustes realizados

- [ ] Planear Fase 2 (imágenes optimizadas) si Fase 1 exitosa

## 🎯 Métricas Objetivo (KPIs)

### Memoria

| Métrica | Objetivo | Cómo Verificar |
|---------|----------|----------------|
| Total RAM usada | < 3500 MB | `docker stats` |
| Heap usage por servicio | 40-70% | Grafana JVM Dashboard |
| No OOM kills | 0 | `docker inspect \| grep OOMKilled` |

### Performance

| Métrica | Objetivo | Cómo Verificar |
|---------|----------|----------------|
| P99 latency | < 500ms | Prometheus query `http_server_requests_seconds{quantile="0.99"}` |
| GC pause time | < 100ms | Grafana `jvm_gc_pause_seconds` |
| Startup time | < 15s | Logs "Started ... in X seconds" |

### Disponibilidad

| Métrica | Objetivo | Cómo Verificar |
|---------|----------|----------------|
| Uptime | > 99.5% | Prometheus `up` metric |
| Health checks | 100% passing | `docker-compose ps` |
| Eureka registrations | 6/6 | Eureka dashboard |

## 📊 Scripts de Monitoreo

### Script 1: Resumen Rápido

```powershell
# monitoring-summary.ps1
Write-Host "`n═══ RESUMEN DE MEMORIA ═══`n" -ForegroundColor Cyan

Write-Host "SERVICIOS JAVA:" -ForegroundColor Yellow
docker stats --no-stream --format "table {{.Name}}\t{{.MemUsage}}" | Select-String -Pattern "eureka|config|gateway|auth|user|workout|admin"

Write-Host "`nINFRAESTRUCTURA:" -ForegroundColor Yellow
docker stats --no-stream --format "table {{.Name}}\t{{.MemUsage}}" | Select-String -Pattern "postgres|rabbitmq|prometheus|grafana|alert"

Write-Host "`nHEALTH STATUS:" -ForegroundColor Yellow
docker-compose ps
```

### Script 2: Comparación con Límites

```powershell
# memory-vs-limits.ps1
$containers = docker ps --format "{{.Names}}"

foreach ($container in $containers) {
    $stats = docker stats --no-stream --format "{{.MemUsage}}" $container
    $limit = docker inspect $container | ConvertFrom-Json | Select-Object -ExpandProperty HostConfig | Select-Object -ExpandProperty Memory
    
    if ($limit -gt 0) {
        $limitMB = [math]::Round($limit / 1MB)
        Write-Host "$container : $stats / ${limitMB}MB" -ForegroundColor White
    }
}
```

## 🔄 Proceso de Ajuste Fino

### Si un servicio usa < 40% de su límite:

1. Esperar 1 semana para confirmar patrón
2. Reducir límite en 128MB
3. Monitorear 48h
4. Si estable, considerar reducción adicional

### Si un servicio usa > 80% de su límite:

1. Verificar si es un leak o uso legítimo
   - Revisar heap dump si es leak
   - Ver si memoria crece indefinidamente

2. Si uso legítimo:
   - Incrementar límite en 256MB
   - Revisar MaxRAMPercentage (subir a 75-80%)
   - Monitorear GC pauses

3. Si es leak:
   - Investigar con profiler
   - Fix de código necesario

## 📝 Template de Reporte

```markdown
## Reporte de Optimización - [FECHA]

### Baseline de Memoria

**Antes**: X MB total
**Después**: Y MB total
**Reducción**: Z MB (W%)

### Uso por Servicio

| Servicio | Límite | Usado | % |
|----------|--------|-------|---|
| ... | ... | ... | ... |

### Métricas de Performance

- P99 Latency: X ms
- GC Pause Avg: Y ms
- Uptime: Z%

### Issues Encontrados

1. [Descripción]
2. [Descripción]

### Ajustes Realizados

1. [Servicio]: [Cambio] - [Razón]

### Próximos Pasos

- [ ] ...
```

---

**Última actualización**: 2 de noviembre de 2025
**Versión**: 1.0
**Autor**: EmaSleal
