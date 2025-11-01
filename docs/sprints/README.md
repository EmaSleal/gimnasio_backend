# Sprints - Gestión de Tareas por Iteraciones

## 📋 Sistema de Gestión de Sprints

Esta carpeta contiene la planificación y seguimiento de las tareas organizadas en sprints (iteraciones de 1 semana).

## 🏷️ Sistema de Clasificación

Cada tarea se clasifica usando **2 dimensiones**:

### 1. Categoría (Tipo de Tarea)

| Categoría | Emoji | Descripción |
|-----------|-------|-------------|
| **SEGURIDAD** | 🔒 | Vulnerabilidades, exposición de datos, autenticación |
| **ESTABILIDAD** | 💾 | Pérdida de datos, crashes, disponibilidad |
| **ARQUITECTURA** | 🏗️ | Diseño de servicios, patrones, escalabilidad |
| **OBSERVABILIDAD** | 📊 | Monitoreo, logs, métricas, debugging |
| **CONFIGURACIÓN** | ⚙️ | Gestión de config, portabilidad, multi-entorno |

### 2. Prioridad (Urgencia)

| Prioridad | Descripción | Cuándo usar |
|-----------|-------------|-------------|
| **P0 - Crítico** | Bloquea producción | Debe hacerse YA |
| **P1 - Alto** | Importante para este sprint | Debe hacerse esta semana |
| **P2 - Medio** | Importante pero puede esperar | Próximo sprint |
| **P3 - Bajo** | Nice to have | Cuando haya tiempo |
| **P4 - Backlog** | Mejora futura | Sin fecha definida |

## 📁 Estructura de Sprints

```
sprints/
├── README.md (este archivo)
├── sprint-1/              # 🟡 Planificado - Seguridad y Estabilidad
│   ├── decisiones.md       # 6 tareas críticas (24h)
│   ├── progreso.md         # Seguimiento diario
│   └── retrospectiva.md    # Aprendizajes (al finalizar)
├── sprint-2/              # 📋 Por iniciar - Observabilidad Avanzada
│   └── README.md           # Prometheus + Grafana (12-16h)
└── sprint-N/
    └── ...
```

## 🎯 Sprints Disponibles

### [Sprint 1](./sprint-1/) - Estabilización y Seguridad
**Estado**: 🟡 Planificado  
**Duración**: 5 días (4-8 noviembre 2025)  
**Objetivo**: Eliminar riesgos críticos de seguridad y pérdida de datos

**Tareas Principales**:
1. 🔒 Gestión de Secretos (P0)
2. 💾 Persistencia con Flyway (P0)
3. ⚙️ Eliminar IPs hardcodeadas (P1)
4. 📊 Admin Service para Actuator (P1)
5. ⚙️ Securizar Actuator endpoints (P1)

**Estimación Total**: 24 horas

---

### [Sprint 2](./sprint-2/) - Observabilidad Avanzada
**Estado**: 📋 Por iniciar  
**Duración**: 3-4 días (después de Sprint 1)  
**Objetivo**: Implementar stack completo de métricas y monitoreo

**Tareas Principales**:
1. 📊 Configurar Prometheus (P1)
2. 📈 Configurar Grafana (P1)
3. 📊 Importar dashboards predefinidos (P2)
4. 🔔 Configurar alertas básicas (P2)

**Estimación Total**: 12-16 horas

**Stack Tecnológico**:
- Prometheus 2.47.0 (scraping de métricas)
- Grafana 10.1.0 (visualización y alertas)
- Micrometer Registry Prometheus
- Dashboards: Spring Boot, JVM, PostgreSQL

---

### Sprint 3 - Arquitectura y Eventos
**Estado**: 📝 Por planificar  
**Duración**: 5 días (Por definir)  
**Objetivo**: Implementar comunicación asíncrona y desacoplar servicios

**Tareas Candidatas**:
- 🏗️ Implementar RabbitMQ para eventos
- 🏗️ Desacoplar Authentication de User Service
- 📊 Configurar Circuit Breakers
- ⚙️ Centralizar configuración con Config Service
- 🏗️ Crear Email Service independiente

---

### Sprint 4 - Calidad y Documentación
**Estado**: 📝 Por planificar  
**Duración**: 5 días (18-22 noviembre 2025)  
**Objetivo**: Mejorar calidad de código y experiencia de desarrollo

**Tareas Candidatas**:
- 📊 Estandarizar respuestas de API
- 🔒 Validación de DTOs
- 📊 Global exception handler
- 📊 Documentación con Swagger
- 🏗️ Health checks personalizados

---

## 📊 Métricas Globales

### Progreso General

| Sprint | Tareas Planeadas | Completadas | % Completado | Horas Estimadas | Horas Reales |
|--------|------------------|-------------|--------------|-----------------|--------------|
| Sprint 1 | 6 | 0 | 0% | 24h | - |
| Sprint 2 | - | - | - | - | - |
| Sprint 3 | - | - | - | - | - |

### Distribución por Categoría (Todas las Sprints)

| Categoría | Tareas | Porcentaje |
|-----------|--------|------------|
| 🔒 SEGURIDAD | 2 | - |
| 💾 ESTABILIDAD | 1 | - |
| 🏗️ ARQUITECTURA | 0 | - |
| 📊 OBSERVABILIDAD | 1 | - |
| ⚙️ CONFIGURACIÓN | 2 | - |

---

## 🔄 Metodología

### Daily Standup (15 minutos diarios)
Responder 3 preguntas:
1. ¿Qué hice ayer?
2. ¿Qué haré hoy?
3. ¿Hay algún bloqueador?

### Sprint Planning (inicio de sprint)
- Revisar backlog
- Seleccionar tareas para el sprint
- Estimar esfuerzo
- Asignar responsables

### Sprint Review (fin de sprint)
- Demostrar trabajo completado
- Validar con stakeholders
- Actualizar documentación

### Sprint Retrospective (fin de sprint)
- ¿Qué funcionó bien?
- ¿Qué podemos mejorar?
- Definir acciones de mejora

---

## 📝 Convenciones

### Nombres de Branches
```
feat/sprint1-gestion-secretos
fix/sprint1-flyway-migration
refactor/sprint2-decouple-auth
```

### Mensajes de Commit
```
feat(sprint1): migrate secrets to environment variables

- Move JWT_SECRET to .env
- Update application.yml to use ${JWT_SECRET}
- Add .env to .gitignore

Closes #TAREA-1
```

### Pull Requests
- Título: `[Sprint 1] Gestión de Secretos`
- Descripción: Link a tarea en `decisiones.md`
- Reviewers: Al menos 1 persona
- Checks: Build + Tests pasando

---

## 🎓 Recursos

- **Gestión de Tareas**: [decisiones.md](./sprint-1/decisiones.md) de cada sprint
- **Documentación Técnica**: [/docs](../)
- **Checklist General**: [05-checklist-implementacion.md](../05-checklist-implementacion.md)
- **Puntos de Mejora**: [03-puntos-de-mejora.md](../03-puntos-de-mejora.md)

---

## 🚀 Quick Start

### Para empezar un nuevo sprint:

1. **Crear carpeta**: `mkdir sprint-N`
2. **Copiar template**: Usar `sprint-1/decisiones.md` como base
3. **Planificar tareas**: Seleccionar del backlog
4. **Estimar**: Asignar horas y prioridades
5. **Asignar**: Distribuir entre el equipo
6. **¡Comenzar!**: Daily standups y seguimiento

### Para cerrar un sprint:

1. **Review**: Demostrar trabajo completado
2. **Retrospectiva**: Completar sección en `decisiones.md`
3. **Métricas**: Actualizar horas reales y velocity
4. **Documentar**: Lecciones aprendidas
5. **Planificar siguiente**: Sprint planning del próximo

---

**Última actualización**: 1 de noviembre de 2025  
**Mantenido por**: Equipo de Desarrollo
