# Script de verificación de configuración Docker
# Valida que las variables de entorno estén correctamente configuradas

Write-Host "🔍 Verificando configuración de Docker Compose..." -ForegroundColor Cyan
Write-Host ""

# Verificar que existe .env
if (Test-Path ".env") {
    Write-Host "✅ Archivo .env encontrado" -ForegroundColor Green
} else {
    Write-Host "❌ Archivo .env NO encontrado" -ForegroundColor Red
    exit 1
}

# Validar docker-compose.yml
Write-Host "📋 Validando docker-compose.yml..." -ForegroundColor Cyan
$validateResult = docker-compose config 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ docker-compose.yml es válido" -ForegroundColor Green
} else {
    Write-Host "❌ docker-compose.yml tiene errores:" -ForegroundColor Red
    Write-Host $validateResult
    exit 1
}

# Verificar servicios con base de datos
Write-Host ""
Write-Host "🗄️  Servicios que requieren PostgreSQL:" -ForegroundColor Cyan
Write-Host "  - authentication (DB_HOST debe ser 'postgres')" -ForegroundColor Yellow
Write-Host "  - user-service (DB_HOST debe ser 'postgres')" -ForegroundColor Yellow
Write-Host "  - workout-service (DB_HOST debe ser 'postgres')" -ForegroundColor Yellow

# Verificar que DB_PASSWORD esté configurado
Write-Host ""
Write-Host "🔐 Verificando variables críticas en .env:" -ForegroundColor Cyan

$envContent = Get-Content ".env" -Raw

$criticalVars = @(
    "DB_PASSWORD",
    "JWT_SECRET",
    "RESEND_API_KEY",
    "GRAFANA_ADMIN_PASSWORD"
)

foreach ($var in $criticalVars) {
    if ($envContent -match "$var=.+") {
        Write-Host "  ✅ $var configurado" -ForegroundColor Green
    } else {
        Write-Host "  ❌ $var NO configurado o vacío" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "📝 Resumen de configuración:" -ForegroundColor Cyan
Write-Host "  - Los servicios en Docker usarán 'postgres' como DB_HOST" -ForegroundColor White
Write-Host "  - El docker-compose.yml sobrescribe DB_HOST automáticamente" -ForegroundColor White
Write-Host "  - Para desarrollo local, usa DB_HOST=localhost en .env" -ForegroundColor White

Write-Host ""
Write-Host "✨ Verificación completada!" -ForegroundColor Green
Write-Host ""
Write-Host "Para iniciar los servicios, ejecuta:" -ForegroundColor Cyan
Write-Host "  docker-compose up -d" -ForegroundColor Yellow
Write-Host ""
Write-Host "Para ver los logs:" -ForegroundColor Cyan
Write-Host "  docker-compose logs -f [nombre-servicio]" -ForegroundColor Yellow
