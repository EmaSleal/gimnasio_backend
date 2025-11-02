# 📊 Monitoreo y Observabilidad - Sistema Gimnasio

Este directorio contiene la configuración de las herramientas de monitoreo y observabilidad del sistema de gimnasio.

---

## 🛠️ Herramientas Instaladas

### 1. **Prometheus** - Recolección de Métricas
- **Versión**: Latest
- **Puerto**: 9090
- **URL de Acceso**: http://localhost:9090
- **Descripción**: Sistema de monitoreo y base de datos de series temporales

**Características**:
- ✅ Scraping automático de 7 microservicios
- ✅ Intervalo de recolección: 15 segundos
- ✅ Persistencia de datos en volumen Docker
- ✅ Configuración en `prometheus/prometheus.yml`

**Targets Monitoreados**:
1. **prometheus** (localhost:9090) - Auto-monitoreo
2. **eureka-server** (8761) - Service Discovery
3. **config-service** (8889) - Configuración Centralizada
4. **authentication** (8583) - Autenticación JWT
5. **user-service** (8588) - Gestión de Usuarios
6. **workout-service** (8586) - Gestión de Ejercicios
7. **api-gateway** (8590) - Gateway y Routing
8. **admin-service** (9000) - Monitoreo en Tiempo Real

**Endpoints Útiles**:
- Targets: http://localhost:9090/targets
- Graph: http://localhost:9090/graph
- Alerts: http://localhost:9090/alerts

---

### 2. **Grafana** - Visualización y Dashboards
- **Versión**: Latest
- **Puerto**: 3000
- **URL de Acceso**: http://localhost:3000
- **Descripción**: Plataforma de visualización y análisis de métricas

**Credenciales de Acceso**:
```
Usuario: admin
Password: Ver archivo .env (GRAFANA_ADMIN_PASSWORD)
```

**⚠️ SEGURIDAD**: 
- El password se configura mediante variables de entorno
- NUNCA usar credenciales por defecto en producción
- Cambiar password inmediatamente después del primer login

**Características**:
- ✅ Datasource Prometheus configurado automáticamente
- ✅ Persistencia de dashboards y configuración
- ✅ Plugins pre-instalados:
  - grafana-clock-panel
  - grafana-simple-json-datasource
  - grafana-piechart-panel

**Directorios**:
- `grafana/provisioning/datasources/` - Configuración automática de datasources
- `grafana/provisioning/dashboards/` - Configuración de dashboards
- `grafana/dashboards/` - Archivos JSON de dashboards

---

## 🚀 Inicio Rápido

### Levantar Servicios de Monitoreo

```bash
# Levantar todo el stack (incluye Prometheus y Grafana)
docker-compose up -d

# Verificar que Prometheus está corriendo
docker-compose ps prometheus

# Verificar que Grafana está corriendo
docker-compose ps grafana

# Ver logs de Prometheus
docker-compose logs -f prometheus

# Ver logs de Grafana
docker-compose logs -f grafana
```

### Validar Instalación

1. **Verificar Prometheus**:
   - Abrir http://localhost:9090
   - Ir a Status > Targets
   - Verificar que todos los targets estén **UP**

2. **Verificar Grafana**:
   - Abrir http://localhost:3000
   - Login con credenciales del .env
   - Ir a Configuration > Data Sources
   - Verificar que "Prometheus" esté configurado y funcionando

---

## 📈 Métricas Disponibles

### JVM (Todos los servicios)
```promql
# Memoria usada por la JVM
jvm_memory_used_bytes{application="user-service"}

# Threads activos
jvm_threads_live_threads{application="api-gateway"}

# Garbage Collection
jvm_gc_pause_seconds_count{application="authentication"}
```

### HTTP (Servicios con endpoints REST)
```promql
# Total de requests
http_server_requests_seconds_count{application="user-service"}

# Latencia (percentil 95)
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))

# Tasa de errores 5xx
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
```

### Sistema
```promql
# Uso de CPU del proceso
process_cpu_usage{application="workout-service"}

# Carga del sistema
system_load_average_1m
```

### Base de Datos (Servicios con JPA)
```promql
# Conexiones activas HikariCP
hikaricp_connections_active{application="user-service"}

# Conexiones pendientes
hikaricp_connections_pending{pool="HikariPool-1"}
```

---

## 📊 Dashboards Recomendados

### Importar desde Grafana Labs

1. **Spring Boot 2.1 Statistics** (ID: 10280)
   - Métricas generales de Spring Boot
   - CPU, Memoria, Threads, HTTP Requests

2. **JVM (Micrometer)** (ID: 4701)
   - Métricas detalladas de JVM
   - Heap, Non-Heap, GC, Threads

3. **Spring Cloud Gateway** (ID: 11506)
   - Específico para API Gateway
   - Routes, Circuit Breakers, Filters

**Proceso de Importación**:
1. Grafana > Dashboards > Import
2. Ingresar ID del dashboard
3. Seleccionar datasource "Prometheus"
4. Click en "Import"

---

## 🔧 Configuración Avanzada

### Cambiar Intervalo de Scraping

Editar `monitoring/prometheus/prometheus.yml`:

```yaml
global:
  scrape_interval: 30s  # Cambiar de 15s a 30s
```

Luego reiniciar Prometheus:
```bash
docker-compose restart prometheus
```

### Agregar Nuevo Servicio

1. Editar `monitoring/prometheus/prometheus.yml`
2. Agregar nuevo job en `scrape_configs`:

```yaml
- job_name: 'nuevo-servicio'
  metrics_path: '/actuator/prometheus'
  static_configs:
    - targets: ['nuevo-servicio:PORT']
      labels:
        application: 'nuevo-servicio'
        tier: 'business'
```

3. Reiniciar Prometheus

### Persistencia de Datos

**Prometheus**:
- Volumen: `prometheus_data`
- Ubicación: `/prometheus` en el contenedor
- Retención: 15 días por defecto

**Grafana**:
- Volumen: `grafana_data`
- Ubicación: `/var/lib/grafana` en el contenedor
- Incluye: Dashboards, configuración, usuarios

---

## 🐛 Troubleshooting

### Prometheus no puede conectarse a servicios

**Síntoma**: Targets muestran estado "DOWN"

**Soluciones**:
1. Verificar que los servicios estén en la misma red Docker:
   ```bash
   docker network inspect gimnasio_backend_spring
   ```

2. Verificar que Actuator esté habilitado en cada servicio:
   ```bash
   curl http://localhost:8761/actuator/prometheus
   ```

3. Verificar nombres de contenedores en docker-compose.yml

### Grafana no puede conectarse a Prometheus

**Síntoma**: Error "Bad Gateway" al probar datasource

**Soluciones**:
1. Verificar que Prometheus esté corriendo:
   ```bash
   docker-compose ps prometheus
   ```

2. Verificar URL del datasource: `http://prometheus:9090`

3. Verificar que ambos estén en la misma red Docker

### Dashboards no muestran datos

**Síntoma**: Gráficas vacías o "No data"

**Soluciones**:
1. Verificar que el datasource esté seleccionado correctamente
2. Verificar que las métricas existan:
   ```promql
   up{job="user-service"}
   ```
3. Ajustar rango de tiempo en Grafana (esquina superior derecha)
4. Verificar variables de template en el dashboard

---

## 📚 Referencias

### Documentación Oficial
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Micrometer Documentation](https://micrometer.io/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

### PromQL (Prometheus Query Language)
- [PromQL Basics](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [PromQL Examples](https://prometheus.io/docs/prometheus/latest/querying/examples/)

### Grafana Dashboards
- [Grafana Dashboard Library](https://grafana.com/grafana/dashboards/)
- [Spring Boot Dashboards](https://grafana.com/grafana/dashboards/?search=spring+boot)

---

## 🔐 Seguridad

### Variables de Entorno Sensibles

Todas las credenciales están en el archivo `.env`:

```bash
# Grafana
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=grafana_gym_2024!

# Admin Service
ADMIN_USERNAME=admin
ADMIN_PASSWORD=gym_admin_123-
```

**⚠️ IMPORTANTE**:
- Nunca commitear el archivo `.env`
- Rotar passwords regularmente
- Usar passwords fuertes en producción
- Considerar usar secrets management (Vault, AWS Secrets Manager)

### Acceso a Grafana

Por defecto, Grafana permite:
- ❌ Registro de nuevos usuarios: **DESHABILITADO** (`GF_USERS_ALLOW_SIGN_UP=false`)
- ✅ Solo admin puede crear usuarios
- ✅ Login requerido para ver dashboards

---

## 🎯 Próximos Pasos

### Fase 2 (En Progreso)
- [x] Tarea 2.1: Agregar Grafana a Docker
- [ ] Tarea 2.2: Configurar datasource Prometheus
- [ ] Tarea 2.3: Configurar seguridad Grafana

### Fase 3 (Pendiente)
- [ ] Importar dashboards predefinidos
- [ ] Configurar alertas básicas
- [ ] Crear dashboards custom
- [ ] Documentar queries útiles

---

**Creado**: 1 de noviembre de 2025  
**Última Actualización**: 1 de noviembre de 2025  
**Estado**: 🚀 Operacional  
**Mantenedor**: Equipo de DevOps
