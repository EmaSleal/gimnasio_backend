# Sprint 1 - Decisiones de Implementación

## 📋 Sistema de Clasificación de Tareas

Para organizar mejor las modificaciones, utilizaremos el siguiente sistema de clasificación:

### Categorías

| Categoría | Emoji | Descripción | Criterios |
|-----------|-------|-------------|-----------|
| **SEGURIDAD** | 🔒 | Vulnerabilidades, exposición de datos, autenticación | - Expone secretos<br>- Riesgo de acceso no autorizado<br>- Cumplimiento de estándares |
| **ESTABILIDAD** | 💾 | Pérdida de datos, crashes, comportamiento impredecible | - Puede causar pérdida de datos<br>- Afecta disponibilidad<br>- Comportamiento no determinístico |
| **ARQUITECTURA** | 🏗️ | Diseño de servicios, patrones, escalabilidad | - Acoplamiento alto<br>- Violación de principios SOLID<br>- Dificultad para escalar |
| **OBSERVABILIDAD** | 📊 | Monitoreo, logs, métricas, debugging | - Dificulta debugging<br>- Falta de visibilidad<br>- Sin alertas |
| **CONFIGURACIÓN** | ⚙️ | Gestión de config, portabilidad, multi-entorno | - Hardcoded values<br>- No portable<br>- Duplicación de config |

### Niveles de Prioridad

- **P0 - Crítico**: Debe hacerse inmediatamente (bloquea producción)
- **P1 - Alto**: Debe hacerse en este sprint
- **P2 - Medio**: Importante pero puede esperar
- **P3 - Bajo**: Nice to have
- **P4 - Backlog**: Mejora futura

---

## 🎯 Objetivos del Sprint 1

**Duración**: 1 semana (5 días hábiles)  
**Fecha Inicio**: 4 de noviembre de 2025  
**Fecha Fin**: 8 de noviembre de 2025

### Meta Principal
Estabilizar el sistema y eliminar riesgos críticos de seguridad y pérdida de datos, preparándolo para un entorno productivo.

### Métricas de Éxito
- ✅ Cero secretos en código fuente
- ✅ Persistencia de datos garantizada
- ✅ Sistema observable con dashboard centralizado
- ✅ Configuración portable entre entornos

---

## 📝 Decisiones y Tareas del Sprint 1

### 1. Gestión de Secretos 🔒

**Clasificación**: SEGURIDAD | P0 - Crítico  
**Estimación**: 2 horas  
**Asignado a**: _[Nombre del desarrollador]_

#### Decisión
Migrar TODOS los secretos de archivos `application.yml` a variables de entorno usando archivo `.env` para Docker Compose y variables de entorno nativas para otros despliegues.

#### Problema Actual
```yaml
# ❌ EXPUESTO EN GIT
jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
spring:
  datasource:
    password: Chismosear01
sender:
  key: re_X7qY3NFp_ETffUyjtLJpgTMcrzdhvdB4c
```

#### Solución Implementada
- [x] Crear archivo `.env.example` (template) ✅
- [ ] Crear archivo `.env` real (no commitearlo)
- [ ] Actualizar `.gitignore` para excluir `.env` ✅
- [ ] Modificar `application.yml` para usar `${VARIABLE_NAME}`
- [ ] Actualizar `docker-compose.yml` con `env_file: .env`
- [ ] Rotar TODOS los secretos actuales

#### Archivos Afectados
- `api-gateway/src/main/resources/application.yml`
- `authentication/src/main/resources/application.yml`
- `user-service/src/main/resources/application.yml`
- `workout-service/src/main/resources/application.yml`
- `docker-compose.yml`
- `.env` (nuevo)
- `.gitignore`

#### Comandos de Generación de Secretos
```bash
# Generar nuevo JWT secret (256 bits)
openssl rand -base64 64

# Generar password seguro
openssl rand -base64 32

# Resultado para .env
JWT_SECRET=<resultado_del_comando_1>
DB_PASSWORD=<resultado_del_comando_2>
```

#### Validación
- [ ] Servicios NO inician sin variables de entorno
- [ ] Login funcional con nuevo JWT secret
- [ ] `.env` NO aparece en `git status`
- [ ] Scan con git-secrets pasa exitosamente

#### Riesgos
- **Alto**: Si no se rotan los secretos, los antiguos siguen comprometidos
- **Medio**: Olvidar algún secreto en algún archivo

#### Notas
> ⚠️ **IMPORTANTE**: Después de implementar, ejecutar `git filter-branch` o BFG Repo-Cleaner para eliminar secretos del historial de Git.

---

### 2. Persistencia de Datos con Flyway 💾

**Clasificación**: ESTABILIDAD | P0 - Crítico  
**Estimación**: 8 horas (4h user-service + 4h workout-service)  
**Asignado a**: _[Nombre del desarrollador]_

#### Decisión
Implementar Flyway para migraciones de base de datos versionadas y cambiar `ddl-auto` de `create`/`create-drop` a `validate` para evitar pérdida de datos.

#### Problema Actual
```yaml
# ❌ PÉRDIDA DE DATOS EN CADA REINICIO
spring:
  jpa:
    hibernate:
      ddl-auto: create        # user-service
      ddl-auto: create-drop   # workout-service
```

**Impacto**: Reiniciar servicio = Borrar toda la base de datos

#### Solución Implementada

##### Fase 1: User Service (4 horas)
- [ ] Agregar dependencias Flyway en `pom.xml`
- [ ] Cambiar `ddl-auto: create` → `validate`
- [ ] Crear directorio `src/main/resources/db/migration`
- [ ] Crear `V1__create_users_table.sql`
- [ ] Crear `V2__add_indexes.sql`
- [ ] Configurar Flyway en `application.yml`
- [ ] Validar migraciones

##### Fase 2: Workout Service (4 horas)
- [ ] Repetir proceso de Flyway
- [ ] Cambiar `ddl-auto: create-drop` → `validate`
- [ ] Crear migraciones:
  - `V1__create_muscular_groups_table.sql`
  - `V2__create_workouts_table.sql`
  - `V3__create_workout_plans_table.sql`
  - `V4__create_daily_routines_table.sql`
  - `V5__create_workout_specifications_table.sql`
  - `V6__add_foreign_keys.sql`
  - `V7__add_indexes.sql`

#### Archivos Afectados
- `user-service/pom.xml`
- `user-service/src/main/resources/application.yml`
- `user-service/src/main/resources/db/migration/V*.sql` (nuevos)
- `workout-service/pom.xml`
- `workout-service/src/main/resources/application.yml`
- `workout-service/src/main/resources/db/migration/V*.sql` (nuevos)

#### Configuración Flyway
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # ✅ CAMBIO CRÍTICO
  flyway:
    enabled: true
    baseline-on-migrate: true
    baseline-version: 0
    locations: classpath:db/migration
```

#### Validación
- [ ] Tabla `flyway_schema_history` creada
- [ ] Registrar usuario de prueba
- [ ] Reiniciar servicio con `docker-compose restart user-service`
- [ ] Verificar que usuario sigue existiendo
- [ ] Crear workout de prueba
- [ ] Reiniciar servicio con `docker-compose restart workout-service`
- [ ] Verificar que workout sigue existiendo

#### Riesgos
- **Alto**: Perder datos existentes al migrar si no se hace backup
- **Medio**: Conflictos entre schema actual y migraciones

#### Notas
> 💡 **TIP**: Antes de implementar, hacer backup de PostgreSQL:
> ```bash
> docker exec db-microservices pg_dump -U postgres gym_authentication > backup_auth.sql
> docker exec db-microservices pg_dump -U postgres gym_exercise > backup_exercise.sql
> ```

---

### 3. Eliminar IPs Hardcodeadas ⚙️

**Clasificación**: CONFIGURACIÓN | P1 - Alto  
**Estimación**: 2 horas  
**Asignado a**: _[Nombre del desarrollador]_

#### Decisión
Eliminar todas las IPs hardcodeadas (`192.168.100.111`) y usar nombres de servicio de Docker + perfiles de Spring para diferentes entornos.

#### Problema Actual
```yaml
# ❌ NO PORTABLE
eureka:
  client:
    service-url:
      defaultZone: http://192.168.100.111:8761/eureka/
spring:
  datasource:
    url: jdbc:postgresql://192.168.100.111:5432/gym_authentication
```

**Impacto**: 
- No funciona en otras máquinas
- Rompe networking de Docker Compose
- Imposible deployar en otros entornos

#### Solución Implementada

##### Crear Perfiles por Entorno
- [ ] Crear `application-docker.yml` (para Docker Compose)
- [ ] Crear `application-local.yml` (para desarrollo local)
- [ ] Crear `application-prod.yml` (para producción)
- [ ] Actualizar `docker-compose.yml` con `SPRING_PROFILES_ACTIVE=docker`

##### Configuraciones por Perfil

**application-docker.yml**:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/${DB_NAME}
```

**application-local.yml**:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/gym_authentication
```

#### Archivos Afectados
- Todos los `application.yml` de servicios
- `docker-compose.yml`
- Nuevos archivos `application-docker.yml` y `application-local.yml`

#### Validación
- [ ] Levantar con Docker: `docker-compose up -d`
- [ ] Verificar Eureka dashboard muestra todos los servicios
- [ ] Testear conectividad a PostgreSQL
- [ ] Ejecutar flujo completo: registro → login → consulta
- [ ] Detener Docker y probar ejecución local

#### Riesgos
- **Bajo**: Olvidar algún servicio en la configuración
- **Bajo**: Confusión entre perfiles

---

### 4. Admin Service para Actuator 📊

**Clasificación**: OBSERVABILIDAD | P1 - Alto  
**Estimación**: 6-8 horas  
**Asignado a**: _[Nombre del desarrollador]_

#### Decisión
Crear un microservicio dedicado usando **Spring Boot Admin** para centralizar el monitoreo de todos los endpoints de Actuator en lugar de exponerlos individualmente en cada servicio.

#### Problema Actual
- Actuator embebido en 3 servicios diferentes
- Sin configuración de qué endpoints exponer
- Sin seguridad
- Monitoreo fragmentado (hay que revisar 3 URLs)
- Potencial exposición de información sensible

#### Solución Implementada

##### Crear Nuevo Módulo
- [ ] Agregar `<module>admin-service</module>` en `pom.xml` raíz
- [ ] Crear estructura de directorios
- [ ] Crear `admin-service/pom.xml` con dependencias
- [ ] Crear `AdminServiceApplication.java` con `@EnableAdminServer`
- [ ] Crear `SecurityConfig.java` para proteger dashboard
- [ ] Crear `application.yml` para Admin Service

##### Modificar Servicios Existentes
- [ ] Cambiar dependencia de `spring-boot-admin-starter-server` a `-client` en:
  - `authentication/pom.xml`
  - `user-service/pom.xml`
  - `workout-service/pom.xml`
- [ ] Agregar configuración de Admin Client en cada `application.yml`
- [ ] Configurar exposición de endpoints de Actuator

##### Docker
- [ ] Crear `admin-service/Dockerfile`
- [ ] Agregar servicio al `docker-compose.yml`
- [ ] Exponer puerto 9000

#### Archivos Afectados
- `pom.xml` (raíz)
- `admin-service/` (directorio completo nuevo)
- `authentication/pom.xml` y `application.yml`
- `user-service/pom.xml` y `application.yml`
- `workout-service/pom.xml` y `application.yml`
- `docker-compose.yml`

#### Configuración Admin Client
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"  # Admin Service consumirá estos
  endpoint:
    health:
      show-details: always

spring:
  boot:
    admin:
      client:
        url: http://admin-service:9000
        instance:
          prefer-ip: true
```

#### Validación
- [ ] Build exitoso: `mvn clean install`
- [ ] Levantar servicios: `docker-compose up -d`
- [ ] Acceder a `http://localhost:9000`
- [ ] Login con credenciales de admin
- [ ] Verificar que muestra 3+ servicios registrados
- [ ] Revisar métricas (CPU, memoria, threads)
- [ ] Revisar health checks
- [ ] Revisar logs en tiempo real
- [ ] Detener un servicio y verificar alerta

#### Riesgos
- **Medio**: Complejidad adicional al sistema
- **Bajo**: Posible overhead en performance (mínimo)

#### Beneficios
- ✅ Dashboard único centralizado
- ✅ Monitoreo en tiempo real
- ✅ Alertas automáticas
- ✅ Logs agregados
- ✅ Seguridad centralizada

---

### 5. Securizar Actuator Endpoints ⚙️

**Clasificación**: CONFIGURACIÓN + SEGURIDAD | P1 - Alto  
**Estimación**: 2 horas  
**Asignado a**: _[Nombre del desarrollador]_

#### Decisión
Configurar correctamente qué endpoints de Actuator están expuestos y con qué nivel de detalle, evitando exposición de información sensible.

#### Problema Actual
- Actuator incluido sin configuración específica
- Puede exponer variables de entorno, propiedades, etc.
- Sin control de acceso

#### Solución Implementada

##### Configuración Segura
- [ ] Configurar endpoints expuestos en cada servicio
- [ ] Configurar `show-details: when-authorized`
- [ ] Habilitar probes para Kubernetes (futuro)
- [ ] Exponer solo endpoints necesarios

#### Archivos Afectados
- Todos los `application.yml` de servicios con Actuator

#### Configuración
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true
  metrics:
    export:
      prometheus:
        enabled: true

info:
  app:
    name: @project.name@
    version: @project.version@
    description: User management service
```

#### Validación
- [ ] Acceder a `/actuator` sin autenticación
- [ ] Verificar que solo muestra endpoints permitidos
- [ ] Verificar que `/actuator/env` NO está expuesto
- [ ] Verificar que health muestra detalles solo con auth
- [ ] Scan de seguridad pasa

---

## 📊 Resumen del Sprint 1

### Distribución de Tareas por Categoría

| Categoría | Tareas | Horas Estimadas |
|-----------|--------|-----------------|
| 🔒 SEGURIDAD | 2 | 4h |
| 💾 ESTABILIDAD | 1 | 8h |
| 🏗️ ARQUITECTURA | 0 | 0h |
| 📊 OBSERVABILIDAD | 1 | 8h |
| ⚙️ CONFIGURACIÓN | 2 | 4h |
| **TOTAL** | **6** | **24h** |

### Distribución por Prioridad

| Prioridad | Tareas | Porcentaje |
|-----------|--------|------------|
| P0 - Crítico | 2 | 33% |
| P1 - Alto | 4 | 67% |
| P2 - Medio | 0 | 0% |
| P3 - Bajo | 0 | 0% |

### Capacidad del Sprint

- **Horas totales estimadas**: 24 horas
- **Días del sprint**: 5 días
- **Horas por día**: ~5 horas
- **Desarrolladores**: 1-2 personas

---

## 🎯 Definición de "Hecho" (Definition of Done)

Una tarea se considera completada cuando:

- [ ] ✅ Código implementado y testeado localmente
- [ ] ✅ Tests automatizados pasando (si aplica)
- [ ] ✅ Documentación actualizada
- [ ] ✅ Code review completado
- [ ] ✅ Validación manual exitosa
- [ ] ✅ Commit realizado con mensaje descriptivo
- [ ] ✅ Sin warnings ni errores en build
- [ ] ✅ Docker Compose levanta sin errores

---

## 📅 Planificación Diaria

### Día 1 (Lunes) - Seguridad
- [ ] Tarea 1: Gestión de Secretos (2h)
- [ ] Tarea 5: Securizar Actuator (2h)
- [ ] Testing y validación (1h)

### Día 2 (Martes) - Persistencia User Service
- [ ] Tarea 2.1: Flyway en user-service (4h)
- [ ] Validación y testing (1h)

### Día 3 (Miércoles) - Persistencia Workout Service
- [ ] Tarea 2.2: Flyway en workout-service (4h)
- [ ] Validación y testing (1h)

### Día 4 (Jueves) - Configuración
- [ ] Tarea 3: Eliminar IPs hardcodeadas (2h)
- [ ] Inicio Tarea 4: Admin Service - Setup (3h)

### Día 5 (Viernes) - Observabilidad
- [ ] Tarea 4: Admin Service - Completar (5h)
- [ ] Testing integral (2h)
- [ ] Retrospectiva del sprint (1h)

---

## 🚧 Bloqueadores Potenciales

1. **Pérdida de datos al migrar a Flyway**
   - Mitigación: Backups antes de cada cambio

2. **Conflictos de merge en archivos de configuración**
   - Mitigación: Trabajar en ramas separadas por tarea

3. **Servicios no levantan después de cambios**
   - Mitigación: Validar cada cambio inmediatamente

4. **Secretos antiguos siguen en historial de Git**
   - Mitigación: Ejecutar git filter-branch al final

---

## 📈 Métricas de Seguimiento

### Burndown Chart (Actualizar diariamente)

| Día | Horas Restantes | Tareas Completadas |
|-----|-----------------|-------------------|
| Día 0 (Inicio) | 24h | 0/6 |
| Día 1 | ___ | ___ |
| Día 2 | ___ | ___ |
| Día 3 | ___ | ___ |
| Día 4 | ___ | ___ |
| Día 5 | ___ | ___ |

### Velocity
- **Estimado**: 24 horas en 5 días
- **Real**: ___ horas (actualizar al final)
- **Tareas completadas**: ___/6

---

## ✅ Criterio de Finalización Sprint 1

- [ ] Ningún secreto en application.yml
- [ ] Archivos .env creados y documentados
- [ ] DDL auto = validate en ambos servicios
- [ ] Al menos 3 migraciones Flyway funcionando
- [ ] Base de datos preservada entre reinicios
- [ ] Config Server entregando configuraciones correctas
- [ ] Admin Service mostrando dashboard
- [ ] Actuator protegido y funcional
- [ ] IP configurable por perfiles

---

## 🔮 Temas para Sprint 2

### Observabilidad Avanzada (Diferido)
**� Stack de Métricas y Monitoreo**

**Contexto**: Micrometer actualmente solo exporta a Zipkin. Para una observabilidad completa del sistema, se necesita:

```
Capa Actual:
  [Servicios] → [Micrometer] → [Zipkin (Tracing)]
                              ❌ Sin métricas persistentes
                              ❌ Sin dashboards de rendimiento

Capa Propuesta Sprint 2:
  [Servicios] → [Micrometer] → [Prometheus] → [Grafana]
                             ↘ [Zipkin (Tracing)]
```

**Componentes a implementar**:
1. **Prometheus** (puerto 9090): Almacenamiento de métricas time-series
2. **Grafana** (puerto 3000): Dashboards y alertas visuales
3. **Micrometer Registry**: Configurar exportación a Prometheus
4. **Dashboards predefinidos**: JVM, Spring Boot, PostgreSQL, RabbitMQ

**Estimación**: 12-16 horas (Sprint 2 completo)

Ver planificación completa en: **[Sprint 2 README](../sprint-2/README.md)**

---

## �🔄 Retrospectiva (Completar al final del sprint)

### ¿Qué funcionó bien?
- 

### ¿Qué podemos mejorar?
- 

### ¿Qué impedimentos tuvimos?
- 

### Acciones para el próximo sprint
-

---

## 📝 Notas Adicionales

- Todas las tareas deben hacerse en ramas separadas: `feat/sprint1-tarea-N`
- Pull requests obligatorios antes de merge a `main`
- Reunión diaria de 15 minutos para sincronización
- Documentar cualquier decisión técnica en este archivo

---

**Última actualización**: 1 de noviembre de 2025  
**Responsable del sprint**: _[Nombre]_  
**Estado**: 🟡 Planificado
