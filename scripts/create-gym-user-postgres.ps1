# Script para crear el usuario gym_user en PostgreSQL existente
# Alternativa SIN borrar datos

Write-Host "🔧 Creando usuario 'gym_user' en PostgreSQL existente..." -ForegroundColor Cyan
Write-Host "Este script NO borrará datos existentes" -ForegroundColor Green
Write-Host ""

# Leer password del .env
$envContent = Get-Content ".env" -Raw
if ($envContent -match 'DB_PASSWORD=(.+)') {
    $dbPassword = $matches[1].Trim()
    Write-Host "✅ Password encontrado en .env" -ForegroundColor Green
} else {
    Write-Host "❌ No se pudo leer DB_PASSWORD del archivo .env" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📝 Creando usuario 'gym_user' con permisos..." -ForegroundColor Cyan

# Crear el usuario si no existe
$createUserSQL = @"
DO \$\$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_user WHERE usename = 'gym_user') THEN
        CREATE USER gym_user WITH PASSWORD '$dbPassword';
    END IF;
END
\$\$;
"@

docker-compose exec -T postgres psql -U postgres -c "$createUserSQL"

Write-Host ""
Write-Host "🔐 Otorgando permisos a 'gym_user' en gym_authentication..." -ForegroundColor Cyan
docker-compose exec -T postgres psql -U postgres -d gym_authentication -c "GRANT ALL PRIVILEGES ON DATABASE gym_authentication TO gym_user;"
docker-compose exec -T postgres psql -U postgres -d gym_authentication -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO gym_user;"
docker-compose exec -T postgres psql -U postgres -d gym_authentication -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO gym_user;"
docker-compose exec -T postgres psql -U postgres -d gym_authentication -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO gym_user;"

Write-Host ""
Write-Host "🔐 Otorgando permisos a 'gym_user' en gym_exercise..." -ForegroundColor Cyan

# Crear gym_exercise si no existe
docker-compose exec -T postgres psql -U postgres -c "SELECT 1 FROM pg_database WHERE datname = 'gym_exercise'" | Select-String -Pattern "1" -Quiet
if (-not $?) {
    Write-Host "  Creando base de datos gym_exercise..." -ForegroundColor Yellow
    docker-compose exec -T postgres psql -U postgres -c "CREATE DATABASE gym_exercise;"
}

docker-compose exec -T postgres psql -U postgres -d gym_exercise -c "GRANT ALL PRIVILEGES ON DATABASE gym_exercise TO gym_user;"
docker-compose exec -T postgres psql -U postgres -d gym_exercise -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO gym_user;"
docker-compose exec -T postgres psql -U postgres -d gym_exercise -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO gym_user;"
docker-compose exec -T postgres psql -U postgres -d gym_exercise -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO gym_user;"

Write-Host ""
Write-Host "✅ Usuario 'gym_user' creado y configurado!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Verificando usuario..." -ForegroundColor Cyan
docker-compose exec -T postgres psql -U postgres -c "\du gym_user"

Write-Host ""
Write-Host "🔄 Ahora actualiza el .env con:" -ForegroundColor Cyan
Write-Host "  DB_USERNAME=gym_user" -ForegroundColor Yellow
Write-Host ""
Write-Host "Y reinicia los servicios:" -ForegroundColor Cyan
Write-Host "  docker-compose restart user-service authentication workout-service" -ForegroundColor Yellow
