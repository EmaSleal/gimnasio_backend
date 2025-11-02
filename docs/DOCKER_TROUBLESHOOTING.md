# 🐛 Guía de Troubleshooting - Docker Compose

## ❌ Problema: Connection to localhost:5432 refused

### Causa
Los servicios dentro de Docker intentan conectarse a `localhost`, pero necesitan usar el **nombre del servicio Docker** como hostname.

### Solución Aplicada ✅

Se actualizó `docker-compose.yml` para pasar las variables de entorno correctas:

```yaml
authentication:
  environment:
    - DB_HOST=postgres  # ← Usa el nombre del contenedor
    - DB_PORT=5432
    - DB_NAME_AUTH=${DB_NAME_AUTH:-gym_authentication}
    # ... otras variables
```

### Servicios Afectados
- ✅ `authentication` → usa `gym_authentication`
- ✅ `user-service` → usa `gym_authentication`
- ✅ `workout-service` → usa `gym_exercise`

### Comandos para Reiniciar

```powershell
# 1. Detener todos los contenedores
docker-compose down

# 2. Limpiar volúmenes si es necesario (CUIDADO: borra datos)
docker-compose down -v

# 3. Reconstruir y reiniciar
docker-compose up -d --build

# 4. Verificar logs de un servicio específico
docker-compose logs -f user-service

# 5. Verificar conectividad a PostgreSQL desde un contenedor
docker-compose exec user-service ping postgres
```

---

## 🔍 Verificación de Estado

### 1. Verificar que PostgreSQL está corriendo
```powershell
docker-compose ps postgres
```
Debe mostrar `Up` y puerto `5432/tcp`

### 2. Verificar logs de PostgreSQL
```powershell
docker-compose logs postgres
```
Buscar: `database system is ready to accept connections`

### 3. Verificar conectividad desde un servicio
```powershell
# Entrar al contenedor
docker-compose exec user-service bash

# Dentro del contenedor, probar conexión
apt-get update && apt-get install -y postgresql-client
psql -h postgres -U postgres -d gym_authentication -c "\l"
```

### 4. Verificar variables de entorno
```powershell
# Ver variables de un servicio
docker-compose exec user-service env | grep DB_
```

Debe mostrar:
```
DB_HOST=postgres
DB_PORT=5432
DB_NAME_AUTH=gym_authentication
DB_USERNAME=postgres
DB_PASSWORD=<tu-password>
```

---

## 📋 Orden de Inicio Correcto

Los servicios deben iniciar en este orden (automático con `depends_on`):

1. **postgres** (base de datos)
2. **rabbitmq** (mensajería)
3. **eureka-server** (service discovery)
4. **config-service** (configuración)
5. **api-gateway** (enrutamiento)
6. **user-service** (requiere postgres)
7. **authentication** (requiere postgres + user-service)
8. **workout-service** (requiere postgres)
9. **admin-service** (monitoreo)
10. **prometheus** (métricas)
11. **grafana** (dashboards)

---

## 🔧 Problemas Comunes

### Error: "Flyway failed to initialize"
**Causa**: Base de datos no existe o credenciales incorrectas

**Solución**:
```powershell
# Crear las bases de datos manualmente
docker-compose exec postgres psql -U postgres -c "CREATE DATABASE gym_authentication;"
docker-compose exec postgres psql -U postgres -c "CREATE DATABASE gym_exercise;"

# Reiniciar servicios
docker-compose restart user-service authentication workout-service
```

### Error: "Unable to find valid certification path"
**Causa**: Problemas de SSL con PostgreSQL

**Solución**: Agregar a `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?sslmode=disable
```

### Error: "Eureka server not available"
**Causa**: Eureka server no ha iniciado completamente

**Solución**:
```powershell
# Esperar a que Eureka esté listo
docker-compose logs -f eureka-server

# Una vez listo, reiniciar los demás servicios
docker-compose restart api-gateway user-service authentication workout-service
```

---

## 📊 Validación Final

Una vez que todos los servicios estén corriendo:

### 1. Verificar Eureka Dashboard
```
http://localhost:8761
```
Debe mostrar todos los servicios registrados

### 2. Verificar Prometheus Targets
```
http://localhost:9090/targets
```
Debe mostrar 8 targets en estado `UP`

### 3. Verificar Grafana
```
http://localhost:3000
```
- Usuario: `admin`
- Password: `grafana_gym_2024!`

### 4. Verificar Spring Boot Admin
```
http://localhost:9000
```
- Usuario: `admin`
- Password: `gym_admin_123-`

### 5. Verificar Health de cada servicio
```powershell
# Eureka
curl http://localhost:8761/actuator/health

# Config Service
curl http://localhost:8889/actuator/health

# API Gateway
curl http://localhost:8590/actuator/health

# User Service
curl http://localhost:8588/actuator/health

# Authentication
curl http://localhost:8583/actuator/health

# Workout Service
curl http://localhost:8586/actuator/health

# Admin Service
curl http://localhost:9000/actuator/health
```

Todos deben responder: `{"status":"UP"}`

---

## 🚀 Comandos Útiles

```powershell
# Ver todos los contenedores corriendo
docker-compose ps

# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f user-service

# Reiniciar un servicio específico
docker-compose restart user-service

# Reconstruir un servicio específico
docker-compose up -d --build user-service

# Detener todo
docker-compose down

# Detener y limpiar volúmenes (CUIDADO)
docker-compose down -v

# Ver uso de recursos
docker stats

# Entrar a un contenedor
docker-compose exec user-service bash
```

---

## 📞 Soporte

Si los problemas persisten:

1. Verifica el archivo `.env` tiene todas las variables configuradas
2. Ejecuta el script de verificación: `.\scripts\verify-docker-config.ps1`
3. Revisa los logs completos: `docker-compose logs > logs.txt`
4. Verifica que no haya conflictos de puertos: `netstat -ano | findstr "8761"`
