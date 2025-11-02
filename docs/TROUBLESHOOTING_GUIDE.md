# Guía de Troubleshooting - Sistema de Gimnasio

## 📚 Índice

1. [Problemas de Docker y Contenedores](#problemas-de-docker-y-contenedores)
2. [Problemas de Conectividad](#problemas-de-conectividad)
3. [Problemas de RabbitMQ](#problemas-de-rabbitmq)
4. [Problemas de Base de Datos](#problemas-de-base-de-datos)
5. [Problemas de Monitoreo](#problemas-de-monitoreo)
6. [Problemas de Alertas](#problemas-de-alertas)
7. [Problemas de Dashboards](#problemas-de-dashboards)
8. [Comandos Útiles](#comandos-útiles)

---

## Problemas de Docker y Contenedores

### 🔴 Contenedor no inicia o se reinicia constantemente

**Síntomas:**
```bash
docker-compose ps
# Output: STATUS muestra "Restarting" o "Up X seconds"
```

**Diagnóstico:**
```powershell
# Ver logs del contenedor
docker-compose logs -f [servicio]

# Ver últimas 50 líneas
docker-compose logs --tail=50 [servicio]

# Inspeccionar contenedor
docker inspect [nombre_contenedor]
```

**Soluciones Comunes:**

1. **Error de conexión a base de datos**:
   ```bash
   # Verificar que postgres esté UP
   docker-compose ps postgres
   
   # Verificar red postgres
   docker network inspect gimnasio_backend_postgres
   
   # Recrear servicio si no está en la red correcta
   docker-compose stop [servicio]
   docker-compose rm [servicio]
   docker-compose up -d [servicio]
   ```

2. **Error de dependencias**:
   ```powershell
   # Verificar orden de inicio
   docker-compose ps
   
   # Reiniciar respetando depends_on
   docker-compose down
   docker-compose up -d
   ```

3. **Puerto ya en uso**:
   ```powershell
   # Verificar puertos en uso
   netstat -ano | findstr :[PUERTO]
   
   # Cambiar puerto en docker-compose.yml
   # O matar proceso que usa el puerto
   taskkill /PID [PID] /F
   ```

### 🔴 Contenedores no pueden comunicarse entre sí

**Síntomas:**
```
java.net.UnknownHostException: [servicio]
Connection refused
```

**Diagnóstico:**
```powershell
# Ver redes de un contenedor
docker inspect [contenedor] | Select-String -Pattern "Networks" -Context 0,10

# Listar todas las redes
docker network ls

# Inspeccionar red específica
docker network inspect gimnasio_backend_spring
docker network inspect gimnasio_backend_postgres
```

**Soluciones:**

1. **Servicio no en red correcta**:
   ```powershell
   # Recrear contenedor (automáticamente conecta a redes definidas)
   docker-compose stop [servicio]
   docker-compose rm [servicio]
   docker-compose up -d [servicio]
   ```

2. **Usar nombre de servicio, no localhost**:
   ```yaml
   # ❌ INCORRECTO
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/database
   
   # ✅ CORRECTO
   spring:
     datasource:
       url: jdbc:postgresql://postgres:5432/database
   ```

### 🔴 Cambios en código no se reflejan

**Síntomas:**
- Modificas código Java pero el contenedor sigue con versión antigua

**Solución:**
```powershell
# 1. Reconstruir JAR
mvn clean package -DskipTests

# 2. Rebuild de imagen Docker
docker-compose up -d --build [servicio]

# 3. Verificar que el nuevo JAR se copió
docker-compose exec [servicio] ls -la /app/*.jar
```

### 🟡 Contenedor consume demasiada memoria

**Diagnóstico:**
```powershell
# Ver stats en tiempo real
docker stats

# Ver uso de memoria específico
docker stats [contenedor] --no-stream
```

**Soluciones:**

1. **Limitar memoria en docker-compose.yml**:
   ```yaml
   services:
     mi-servicio:
       deploy:
         resources:
           limits:
             memory: 512M
   ```

2. **Ajustar heap de JVM**:
   ```yaml
   environment:
     JAVA_OPTS: "-Xmx256m -Xms128m"
   ```

---

## Problemas de Conectividad

### 🔴 "Connection refused" a otro servicio

**Síntomas:**
```
java.net.ConnectException: Connection refused: connect
```

**Checklist de diagnóstico:**

1. **Verificar servicio destino está UP**:
   ```powershell
   docker-compose ps
   # Todos deben mostrar "Up" y "healthy" si tienen healthcheck
   ```

2. **Verificar puerto correcto**:
   ```powershell
   # Dentro de Docker, usar puerto interno (no mapeado)
   # ❌ INCORRECTO: http://eureka-server:8761 (puerto del host)
   # ✅ CORRECTO: http://eureka-server:8761 (mismo puerto interno)
   ```

3. **Verificar red**:
   ```powershell
   # Ambos servicios deben estar en misma red
   docker network inspect gimnasio_backend_spring
   ```

4. **Verificar nombre de servicio**:
   ```powershell
   # Usar nombre del servicio en docker-compose.yml
   # Ejemplo: "postgres" no "db-microservices" (nombre del contenedor)
   ```

**Solución:**
```yaml
# application.yml debe usar nombres de servicios Docker
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka-server:8761/eureka/

spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/${DB_NAME}
```

### 🔴 "UnknownHostException"

**Síntomas:**
```
java.net.UnknownHostException: postgres
```

**Causa**: Servicio no está en la red que contiene el host solicitado

**Solución:**
```yaml
# docker-compose.yml
services:
  user-service:
    networks:
      - spring    # Para comunicarse con otros microservicios
      - postgres  # Para comunicarse con PostgreSQL
```

Luego recrear:
```powershell
docker-compose stop user-service
docker-compose rm user-service
docker-compose up -d user-service
```

### 🟡 Timeout al conectar a servicios externos

**Síntomas:**
```
SocketTimeoutException: Read timed out
```

**Soluciones:**

1. **Aumentar timeouts**:
   ```yaml
   spring:
     cloud:
       gateway:
         httpclient:
           connect-timeout: 5000
           response-timeout: 10s
   ```

2. **Verificar DNS**:
   ```powershell
   # Dentro del contenedor
   docker-compose exec [servicio] nslookup [host]
   ```

---

## Problemas de RabbitMQ

### 🔴 "Connection refused: localhost:5672"

**Síntomas:**
```
java.net.ConnectException: Connection refused: localhost:5672
```

**Causa**: Servicio intenta conectar a localhost en lugar del servicio RabbitMQ

**Solución:**

1. **Agregar configuración en application.yml**:
   ```yaml
   spring:
     rabbitmq:
       host: ${RABBITMQ_HOST:localhost}
       port: ${RABBITMQ_PORT:5672}
       username: ${RABBITMQ_USERNAME:guest}
       password: ${RABBITMQ_PASSWORD:guest}
   ```

2. **Agregar variables en docker-compose.yml**:
   ```yaml
   services:
     mi-servicio:
       environment:
         RABBITMQ_HOST: rabbitmq
         RABBITMQ_PORT: 5672
         RABBITMQ_USERNAME: guest
         RABBITMQ_PASSWORD: guest
   ```

3. **Rebuild del servicio**:
   ```powershell
   docker-compose up -d --build mi-servicio
   ```

**Verificación:**
```powershell
# Ver logs para confirmar conexión
docker-compose logs mi-servicio | Select-String -Pattern "rabbitmq"

# Debe mostrar algo como:
# "Created new connection: rabbitConnectionFactory... amqp://guest@172.30.0.X:5672/"
```

### 🟡 RabbitMQ no acepta conexiones

**Diagnóstico:**
```powershell
# Verificar estado de RabbitMQ
docker-compose ps rabbitmq

# Ver logs
docker-compose logs rabbitmq

# Acceder a management UI
Start-Process "http://localhost:15672"
# Login: guest/guest
```

**Soluciones:**

1. **Reiniciar RabbitMQ**:
   ```powershell
   docker-compose restart rabbitmq
   
   # Esperar a que esté ready
   docker-compose logs -f rabbitmq
   # Buscar: "Server startup complete"
   ```

2. **Recrear contenedor**:
   ```powershell
   docker-compose stop rabbitmq
   docker-compose rm rabbitmq
   docker-compose up -d rabbitmq
   ```

### 🟡 Mensajes no se consumen

**Diagnóstico:**
1. Acceder a http://localhost:15672
2. Ver "Queues" tab
3. Verificar "Ready" messages

**Verificaciones:**
- Consumer está ejecutándose
- No hay excepciones en logs del consumer
- Binding entre exchange y queue es correcto

---

## Problemas de Base de Datos

### 🔴 "FATAL: database does not exist"

**Síntomas:**
```
PSQLException: FATAL: database "gym_authentication" does not exist
```

**Solución:**

1. **Crear base de datos**:
   ```powershell
   # Entrar al contenedor postgres
   docker-compose exec postgres psql -U postgres
   
   # En psql:
   CREATE DATABASE gym_authentication;
   CREATE DATABASE gym_exercise;
   \l  # Listar databases
   \q  # Salir
   ```

2. **Verificar variables de entorno**:
   ```yaml
   # docker-compose.yml
   services:
     postgres:
       environment:
         POSTGRES_USER: postgres
         POSTGRES_PASSWORD: postgres
         POSTGRES_DB: postgres  # DB inicial
   ```

### 🔴 Flyway migration falla

**Síntomas:**
```
FlywayException: Validate failed: Migration checksum mismatch
```

**Soluciones:**

1. **Limpiar historial de Flyway**:
   ```powershell
   docker-compose exec postgres psql -U postgres -d gym_authentication
   
   # En psql:
   DELETE FROM flyway_schema_history WHERE version > '1';
   SELECT * FROM flyway_schema_history;
   \q
   ```

2. **Resetear base de datos** (⚠️ DATOS SE PIERDEN):
   ```powershell
   docker-compose exec postgres psql -U postgres
   
   DROP DATABASE gym_authentication;
   CREATE DATABASE gym_authentication;
   \q
   
   # Reiniciar servicio para re-ejecutar migraciones
   docker-compose restart authentication
   ```

### 🟡 Conexión lenta a PostgreSQL

**Diagnóstico:**
```powershell
# Ver conexiones activas
docker-compose exec postgres psql -U postgres -c "SELECT * FROM pg_stat_activity;"
```

**Soluciones:**

1. **Aumentar pool de conexiones**:
   ```yaml
   spring:
     datasource:
       hikari:
         maximum-pool-size: 10
         minimum-idle: 5
   ```

2. **Verificar queries lentas**:
   ```sql
   -- En psql
   SELECT * FROM pg_stat_statements 
   ORDER BY mean_time DESC 
   LIMIT 10;
   ```

---

## Problemas de Monitoreo

### 🔴 Prometheus targets DOWN

**Síntomas:**
- Acceder a http://localhost:9090/targets
- Algunos targets muestran estado "DOWN"

**Diagnóstico:**
```powershell
# Verificar que servicio esté UP
docker-compose ps [servicio]

# Verificar endpoint actuator
curl http://localhost:[puerto]/actuator/prometheus

# Verificar desde dentro de red Docker
docker-compose exec prometheus wget -O- http://[servicio]:[puerto]/actuator/prometheus
```

**Soluciones:**

1. **Servicio no tiene Actuator configurado**:
   ```xml
   <!-- pom.xml -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
   <dependency>
       <groupId>io.micrometer</groupId>
       <artifactId>micrometer-registry-prometheus</artifactId>
   </dependency>
   ```
   
   ```yaml
   # application.yml
   management:
     endpoints:
       web:
         exposure:
           include: health,info,prometheus
   ```

2. **Prometheus no puede alcanzar servicio**:
   ```yaml
   # prometheus.yml
   scrape_configs:
     - job_name: 'mi-servicio'
       static_configs:
         - targets: ['mi-servicio:8080']  # Nombre del servicio Docker
   ```

3. **Reiniciar Prometheus**:
   ```powershell
   docker-compose restart prometheus
   ```

### 🟡 Métricas no aparecen en Prometheus

**Diagnóstico:**
1. Ir a http://localhost:9090/graph
2. Buscar métrica en "Expression browser"
3. Si no aparece, ejecutar query genérica: `up`

**Soluciones:**

1. **Verificar scrape interval**:
   ```yaml
   # prometheus.yml
   global:
     scrape_interval: 15s  # Puede tardar hasta 15s en aparecer
   ```

2. **Forzar scrape**:
   - No hay forma manual, esperar próximo scrape
   - O reiniciar Prometheus

3. **Verificar nombre de métrica**:
   ```powershell
   # Ver todas las métricas disponibles
   curl http://localhost:9090/api/v1/label/__name__/values
   ```

---

## Problemas de Alertas

### 🔴 Alertas no se disparan

**Diagnóstico:**
```powershell
# Ver estado de alertas en Prometheus
Start-Process "http://localhost:9090/alerts"

# Ver logs de Alertmanager
docker-compose logs alertmanager

# Verificar configuración de alertas
docker-compose exec prometheus cat /etc/prometheus/alert.rules.yml
```

**Soluciones:**

1. **Regla no cargada**:
   ```powershell
   # Verificar en Prometheus UI que reglas estén cargadas
   # http://localhost:9090/rules
   
   # Si no aparecen, verificar prometheus.yml
   docker-compose exec prometheus cat /etc/prometheus/prometheus.yml
   
   # Debe tener:
   # rule_files:
   #   - /etc/prometheus/alert.rules.yml
   
   # Reiniciar Prometheus
   docker-compose restart prometheus
   ```

2. **Condición no se cumple**:
   ```yaml
   # Verificar threshold en alert.rules.yml
   - alert: HighMemoryUsage
     expr: jvm_memory_used_bytes / jvm_memory_max_bytes > 0.9
     for: 2m  # Debe cumplirse por 2 minutos
   ```

3. **Alertmanager no recibe alertas**:
   ```yaml
   # prometheus.yml debe tener
   alerting:
     alertmanagers:
       - static_configs:
           - targets: ['alertmanager:9093']
   ```

### 🟡 Alertas se disparan demasiado

**Síntomas:**
- Spam de notificaciones
- Alertas por eventos transitorios

**Soluciones:**

1. **Aumentar duración**:
   ```yaml
   - alert: ServiceDown
     expr: up == 0
     for: 5m  # Cambiar de 1m a 5m
   ```

2. **Configurar inhibit rules**:
   ```yaml
   # alertmanager.yml
   inhibit_rules:
     - source_match:
         severity: 'critical'
       target_match:
         severity: 'warning'
       equal: ['instance']
   ```

3. **Agrupar alertas**:
   ```yaml
   route:
     group_by: ['alertname', 'severity']
     group_wait: 30s
     group_interval: 5m
     repeat_interval: 4h
   ```

### 🟡 Notificaciones no llegan

**Diagnóstico:**
```powershell
# Ver logs de Alertmanager
docker-compose logs alertmanager | Select-String -Pattern "error|fail"

# Verificar receivers configurados
docker-compose exec alertmanager cat /etc/alertmanager/alertmanager.yml
```

**Soluciones:**

1. **Email no configurado**:
   ```yaml
   # Verificar SMTP en alertmanager.yml
   receivers:
     - name: 'email'
       email_configs:
         - to: 'alerts@example.com'
           from: 'prometheus@example.com'
           smarthost: 'smtp.gmail.com:587'
           auth_username: 'user@gmail.com'
           auth_password: 'app-password'  # ⚠️ No usar contraseña normal
   ```

2. **Slack webhook inválido**:
   ```yaml
   receivers:
     - name: 'slack'
       slack_configs:
         - api_url: 'https://hooks.slack.com/services/XXX/YYY/ZZZ'
           channel: '#alerts'
   ```

3. **Route no coincide**:
   ```yaml
   route:
     receiver: 'default'  # Fallback
     routes:
       - match:
           severity: critical
         receiver: 'pagerduty'
       - match_re:
           service: .*-service
         receiver: 'slack'
   ```

---

## Problemas de Dashboards

### 🔴 Dashboard muestra "No Data"

**Diagnóstico:**
1. Acceder al dashboard en Grafana
2. Click en panel que muestra "No Data"
3. Click en "Edit"
4. Ver "Query Inspector" → "Data"

**Soluciones:**

1. **Datasource no configurado**:
   - Settings → Data Sources → Prometheus
   - Test connection
   - Si falla, verificar URL: http://prometheus:9090

2. **Query incorrecta**:
   ```promql
   # Verificar query en Prometheus primero
   # http://localhost:9090/graph
   # Luego copiar query exacta a Grafana
   ```

3. **Time range incorrecto**:
   - Cambiar time range en dashboard (arriba derecha)
   - Verificar que haya datos en ese rango

4. **Servicio no genera métricas**:
   ```powershell
   # Verificar endpoint
   curl http://localhost:[puerto]/actuator/prometheus
   
   # Buscar métrica específica
   curl http://localhost:[puerto]/actuator/prometheus | Select-String -Pattern "[metrica]"
   ```

### 🟡 Dashboard muy lento

**Síntomas:**
- Paneles tardan en cargar
- Grafana UI lenta

**Soluciones:**

1. **Reducir resolution**:
   - Dashboard Settings → Variables
   - Cambiar $__interval de "auto" a "1m"

2. **Aumentar refresh interval**:
   - Arriba derecha, cambiar de "5s" a "30s" o "1m"

3. **Optimizar queries**:
   ```promql
   # ❌ LENTO - demasiados labels
   jvm_memory_used_bytes
   
   # ✅ RÁPIDO - filtrar por servicio
   jvm_memory_used_bytes{application="user-service"}
   ```

4. **Aumentar recursos Grafana**:
   ```yaml
   # docker-compose.yml
   services:
     grafana:
       deploy:
         resources:
           limits:
             memory: 512M
   ```

### 🟡 Gráfico no se actualiza

**Soluciones:**

1. **Verificar auto-refresh**:
   - Arriba derecha, debe estar activo (no en "Off")

2. **Forzar refresh**:
   - Click en icono de refresh (arriba derecha)
   - O Ctrl+R

3. **Limpiar cache**:
   - Dashboard Settings → JSON Model
   - Copiar JSON
   - Borrar dashboard
   - Import JSON nuevamente

---

## Comandos Útiles

### Docker Compose

```powershell
# Ver estado de todos los servicios
docker-compose ps

# Ver logs de un servicio
docker-compose logs -f [servicio]

# Ver últimas N líneas de logs
docker-compose logs --tail=100 [servicio]

# Reiniciar servicio
docker-compose restart [servicio]

# Reconstruir servicio
docker-compose up -d --build [servicio]

# Detener todo
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Ver uso de recursos
docker stats

# Limpiar recursos no usados
docker system prune -a
```

### Docker Networks

```powershell
# Listar redes
docker network ls

# Inspeccionar red
docker network inspect [red]

# Ver qué contenedores están en una red
docker network inspect gimnasio_backend_spring | Select-String -Pattern "Name"

# Conectar contenedor a red
docker network connect [red] [contenedor]

# Desconectar
docker network disconnect [red] [contenedor]
```

### PostgreSQL

```powershell
# Entrar a psql
docker-compose exec postgres psql -U postgres

# Ejecutar query desde shell
docker-compose exec postgres psql -U postgres -d gym_authentication -c "SELECT * FROM users;"

# Ver bases de datos
docker-compose exec postgres psql -U postgres -c "\l"

# Ver tablas de una BD
docker-compose exec postgres psql -U postgres -d gym_authentication -c "\dt"

# Backup de BD
docker-compose exec postgres pg_dump -U postgres gym_authentication > backup.sql

# Restore de BD
cat backup.sql | docker-compose exec -T postgres psql -U postgres -d gym_authentication
```

### RabbitMQ

```powershell
# Ver estado de queues
docker-compose exec rabbitmq rabbitmqctl list_queues

# Ver exchanges
docker-compose exec rabbitmq rabbitmqctl list_exchanges

# Ver bindings
docker-compose exec rabbitmq rabbitmqctl list_bindings

# Ver conexiones activas
docker-compose exec rabbitmq rabbitmqctl list_connections

# Purgar queue
docker-compose exec rabbitmq rabbitmqctl purge_queue [queue_name]
```

### Prometheus

```powershell
# Verificar configuración
docker-compose exec prometheus promtool check config /etc/prometheus/prometheus.yml

# Verificar reglas de alerta
docker-compose exec prometheus promtool check rules /etc/prometheus/alert.rules.yml

# Ver targets desde CLI
curl http://localhost:9090/api/v1/targets | ConvertFrom-Json

# Ejecutar query
curl "http://localhost:9090/api/v1/query?query=up"

# Ver alertas activas
curl http://localhost:9090/api/v1/alerts | ConvertFrom-Json
```

### Grafana

```powershell
# Reiniciar Grafana
docker-compose restart grafana

# Ver logs
docker-compose logs -f grafana

# Backup de dashboards
# UI: Share → Export → Save to file

# Listar datasources via API
curl -u admin:admin http://localhost:3000/api/datasources
```

### Testing

```powershell
# Test de endpoint
curl http://localhost:[puerto]/actuator/health

# Test con autenticación
$token = "eyJhbGc..."
curl -H "Authorization: Bearer $token" http://localhost:8590/workout/all

# Test de Eureka
curl http://localhost:8761/eureka/apps

# Ver servicios registrados
curl http://localhost:8761/eureka/apps | Select-String -Pattern "application"

# Test Prometheus metrics
curl http://localhost:8590/actuator/prometheus

# Ejecutar script de testing de alertas
.\scripts\test-alerts.ps1
```

### Maven

```powershell
# Build de todos los servicios
mvn clean package -DskipTests

# Build de un servicio específico
cd [servicio]
mvn clean package -DskipTests

# Ejecutar tests
mvn test

# Ver árbol de dependencias
mvn dependency:tree

# Actualizar dependencias
mvn versions:use-latest-versions
```

---

## 🆘 Escalado de Problemas

### Nivel 1: Auto-diagnóstico
1. Revisar logs del servicio afectado
2. Verificar estado con `docker-compose ps`
3. Consultar esta guía de troubleshooting
4. Intentar soluciones documentadas

### Nivel 2: Investigación
1. Revisar documentación específica:
   - [ALERTAS.md](./monitoring/ALERTAS.md)
   - [GRAFANA_DASHBOARDS.md](./monitoring/GRAFANA_DASHBOARDS.md)
   - [SPRINT2_SUMMARY.md](./SPRINT2_SUMMARY.md)
2. Ejecutar scripts de testing
3. Verificar métricas en Prometheus/Grafana

### Nivel 3: Soporte
1. Documentar el problema:
   - Logs relevantes
   - Configuración afectada
   - Pasos para reproducir
2. Crear issue en repositorio
3. Incluir información del sistema:
   ```powershell
   docker --version
   docker-compose --version
   java --version
   mvn --version
   ```

---

**Última actualización**: 2 de noviembre de 2025
**Versión**: 1.0.0
**Mantenido por**: EmaSleal
