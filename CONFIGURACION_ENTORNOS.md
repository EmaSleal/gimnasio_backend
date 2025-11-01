# Configuración de Entornos - Sistema de Gimnasio Backend

## 📋 Resumen

Este proyecto utiliza **variables de entorno** para configurar diferentes entornos (desarrollo local, Docker, producción) sin necesidad de perfiles de Spring complejos.

## 🔧 Configuración Actual

### Valores por Defecto (application.yml)

Todos los servicios están configurados con defaults seguros para desarrollo local:

```yaml
DB_HOST: localhost        # Base de datos local
DB_PORT: 5432            # Puerto estándar PostgreSQL
EUREKA_HOST: localhost   # Eureka Server local
EUREKA_PORT: 8761        # Puerto estándar Eureka
```

### Valores para Servidores Remotos (.env)

El archivo `.env` en la raíz del proyecto contiene las configuraciones específicas de tu entorno:

```properties
# Para conectar a servidor remoto
DB_HOST=192.168.100.207
DB_PORT=5432

# Para Eureka remoto (si aplica)
EUREKA_HOST=192.168.100.207
EUREKA_PORT=8761
```

## 🚀 Uso en Diferentes Entornos

### 1. Desarrollo Local (PostgreSQL + Eureka locales)

**Requisitos**:
- PostgreSQL instalado localmente
- Bases de datos `gym_authentication` y `gym_exercise` creadas

**Configuración**:
```bash
# Opción 1: Sin .env (usa defaults de application.yml)
mvn spring-boot:run

# Opción 2: Con .env modificado
# Editar .env:
DB_HOST=localhost
EUREKA_HOST=localhost
```

**Comandos**:
```powershell
# 1. Iniciar Eureka
cd eureka_server
mvn spring-boot:run

# 2. Iniciar API Gateway
cd api-gateway
mvn spring-boot:run

# 3. Iniciar servicios
cd user-service
mvn spring-boot:run

cd workout-service
mvn spring-boot:run
```

### 2. Desarrollo con Servidor Remoto

**Requisitos**:
- Acceso a servidor PostgreSQL remoto (ej: 192.168.100.207)
- Eureka puede ser local o remoto

**Configuración**:
```bash
# Archivo .env
DB_HOST=192.168.100.207
DB_PASSWORD=TuPasswordSeguro
EUREKA_HOST=localhost  # O IP del servidor Eureka si es remoto
```

**Cargar variables desde .env (PowerShell)**:
```powershell
# Cargar todas las variables del .env
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^#][^=]+)=(.*)$') {
        [System.Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim(), 'Process')
    }
}

# Luego iniciar el servicio
cd user-service
mvn spring-boot:run
```

### 3. Docker Compose

**Configuración**:
```yaml
# docker-compose.yml
services:
  user-service:
    environment:
      - DB_HOST=postgres      # Nombre del servicio PostgreSQL
      - EUREKA_HOST=eureka-server  # Nombre del servicio Eureka
      - DB_PORT=5432
```

El `docker-compose.yml` automáticamente lee el archivo `.env` para otros valores.

**Comandos**:
```bash
docker-compose up -d
```

### 4. Producción

**Configuración**:
```bash
# Variables de entorno del sistema o CI/CD
export DB_HOST=production-db.example.com
export DB_PASSWORD=StrongProductionPassword
export EUREKA_HOST=eureka.example.com
export JWT_SECRET=SuperSecureRandomGeneratedSecret

# Iniciar con perfil de producción
java -jar -Dspring.profiles.active=prod user-service.jar
```

## 🔑 Variables de Entorno Disponibles

### Base de Datos
| Variable | Default | Descripción |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | Host del servidor PostgreSQL |
| `DB_PORT` | `5432` | Puerto PostgreSQL |
| `DB_USERNAME` | `postgres` | Usuario de base de datos |
| `DB_PASSWORD` | *(requerido)* | Contraseña de base de datos |
| `DB_NAME_AUTH` | `gym_authentication` | Nombre BD de autenticación |
| `DB_NAME_EXERCISE` | `gym_exercise` | Nombre BD de ejercicios |

### Service Discovery
| Variable | Default | Descripción |
|----------|---------|-------------|
| `EUREKA_HOST` | `localhost` | Host del Eureka Server |
| `EUREKA_PORT` | `8761` | Puerto del Eureka Server |

### Seguridad
| Variable | Default | Descripción |
|----------|---------|-------------|
| `JWT_SECRET` | *(requerido)* | Clave secreta para JWT (mínimo 256 bits) |
| `RESEND_API_KEY` | *(requerido)* | API Key de Resend para emails |

### Otros
| Variable | Default | Descripción |
|----------|---------|-------------|
| `ZIPKIN_BASE_URL` | `http://localhost:9411` | URL del servidor Zipkin |
| `SPRING_PROFILES_ACTIVE` | `default` | Perfil de Spring activo |

## 📝 Ejemplos de Configuración por Entorno

### Archivo .env para Desarrollo Local

```properties
# Base de Datos Local
DB_HOST=localhost
DB_PASSWORD=local_password

# Eureka Local
EUREKA_HOST=localhost

# Seguridad (usar valores de prueba)
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
RESEND_API_KEY=re_test_key

# Otros
ZIPKIN_ENABLED=false
```

### Archivo .env para Desarrollo con Servidor Remoto

```properties
# Base de Datos Remota
DB_HOST=192.168.100.207
DB_PASSWORD=Chismosear01

# Eureka Local (o remoto)
EUREKA_HOST=localhost  # O 192.168.100.111 si es remoto

# Seguridad
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
RESEND_API_KEY=re_X7qY3NFp_ETffUyjtLJpgTMcrzdhvdB4c
```

### Variables para Docker Compose

```yaml
# docker-compose.yml
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: ${DB_PASSWORD}
  
  eureka-server:
    build: ./eureka_server
    ports:
      - "8761:8761"
  
  user-service:
    build: ./user-service
    environment:
      DB_HOST: postgres          # Nombre del servicio
      DB_PASSWORD: ${DB_PASSWORD}
      EUREKA_HOST: eureka-server  # Nombre del servicio
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      - postgres
      - eureka-server
```

## ⚡ Trucos y Recomendaciones

### PowerShell: Función para Cargar .env

Agregar al perfil de PowerShell (`$PROFILE`):

```powershell
function Load-DotEnv {
    param([string]$Path = ".env")
    
    if (Test-Path $Path) {
        Get-Content $Path | ForEach-Object {
            if ($_ -match '^([^#][^=]+)=(.*)$') {
                $name = $matches[1].Trim()
                $value = $matches[2].Trim()
                [System.Environment]::SetEnvironmentVariable($name, $value, 'Process')
                Write-Host "✓ $name" -ForegroundColor Green
            }
        }
        Write-Host "`n✓ Variables cargadas desde $Path" -ForegroundColor Cyan
    } else {
        Write-Host "✗ Archivo $Path no encontrado" -ForegroundColor Red
    }
}

# Uso:
# Load-DotEnv
# mvn spring-boot:run
```

### Bash: Función para Cargar .env

Agregar a `.bashrc` o `.zshrc`:

```bash
load_env() {
    if [ -f .env ]; then
        export $(cat .env | grep -v '^#' | xargs)
        echo "✓ Variables cargadas desde .env"
    else
        echo "✗ Archivo .env no encontrado"
    fi
}

# Uso:
# load_env
# mvn spring-boot:run
```

### Verificar Variables Cargadas

```powershell
# PowerShell
$env:DB_HOST
$env:EUREKA_HOST

# Bash
echo $DB_HOST
echo $EUREKA_HOST
```

## 🔒 Seguridad

### ✅ Mejores Prácticas

1. **Nunca commitear `.env`** - Ya está en `.gitignore`
2. **Rotar secretos regularmente** - Especialmente `JWT_SECRET`
3. **Usar secretos diferentes por entorno**
4. **Generar JWT_SECRET seguro**:
   ```bash
   # Linux/Mac
   openssl rand -base64 64
   
   # PowerShell
   -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 64 | % {[char]$_})
   ```

### ❌ Anti-Patrones

- ❌ IPs hardcodeadas en código
- ❌ Passwords en application.yml
- ❌ Compartir archivo .env en Slack/Discord
- ❌ Usar mismo JWT_SECRET en todos los entornos
- ❌ Commitear .env al repositorio

## 🐛 Solución de Problemas

### Error: "Connection refused" a Base de Datos

**Causa**: `DB_HOST` incorrecto o PostgreSQL no está corriendo.

**Solución**:
```powershell
# Verificar variable
echo $env:DB_HOST

# Si es localhost, verificar que PostgreSQL esté corriendo
Get-Service postgresql*  # Windows
sudo systemctl status postgresql  # Linux

# Si es IP remota, verificar conectividad
Test-NetConnection -ComputerName 192.168.100.207 -Port 5432
```

### Error: Eureka no encuentra servicios

**Causa**: `EUREKA_HOST` incorrecto o servicios no registrados.

**Solución**:
```powershell
# Verificar variable
echo $env:EUREKA_HOST

# Acceder al dashboard de Eureka
Start-Process "http://localhost:8761"

# Verificar que el servicio se registró
# Buscar en logs: "registration status: 204"
```

### Variables no se cargan desde .env

**Causa**: Archivo .env tiene formato incorrecto o shell no compatible.

**Solución**:
```powershell
# Verificar formato del .env
Get-Content .env | Select-String "="

# Cargar manualmente
$env:DB_HOST="192.168.100.207"
$env:DB_PASSWORD="TuPassword"
```

## 📚 Referencias

- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [12 Factor App - Config](https://12factor.net/config)
- [Docker Compose Environment Variables](https://docs.docker.com/compose/environment-variables/)

---

**Última Actualización**: 1 de noviembre de 2024  
**Responsable**: Equipo de Desarrollo  
**Estado**: ✅ Activo
