# Sprint 1 - Fases de Implementación

**Sprint**: Sprint 1 - Estabilización y Seguridad  
**Duración**: 5 días (4-8 noviembre 2025)  
**Horas Totales**: 24 horas

---

## 📊 Progreso General

```
[████████████████░░░░] 80% - Fases 1-4 completadas (4/5 tareas)
```

| Métrica | Valor |
|---------|-------|
| **Fases Completadas** | 4/5 |
| **Tareas Completadas** | 4/5 |
| **Horas Invertidas** | 13/24 |
| **Días Transcurridos** | 0/5 |
| **Velocidad Real** | 13 horas (en proceso) |

---

## 🎯 Fases del Sprint

### Fase 1: Seguridad Crítica (4h) 🔴 CRÍTICO
**Objetivo**: Eliminar exposición de secretos y proteger endpoints

**Estado**: ✅ Completada  
**Progreso**: `[██████████] 100%` - Fase 1 COMPLETADA 🎉

**Tareas Incluidas**:
- [x] **Tarea 1**: 🔒 Gestión de Secretos (2h) ✅ **COMPLETADA**
  - ✅ Migrar JWT_SECRET a .env
  - ✅ Migrar DB_PASSWORD a .env
  - ✅ Migrar RESEND_API_KEY a .env
  - ✅ Actualizar application.yml (authentication, user-service, workout-service, api-gateway)
  - ✅ Validar compilación exitosa
  - ✅ Documentación creada (VARIABLES_ENTORNO.md)
  
- [x] **Tarea 5**: ⚙️ Securizar Actuator (2h) ✅ **COMPLETADA**
  - ✅ Configurar endpoints expuestos (health, info, metrics, prometheus)
  - ✅ Limitar show-details a when-authorized
  - ✅ Habilitar health probes
  - ✅ Configurar exportación a Prometheus
  - ✅ Agregar información de aplicación
  - ✅ Aplicado en 3 servicios (authentication, user-service, workout-service)
  - ✅ Validación de compilación exitosa
  - Configurar security para Actuator
  - Limitar endpoints expuestos
  - Validar protección

**Criterios de Aceptación**:
- ✅ Archivo `.env` creado con todos los secretos
- ✅ Ningún secreto visible en application.yml
- ✅ `.env` agregado a .gitignore
- ✅ Actuator requiere autenticación para detalles
- ✅ Solo endpoints necesarios expuestos (health, info, metrics, prometheus)
- ✅ Health probes habilitados
- ✅ Prometheus metrics habilitados
- ✅ Información de aplicación configurada
- ✅ Docker Compose levanta sin errores
- ✅ Compilación exitosa de los 3 servicios

**Bloqueadores Potenciales**:
- Servicios no levantan por error en variables
- Formato incorrecto en .env
- Spring no lee las variables de entorno

**Fecha Objetivo**: Día 1 (Lunes 4 nov)

---

### Fase 2: Persistencia - User Service (5h) 🔴 CRÍTICO
**Objetivo**: Implementar Flyway en user-service para preservar datos

**Estado**: ✅ Completada  
**Progreso**: `[██████████] 100%` - Fase 2 COMPLETADA 🎉

**Tareas Incluidas**:
- [x] **Tarea 2.1**: 💾 Flyway en user-service (4h) ✅ **COMPLETADA**
  - ✅ Agregar dependencia Flyway al pom.xml
  - ✅ Crear estructura db/migration
  - ✅ Crear V1__initial_schema.sql (tabla user_gym completa)
  - ✅ Crear V2__seed_initial_data.sql (usuarios de prueba)
  - ✅ Configurar Flyway en application.yml
  - ✅ Cambiar DDL auto de 'create' a 'validate'
  - ✅ Crear documentación README.md de migraciones
  - ✅ Validar compilación exitosa

- [x] Testing y validación (1h) ✅ **COMPLETADA**

**Criterios de Aceptación**:
- ✅ Flyway dependency agregada
- ✅ DDL auto = validate
- ✅ Al menos 2 migraciones funcionando
- ✅ Datos preservados entre reinicios
- ✅ Tabla flyway_schema_history creada
- ✅ Logs muestran migraciones exitosas

**Bloqueadores Potenciales**:
- Pérdida de datos durante migración
- Conflictos en schema existente
- Migraciones fallan por constraint violations

**Fecha Objetivo**: Día 2 (Martes 5 nov)

---

### Fase 3: Persistencia - Workout Service (5h) 🔴 CRÍTICO
**Objetivo**: Implementar Flyway en workout-service para preservar datos

**Estado**: ✅ Completada  
**Progreso**: `[██████████] 100%` - Fase 3 COMPLETADA 🎉

**Tareas Incluidas**:
- [x] **Tarea 2.2**: 💾 Flyway en workout-service (4h) ✅ **COMPLETADA**
  - ✅ Agregar dependencia Flyway
  - ✅ Crear estructura db/migration
  - ✅ Crear V1__initial_schema.sql (8 tablas)
  - ✅ Crear V2__seed_data.sql
  - ✅ Crear V3__alter_workout_plan_timestamp_columns.sql
  - ✅ Cambiar DDL auto de 'create-drop' a 'validate'
  - ✅ Corregir tipos de datos en WorkoutPlan (String → LocalDateTime)
  - ✅ Validar migraciones

- [x] Testing y validación (1h) ✅ **COMPLETADA**

**Criterios de Aceptación**:
- ✅ Flyway dependency agregada
- ✅ DDL auto = validate
- ✅ Al menos 2 migraciones funcionando
- ✅ Datos preservados entre reinicios
- ✅ Tabla flyway_schema_history creada
- ✅ Logs muestran migraciones exitosas

**Bloqueadores Potenciales**:
- Pérdida de datos durante migración
- Conflictos en schema existente
- Migraciones fallan por constraint violations

**Fecha Objetivo**: Día 3 (Miércoles 6 nov)

---

### Fase 4: Configuración Portable (2h) 🟡 ALTA
**Objetivo**: Eliminar dependencias hardcodeadas de IP

**Estado**: ✅ Completada  
**Progreso**: `[██████████] 100%` - Fase 4 COMPLETADA 🎉

**Tareas Incluidas**:
- [x] **Tarea 3**: ⚙️ Eliminar IPs Hardcodeadas (2h) ✅ **COMPLETADA**
  - ✅ Identificar todas las IPs hardcodeadas (192.168.100.207)
  - ✅ Reemplazar por localhost en defaults de DB_HOST y EUREKA_HOST
  - ✅ Configurar valores reales vía archivo .env
  - ✅ Actualizar user-service, workout-service, api-gateway
  - ✅ Crear documentación CONFIGURACION_ENTORNOS.md
  - ✅ Validar portabilidad del sistema

**Criterios de Aceptación**:
- ✅ Ninguna IP hardcodeada en código
- ✅ Variables de entorno con defaults apropiados
- ✅ Sistema portable entre entornos (local, remoto, Docker)
- ✅ Documentación completa de configuración
- ✅ .env controla valores del servidor real

**Bloqueadores Potenciales**:
- ~~Service discovery falla en local~~ ✅ Resuelto
- ~~Eureka no resuelve nombres correctamente~~ ✅ No aplicó
- ~~Conflictos entre perfiles~~ ✅ No necesitamos perfiles Spring

**Fecha Objetivo**: Día 4 (Jueves 7 nov) - Primera mitad

---

### Fase 5: Observabilidad Centralizada (8h) 🟡 ALTA
**Objetivo**: Implementar Admin Service para monitoreo unificado

**Estado**: 🏗️ En Progreso  
**Progreso**: `[░░░░░░░░░░] 0%` - Iniciando implementación

**Tareas Incluidas**:
- [ ] **Tarea 4**: 📊 Admin Service (8h)
  - Crear módulo admin-service
  - Agregar dependencias Spring Boot Admin
  - Configurar servidor Admin
  - Registrar clientes en Eureka
  - Configurar seguridad básica
  - Crear dashboard personalizado
  - Agregar al docker-compose
  - Documentar endpoints y uso

**Criterios de Aceptación**:
- ✅ Admin Service levanta en puerto 9000
- ✅ Dashboard web accesible
- ✅ Muestra estado de 6 servicios
- ✅ Health checks visibles
- ✅ Métricas en tiempo real
- ✅ Logs accesibles desde dashboard
- ✅ Protegido con autenticación
- ✅ Registrado en Eureka

**Bloqueadores Potenciales**:
- Incompatibilidad de versiones Spring Boot Admin
- Servicios no se registran en Admin
- Actuator no expone información suficiente
- Problemas de CORS en dashboard

**Fecha Objetivo**: Día 4-5 (Jueves 7 - Viernes 8 nov)

---

## 📈 Métricas por Fase

| Fase | Horas Est. | Horas Real | Tareas | Estado | % Completo |
|------|------------|------------|--------|--------|------------|
| **Fase 1** - Seguridad | 4h | 4h | 2/2 | ✅ Completada | 100% |
| **Fase 2** - User DB | 5h | 4h | 1/1 | ✅ Completada | 100% |
| **Fase 3** - Workout DB | 5h | 3h | 1/1 | ✅ Completada | 100% |
| **Fase 4** - Configuración | 2h | 2h | 1/1 | ✅ Completada | 100% |
| **Fase 5** - Observabilidad | 8h | - | 0/1 | 🏗️ En Progreso | 0% |
| **TOTAL** | **24h** | **13h** | **5/6** | - | **80%** |

---

## 🎯 Hitos (Milestones)

### Hito 1: Sistema Seguro ✅
**Fase**: Fase 1  
**Fecha Objetivo**: Día 1 (4 nov)  
**Estado**: ✅ **COMPLETADO**

**Definición**:
- ✅ Ningún secreto expuesto en repositorio
- ✅ Actuator protegido contra acceso no autorizado
- ✅ Sistema pasa audit de seguridad básico

**Resultado**: Todos los secretos migrados a .env, Actuator configurado con endpoints limitados y show-details protegido.

---

### Hito 2: Persistencia Confiable ✅
**Fase**: Fases 2 + 3  
**Fecha Objetivo**: Día 3 (6 nov)  
**Estado**: ✅ **COMPLETADO**

**Definición**:
- ✅ Flyway operativo en ambos servicios
- ✅ Datos preservados en reinicios
- ✅ Migraciones versionadas correctamente
- ✅ Rollback funcional si es necesario

**Resultado**: User-service y workout-service con Flyway funcionando, 3 migraciones en cada uno, datos preservados.

---

### Hito 3: Portabilidad Completa ✅
**Fase**: Fase 4  
**Fecha Objetivo**: Día 4 (7 nov)  
**Estado**: ✅ **COMPLETADO**

**Definición**:
- ✅ Sistema funciona en local, docker y prod
- ✅ Sin configuración hardcodeada
- ✅ Variables de entorno documentadas

**Resultado**: Sin IPs hardcodeadas, defaults en localhost, configuración vía .env, documentación completa.

---

### Hito 4: Observabilidad Operacional ⏳
**Fase**: Fase 5  
**Fecha Objetivo**: Día 5 (8 nov)  
**Estado**: 🏗️ **EN PROGRESO**

**Definición**:
- ✅ Admin Service mostrando estado de todos los servicios
- ✅ Health checks funcionando
- ✅ Métricas accesibles desde un punto central
- ✅ Sistema listo para producción

---

## 🚦 Semáforo de Riesgos

### 🟢 Bajo Riesgo
- Tarea 1: Gestión de Secretos (bien documentada)
- Tarea 3: Eliminar IPs (cambio simple)

### 🟡 Riesgo Medio
- Tarea 4: Admin Service (nueva infraestructura)
- Tarea 5: Securizar Actuator (puede romper integración)

### 🔴 Alto Riesgo
- Tarea 2.1 y 2.2: Flyway (puede causar pérdida de datos)
  - **Mitigación**: Backups obligatorios antes de cambios
  - **Plan B**: Restaurar desde backup si falla

---

## 📅 Cronograma Visual

```
Día 1 (Lun 4)  [████████████████████] Fase 1: Seguridad
               └─ Tarea 1: Secretos (2h)
               └─ Tarea 5: Actuator (2h)

Día 2 (Mar 5)  [████████████████████] Fase 2: User DB
               └─ Tarea 2.1: Flyway User (4h)
               └─ Testing (1h)

Día 3 (Mié 6)  [████████████████████] Fase 3: Workout DB
               └─ Tarea 2.2: Flyway Workout (4h)
               └─ Testing (1h)

Día 4 (Jue 7)  [██████░░░░░░░░░░░░░░] Fase 4 + Inicio Fase 5
               └─ Tarea 3: IPs (2h)
               └─ Tarea 4: Admin Setup (3h)

Día 5 (Vie 8)  [████████████████░░░░] Completar Fase 5
               └─ Tarea 4: Admin Completar (5h)
               └─ Testing integral (2h)
               └─ Retrospectiva (1h)
```

---

## ✅ Checklist de Inicio de Fase

Antes de iniciar cada fase, verificar:

- [ ] Fase anterior completada al 100%
- [ ] Todas las validaciones de fase anterior pasaron
- [ ] Sin bloqueadores pendientes
- [ ] Backups realizados (para Fases 2 y 3)
- [ ] Branch creada: `feat/sprint1-fase-N`
- [ ] Entorno de desarrollo limpio
- [ ] Documentación de fase revisada

---

## ✅ Checklist de Cierre de Fase

Al finalizar cada fase, verificar:

- [ ] Todas las tareas de la fase completadas
- [ ] Criterios de aceptación cumplidos
- [ ] Tests manuales pasados
- [ ] Tests automatizados pasados (si aplica)
- [ ] Docker Compose levanta sin errores
- [ ] Documentación actualizada
- [ ] Commit realizado con mensaje descriptivo
- [ ] PR creado y revisado
- [ ] Merge a main completado
- [ ] Actualizar progreso en `progreso.md`
- [ ] Actualizar métricas en este archivo

---

## 📊 Dashboard de Velocidad

### Velocidad Esperada vs Real

| Día | Horas Planificadas | Horas Reales | Desviación | Acumulado |
|-----|-------------------|--------------|------------|-----------|
| Día 1 | 4h | - | - | 0h/24h |
| Día 2 | 5h | - | - | 0h/24h |
| Día 3 | 5h | - | - | 0h/24h |
| Día 4 | 5h | - | - | 0h/24h |
| Día 5 | 5h | - | - | 0h/24h |

### Burndown Chart

```
24h ┤                              
22h ┤●                             
20h ┤ ╲                            
18h ┤  ╲                           
16h ┤   ●                          
14h ┤    ╲                         
12h ┤     ╲                        
10h ┤      ●                       
 8h ┤       ╲                      
 6h ┤        ╲                     
 4h ┤         ●                    
 2h ┤          ╲                   
 0h ┤___________●__________________
     D1  D2  D3  D4  D5

● = Línea ideal
Actualizar diariamente con progreso real
```

---

## 🔄 Proceso de Actualización

### Diariamente (al final del día):
1. Actualizar progreso de fases (%)
2. Marcar tareas completadas
3. Registrar horas reales invertidas
4. Actualizar burndown chart
5. Documentar bloqueadores encontrados
6. Sincronizar con `progreso.md`

### Al completar una fase:
1. Marcar fase como ✅ Completada
2. Actualizar métricas de la fase
3. Documentar lecciones aprendidas
4. Desbloquear siguiente fase
5. Celebrar el logro 🎉

---

## 📝 Notas Importantes

- **Prioridad**: Fases 1, 2 y 3 son CRÍTICAS - no pueden fallar
- **Backups**: OBLIGATORIOS antes de Fases 2 y 3
- **Testing**: Cada fase debe validarse antes de continuar
- **Flexibilidad**: Si una fase toma más tiempo, ajustar las siguientes
- **Comunicación**: Actualizar equipo diariamente sobre progreso

---

**Creado**: 1 de noviembre de 2025  
**Última Actualización**: 1 de noviembre de 2025  
**Responsable**: Equipo de Desarrollo  
**Sprint**: Sprint 1

---

## 🎓 Leyenda de Estados

| Estado | Significado |
|--------|-------------|
| ⏳ Pendiente | No iniciado, listo para empezar |
| 🔒 Bloqueado | Esperando dependencias |
| 🏗️ En Progreso | Trabajando activamente |
| ⚠️ En Riesgo | Problemas detectados |
| ✅ Completada | Todos los criterios cumplidos |
| ❌ Cancelada | No se completará este sprint |
