# Sistema de Gestión de Gimnasio - Microservicios

Sistema backend basado en arquitectura de microservicios usando Spring Boot y Spring Cloud para la gestión integral de un gimnasio.

## 🏗️ Arquitectura

Este proyecto implementa una arquitectura de microservicios con los siguientes componentes:

```
Cliente (Angular) → API Gateway → Eureka → Microservicios → PostgreSQL
                         ↓
                    RabbitMQ (para eventos)
```

### Servicios Principales

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| **Eureka Server** | 8761 | Service Discovery y registro de microservicios |
| **Config Service** | 8889 | Configuración centralizada desde GitHub |
| **API Gateway** | 8590 | Punto de entrada único, autenticación JWT, CORS |
| **Authentication** | 8583 | Generación de tokens JWT, recuperación de contraseña |
| **User Service** | 8588 | Gestión de usuarios y credenciales |
| **Workout Service** | 8586 | Gestión de ejercicios, planes y rutinas |

### Infraestructura

- **PostgreSQL** (5432): Base de datos relacional
  - `gym_authentication`: Usuarios y credenciales
  - `gym_exercise`: Ejercicios y rutinas
- **RabbitMQ** (5672/15672): Message broker para eventos asíncronos

## 📚 Documentación Completa

**🔍 Para un análisis detallado del sistema, consulta la carpeta [`/docs`](./docs/)**

### Índice de Documentos

0. **[Resumen Ejecutivo](./docs/00-resumen-ejecutivo.md)** ⭐ **EMPIEZA AQUÍ**
   - Respuesta sobre Actuator y Admin Service
   - Hallazgos críticos del análisis
   - Plan de acción inmediato

1. **[Análisis de Arquitectura](./docs/01-analisis-arquitectura.md)**
   - Componentes del sistema
   - Patrones de diseño implementados
   - Stack tecnológico

2. **[Conexiones entre Servicios](./docs/02-conexiones-entre-servicios.md)**
   - Flujos de datos completos
   - Diagramas de comunicación
   - Dependencias entre servicios

3. **[Puntos de Mejora](./docs/03-puntos-de-mejora.md)**
   - Mejoras críticas, altas, medias y bajas
   - Implementación de Admin Service (Actuator centralizado)
   - Código de ejemplo para cada mejora

4. **[Diagramas](./docs/04-diagramas.md)**
   - Arquitectura visual
   - Flujos de autenticación
   - Service Discovery

5. **[Checklist de Implementación](./docs/05-checklist-implementacion.md)** ✅
   - Tareas detalladas por fase
   - Estimaciones de tiempo
   - Criterios de validación

## 🚀 Inicio Rápido

### Prerrequisitos

- Java 21
- Maven 3.8+
- Docker & Docker Compose

### Ejecución con Docker Compose (Recomendado)

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/EmaSleal/gimnasio_backend.git
   cd gimnasio_backend
   ```

2. **Configurar variables de entorno** ⚠️ IMPORTANTE
   ```bash
   # Crear archivo .env en la raíz
   cp .env.example .env
   # Editar .env con tus secretos
   ```

3. **Construir y ejecutar**
   ```bash
   # Construir los JARs
   mvn clean package -DskipTests

   # Levantar todos los servicios
   docker-compose up -d
   ```

4. **Verificar servicios**
   - Eureka Dashboard: http://localhost:8761
   - API Gateway: http://localhost:8590
   - RabbitMQ Management: http://localhost:15672 (guest/guest)

### Orden de Inicio de Servicios

> ⚠️ **IMPORTANTE**: Los servicios deben iniciarse en este orden:

1. `postgres`, `rabbitmq`, `eureka-server` (independientes)
2. `config-service`
3. `api-gateway`
4. `user-service`
5. `authentication`, `workout-service`

Docker Compose maneja esto automáticamente con `depends_on`.

## 🧪 Pruebas

### Registro de Usuario

```bash
POST http://localhost:8590/user/register
Content-Type: application/json

{
  "userName": "testuser",
  "email": "test@example.com",
  "password": "Password123",
  "role": "CLIENT"
}
```

### Login

```bash
POST http://localhost:8590/Login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "Password123"
}
```

Respuesta:
```json
{
  "id": 1,
  "userName": "testuser",
  "email": "test@example.com",
  "role": "CLIENT",
  "token": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### Peticiones Autenticadas

```bash
GET http://localhost:8590/workout/all
Authorization: Bearer {token}
```

## 🔒 Seguridad

- **Autenticación**: JWT (JSON Web Tokens)
- **Roles**: ADMIN, TRAINER, CLIENT
- **CORS**: Configurado para http://localhost:4200

> ⚠️ **ADVERTENCIA DE SEGURIDAD**: 
> - Los secretos actualmente están en archivos YAML (NO SEGURO)
> - Ver [03-puntos-de-mejora.md](./docs/03-puntos-de-mejora.md) para migrar a variables de entorno

## 🛠️ Stack Tecnológico

- **Java**: 21
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0 (Eureka, Gateway, Config)
- **PostgreSQL**: 16
- **RabbitMQ**: 3.9.11
- **JWT**: jjwt 0.11.5
- **Docker & Docker Compose**
- **Micrometer Tracing** + **Brave** (distributed tracing)

## ⚠️ Problemas Conocidos

1. **🔴 CRÍTICO**: Secretos expuestos en `application.yml`
   - **Solución**: Ver [checklist](./docs/05-checklist-implementacion.md) - Día 1

2. **🔴 CRÍTICO**: `ddl-auto: create/create-drop` causa pérdida de datos
   - **Solución**: Implementar Flyway - Ver [checklist](./docs/05-checklist-implementacion.md) - Días 2-3

3. **🟡 ALTO**: IPs hardcodeadas (`192.168.100.207`)
   - **Solución**: Usar nombres de servicio - Ver [checklist](./docs/05-checklist-implementacion.md) - Día 4

4. **🟡 ALTO**: RabbitMQ configurado pero sin usar
   - **Solución**: Implementar eventos asíncronos - Ver [docs/03-puntos-de-mejora.md](./docs/03-puntos-de-mejora.md#33-implementar-comunicación-asíncrona-con-rabbitmq)

5. **🟡 ALTO**: Actuator sin gestión centralizada
   - **Solución**: Crear Admin Service - Ver [docs/03-puntos-de-mejora.md](./docs/03-puntos-de-mejora.md#31-actuator-crear-servicio-dedicado-de-monitoreo)

Ver análisis completo en [/docs](./docs/).

## 📊 Roadmap

### ✅ Completado
- Arquitectura de microservicios básica
- Service Discovery con Eureka
- API Gateway con autenticación JWT
- CRUD de usuarios y ejercicios
- Containerización con Docker

### 🔄 En Progreso
- Migración de secretos a variables de entorno
- Implementación de Flyway para migraciones
- Creación de Admin Service para monitoreo

### 📋 Pendiente
- Comunicación asíncrona con RabbitMQ
- Configuración centralizada activa
- Documentación con Swagger
- Tests automatizados
- CI/CD pipeline

Ver [roadmap completo](./docs/03-puntos-de-mejora.md#6-roadmap-de-implementación).

## 👥 Contribución

1. Fork del repositorio
2. Crear rama para feature (`git checkout -b feature/amazing-feature`)
3. Commit cambios (`git commit -m 'feat: add amazing feature'`)
4. Push a la rama (`git push origin feature/amazing-feature`)
5. Abrir Pull Request

**Antes de contribuir**, revisa:
- [Checklist de Implementación](./docs/05-checklist-implementacion.md)
- [Puntos de Mejora](./docs/03-puntos-de-mejora.md)

## 📄 Licencia

Este proyecto está bajo licencia privada - ver archivo `LICENSE` para detalles.

## 📧 Contacto

- **Repositorio**: https://github.com/EmaSleal/gimnasio_backend
- **Autor**: EmaSleal

---

## 🎯 Para Nuevos Desarrolladores

1. **Lee primero**: [docs/00-resumen-ejecutivo.md](./docs/00-resumen-ejecutivo.md)
2. **Entiende la arquitectura**: [docs/01-analisis-arquitectura.md](./docs/01-analisis-arquitectura.md)
3. **Configura el entorno**: Sigue la sección "Inicio Rápido" arriba
4. **Implementa mejoras**: Usa [docs/05-checklist-implementacion.md](./docs/05-checklist-implementacion.md)

**¡Bienvenido al equipo! 🚀**