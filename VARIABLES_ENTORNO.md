# 🔐 Guía de Variables de Entorno

## 📋 Resumen

Este proyecto utiliza variables de entorno para gestionar secretos y configuraciones sensibles. **NUNCA** se deben commitear secretos en el repositorio.

---

## 🚀 Configuración Inicial

### 1. Crear archivo .env

```bash
# En la raíz del proyecto
cp .env.example .env
```

### 2. Editar valores en .env

Abre el archivo `.env` y reemplaza los valores de ejemplo:

```bash
# Antes (valores de ejemplo)
DB_PASSWORD=CAMBIAR_ESTE_PASSWORD_SEGURO
JWT_SECRET=CAMBIAR_ESTE_SECRET_MUY_LARGO_Y_SEGURO_GENERADO_CON_OPENSSL

# Después (tus valores reales)
DB_PASSWORD=tu_password_real_aqui
JWT_SECRET=tu_secret_generado_con_openssl_aqui
```

### 3. Verificar .gitignore

El archivo `.gitignore` debe contener:

```
.env
.env.local
.env.*.local
```

✅ **Ya está configurado** - No necesitas hacer nada.

---

## 🔑 Variables Disponibles

### Base de Datos

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_HOST` | Host del servidor PostgreSQL | `192.168.100.111` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_USERNAME` | Usuario de la base de datos | `postgres` |
| `DB_PASSWORD` | ⚠️ **SECRETO** - Contraseña de la BD | `Mi_Password_123` |
| `DB_NAME_AUTH` | Nombre de la BD de autenticación | `gym_authentication` |
| `DB_NAME_EXERCISE` | Nombre de la BD de ejercicios | `gym_exercise` |

### JWT (Autenticación)

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `JWT_SECRET` | ⚠️ **SECRETO** - Clave para firmar tokens | Ver generación abajo |
| `JWT_EXPIRATION` | Tiempo de expiración en milisegundos | `86400000` (24h) |

### Servicios Externos

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `RESEND_API_KEY` | ⚠️ **SECRETO** - API key de Resend | `re_xxxxxxxxxxxxx` |
| `ZIPKIN_BASE_URL` | URL del servidor Zipkin | `http://localhost:9411` |

### Service Discovery

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `EUREKA_HOST` | Host del servidor Eureka | `192.168.100.111` |
| `EUREKA_PORT` | Puerto de Eureka | `8761` |

---

## 🔒 Generación de Secretos Seguros

### JWT Secret

```bash
# Generar un secret de 512 bits (64 bytes en base64)
openssl rand -base64 64

# Resultado (ejemplo):
# kR7vF2xT9wY3nH8mJ5qL6sP1oE4uI7yW0zX3cV6bN9aS8dF1gH2jK4lM5nP7qR8t...
```

Copia el resultado y úsalo como valor de `JWT_SECRET` en el `.env`.

### Contraseñas de Base de Datos

```bash
# Generar password seguro de 256 bits (32 bytes)
openssl rand -base64 32

# Resultado (ejemplo):
# xY9zA2bC3dE4fG5hI6jK7lM8nO9pQ0rS1tU2vW3xY4zA=
```

---

## 🎯 Uso en Código

### Spring Boot (application.yml)

Las variables se usan con la sintaxis `${VARIABLE_NAME:valor_por_defecto}`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME_AUTH}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD}
    
jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:86400000}
```

**Nota**: El valor después de `:` es el valor por defecto si la variable no existe.

### IntelliJ IDEA

1. **Run Configuration** → **Environment Variables**
2. Pegar el contenido del archivo `.env` (sin comentarios)
3. O usar plugin **EnvFile**:
   - Instalar plugin `EnvFile`
   - Edit Configuration → Enable EnvFile
   - Seleccionar archivo `.env`

### VS Code

1. **Launch Configuration** (.vscode/launch.json):

```json
{
  "configurations": [
    {
      "type": "java",
      "name": "User Service",
      "envFile": "${workspaceFolder}/.env"
    }
  ]
}
```

### Maven

```bash
# Cargar variables antes de ejecutar
export $(cat .env | xargs)
mvn spring-boot:run
```

### PowerShell

```powershell
# Cargar variables desde .env
Get-Content .env | foreach {
    $name, $value = $_.split('=')
    if ($name -and $value) {
        [Environment]::SetEnvironmentVariable($name, $value, "Process")
    }
}

# Ejecutar servicio
mvn spring-boot:run
```

---

## 🐳 Docker Compose

Docker Compose lee automáticamente el archivo `.env`:

```yaml
# docker-compose.yml
services:
  user-service:
    environment:
      - DB_HOST=${DB_HOST}
      - DB_PORT=${DB_PORT}
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
```

**Ejecutar**:
```bash
docker-compose up
```

---

## 🌍 Múltiples Entornos

### Desarrollo Local (.env)
```bash
DB_HOST=localhost
EUREKA_HOST=localhost
SPRING_PROFILES_ACTIVE=dev
```

### Docker (.env.docker)
```bash
DB_HOST=postgres
EUREKA_HOST=eureka-server
SPRING_PROFILES_ACTIVE=docker
```

### Producción (.env.prod)
```bash
DB_HOST=prod-db.example.com
EUREKA_HOST=prod-eureka.example.com
SPRING_PROFILES_ACTIVE=prod
# ⚠️ NO COMMITEAR ESTE ARCHIVO
```

**Usar entorno específico**:
```bash
# Copiar el apropiado
cp .env.docker .env
# O especificar en docker-compose
docker-compose --env-file .env.docker up
```

---

## ✅ Checklist de Seguridad

Antes de deployar o compartir código:

- [ ] ✅ Archivo `.env` está en `.gitignore`
- [ ] ✅ No hay secretos hardcodeados en `application.yml`
- [ ] ✅ Todos los secretos se leen desde variables de entorno
- [ ] ✅ `.env.example` contiene solo valores de ejemplo
- [ ] ✅ Secretos de producción son diferentes a desarrollo
- [ ] ✅ JWT_SECRET tiene al menos 256 bits (64 caracteres)
- [ ] ✅ Contraseñas tienen al menos 16 caracteres
- [ ] ✅ API keys son válidas y activas

---

## 🚨 ¿Qué hacer si commiteaste secretos?

Si accidentalmente commiteaste el archivo `.env` o secretos:

### 1. Remover del commit actual
```bash
git rm .env
git commit --amend
```

### 2. Limpiar historial (si ya está pusheado)
```bash
# Instalar git-filter-repo
pip install git-filter-repo

# Remover archivo de todo el historial
git filter-repo --path .env --invert-paths

# Force push (⚠️ cuidado en repos compartidos)
git push --force
```

### 3. **ROTAR TODOS LOS SECRETOS**
- ✅ Cambiar contraseña de base de datos
- ✅ Generar nuevo JWT_SECRET
- ✅ Regenerar API keys (Resend, etc.)
- ✅ Actualizar secretos en producción

---

## 📚 Referencias

- [12 Factor App - Config](https://12factor.net/config)
- [Spring Boot - Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Docker Compose - Environment Variables](https://docs.docker.com/compose/environment-variables/)
- [OpenSSL Documentation](https://www.openssl.org/docs/)

---

## 🆘 Soporte

Si tienes problemas con las variables de entorno:

1. Verificar que `.env` existe en la raíz del proyecto
2. Verificar que no hay espacios en los valores: `VAR=valor` ✅ vs `VAR = valor` ❌
3. Revisar logs para ver qué valores está leyendo Spring Boot
4. Usar `echo $VARIABLE_NAME` para verificar que está cargada

---

**Última actualización**: 1 de noviembre de 2025  
**Tarea**: Sprint 1 - Fase 1 - Gestión de Secretos
