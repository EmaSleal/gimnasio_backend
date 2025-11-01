# Sprint 1 - Seguimiento de Progreso

## 📊 Dashboard del Sprint

**Estado**: 🟡 En Planificación  
**Inicio**: 4 de noviembre de 2025  
**Fin**: 8 de noviembre de 2025  
**Días restantes**: 5 días

---

## 🎯 Objetivos del Sprint

- [x] ✅ Cero secretos en código fuente
- [ ] ✅ Persistencia de datos garantizada
- [ ] ✅ Sistema observable con dashboard centralizado
- [ ] ✅ Configuración portable entre entornos

---

## 📈 Métricas Generales

### Progreso de Tareas

```
Completadas:  ████████░░░░░░░░░░░░░░░░░░░░ 0/6 (0%)
En Progreso:  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░ 0/6 (0%)
Bloqueadas:   ░░░░░░░░░░░░░░░░░░░░░░░░░░░░ 0/6 (0%)
Pendientes:   ████████████████████████████ 6/6 (100%)
```

### Burndown Chart

| Día | Fecha | Horas Pendientes | Horas Completadas | Tareas Completadas | Notas |
|-----|-------|------------------|-------------------|--------------------|-------|
| **Día 0** | 1 nov | 24h | 0h | 0/6 | Sprint planificado |
| **Día 1** | 4 nov | - | - | -/6 | - |
| **Día 2** | 5 nov | - | - | -/6 | - |
| **Día 3** | 6 nov | - | - | -/6 | - |
| **Día 4** | 7 nov | - | - | -/6 | - |
| **Día 5** | 8 nov | - | - | -/6 | - |

### Velocity

- **Horas planificadas**: 24h
- **Horas completadas**: 0h
- **Tareas completadas**: 0/6
- **Eficiencia**: -% (calcular al final)

---

## 📋 Estado de las Tareas

### Tarea 1: Gestión de Secretos 🔒
**Prioridad**: P0 - Crítico  
**Estimación**: 2h  
**Estado**: 🔴 Pendiente  
**Asignado a**: _[Nombre]_

#### Subtareas
- [ ] Crear archivo `.env` real (no commitearlo)
- [ ] Modificar `application.yml` para usar variables de entorno
- [ ] Actualizar `docker-compose.yml` con `env_file`
- [ ] Generar nuevos secretos con OpenSSL
- [ ] Rotar TODOS los secretos actuales
- [ ] Validar que servicios NO inician sin variables

#### Progreso
```
░░░░░░░░░░░░░░░░░░░░░░░░░░░░ 0% (0/6 subtareas)
```

#### Tiempo Invertido
- **Estimado**: 2h
- **Real**: -
- **Variación**: -

#### Bloqueadores
_Ninguno por el momento_

#### Notas
_Agregar notas aquí..._

---

### Tarea 2: Persistencia con Flyway 💾
**Prioridad**: P0 - Crítico  
**Estimación**: 8h (4h + 4h)  
**Estado**: 🔴 Pendiente  
**Asignado a**: _[Nombre]_

#### Subtareas - User Service (4h)
- [ ] Agregar dependencias Flyway en `user-service/pom.xml`
- [ ] Cambiar `ddl-auto: create` → `validate`
- [ ] Crear directorio `db/migration`
- [ ] Crear `V1__create_users_table.sql`
- [ ] Crear `V2__add_indexes.sql`
- [ ] Configurar Flyway en `application.yml`
- [ ] Validar migraciones (backup, test, restore)

#### Subtareas - Workout Service (4h)
- [ ] Agregar dependencias Flyway en `workout-service/pom.xml`
- [ ] Cambiar `ddl-auto: create-drop` → `validate`
- [ ] Crear 7 migraciones (V1-V7)
- [ ] Validar migraciones

#### Progreso
```
User Service:    ░░░░░░░░░░░░░░ 0% (0/7 subtareas)
Workout Service: ░░░░░░░░░░░░░░ 0% (0/4 subtareas)
Total:           ░░░░░░░░░░░░░░ 0% (0/11 subtareas)
```

#### Tiempo Invertido
- **Estimado**: 8h
- **Real**: -
- **Variación**: -

#### Bloqueadores
_Ninguno por el momento_

#### Notas
⚠️ **IMPORTANTE**: Hacer backup antes de empezar:
```bash
docker exec db-microservices pg_dump -U postgres gym_authentication > backup_auth.sql
docker exec db-microservices pg_dump -U postgres gym_exercise > backup_exercise.sql
```

---

### Tarea 3: Eliminar IPs Hardcodeadas ⚙️
**Prioridad**: P1 - Alto  
**Estimación**: 2h  
**Estado**: 🔴 Pendiente  
**Asignado a**: _[Nombre]_

#### Subtareas
- [ ] Crear `application-docker.yml` en cada servicio
- [ ] Crear `application-local.yml` en cada servicio
- [ ] Actualizar `docker-compose.yml` con `SPRING_PROFILES_ACTIVE=docker`
- [ ] Reemplazar `192.168.100.207` con nombres de servicio
- [ ] Validar con Docker Compose
- [ ] Validar ejecución local

#### Progreso
```
░░░░░░░░░░░░░░░░░░░░░░░░░░░░ 0% (0/6 subtareas)
```

#### Tiempo Invertido
- **Estimado**: 2h
- **Real**: -
- **Variación**: -

---

### Tarea 4: Admin Service 📊
**Prioridad**: P1 - Alto  
**Estimación**: 8h  
**Estado**: 🔴 Pendiente  
**Asignado a**: _[Nombre]_

#### Subtareas - Crear Módulo (4h)
- [ ] Agregar módulo en `pom.xml` raíz
- [ ] Crear estructura de directorios
- [ ] Crear `admin-service/pom.xml`
- [ ] Crear `AdminServiceApplication.java`
- [ ] Crear `SecurityConfig.java`
- [ ] Crear `application.yml`
- [ ] Crear `Dockerfile`

#### Subtareas - Modificar Servicios (4h)
- [ ] Cambiar dependencia en `authentication`
- [ ] Cambiar dependencia en `user-service`
- [ ] Cambiar dependencia en `workout-service`
- [ ] Configurar Admin Client en cada servicio
- [ ] Agregar al `docker-compose.yml`
- [ ] Validar dashboard funciona

#### Progreso
```
Crear Módulo:       ░░░░░░░░░░░░░░ 0% (0/7 subtareas)
Modificar Servicios: ░░░░░░░░░░░░░░ 0% (0/6 subtareas)
Total:              ░░░░░░░░░░░░░░ 0% (0/13 subtareas)
```

#### Tiempo Invertido
- **Estimado**: 8h
- **Real**: -
- **Variación**: -

---

### Tarea 5: Securizar Actuator ⚙️
**Prioridad**: P1 - Alto  
**Estimación**: 2h  
**Estado**: 🔴 Pendiente  
**Asignado a**: _[Nombre]_

#### Subtareas
- [ ] Configurar endpoints expuestos en `authentication`
- [ ] Configurar endpoints expuestos en `user-service`
- [ ] Configurar endpoints expuestos en `workout-service`
- [ ] Configurar `show-details: when-authorized`
- [ ] Habilitar probes para Kubernetes
- [ ] Validar seguridad (scan)

#### Progreso
```
░░░░░░░░░░░░░░░░░░░░░░░░░░░░ 0% (0/6 subtareas)
```

#### Tiempo Invertido
- **Estimado**: 2h
- **Real**: -
- **Variación**: -

---

## 🚧 Bloqueadores e Impedimentos

### Activos
_Ninguno por el momento_

### Resueltos
_Ninguno aún_

---

## 📅 Daily Standup

### Lunes 4 de noviembre
**Participantes**: _[Nombres]_

#### ¿Qué hicimos ayer?
- Sprint planning completado
- Documentación revisada

#### ¿Qué haremos hoy?
- Tarea 1: Gestión de Secretos
- Tarea 5: Securizar Actuator

#### Bloqueadores
- Ninguno

#### Notas
- 

---

### Martes 5 de noviembre
**Participantes**: _[Nombres]_

#### ¿Qué hicimos ayer?
- 

#### ¿Qué haremos hoy?
- Tarea 2.1: Flyway en user-service

#### Bloqueadores
- 

#### Notas
- 

---

### Miércoles 6 de noviembre
**Participantes**: _[Nombres]_

#### ¿Qué hicimos ayer?
- 

#### ¿Qué haremos hoy?
- Tarea 2.2: Flyway en workout-service

#### Bloqueadores
- 

#### Notas
- 

---

### Jueves 7 de noviembre
**Participantes**: _[Nombres]_

#### ¿Qué hicimos ayer?
- 

#### ¿Qué haremos hoy?
- Tarea 3: Eliminar IPs
- Tarea 4: Inicio Admin Service

#### Bloqueadores
- 

#### Notas
- 

---

### Viernes 8 de noviembre
**Participantes**: _[Nombres]_

#### ¿Qué hicimos ayer?
- 

#### ¿Qué haremos hoy?
- Tarea 4: Completar Admin Service
- Testing integral
- Sprint Retrospective

#### Bloqueadores
- 

#### Notas
- 

---

## 📊 Métricas por Categoría

### 🔒 SEGURIDAD
- Tareas totales: 2
- Completadas: 0
- Horas estimadas: 4h
- Horas reales: -

### 💾 ESTABILIDAD
- Tareas totales: 1
- Completadas: 0
- Horas estimadas: 8h
- Horas reales: -

### 📊 OBSERVABILIDAD
- Tareas totales: 1
- Completadas: 0
- Horas estimadas: 8h
- Horas reales: -

### ⚙️ CONFIGURACIÓN
- Tareas totales: 2
- Completadas: 0
- Horas estimadas: 4h
- Horas reales: -

---

## ✅ Definition of Done

Una tarea se marca como completada cuando:

- [ ] ✅ Código implementado
- [ ] ✅ Tests pasando
- [ ] ✅ Documentación actualizada
- [ ] ✅ Code review aprobado
- [ ] ✅ Validación manual exitosa
- [ ] ✅ Commit realizado
- [ ] ✅ Sin warnings en build
- [ ] ✅ Docker Compose funciona

---

## 🎬 Conclusión del Sprint

### Tareas Completadas
_Lista de tareas finalizadas_

### Tareas No Completadas
_Lista de tareas que pasaron al siguiente sprint_

### Lecciones Aprendidas
_Agregar al final del sprint_

### Acciones de Mejora
_Para el próximo sprint_

---

**Última actualización**: 1 de noviembre de 2025  
**Actualizado por**: _[Nombre]_
