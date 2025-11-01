# Sprint 2 - Fases de Implementación

**Sprint**: Sprint 2 - Observabilidad Avanzada  
**Duración**: 3-4 días (1-5 noviembre 2025)  
**Horas Totales**: 12-16 horas

---

## 📊 Progreso General

```
[░░░░░░░░░░░░░░░░░░░░] 0% - Sprint 2 listo para iniciar
```

| Métrica | Valor |
|---------|-------|
| **Fases Completadas** | 0/3 |
| **Tareas Completadas** | 0/4 |
| **Horas Invertidas** | 0/16 |
| **Días Transcurridos** | 0/4 |
| **Velocidad Real** | - |

---

## 🎯 Contexto del Sprint 1 (Completado)

**Estado Sprint 1**: ✅ **COMPLETADO 100%** (17h/24h estimadas - 71% eficiencia)

**Logros Relevantes para Sprint 2**:
- ✅ Admin Service implementado (puerto 9000) - Monitoreo en tiempo real
- ✅ Actuator securizado en todos los servicios
- ✅ Endpoints `/actuator/health`, `/actuator/info`, `/actuator/metrics` expuestos
- ✅ Variables de entorno configuradas (archivo `.env`)
- ✅ Sistema portable sin IPs hardcodeadas

**Servicios Activos** (7 en total):
1. eureka-server (8761)
2. config-service (8888)
3. authentication (8589)
4. user-service (8588)
5. workout-service (8586)
6. api-gateway (8590)
7. admin-service (9000)

---

## 🎯 Fases del Sprint 2

### Fase 1: Configuración de Prometheus (4h) 🔴 CRÍTICO
**Objetivo**: Implementar scraping de métricas desde todos los servicios

**Estado**: 🏗️ Pendiente  
**Progreso**: `[░░░░░░░░░░] 0%` - No iniciado

**Tareas Incluidas**:
- [ ] **Tarea 1.1**: Agregar Micrometer Registry Prometheus (1h)
  - Agregar dependencia `micrometer-registry-prometheus` en pom.xml de cada servicio
  - Actualizar 7 archivos pom.xml
  - Validar compilación exitosa
  
- [ ] **Tarea 1.2**: Habilitar exportación Prometheus (1h)
  - Actualizar application.yml de cada servicio
  - Agregar configuración `management.metrics.export.prometheus.enabled: true`
  - Validar endpoints `/actuator/prometheus` funcionando
  
- [ ] **Tarea 1.3**: Configurar Docker Compose Prometheus (1h)
  - Crear archivo `docker-compose.prometheus.yml`
  - Configurar volúmenes para persistencia
  - Agregar red `spring-cloud-network`
  - Configurar restart policies
  
- [ ] **Tarea 1.4**: Crear configuración Prometheus (1h)
  - Crear `monitoring/prometheus/prometheus.yml`
  - Configurar 7 jobs (uno por servicio)
  - Configurar scrape_interval: 15s
  - Validar targets en Prometheus UI

**Criterios de Aceptación**:
- ✅ Prometheus accesible en http://localhost:9090
- ✅ Dashboard Prometheus muestra 7 targets UP
- ✅ Cada servicio expone endpoint `/actuator/prometheus`
- ✅ Métricas visibles en formato Prometheus
- ✅ Queries básicas funcionan: `up`, `jvm_memory_used_bytes`, `http_server_requests_seconds_count`
- ✅ Datos persisten en volumen Docker
- ✅ Admin Service también monitoreado por Prometheus

**Archivos a Crear**:
- `monitoring/prometheus/prometheus.yml`
- `docker-compose.prometheus.yml` (o integrar en docker-compose.yml existente)

**Archivos a Modificar**:
- `pom.xml` de 7 servicios (agregar dependencia)
- `application.yml` de 7 servicios (habilitar Prometheus export)
- `.env` (si se requieren variables adicionales)

**Bloqueadores Potenciales**:
- Versión de Micrometer incompatible con Spring Boot 3.2.0
- Endpoints Prometheus no expuestos (verificar Actuator)
- Docker network issues
- Prometheus no puede resolver nombres de servicios

**Fecha Objetivo**: Día 1 (Viernes 1 nov)

---

### Fase 2: Configuración de Grafana (3h) 🟡 ALTA
**Objetivo**: Instalar Grafana y conectar con Prometheus como datasource

**Estado**: 🏗️ Pendiente  
**Progreso**: `[░░░░░░░░░░] 0%` - No iniciado

**Tareas Incluidas**:
- [ ] **Tarea 2.1**: Agregar servicio Grafana a Docker (1h)
  - Agregar servicio Grafana a docker-compose
  - Configurar puerto 3000
  - Configurar volúmenes para persistencia
  - Configurar plugins (grafana-clock-panel, grafana-simple-json-datasource)
  
- [ ] **Tarea 2.2**: Configurar datasource Prometheus (1h)
  - Crear `monitoring/grafana/provisioning/datasources/prometheus.yml`
  - Configurar conexión automática a Prometheus
  - Configurar como datasource por defecto
  - Validar conexión exitosa
  
- [ ] **Tarea 2.3**: Configurar seguridad Grafana (1h)
  - Agregar `GRAFANA_PASSWORD` a archivo `.env`
  - Configurar variables de entorno en docker-compose
  - Cambiar password por defecto en primer login
  - Documentar credenciales de acceso

**Criterios de Aceptación**:
- ✅ Grafana accesible en http://localhost:3000
- ✅ Login funciona con credenciales configuradas
- ✅ Datasource Prometheus conectado y funcionando
- ✅ Puede ejecutar queries contra Prometheus desde Grafana
- ✅ Explorador de métricas funciona
- ✅ Password seguro configurado (no usar admin/admin en producción)
- ✅ Datos persisten después de reiniciar contenedor

**Archivos a Crear**:
- `monitoring/grafana/provisioning/datasources/prometheus.yml`

**Archivos a Modificar**:
- `docker-compose.yml` o crear `docker-compose.grafana.yml`
- `.env` (agregar GRAFANA_PASSWORD)

**Bloqueadores Potenciales**:
- Prometheus no accesible desde Grafana (Docker network)
- Puerto 3000 ocupado por otro servicio
- Volúmenes no persisten datos correctamente
- Provisioning no funciona automáticamente

**Fecha Objetivo**: Día 2 (Sábado 2 nov)

---

### Fase 3: Dashboards y Alertas (5h) 🟡 ALTA
**Objetivo**: Importar dashboards predefinidos y configurar alertas básicas

**Estado**: 🏗️ Pendiente  
**Progreso**: `[░░░░░░░░░░] 0%` - No iniciado

**Tareas Incluidas**:
- [ ] **Tarea 3.1**: Importar dashboards predefinidos (2h)
  - Importar **Spring Boot 2.1 Statistics** (ID: 10280)
  - Importar **JVM (Micrometer)** (ID: 4701)
  - Importar **Spring Cloud Gateway** (ID: 11506)
  - Configurar refresh automático
  - Ajustar variables de template según servicios
  - Exportar JSON de dashboards configurados
  
- [ ] **Tarea 3.2**: Configurar alertas básicas (2h)
  - Configurar alerta: Servicio Caído (up == 0)
  - Configurar alerta: Memoria JVM Alta (> 90%)
  - Configurar alerta: HTTP Error Rate Alto (5xx > 10/min)
  - Configurar alerta: Latencia Alta (p95 > 1s)
  - Crear Contact Point (configurar email/Slack)
  - Crear Notification Policy
  
- [ ] **Tarea 3.3**: Documentación y testing (1h)
  - Documentar dashboards importados
  - Documentar alertas configuradas
  - Probar alertas manualmente (detener un servicio)
  - Crear guía de acceso para equipo
  - Actualizar README principal

**Criterios de Aceptación**:
- ✅ Al menos 3 dashboards importados y funcionando
- ✅ Dashboards muestran datos en tiempo real
- ✅ Gráficas de JVM memory visibles
- ✅ HTTP metrics (request count, latency) visibles
- ✅ Al menos 4 alertas configuradas
- ✅ Alertas se disparan correctamente (probado)
- ✅ Notificaciones llegan al canal configurado
- ✅ Dashboards exportados en `monitoring/grafana/dashboards/`
- ✅ Documentación actualizada

**Archivos a Crear**:
- `monitoring/grafana/provisioning/dashboards/dashboards.yml`
- `monitoring/grafana/dashboards/spring-boot-stats.json`
- `monitoring/grafana/dashboards/jvm-micrometer.json`
- `monitoring/grafana/dashboards/spring-cloud-gateway.json`
- `docs/GRAFANA_DASHBOARDS.md` (documentación)

**Archivos a Modificar**:
- `README.md` (agregar sección Observabilidad)
- `docs/sprints/sprint-2/README.md` (actualizar progreso)

**Bloqueadores Potenciales**:
- Dashboards incompatibles con versión Grafana 10.x
- Métricas no disponibles (nombres diferentes)
- Alertas no se disparan (configuración incorrecta)
- Canal de notificaciones no configurado

**Fecha Objetivo**: Día 3-4 (Domingo 3 - Lunes 4 nov)

---

## 📈 Métricas por Fase

| Fase | Horas Est. | Horas Real | Tareas | Estado | % Completo |
|------|------------|------------|--------|--------|------------|
| **Fase 1** - Prometheus | 4h | - | 0/4 | 🏗️ Pendiente | 0% |
| **Fase 2** - Grafana | 3h | - | 0/3 | 🏗️ Pendiente | 0% |
| **Fase 3** - Dashboards/Alertas | 5h | - | 0/3 | 🏗️ Pendiente | 0% |
| **Documentación** | 2h | - | - | 🏗️ Pendiente | 0% |
| **Testing Final** | 2h | - | - | 🏗️ Pendiente | 0% |
| **TOTAL** | **16h** | **0h** | **0/10** | - | **0%** |

---

## 🎯 Hitos (Milestones)

### Hito 1: Prometheus Operacional ⏳
**Fase**: Fase 1  
**Fecha Objetivo**: Día 1 (Viernes 1 nov)  
**Estado**: 🏗️ **PENDIENTE**

**Definición**:
- ✅ Prometheus scrapeando métricas de 7 servicios
- ✅ Todos los targets UP en Prometheus UI
- ✅ Métricas JVM visibles
- ✅ Métricas HTTP visibles
- ✅ Datos persistiendo en volumen

---

### Hito 2: Grafana Conectado ⏳
**Fase**: Fase 2  
**Fecha Objetivo**: Día 2 (Sábado 2 nov)  
**Estado**: 🏗️ **PENDIENTE**

**Definición**:
- ✅ Grafana accesible con autenticación
- ✅ Datasource Prometheus conectado
- ✅ Puede ejecutar queries desde Explore
- ✅ Password seguro configurado

---

### Hito 3: Observabilidad Completa ⏳
**Fase**: Fase 3  
**Fecha Objetivo**: Día 4 (Lunes 4 nov)  
**Estado**: 🏗️ **PENDIENTE**

**Definición**:
- ✅ Al menos 3 dashboards funcionando
- ✅ Al menos 4 alertas configuradas
- ✅ Alertas probadas y funcionando
- ✅ Documentación completa
- ✅ Sistema listo para producción

---

## 🚦 Semáforo de Riesgos

### 🟢 Bajo Riesgo
- Tarea 2.1: Agregar Grafana a Docker (bien documentada)
- Tarea 3.1: Importar dashboards (proceso estándar)

### 🟡 Riesgo Medio
- Tarea 1.2: Habilitar exportación Prometheus (puede afectar rendimiento)
- Tarea 3.2: Configurar alertas (requiere ajuste fino)

### 🔴 Alto Riesgo
- Tarea 1.1: Agregar dependencias (puede romper build)
  - **Mitigación**: Compilar cada servicio individualmente
  - **Plan B**: Revertir cambios si falla
- Tarea 1.4: Configuración Prometheus (networking complejo)
  - **Mitigación**: Probar con un servicio primero
  - **Plan B**: Usar configuración estática si discovery falla

---

## 🔗 Dependencias entre Fases

```
Fase 1: Prometheus
    ↓
    ├── Tarea 1.1 (dependencias) → Tarea 1.2 (configuración)
    └── Tarea 1.3 (Docker) → Tarea 1.4 (prometheus.yml)
    ↓
Fase 2: Grafana
    ↓
    ├── Tarea 2.1 (Docker) → Tarea 2.2 (datasource)
    └── Tarea 2.2 (datasource) → Tarea 2.3 (seguridad)
    ↓
Fase 3: Dashboards/Alertas
    ↓
    ├── Tarea 3.1 (dashboards) → Tarea 3.3 (documentación)
    └── Tarea 3.2 (alertas) → Tarea 3.3 (documentación)
```

**Bloqueantes Críticos**:
- ⚠️ Fase 2 NO puede iniciar sin Fase 1 completa (Grafana necesita Prometheus)
- ⚠️ Fase 3 NO puede iniciar sin Fase 2 completa (Dashboards necesitan datasource)

---

## 📋 Checklist Pre-Sprint

Validar antes de iniciar Sprint 2:

**Infraestructura**:
- [x] Docker instalado y funcionando
- [x] Docker Compose instalado
- [x] Puertos disponibles: 9090 (Prometheus), 3000 (Grafana)
- [x] Espacio en disco para volúmenes (mínimo 5GB)

**Código**:
- [x] Sprint 1 completado 100%
- [x] Admin Service funcionando (puerto 9000)
- [x] Actuator endpoints expuestos en todos los servicios
- [x] Archivo `.env` configurado
- [x] Todos los servicios compilan sin errores

**Documentación**:
- [x] Sprint 2 README actualizado
- [x] Adaptaciones documentadas
- [x] Fases definidas (este archivo)

---

## 📚 Referencias y Recursos

### Documentación Oficial
- [Prometheus - Getting Started](https://prometheus.io/docs/prometheus/latest/getting_started/)
- [Grafana - Documentation](https://grafana.com/docs/grafana/latest/)
- [Micrometer - Prometheus Registry](https://micrometer.io/docs/registry/prometheus)
- [Spring Boot Actuator Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)

### Dashboards Grafana
- [Dashboard 10280 - Spring Boot 2.1 Statistics](https://grafana.com/grafana/dashboards/10280)
- [Dashboard 4701 - JVM Micrometer](https://grafana.com/grafana/dashboards/4701)
- [Dashboard 11506 - Spring Cloud Gateway](https://grafana.com/grafana/dashboards/11506)

### Configuraciones Ejemplo
- [Prometheus Config Examples](https://github.com/prometheus/prometheus/tree/main/documentation/examples)
- [Grafana Provisioning](https://grafana.com/docs/grafana/latest/administration/provisioning/)

---

## 🎯 Criterios de Finalización Sprint 2

**Técnicos**:
- [ ] Prometheus scrapeando métricas de 7 servicios (100% uptime)
- [ ] Grafana accesible con password seguro
- [ ] Al menos 3 dashboards funcionando con datos en tiempo real
- [ ] Datos históricos visibles (mínimo 24 horas)
- [ ] Al menos 4 alertas configuradas y probadas
- [ ] Volúmenes Docker persistiendo datos correctamente
- [ ] Todas las queries básicas funcionan

**Documentación**:
- [ ] README principal actualizado con sección Observabilidad
- [ ] Guía de dashboards creada
- [ ] Guía de alertas documentada
- [ ] Credenciales de acceso documentadas (en .env.example)
- [ ] Troubleshooting guide creado

**Integración**:
- [ ] Admin Service (Sprint 1) y Prometheus/Grafana (Sprint 2) funcionan en conjunto
- [ ] No hay conflictos de puertos
- [ ] Networking Docker funciona correctamente
- [ ] Variables de entorno centralizadas en `.env`

**Calidad**:
- [ ] Sin degradación de rendimiento en servicios
- [ ] Métricas precisas y confiables
- [ ] Alertas sin falsos positivos
- [ ] Sistema probado con carga simulada

---

## 📊 Comparativa: Admin Service vs Prometheus/Grafana

| Aspecto | Admin Service (Sprint 1) | Prometheus + Grafana (Sprint 2) |
|---------|-------------------------|----------------------------------|
| **Estado** | ✅ Implementado | 🏗️ Por implementar |
| **Propósito** | Monitoreo tiempo real | Métricas históricas + Alerting |
| **Retención** | No persistente | Persistente (días/semanas) |
| **Dashboards** | UI básico | Dashboards avanzados customizables |
| **Logs** | ✅ Acceso en vivo | ❌ No (considerar ELK Sprint 3) |
| **Thread Dumps** | ✅ Sí | ❌ No |
| **Alerting** | Eventos simples | Alerting robusto multi-canal |
| **Análisis** | Snapshot actual | Tendencias históricas |

**Estrategia de Uso**:
- **Día a día**: Usar Admin Service para debugging y operaciones
- **Planificación**: Usar Grafana para análisis de tendencias
- **Emergencias**: Combinar ambos para diagnóstico completo

---

## 🔄 Iteración y Mejora Continua

**Feedback Loop**:
1. Implementar fase
2. Validar criterios de aceptación
3. Documentar hallazgos
4. Ajustar siguiente fase si es necesario
5. Repetir

**Retrospectiva al Final**:
- ¿Qué funcionó bien?
- ¿Qué mejorar para Sprint 3?
- ¿Métricas adicionales necesarias?
- ¿Dashboards custom requeridos?

---

**Creado**: 1 de noviembre de 2025  
**Última Actualización**: 1 de noviembre de 2025  
**Estado**: 📋 Planificado - Listo para iniciar  
**Siguiente Acción**: Iniciar Fase 1 - Tarea 1.1 (Agregar dependencia Micrometer)
