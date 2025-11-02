# Script de verificación de registro en Eureka
# Valida que todos los servicios estén correctamente registrados

Write-Host "🔍 Verificando registro de servicios en Eureka..." -ForegroundColor Cyan
Write-Host ""

$eurekaUrl = "http://localhost:8761"
$maxRetries = 10
$retryDelay = 3

# Esperar a que Eureka esté disponible
Write-Host "⏳ Esperando a que Eureka esté disponible..." -ForegroundColor Yellow
$eurekaReady = $false
for ($i = 1; $i -le $maxRetries; $i++) {
    try {
        $response = Invoke-WebRequest -Uri "$eurekaUrl/actuator/health" -UseBasicParsing -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            $eurekaReady = $true
            Write-Host "✅ Eureka Server está disponible" -ForegroundColor Green
            break
        }
    } catch {
        Write-Host "  Intento $i/$maxRetries - Eureka no disponible aún..." -ForegroundColor Gray
        Start-Sleep -Seconds $retryDelay
    }
}

if (-not $eurekaReady) {
    Write-Host "❌ Eureka Server no está disponible después de $maxRetries intentos" -ForegroundColor Red
    Write-Host "   Verifica que el contenedor esté corriendo: docker-compose ps eureka-server" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "📋 Consultando servicios registrados..." -ForegroundColor Cyan

try {
    # Obtener información de aplicaciones registradas
    $response = Invoke-RestMethod -Uri "$eurekaUrl/eureka/apps" -Headers @{Accept="application/json"} -ErrorAction Stop
    
    $registeredApps = $response.applications.application
    
    if ($null -eq $registeredApps) {
        Write-Host "⚠️  No hay servicios registrados en Eureka" -ForegroundColor Yellow
        exit 1
    }
    
    # Servicios esperados (sin eureka-server que no se auto-registra)
    $expectedServices = @(
        "CONFIG-SERVICE",
        "API-GATEWAY",
        "AUTHENTICATION",
        "USER-SERVICE",
        "WORKOUT-SERVICE",
        "ADMIN-SERVICE"
    )
    
    Write-Host ""
    Write-Host "📊 Servicios Esperados vs Registrados:" -ForegroundColor Cyan
    Write-Host "=======================================" -ForegroundColor Cyan
    
    $registeredCount = 0
    $missingServices = @()
    
    foreach ($service in $expectedServices) {
        $app = $registeredApps | Where-Object { $_.name -eq $service }
        
        if ($app) {
            $instanceCount = if ($app.instance -is [Array]) { $app.instance.Count } else { 1 }
            $status = $app.instance[0].status
            
            if ($status -eq "UP") {
                Write-Host "  ✅ $service" -ForegroundColor Green -NoNewline
                Write-Host " - $instanceCount instancia(s) - Status: $status" -ForegroundColor White
                $registeredCount++
            } else {
                Write-Host "  ⚠️  $service" -ForegroundColor Yellow -NoNewline
                Write-Host " - Status: $status (no UP)" -ForegroundColor Yellow
            }
        } else {
            Write-Host "  ❌ $service" -ForegroundColor Red -NoNewline
            Write-Host " - NO REGISTRADO" -ForegroundColor Red
            $missingServices += $service
        }
    }
    
    Write-Host ""
    Write-Host "=======================================" -ForegroundColor Cyan
    Write-Host "Resumen: $registeredCount de $($expectedServices.Count) servicios registrados correctamente" -ForegroundColor $(if ($registeredCount -eq $expectedServices.Count) { "Green" } else { "Yellow" })
    
    if ($missingServices.Count -gt 0) {
        Write-Host ""
        Write-Host "⚠️  Servicios faltantes:" -ForegroundColor Yellow
        foreach ($service in $missingServices) {
            Write-Host "   - $service" -ForegroundColor Red
        }
        
        Write-Host ""
        Write-Host "💡 Posibles causas:" -ForegroundColor Cyan
        Write-Host "   1. El servicio no ha terminado de iniciar (espera 30-60s más)" -ForegroundColor White
        Write-Host "   2. Error en la configuración de Eureka en application.yml" -ForegroundColor White
        Write-Host "   3. Variable de entorno EUREKA_HOST incorrecta" -ForegroundColor White
        Write-Host "   4. El servicio falló al iniciar (revisar logs)" -ForegroundColor White
        
        Write-Host ""
        Write-Host "🔧 Comandos de diagnóstico:" -ForegroundColor Cyan
        foreach ($service in $missingServices) {
            $containerName = $service.ToLower() -replace '-','-'
            Write-Host "   docker-compose logs $containerName | Select-String 'eureka'" -ForegroundColor Yellow
        }
    } else {
        Write-Host ""
        Write-Host "🎉 ¡Todos los servicios están registrados correctamente!" -ForegroundColor Green
    }
    
    # Mostrar información adicional de instancias
    Write-Host ""
    Write-Host "📍 Detalles de instancias registradas:" -ForegroundColor Cyan
    Write-Host "=======================================" -ForegroundColor Cyan
    
    foreach ($app in $registeredApps) {
        $instances = if ($app.instance -is [Array]) { $app.instance } else { @($app.instance) }
        
        foreach ($instance in $instances) {
            $name = $app.name
            $ipAddr = $instance.ipAddr
            $port = $instance.port.'$'
            $status = $instance.status
            $healthUrl = $instance.healthCheckUrl
            
            Write-Host "  📦 $name" -ForegroundColor White
            Write-Host "     IP: $ipAddr : $port" -ForegroundColor Gray
            Write-Host "     Status: $status" -ForegroundColor $(if ($status -eq "UP") { "Green" } else { "Yellow" })
            Write-Host "     Health: $healthUrl" -ForegroundColor Gray
            Write-Host ""
        }
    }
    
} catch {
    Write-Host "❌ Error al consultar Eureka: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "💡 Sugerencias:" -ForegroundColor Cyan
    Write-Host "   1. Verifica que Eureka esté corriendo: docker-compose ps eureka-server" -ForegroundColor White
    Write-Host "   2. Verifica los logs: docker-compose logs eureka-server" -ForegroundColor White
    Write-Host "   3. Accede manualmente a: $eurekaUrl" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "✨ Verificación completada" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Accede al dashboard de Eureka: $eurekaUrl" -ForegroundColor Cyan
