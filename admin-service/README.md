# Admin Service - Spring Boot Admin

## 📋 Descripción

Servicio de monitoreo centralizado basado en **Spring Boot Admin** que proporciona un dashboard web para visualizar y gestionar todos los microservicios del sistema Gym.

## 🎯 Características Principales

- **Dashboard Web Interactivo**: Interfaz gráfica para monitoreo en tiempo real
- **Descubrimiento Automático**: Integración con Eureka para detectar servicios
- **Métricas en Vivo**: Visualización de memoria, CPU, threads, y más
- **Logs Centralizados**: Acceso a logs de todos los servicios desde un solo lugar
- **Health Checks**: Estado de salud de cada microservicio
- **Notificaciones**: Alertas cuando servicios caen o se recuperan
- **Seguridad**: Autenticación básica para acceso al dashboard

## 🚀 Inicio Rápido

### Requisitos Previos

- Java 21
- Maven 3.8+
- Eureka Server en ejecución (puerto 8761)

### Compilación

```bash
mvn clean install
```

### Ejecución Local

```bash
# Opción 1: Maven
mvn spring-boot:run

# Opción 2: JAR
java -jar target/admin-service-1.0-SNAPSHOT.jar

# Opción 3: Con variables de entorno
Get-Content .env | ForEach-Object { if ($_ -match '^\s*([^#][^=]*?)\s*=\s*(.+?)\s*$') { [Environment]::SetEnvironmentVariable($matches[1], $matches[2]) } }
mvn spring-boot:run
```

### Acceso al Dashboard

- **URL**: http://localhost:9000
- **Usuario**: `admin` (configurable con `ADMIN_USER`)
- **Contraseña**: `admin123` (configurable con `ADMIN_PASSWORD`)

## ⚙️ Configuración

### Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `ADMIN_USER` | Usuario para acceso al dashboard | `admin` |
| `ADMIN_PASSWORD` | Contraseña del dashboard | `admin123` |
| `EUREKA_HOST` | Servidor Eureka | `localhost` |
| `HOSTNAME` | Hostname del servicio | `localhost` |

### Archivo `.env` (Ejemplo)

```env
ADMIN_USER=admin
ADMIN_PASSWORD=secure_password_123
EUREKA_HOST=192.168.100.207
```

## 📊 Funcionalidades del Dashboard

### 1. Vista General (Wallboard)
- Lista de todos los servicios registrados
- Estado de salud (UP/DOWN)
- Número de instancias por servicio

### 2. Detalles de Aplicación
Para cada servicio puedes ver:
- **Health**: Estado detallado de salud (DB, Disk, etc.)
- **Metrics**: Métricas JVM, memoria, CPU, threads
- **Environment**: Variables de entorno y properties
- **Loggers**: Configuración de logging (puedes cambiar niveles en vivo)
- **JVM**: Thread dump, heap dump
- **Mappings**: Endpoints HTTP disponibles
- **Scheduled Tasks**: Tareas programadas
- **Caches**: Estado de cachés

### 3. Logs en Tiempo Real
- Ver logs de cualquier servicio en tiempo real
- Filtrado y búsqueda de logs
- Descarga de archivos de log

### 4. Notificaciones
Configurable para enviar alertas cuando:
- Un servicio se cae
- Un servicio se recupera
- Problemas de salud detectados

## 🔒 Seguridad

### Autenticación Básica

El dashboard está protegido con Spring Security. Para acceder, debes autenticarte con las credenciales configuradas.

### Endpoints Públicos

Los siguientes endpoints NO requieren autenticación (necesarios para monitoring):
- `/actuator/**` - Endpoints de actuator
- `/assets/**` - Recursos estáticos del dashboard

### Cambiar Credenciales

**Desarrollo:**
```yaml
# application.yml
spring:
  security:
    user:
      name: admin
      password: admin123
```

**Producción (usar variables de entorno):**
```bash
export ADMIN_USER=my_secure_username
export ADMIN_PASSWORD=my_very_secure_password_12345
```

## 🐋 Docker

### Build

```bash
docker build -t gym/admin-service:latest .
```

### Run

```bash
docker run -d \
  -p 9000:9000 \
  -e ADMIN_USER=admin \
  -e ADMIN_PASSWORD=admin123 \
  -e EUREKA_HOST=eureka-server \
  --name admin-service \
  gym/admin-service:latest
```

## 📡 Integración con Otros Servicios

Los otros microservicios deben exponer sus actuator endpoints para ser monitoreados:

```yaml
# application.yml de cualquier microservicio
management:
  endpoints:
    web:
      exposure:
        include: "*"  # O específicos: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### Metadatos de Autenticación (Opcional)

Si tus servicios requieren autenticación para actuator:

```yaml
eureka:
  instance:
    metadataMap:
      user.name: ${spring.security.user.name}
      user.password: ${spring.security.user.password}
```

## 🛠️ Troubleshooting

### Problema: No aparecen servicios en el dashboard

**Soluciones:**
1. Verificar que Eureka está corriendo: http://localhost:8761
2. Confirmar que los servicios están registrados en Eureka
3. Revisar que los servicios exponen actuator endpoints
4. Verificar logs del Admin Service:
   ```bash
   # Buscar errores de conexión
   grep -i "error\|exception" logs/admin-service.log
   ```

### Problema: "401 Unauthorized" al acceder a métricas

**Solución:**
Los servicios deben tener sus actuator endpoints públicos o configurar metadatos de autenticación en Eureka.

### Problema: Dashboard lento o no responde

**Soluciones:**
1. Reducir el número de endpoints expuestos en servicios
2. Aumentar memoria JVM: `java -Xmx512m -jar admin-service.jar`
3. Desactivar logs en tiempo real si no se usan

## 📈 Métricas Útiles

Puedes monitorear:
- **JVM Memory**: Heap, Non-Heap, Garbage Collection
- **CPU Usage**: % de uso de CPU
- **Threads**: Threads activos, daemon, peak
- **HTTP Requests**: Contadores y tiempos de respuesta
- **Database Connections**: Pool de conexiones activas
- **Custom Metrics**: Si los servicios las definen

## 🔄 Actualizaciones en Vivo

Puedes hacer cambios en vivo sin reiniciar:
- Cambiar niveles de logging
- Refrescar configuración (con Spring Cloud Config)
- Ver cambios en environment properties

## 📚 Referencias

- [Spring Boot Admin Docs](https://docs.spring-boot-admin.com/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Spring Security](https://spring.io/projects/spring-security)

## 🎨 Personalización UI

Puedes personalizar el dashboard editando `application.yml`:

```yaml
spring:
  boot:
    admin:
      ui:
        title: "Mi Sistema Monitor"
        brand: "<img src='logo.png'><span>Mi Empresa</span>"
        favicon: "favicon.ico"
```

## 📝 Notas Importantes

1. **Seguridad en Producción**: Cambiar credenciales por defecto
2. **HTTPS**: Configurar SSL/TLS en producción
3. **Recursos**: El dashboard consume memoria proporcional al número de servicios monitoreados
4. **Retención de Datos**: Los datos históricos se pierden al reiniciar (considerar integración con Prometheus para persistencia)

## 🚦 Estado de Servicio

- Puerto: **9000**
- Eureka: **Habilitado**
- Security: **Básica (Form + HTTP Basic)**
- Actuator: **Expuesto completamente**

---

**Autor**: Equipo Backend Gym  
**Versión**: 1.0  
**Última actualización**: 2024
