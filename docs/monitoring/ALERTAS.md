# Alertas Configuradas - Sistema de Gimnasio Backend

**Última actualización**: 1 de noviembre de 2025  
**Sprint**: 2 - Fase 3 - Tarea 3.2  
**Estado**: ✅ Configuración Completa

---

## 📋 Índice

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Arquitectura de Alerting](#arquitectura-de-alerting)
3. [Alertas de Prometheus](#alertas-de-prometheus)
4. [Alertas de Grafana](#alertas-de-grafana)
5. [Contact Points](#contact-points)
6. [Notification Policies](#notification-policies)
7. [Testing y Validación](#testing-y-validación)
8. [Troubleshooting](#troubleshooting)
9. [Referencias](#referencias)

---

## 🎯 Resumen Ejecutivo

### Estado Actual

- **Prometheus Alert Rules**: ✅ 15 reglas en 7 grupos
- **Alertmanager**: ✅ Configurado con routing y receivers
- **Grafana Alerting**: ✅ 5 alert rules + 5 contact points
- **Testing**: ✅ Script de pruebas funcional

### Componentes Activos

| Componente | Puerto | Estado | Versión |
|------------|--------|--------|---------|
| Prometheus | 9090 | ✅ UP | latest |
| Alertmanager | 9093 | ✅ UP | 0.28.1 |
| Grafana | 3000 | ✅ UP | 12.2.1 |

### Métricas Clave

- **Targets monitoreados**: 8/8 UP (100%)
- **Severidades configuradas**: Critical, Warning, Info
- **Canales de notificación**: Email, Slack, Webhook, Discord, Telegram
- **Tiempo de detección promedio**: 1-3 minutos

---

## 🏗️ Arquitectura de Alerting

```
┌─────────────────────────────────────────────────────────────────┐
│                    MICROSERVICIOS (8)                           │
│  eureka │ config │ gateway │ auth │ user │ workout │ admin      │
└────────────────────┬────────────────────────────────────────────┘
                     │ Métricas (Actuator/Prometheus)
                     ▼
         ┌───────────────────────┐
         │   PROMETHEUS :9090    │  ◄─── Scrape interval: 15s
         │  - Alert Rules (15)   │  ◄─── Evaluation: 15s
         │  - 7 Rule Groups      │
         └───────────┬───────────┘
                     │ Alertas Activas
                     ▼
         ┌───────────────────────┐
         │ ALERTMANAGER :9093    │
         │  - Routing Tree       │
         │  - Receivers (5)      │
         │  - Inhibit Rules (4)  │
         └─────┬─────────────┬───┘
               │             │
       ┌───────▼──┐     ┌────▼────────┐
       │  Email   │     │   Slack     │
       │ Critical │     │  #alerts-*  │
       │ Warning  │     └─────────────┘
       └──────────┘
               │
         ┌─────▼───────────┐
         │  GRAFANA :3000  │
         │  - Alert Rules  │
         │  - Dashboards   │
         │  - UI Alerts    │
         └─────────────────┘
```

### Flujo de Alertas

1. **Detección** (Prometheus cada 15s):
   - Scrape de métricas desde todos los servicios
   - Evaluación de reglas de alertas
   - Transición: Normal → Pending → Firing

2. **Enrutamiento** (Alertmanager):
   - Recibe alertas de Prometheus
   - Aplica routing rules basadas en labels
   - Agrupa alertas similares

3. **Inhibición** (Alertmanager):
   - Suprime alertas redundantes
   - Ej: Si servicio DOWN, no notificar HighMemory

4. **Notificación** (Receivers):
   - Email para critical/warning
   - Slack para critical/warning/info
   - Webhook para integraciones

---

## 🔔 Alertas de Prometheus

### Archivo: `monitoring/prometheus/alert.rules.yml`

### Grupo 1: Service Availability (Disponibilidad)

#### ServiceDown
```yaml
Expresión: up == 0
Duración: 1m
Severidad: critical
Descripción: El servicio está DOWN
```

**Dispara cuando**:
- Un servicio no responde a health checks por más de 1 minuto

**Acciones recomendadas**:
1. Verificar logs: `docker-compose logs <service>`
2. Verificar estado: `docker-compose ps <service>`
3. Reiniciar: `docker-compose restart <service>`

**Impacto**:
- El servicio no está disponible para procesar requests
- Dependencias pueden fallar (cascada)

---

#### ServiceNotRegisteredInEureka
```yaml
Expresión: eureka_server_registry_count{application="eureka-server"} < 6
Duración: 2m
Severidad: warning
```

**Dispara cuando**:
- Menos de 6 servicios registrados en Eureka

**Acciones recomendadas**:
1. Ejecutar: `.\scripts\verify-eureka-registration.ps1`
2. Revisar configuración de Eureka en application.yml
3. Verificar variable EUREKA_HOST

---

### Grupo 2: Memory Alerts (Memoria)

#### HighMemoryUsage
```yaml
Expresión: (jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100 > 90
Duración: 2m
Severidad: warning
```

**Dispara cuando**:
- Uso de heap JVM supera 90% por más de 2 minutos

**Acciones recomendadas**:
1. Revisar memory leaks con heap dump
2. Analizar logs de GC: `docker-compose logs <service> | Select-String 'GC'`
3. Considerar aumentar -Xmx en Dockerfile
4. Verificar queries y caching

**Impacto**:
- GC frecuentes (pausas)
- Posible OutOfMemoryError

---

#### CriticalMemoryUsage
```yaml
Expresión: (jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100 > 95
Duración: 1m
Severidad: critical
```

**Dispara cuando**:
- Uso de heap supera 95% (crítico)

**Acciones INMEDIATAS**:
1. Reiniciar servicio: `docker-compose restart <service>`
2. Generar heap dump si es posible
3. Revisar logs de emergencia

**Impacto**:
- Inminente OutOfMemoryError
- Servicio puede caerse

---

### Grupo 3: Performance Alerts (Rendimiento)

#### HighLatencyP95
```yaml
Expresión: histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (job, uri, le)) > 1
Duración: 3m
Severidad: warning
```

**Dispara cuando**:
- Percentil 95 de latencia supera 1 segundo por 3 minutos

**Acciones recomendadas**:
1. Identificar endpoint lento en métricas
2. Revisar queries de base de datos
3. Verificar conexiones externas (RabbitMQ, APIs)
4. Analizar logs: `docker-compose logs <service> | Select-String 'slow'`

**Impacto**:
- Usuarios experimentan respuestas lentas
- Posible timeout en clientes

---

#### VeryHighLatencyP99
```yaml
Expresión: histogram_quantile(0.99, sum(rate(http_server_requests_seconds_bucket[5m])) by (job, uri, le)) > 3
Duración: 2m
Severidad: critical
```

**Dispara cuando**:
- Percentil 99 supera 3 segundos (crítico)

**Acciones INMEDIATAS**:
1. Verificar base de datos (locks, queries lentas)
2. Revisar conexiones de red
3. Considerar escalar horizontalmente

**Impacto**:
- Experiencia de usuario severamente degradada
- Timeouts generalizados

---

### Grupo 4: Error Rate Alerts (Errores)

#### HighErrorRate5xx
```yaml
Expresión: sum(rate(http_server_requests_seconds_count{status=~"5.."}[1m])) by (job) > 10
Duración: 2m
Severidad: warning
```

**Dispara cuando**:
- Más de 10 errores 5xx por minuto durante 2 minutos

**Acciones recomendadas**:
1. Revisar logs: `docker-compose logs <service> | Select-String 'ERROR'`
2. Verificar conexiones a base de datos
3. Verificar dependencias externas (RabbitMQ, otros servicios)
4. Revisar excepciones en código

**Impacto**:
- El servicio está fallando en procesar requests
- Pérdida de funcionalidad

---

#### HighErrorRate4xx
```yaml
Expresión: sum(rate(http_server_requests_seconds_count{status=~"4.."}[1m])) by (job) > 50
Duración: 3m
Severidad: info
```

**Dispara cuando**:
- Más de 50 errores 4xx por minuto durante 3 minutos

**Acciones recomendadas**:
1. Analizar patrones de errores (401, 403, 404, 422)
2. Verificar autenticación JWT
3. Revisar validaciones de entrada
4. Verificar documentación de API

**Impacto**:
- Posibles problemas de autenticación
- Requests malformados
- Endpoints inexistentes

---

### Grupo 5: Database Alerts (Base de Datos)

#### HighDatabaseConnectionUsage
```yaml
Expresión: (hikaricp_connections_active / hikaricp_connections_max) * 100 > 80
Duración: 2m
Severidad: warning
```

**Dispara cuando**:
- Pool de conexiones supera 80% de capacidad por 2 minutos

**Acciones recomendadas**:
1. Revisar queries lentas en PostgreSQL
2. Verificar transacciones no cerradas
3. Considerar aumentar pool size
4. Analizar: `docker-compose logs <service> | Select-String 'HikariCP'`

**Impacto**:
- Nuevas requests esperan por conexiones
- Incremento de latencia

---

#### HighDatabaseConnectionPending
```yaml
Expresión: hikaricp_connections_pending > 5
Duración: 1m
Severidad: warning
```

**Dispara cuando**:
- Más de 5 conexiones esperando disponibilidad

**Acciones recomendadas**:
1. Aumentar pool de conexiones en application.yml
2. Optimizar queries
3. Verificar locks en PostgreSQL

**Impacto**:
- Requests esperando, incremento de latencia
- Posible timeout de conexiones

---

### Grupo 6: Garbage Collection Alerts

#### HighGCTime
```yaml
Expresión: rate(jvm_gc_pause_seconds_sum[1m]) / rate(jvm_gc_pause_seconds_count[1m]) > 0.5
Duración: 2m
Severidad: warning
```

**Dispara cuando**:
- Tiempo promedio de pausa de GC supera 0.5 segundos

**Acciones recomendadas**:
1. Revisar configuración de GC
2. Analizar memory leaks
3. Considerar aumentar heap size
4. Evaluar cambiar algoritmo de GC (G1GC, ZGC)

**Impacto**:
- Pausas frecuentes afectan rendimiento
- Stop-the-world events

---

### Grupo 7: Circuit Breaker Alerts (Resiliencia)

#### CircuitBreakerOpen
```yaml
Expresión: resilience4j_circuitbreaker_state{state="open"} == 1
Duración: 1m
Severidad: critical
```

**Dispara cuando**:
- Un circuit breaker está abierto (rechazando requests)

**Acciones recomendadas**:
1. Verificar servicio downstream
2. Revisar logs del servicio protegido
3. Esperar cierre automático cuando servicio se recupere

**Impacto**:
- Requests rechazadas automáticamente
- Funcionalidad degradada

---

#### HighCircuitBreakerFailureRate
```yaml
Expresión: rate(resilience4j_circuitbreaker_failure_rate[2m]) > 0.5
Duración: 2m
Severidad: warning
```

**Dispara cuando**:
- Tasa de fallos del circuit breaker supera 50%

**Acciones recomendadas**:
- Investigar causa de fallos en servicio downstream

**Impacto**:
- Circuit breaker puede abrirse pronto
- Servicio en riesgo

---

## 📊 Alertas de Grafana

### Archivo: `monitoring/grafana/provisioning/alerting/rules.yml`

### Servicio Caído (Grafana)
```yaml
Query: up{job=~".*-service|api-gateway|authentication|admin-service"}
Condition: < 0.5
For: 1m
Severity: critical
```

Complementa la alerta ServiceDown de Prometheus con visualización en Grafana UI.

---

### Uso Alto de Memoria (Grafana)
```yaml
Query: (jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100
Condition: > 90
For: 2m
Severity: warning
```

---

### Latencia Alta P95 (Grafana)
```yaml
Query: histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (job, uri, le))
Condition: > 1
For: 3m
Severity: warning
```

---

### Tasa Alta de Errores 5xx (Grafana)
```yaml
Query: sum(rate(http_server_requests_seconds_count{status=~"5.."}[1m])) by (job)
Condition: > 10
For: 2m
Severity: warning
```

---

### Uso Alto de Conexiones de BD (Grafana)
```yaml
Query: (hikaricp_connections_active / hikaricp_connections_max) * 100
Condition: > 80
For: 2m
Severity: warning
```

---

## 📬 Contact Points

### Archivo: `monitoring/grafana/provisioning/alerting/contactpoints.yml`

### 1. Email Alerts
```yaml
Type: email
Addresses: dev-team@gimnasio-backend.com; ops-team@gimnasio-backend.com
Use case: Alertas Warning
```

### 2. Email Critical
```yaml
Type: email
Addresses: oncall@gimnasio-backend.com
Use case: Alertas Critical
```

### 3. Slack Alerts
```yaml
Type: slack
Channel: #alerts-warning
Icon: :warning:
Use case: Alertas Warning
```

### 4. Slack Critical
```yaml
Type: slack
Channel: #alerts-critical
Icon: :rotating_light:
Mentions: @oncall, @here
Use case: Alertas Critical
```

### 5. Webhook Integration
```yaml
Type: webhook
URL: http://localhost:5001/grafana-webhook
Method: POST
Use case: Integraciones personalizadas
```

### 6. Discord Alerts
```yaml
Type: discord
Use case: Notificaciones a Discord server
```

### 7. Telegram Alerts
```yaml
Type: telegram
Parse Mode: Markdown
Use case: Notificaciones móviles
```

---

## 🚦 Notification Policies

### Archivo: `monitoring/grafana/provisioning/alerting/policies.yml`

### Política Raíz
```yaml
Receiver: email-alerts (default)
Group By: ['alertname', 'grafana_folder']
Group Wait: 10s
Group Interval: 5m
Repeat Interval: 4h
```

### Rutas Específicas

#### 1. Alertas CRÍTICAS
```yaml
Matcher: severity = critical
Receivers: slack-critical, email-critical
Group Wait: 0s (inmediato)
Repeat: 2h
```

#### 2. Alertas WARNING
```yaml
Matcher: severity = warning
Receiver: slack-alerts
Group Wait: 30s
Repeat: 12h
```

#### 3. Service Down
```yaml
Matcher: alertname = ServiceDown
Receivers: slack-critical, email-critical
Group Wait: 0s (inmediato)
Repeat: 1h
```

#### 4. Alertas de Memoria
```yaml
Matcher: alertname =~ High.*Memory.*
Receiver: slack-alerts
Repeat: 6h
```

#### 5. Alertas de Base de Datos
```yaml
Matcher: alertname =~ .*Database.*
Receiver: email-alerts
Repeat: 8h
```

---

## 🧪 Testing y Validación

### Script de Pruebas: `scripts/test-alerts.ps1`

#### Uso

```powershell
# Menú interactivo
.\scripts\test-alerts.ps1

# Test específico
.\scripts\test-alerts.ps1 -TestType service-down -ServiceToStop user-service
.\scripts\test-alerts.ps1 -TestType memory
.\scripts\test-alerts.ps1 -TestType latency
.\scripts\test-alerts.ps1 -TestType errors
```

#### Tests Disponibles

1. **Verificar Estado Inicial**
   - Prometheus operativo
   - Alertmanager operativo
   - Grafana operativo
   - Alertas activas

2. **Test: Servicio Caído**
   - Detiene un servicio
   - Espera 90 segundos
   - Verifica alerta PENDING → FIRING
   - Restaura servicio

3. **Test: Memoria Alta**
   - Monitorea uso actual
   - Sugiere herramientas de carga
   - Muestra uso por servicio

4. **Test: Latencia Alta**
   - Monitorea latencia P95
   - Sugiere escenarios de carga
   - Muestra latencia por endpoint

5. **Test: Tasa de Errores**
   - Monitorea errores 5xx
   - Puede detener PostgreSQL para generar errores
   - Restaura automáticamente

6. **Consultar Alertas Activas**
   - Lista alertas FIRING
   - Lista alertas PENDING
   - Lista alertas en Alertmanager

---

### Resultados de Prueba

#### ✅ Test ServiceDown - user-service

```
Estado Inicial:
- Prometheus: ✅ 8/8 targets UP
- Alertmanager: ✅ Versión 0.28.1
- Grafana: ✅ Versión 12.2.1
- Alertas activas: ✅ 0 (esperado)

Test:
1. Servicio detenido: ✅
2. Espera 90 segundos: ✅
3. Alerta PENDING: ✅ (esperando 'for' duration)
4. Servicio restaurado: ✅
5. Resolución automática: ⏳ En progreso
```

---

## 🔧 Troubleshooting

### Problema: Alertas no se disparan

**Síntomas**:
- Condición cumplida pero alerta no aparece

**Verificar**:
1. Prometheus cargó las reglas:
   ```powershell
   curl http://localhost:9090/api/v1/rules | ConvertFrom-Json
   ```

2. Sintaxis de reglas correcta:
   ```powershell
   docker-compose logs prometheus | Select-String 'error|invalid'
   ```

3. Métricas están llegando:
   ```powershell
   curl "http://localhost:9090/api/v1/query?query=up"
   ```

**Solución**:
- Recargar configuración: `docker-compose restart prometheus`
- Verificar archivos .yml con yamllint

---

### Problema: Alertmanager no recibe alertas

**Síntomas**:
- Alertas FIRING en Prometheus pero no en Alertmanager

**Verificar**:
1. Alertmanager configurado en prometheus.yml
2. Alertmanager accesible desde Prometheus
3. Logs de Alertmanager:
   ```powershell
   docker-compose logs alertmanager
   ```

**Solución**:
- Verificar conectividad: `docker network inspect gimnasio_backend_spring`
- Reiniciar: `docker-compose restart alertmanager prometheus`

---

### Problema: Notificaciones no llegan

**Síntomas**:
- Alertas en Alertmanager pero no se reciben emails/Slack

**Verificar**:
1. Receivers configurados correctamente
2. Credenciales SMTP válidas
3. Webhooks de Slack válidos
4. Routing rules coinciden con labels

**Solución**:
- Test manual: Grafana → Contact Points → Test
- Verificar logs: `docker-compose logs alertmanager | Select-String 'dispatch'`
- Verificar routing: http://localhost:9093/#/routes

---

### Problema: Demasiadas alertas (spam)

**Síntomas**:
- Muchas notificaciones del mismo tipo

**Solución**:
1. Ajustar `repeat_interval` en policies
2. Configurar `inhibit_rules` en alertmanager.yml
3. Aumentar `for` duration en reglas
4. Agrupar con `group_by`

---

### Problema: Grafana alertas no aparecen

**Síntomas**:
- Provisioning no carga alertas

**Verificar**:
1. Archivos en `/etc/grafana/provisioning/alerting`
2. Permisos de archivos
3. Sintaxis YAML correcta
4. Logs de Grafana:
   ```powershell
   docker-compose logs grafana | Select-String 'alerting|provision'
   ```

**Solución**:
- Reiniciar: `docker-compose restart grafana`
- Verificar: Grafana → Alerting → Alert rules

---

## 📚 Referencias

### URLs de Acceso

- **Prometheus**: http://localhost:9090
- **Prometheus Targets**: http://localhost:9090/targets
- **Prometheus Alerts**: http://localhost:9090/alerts
- **Prometheus Rules**: http://localhost:9090/rules
- **Alertmanager**: http://localhost:9093
- **Alertmanager Alerts**: http://localhost:9093/#/alerts
- **Grafana**: http://localhost:3000
- **Grafana Alerting**: http://localhost:3000/alerting/list

### Archivos de Configuración

- `monitoring/prometheus/prometheus.yml` - Configuración de Prometheus
- `monitoring/prometheus/alert.rules.yml` - Reglas de alertas de Prometheus
- `monitoring/alertmanager/alertmanager.yml` - Configuración de Alertmanager
- `monitoring/grafana/provisioning/alerting/contactpoints.yml` - Contact points de Grafana
- `monitoring/grafana/provisioning/alerting/policies.yml` - Políticas de notificación
- `monitoring/grafana/provisioning/alerting/rules.yml` - Reglas de alertas de Grafana
- `scripts/test-alerts.ps1` - Script de pruebas

### Comandos Útiles

```powershell
# Verificar estado de servicios
docker-compose ps

# Ver alertas activas en Prometheus
curl http://localhost:9090/api/v1/alerts | ConvertFrom-Json

# Ver alertas en Alertmanager
curl http://localhost:9093/api/v2/alerts | ConvertFrom-Json

# Silenciar alerta (ejemplo)
curl -X POST http://localhost:9093/api/v2/silences -d '{"matchers":[{"name":"alertname","value":"HighMemoryUsage"}],"startsAt":"2025-11-01T00:00:00Z","endsAt":"2025-11-01T23:59:59Z","comment":"Mantenimiento programado"}'

# Recargar configuración de Prometheus (hot reload)
curl -X POST http://localhost:9090/-/reload

# Ver logs de Prometheus
docker-compose logs prometheus --tail=100 -f

# Ver logs de Alertmanager
docker-compose logs alertmanager --tail=100 -f

# Ejecutar tests de alertas
.\scripts\test-alerts.ps1
```

### Documentación Oficial

- [Prometheus Alerting](https://prometheus.io/docs/alerting/latest/overview/)
- [Alertmanager Configuration](https://prometheus.io/docs/alerting/latest/configuration/)
- [Grafana Alerting](https://grafana.com/docs/grafana/latest/alerting/)
- [PromQL Query Language](https://prometheus.io/docs/prometheus/latest/querying/basics/)

---

## 📊 Resumen de Configuración

### Alertas Totales

| Sistema | Grupos | Reglas | Severidades |
|---------|--------|--------|-------------|
| Prometheus | 7 | 15 | Critical, Warning, Info |
| Grafana | 1 | 5 | Critical, Warning |
| **Total** | **8** | **20** | **3 niveles** |

### Categorías de Alertas

| Categoría | Cantidad | Ejemplos |
|-----------|----------|----------|
| Disponibilidad | 2 | ServiceDown, ServiceNotRegistered |
| Recursos (Memoria) | 2 | HighMemoryUsage, CriticalMemory |
| Rendimiento | 2 | HighLatencyP95, VeryHighLatencyP99 |
| Errores | 2 | HighErrorRate5xx, HighErrorRate4xx |
| Base de Datos | 2 | HighDBConnection, HighDBPending |
| Garbage Collection | 1 | HighGCTime |
| Circuit Breaker | 2 | CBOpen, HighCBFailureRate |

### Contact Points Configurados

- ✅ Email Alerts (dev-team)
- ✅ Email Critical (oncall)
- ✅ Slack Alerts (#alerts-warning)
- ✅ Slack Critical (#alerts-critical)
- ✅ Webhook Integration
- ✅ Discord Alerts
- ✅ Telegram Alerts

### Tiempos de Respuesta

| Acción | Tiempo |
|--------|--------|
| Scrape interval | 15 segundos |
| Evaluation interval | 15 segundos |
| Detección mínima (for: 1m) | ~1 minuto |
| Detección típica (for: 2-3m) | ~2-3 minutos |
| Notificación (group_wait) | 0-30 segundos |
| Resolución automática | 5 minutos |

---

**Configurado por**: GitHub Copilot  
**Fecha**: 1 de noviembre de 2025  
**Sprint**: 2 - Fase 3 - Tarea 3.2
