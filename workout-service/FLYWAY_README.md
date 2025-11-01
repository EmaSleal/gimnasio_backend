# Migraciones de Base de Datos - Workout Service

Este servicio utiliza **Flyway** para gestionar las migraciones de la base de datos `gym_exercise`.

## 📋 Estructura de Migraciones

```
workout-service/src/main/resources/db/migration/
├── V1__initial_schema.sql          - Schema inicial (tablas)
├── V2__seed_initial_data.sql       - Datos de prueba
└── V3__alter_workout_plan_timestamp_columns.sql - Fix tipos timestamp
```

## 🗃️ Tablas Creadas

### Tablas Principales

1. **`muscular_groups`** - Grupos musculares (Pecho, Espalda, Piernas, etc.)
2. **`workout`** - Ejercicios individuales
3. **`workout_specification`** - Especificaciones de ejercicios (series, repeticiones, peso)
4. **`daily_routine`** - Rutinas diarias
5. **`workout_plan`** - Planes de entrenamiento asignados a usuarios

### Tablas de Relación

6. **`daily_routine_days`** - Días de la semana para cada rutina (ElementCollection)
7. **`workout_specification_daily_routine`** - Relación Many-to-Many entre especificaciones y rutinas
8. **`daily_routine_workout_plan`** - Relación Many-to-Many entre rutinas y planes

## 🔄 Historial de Migraciones

| Versión | Archivo | Descripción | Fecha Aplicación |
|---------|---------|-------------|------------------|
| 1 | `V1__initial_schema.sql` | Creación de 8 tablas del sistema de workout | 2024-11-01 |
| 2 | `V2__seed_initial_data.sql` | Inserts de datos de prueba (6 grupos musculares, 8 workouts, 8 especificaciones, etc.) | 2024-11-01 |
| 3 | `V3__alter_workout_plan_timestamp_columns.sql` | Conversión de created_at/updated_at de VARCHAR a TIMESTAMP | 2024-11-01 |

## 📊 Datos de Prueba (V2)

### Muscular Groups
- Pecho, Espalda, Piernas, Hombros, Brazos, Core

### Workouts (Ejercicios)
- Press de Banca, Flexiones, Dominadas, Remo con Barra, Sentadillas, Press Militar, Curl de Bíceps, Plancha

### Workout Plans
- Plan de fuerza básico (Usuario 3, Entrenador 2)
- Plan de hipertrofia (Usuario 3, Entrenador 2)
- Template: Rutina Full Body (Template reutilizable)

## ⚙️ Configuración Actual

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # Solo validación, NO modifica schema
  flyway:
    enabled: true
    baseline-on-migrate: true  # Permite trabajar con BD existentes
    clean-disabled: true       # Protección contra borrado accidental
    validate-on-migrate: true  # Validación antes de migrar
```

## 🚀 Cómo Usar

### Verificar Estado de Migraciones

```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

### Aplicar Nuevas Migraciones

1. Crear archivo `V4__descripcion.sql` en `src/main/resources/db/migration/`
2. Reiniciar el servicio - Flyway detecta y aplica automáticamente

### Rollback (Manual)

Flyway no soporta rollback automático. Para revertir:

```sql
-- 1. Verificar versión actual
SELECT version FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 1;

-- 2. Eliminar registro de migración
DELETE FROM flyway_schema_history WHERE version = 'X';

-- 3. Revertir cambios manualmente (según la migración)
-- Ejemplo para V3:
ALTER TABLE workout_plan ALTER COLUMN created_at TYPE VARCHAR(50);
ALTER TABLE workout_plan ALTER COLUMN updated_at TYPE VARCHAR(50);
```

## 🛠️ Solución de Problemas

### Error: "Schema-validation: wrong column type"

**Causa**: La base de datos tiene un schema diferente al esperado por Hibernate.

**Solución**: Crear una migración para alinear el schema:
```sql
-- Ejemplo V3: Corregir tipos de columna
ALTER TABLE workout_plan 
ALTER COLUMN created_at TYPE TIMESTAMP WITHOUT TIME ZONE 
USING TO_TIMESTAMP(created_at, 'YYYY-MM-DD:HH24:MI:SS');
```

### Error: "Flyway schema history table not found"

**Causa**: Primera ejecución de Flyway en una BD existente.

**Solución**: Flyway crea automáticamente la tabla con `baseline-on-migrate: true`.

### Resetear Base de Datos (Solo Desarrollo)

**⚠️ CUIDADO: Esto borra TODOS los datos**

```sql
-- Conectar a gym_exercise
DROP TABLE IF EXISTS flyway_schema_history CASCADE;
DROP TABLE IF EXISTS workout_specification_daily_routine CASCADE;
DROP TABLE IF EXISTS daily_routine_workout_plan CASCADE;
DROP TABLE IF EXISTS daily_routine_days CASCADE;
DROP TABLE IF EXISTS workout_specification CASCADE;
DROP TABLE IF EXISTS workout CASCADE;
DROP TABLE IF EXISTS daily_routine CASCADE;
DROP TABLE IF EXISTS workout_plan CASCADE;
DROP TABLE IF EXISTS muscular_groups CASCADE;
```

Luego reiniciar el servicio para recrear todo.

## 📝 Mejores Prácticas

1. **Nunca modificar migraciones ya aplicadas** - Crear nuevas migraciones en su lugar
2. **Usar transacciones** - Flyway las maneja automáticamente para PostgreSQL
3. **Nombrar descriptivamente** - `V{version}__{descripcion_clara}.sql`
4. **Probar en desarrollo** - Antes de aplicar en producción
5. **Backup antes de migrar** - Especialmente en producción

## 🔍 Logs Importantes

Cuando el servicio inicia correctamente con Flyway:

```
✅ Flyway Community Edition 9.22.3 by Redgate
✅ Database: jdbc:postgresql://192.168.100.207:5432/gym_exercise (PostgreSQL 15.1)
✅ Successfully validated 3 migrations (execution time 00:00.029s)
✅ Current version of schema "public": 3
✅ Schema "public" is up to date. No migration necessary.
✅ Initialized JPA EntityManagerFactory for persistence unit 'default'
✅ Started WorkoutServerApplication in X.XXX seconds
```

## 📚 Referencias

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [PostgreSQL Data Types](https://www.postgresql.org/docs/current/datatype.html)
- [Spring Boot Flyway Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

---

**Última Actualización**: 1 de noviembre de 2024  
**Versión Actual**: V3  
**Estado**: ✅ Operativo
