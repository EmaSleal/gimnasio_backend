# Script para recrear PostgreSQL con credenciales correctas
# IMPORTANTE: Esto eliminará TODOS los datos de la base de datos

Write-Host "⚠️  ADVERTENCIA: Este script eliminará todos los datos de PostgreSQL" -ForegroundColor Yellow
Write-Host "Las bases de datos 'gym_authentication' y 'gym_exercise' se recrearán vacías" -ForegroundColor Yellow
Write-Host ""
$confirmation = Read-Host "¿Deseas continuar? (escribe 'SI' para confirmar)"

if ($confirmation -ne "SI") {
    Write-Host "❌ Operación cancelada" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🗑️  Paso 1: Deteniendo servicios que usan PostgreSQL..." -ForegroundColor Cyan
docker-compose stop user-service authentication workout-service

Write-Host ""
Write-Host "🗑️  Paso 2: Deteniendo PostgreSQL..." -ForegroundColor Cyan
docker-compose stop postgres

Write-Host ""
Write-Host "🗑️  Paso 3: Eliminando contenedor de PostgreSQL..." -ForegroundColor Cyan
docker-compose rm -f postgres

Write-Host ""
Write-Host "🗑️  Paso 4: Eliminando volumen de datos (ESTO BORRA TODOS LOS DATOS)..." -ForegroundColor Cyan
docker volume rm gimnasio_backend_postgres -f

Write-Host ""
Write-Host "🔨 Paso 5: Creando nuevo PostgreSQL con credenciales correctas..." -ForegroundColor Cyan
docker-compose up -d postgres

Write-Host ""
Write-Host "⏳ Paso 6: Esperando a que PostgreSQL esté listo (15 segundos)..." -ForegroundColor Cyan
Start-Sleep -Seconds 15

Write-Host ""
Write-Host "🗄️  Paso 7: Creando base de datos 'gym_exercise' (para workout-service)..." -ForegroundColor Cyan
docker-compose exec -T postgres psql -U postgres -c "CREATE DATABASE gym_exercise;"

Write-Host ""
Write-Host "✅ Paso 8: Verificando bases de datos creadas..." -ForegroundColor Cyan
docker-compose exec -T postgres psql -U postgres -c "\l"

Write-Host ""
Write-Host "🚀 Paso 9: Reiniciando servicios que usan PostgreSQL..." -ForegroundColor Cyan
docker-compose up -d user-service authentication workout-service

Write-Host ""
Write-Host "✨ Proceso completado!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Próximos pasos:" -ForegroundColor Cyan
Write-Host "  1. Espera 30-60 segundos para que los servicios inicien" -ForegroundColor White
Write-Host "  2. Verifica los logs: docker-compose logs -f user-service authentication workout-service" -ForegroundColor White
Write-Host "  3. Verifica Eureka: http://localhost:8761" -ForegroundColor White
Write-Host ""
Write-Host "ℹ️  Credenciales de PostgreSQL:" -ForegroundColor Cyan
Write-Host "  Usuario: postgres" -ForegroundColor White
Write-Host "  Password: (ver .env DB_PASSWORD)" -ForegroundColor White
Write-Host "  Bases de datos: gym_authentication, gym_exercise" -ForegroundColor White
