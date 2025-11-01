# Flyway Database Migrations - User Service

## 📋 Descripción

Este directorio contiene las migraciones de base de datos gestionadas por Flyway para el servicio de usuarios (`gym_authentication` database).

---

## 📁 Estructura de Migraciones

```
db/migration/
├── V1__initial_schema.sql      # Schema inicial de user_gym table
├── V2__seed_initial_data.sql   # Datos iniciales (usuarios de prueba)
└── README.md                    # Este archivo
```

---

## 🔢 Convenciones de Nomenclatura

Flyway utiliza un sistema de versionado basado en el nombre del archivo:

```
V{VERSION}__{DESCRIPTION}.sql
```

- **V** = Versioned migration (obligatorio)
- **VERSION** = Número de versión (1, 2, 3, 1.1, 2.5, etc.)
- **__** = Doble guión bajo (separador obligatorio)
- **DESCRIPTION** = Descripción snake_case o camelCase
- **.sql** = Extensión del archivo

### Ejemplos:
- ✅ `V1__initial_schema.sql`
- ✅ `V2__seed_initial_data.sql`
- ✅ `V3__add_user_profile_fields.sql`
- ✅ `V1.1__hotfix_email_constraint.sql`
- ❌ `v1_initial.sql` (v minúscula)
- ❌ `V1_initial.sql` (un solo guión bajo)
- ❌ `V1__initial schema.sql` (espacios no permitidos)

---

## 📝 Migraciones Existentes

### V1__initial_schema.sql
**Propósito**: Crear la tabla `user_gym` con todos los campos necesarios

**Contenido**:
- Tabla `user_gym` con campos:
  - `id` (BIGSERIAL PRIMARY KEY)
  - `user_name` (VARCHAR, UNIQUE, NOT NULL)
  - `password` (VARCHAR, NOT NULL)
  - `email` (VARCHAR, UNIQUE, NOT NULL)
  - `role` (VARCHAR con CHECK constraint: ADMIN, TRAINER, CLIENT)
  - Campos de seguridad: `enabled`, `account_non_expired`, etc.
  - Auditoría: `created_by`, `last_modified_by`, `created_at`, `updated_at`
  
- Índices para performance:
  - `idx_user_gym_username`
  - `idx_user_gym_email`
  - `idx_user_gym_role`
  - `idx_user_gym_enabled`

- Comentarios SQL para documentación

**Ejecutado**: Primera vez que el servicio levanta con Flyway habilitado

---

### V2__seed_initial_data.sql
**Propósito**: Insertar usuarios iniciales para pruebas y desarrollo

**Contenido**:
- Usuario ADMIN: `admin` / `admin123`
- Usuario TRAINER: `trainer1` / `trainer123`
- Usuario CLIENT: `client1` / `client123`

**Notas**:
- Las passwords están hasheadas con BCrypt
- Usar `ON CONFLICT DO NOTHING` para evitar errores en re-ejecución
- ⚠️ **CAMBIAR PASSWORDS EN PRODUCCIÓN**

---

## 🚀 Cómo Funciona Flyway

### 1. Primera Ejecución (Base de datos vacía)
```
1. Flyway crea tabla flyway_schema_history
2. Ejecuta V1__initial_schema.sql
3. Registra en flyway_schema_history: version=1, success=true
4. Ejecuta V2__seed_initial_data.sql  
5. Registra en flyway_schema_history: version=2, success=true
```

### 2. Ejecuciones Subsecuentes
```
1. Flyway lee flyway_schema_history
2. Ve que V1 y V2 ya fueron ejecutadas
3. Solo ejecuta migraciones nuevas (V3, V4, etc.)
4. Registra cada una en flyway_schema_history
```

### 3. Tabla flyway_schema_history
```sql
SELECT * FROM flyway_schema_history;

installed_rank | version | description      | type | script                    | checksum   | installed_by | installed_on        | execution_time | success
---------------|---------|------------------|------|---------------------------|------------|--------------|---------------------|----------------|--------
1              | 1       | initial schema   | SQL  | V1__initial_schema.sql    | 123456789  | postgres     | 2025-11-01 10:00:00 | 45             | true
2              | 2       | seed initial data| SQL  | V2__seed_initial_data.sql | 987654321  | postgres     | 2025-11-01 10:00:00 | 12             | true
```

---

## ➕ Agregar Nueva Migración

### Paso 1: Crear archivo SQL
```bash
# En este directorio: db/migration/
# Crear nuevo archivo con versión siguiente
touch V3__add_phone_number_field.sql
```

### Paso 2: Escribir SQL
```sql
-- V3__add_phone_number_field.sql
ALTER TABLE user_gym 
ADD COLUMN phone_number VARCHAR(20);

CREATE INDEX idx_user_gym_phone ON user_gym(phone_number);
```

### Paso 3: Reiniciar Servicio
```bash
# Flyway detectará automáticamente V3 y lo ejecutará
mvn spring-boot:run
```

### Paso 4: Verificar
```bash
# Conectar a la base de datos
psql -h 192.168.100.207 -U postgres -d gym_authentication

# Ver migraciones ejecutadas
SELECT version, description, installed_on FROM flyway_schema_history;
```

---

## 🔄 Rollback

⚠️ **Flyway Community Edition NO soporta rollback automático**

Para revertir cambios:

### Opción 1: Crear Migración de Reversión
```sql
-- V4__rollback_phone_number.sql
ALTER TABLE user_gym DROP COLUMN phone_number;
DROP INDEX IF EXISTS idx_user_gym_phone;
```

### Opción 2: Restaurar desde Backup
```bash
# Antes de migración importante
pg_dump -h 192.168.100.207 -U postgres gym_authentication > backup_before_v3.sql

# Si falla, restaurar
psql -h 192.168.100.207 -U postgres gym_authentication < backup_before_v3.sql
```

---

## 🛡️ Mejores Prácticas

### ✅ DO (Hacer)
1. **Siempre hacer backup antes de migraciones importantes**
2. **Usar transacciones implícitas** (Flyway las maneja automáticamente)
3. **Probar migraciones en development primero**
4. **Usar ON CONFLICT para inserts idempotentes**
5. **Incluir índices necesarios desde el inicio**
6. **Documentar con comentarios SQL**
7. **Incrementar versiones lógicamente** (V1, V2, V3...)

### ❌ DON'T (Evitar)
1. **NUNCA modificar migraciones ya ejecutadas** (Flyway valida checksums)
2. **No usar DDL destructivo sin backup** (DROP TABLE, TRUNCATE)
3. **No mezclar DDL y DML en producción** (separar schema y data)
4. **No hardcodear datos sensibles** (usar configuración externa)
5. **No usar nombres de archivo con espacios**
6. **No duplicar versiones** (V2, V2, V3... ❌)

---

## 🔍 Troubleshooting

### Error: "Checksum mismatch"
```
Causa: Modificaste un archivo de migración ya ejecutado
Solución: 
1. Revertir cambios al archivo original
2. O crear nueva migración (V3) con los cambios
```

### Error: "Migration failed"
```
Causa: Error en SQL (sintaxis, constraint violation, etc.)
Solución:
1. Revisar logs de Flyway
2. Corregir el SQL
3. Limpiar con flyway:clean (⚠️ BORRA TODO)
4. O restaurar desde backup y corregir migración
```

### Base de datos en estado inconsistente
```
# Ver estado de Flyway
SELECT * FROM flyway_schema_history WHERE success = false;

# Si hay migraciones fallidas, investigar y corregir manualmente
# Luego reparar Flyway:
flyway repair (requiere Flyway CLI o Maven plugin)
```

---

## 📚 Referencias

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Flyway Naming Conventions](https://flywaydb.org/documentation/concepts/migrations#naming)
- [Spring Boot + Flyway](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

---

## 🔧 Configuración en application.yml

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true  # Permite migrar base de datos existente
    baseline-version: 0         # Versión base para baseline
    locations: classpath:db/migration  # Ubicación de scripts
    validate-on-migrate: true   # Validar checksums antes de migrar
    clean-disabled: true        # Deshabilitar flyway:clean (seguridad)
```

---

**Última actualización**: 1 de noviembre de 2025  
**Mantenido por**: Equipo de Desarrollo  
**Sprint**: Sprint 1 - Fase 2 - Tarea 2.1
