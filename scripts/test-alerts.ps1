# =============================================================================
# SCRIPT DE PRUEBA DE ALERTAS - Sistema de Gimnasio Backend
# =============================================================================
#
# Este script simula diferentes escenarios para disparar alertas y validar
# que el sistema de monitoreo está funcionando correctamente.
#
# Pruebas incluidas:
# 1. Servicio Caído (ServiceDown)
# 2. Memoria Alta (simulación de carga)
# 3. Latencia Alta (requests lentas)
# 4. Tasa de Errores Alta (requests que fallan)
#
# =============================================================================

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet('all', 'service-down', 'memory', 'latency', 'errors')]
    [string]$TestType = 'all',
    
    [Parameter(Mandatory=$false)]
    [string]$ServiceToStop = 'user-service'
)

# Colores para output
function Write-Header {
    param([string]$Text)
    Write-Host "`n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
    Write-Host "  $Text" -ForegroundColor Cyan
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
}

function Write-Step {
    param([string]$Text)
    Write-Host "`n▶ $Text" -ForegroundColor Yellow
}

function Write-Success {
    param([string]$Text)
    Write-Host "  ✅ $Text" -ForegroundColor Green
}

function Write-Info {
    param([string]$Text)
    Write-Host "  ℹ️  $Text" -ForegroundColor Blue
}

function Write-Warning {
    param([string]$Text)
    Write-Host "  ⚠️  $Text" -ForegroundColor Yellow
}

function Write-Error-Custom {
    param([string]$Text)
    Write-Host "  ❌ $Text" -ForegroundColor Red
}

# =============================================================================
# FUNCIÓN: Verificar estado inicial
# =============================================================================
function Test-InitialState {
    Write-Header "VERIFICACIÓN DE ESTADO INICIAL"
    
    Write-Step "Verificando Prometheus..."
    try {
        $prometheus = Invoke-RestMethod -Uri "http://localhost:9090/api/v1/targets" -ErrorAction Stop
        $upTargets = ($prometheus.data.activeTargets | Where-Object { $_.health -eq "up" }).Count
        $totalTargets = $prometheus.data.activeTargets.Count
        Write-Success "Prometheus operativo: $upTargets/$totalTargets targets UP"
    } catch {
        Write-Error-Custom "Prometheus no está disponible: $($_.Exception.Message)"
        exit 1
    }
    
    Write-Step "Verificando Alertmanager..."
    try {
        $alertmanager = Invoke-RestMethod -Uri "http://localhost:9093/api/v2/status" -ErrorAction Stop
        Write-Success "Alertmanager operativo: Versión $($alertmanager.versionInfo.version)"
    } catch {
        Write-Error-Custom "Alertmanager no está disponible: $($_.Exception.Message)"
        exit 1
    }
    
    Write-Step "Verificando Grafana..."
    try {
        $grafana = Invoke-RestMethod -Uri "http://localhost:3000/api/health" -ErrorAction Stop
        Write-Success "Grafana operativo: Versión $($grafana.version)"
    } catch {
        Write-Error-Custom "Grafana no está disponible: $($_.Exception.Message)"
        exit 1
    }
    
    Write-Step "Verificando alertas activas en Prometheus..."
    try {
        $alerts = Invoke-RestMethod -Uri "http://localhost:9090/api/v1/alerts" -ErrorAction Stop
        $firingAlerts = ($alerts.data.alerts | Where-Object { $_.state -eq "firing" }).Count
        if ($firingAlerts -gt 0) {
            Write-Warning "Ya hay $firingAlerts alertas activas"
        } else {
            Write-Success "No hay alertas activas (estado esperado)"
        }
    } catch {
        Write-Error-Custom "Error al verificar alertas: $($_.Exception.Message)"
    }
}

# =============================================================================
# TEST 1: SERVICE DOWN - Detener un servicio
# =============================================================================
function Test-ServiceDown {
    Write-Header "TEST 1: ALERTA DE SERVICIO CAÍDO"
    
    Write-Step "Deteniendo servicio: $ServiceToStop"
    Write-Info "Este test disparará la alerta 'ServiceDown' después de ~1 minuto"
    
    try {
        docker-compose stop $ServiceToStop
        Write-Success "Servicio $ServiceToStop detenido"
    } catch {
        Write-Error-Custom "Error al detener servicio: $($_.Exception.Message)"
        return
    }
    
    Write-Step "Esperando 90 segundos para que Prometheus detecte el servicio DOWN..."
    Write-Info "Prometheus scrape_interval: 15s, alerta dispara después de 1m"
    
    for ($i = 90; $i -gt 0; $i -= 10) {
        Write-Host "  ⏳ $i segundos restantes..." -ForegroundColor Gray
        Start-Sleep -Seconds 10
    }
    
    Write-Step "Verificando alertas en Prometheus..."
    try {
        $alerts = Invoke-RestMethod -Uri "http://localhost:9090/api/v1/alerts"
        $serviceDownAlert = $alerts.data.alerts | Where-Object { 
            $_.labels.alertname -eq "ServiceDown" -and $_.labels.job -eq $ServiceToStop 
        }
        
        if ($serviceDownAlert) {
            if ($serviceDownAlert.state -eq "firing") {
                Write-Success "Alerta ServiceDown DISPARADA correctamente"
                Write-Info "Estado: $($serviceDownAlert.state)"
                Write-Info "Servicio: $($serviceDownAlert.labels.job)"
            } elseif ($serviceDownAlert.state -eq "pending") {
                Write-Warning "Alerta ServiceDown en estado PENDING (esperando 'for' duration)"
                Write-Info "Espera otros 30 segundos y vuelve a verificar"
            }
        } else {
            Write-Warning "Alerta ServiceDown aún no detectada, espera más tiempo"
        }
    } catch {
        Write-Error-Custom "Error al verificar alertas: $($_.Exception.Message)"
    }
    
    Write-Step "Verificando en Alertmanager..."
    try {
        $amAlerts = Invoke-RestMethod -Uri "http://localhost:9093/api/v2/alerts"
        $amServiceDown = $amAlerts | Where-Object { 
            $_.labels.alertname -eq "ServiceDown" -and $_.labels.job -eq $ServiceToStop 
        }
        
        if ($amServiceDown) {
            Write-Success "Alerta recibida en Alertmanager"
            Write-Info "Receivers: $($amServiceDown.receivers -join ', ')"
        } else {
            Write-Info "Alerta aún no enviada a Alertmanager (puede tardar hasta 30s)"
        }
    } catch {
        Write-Error-Custom "Error al verificar Alertmanager: $($_.Exception.Message)"
    }
    
    Write-Step "¿Restaurar el servicio? (S/N)"
    $restore = Read-Host
    if ($restore -eq 'S' -or $restore -eq 's') {
        Write-Step "Restaurando servicio $ServiceToStop..."
        docker-compose start $ServiceToStop
        Write-Success "Servicio restaurado. La alerta se resolverá automáticamente en ~5 minutos"
    } else {
        Write-Warning "Servicio $ServiceToStop sigue detenido. Restaurar manualmente con:"
        Write-Info "docker-compose start $ServiceToStop"
    }
}

# =============================================================================
# TEST 2: HIGH MEMORY - Simular carga de memoria
# =============================================================================
function Test-HighMemory {
    Write-Header "TEST 2: ALERTA DE MEMORIA ALTA"
    
    Write-Warning "Esta prueba requiere generar carga en la JVM del servicio"
    Write-Info "Opciones:"
    Write-Info "  1. Ejecutar script de carga (crear objetos en memoria)"
    Write-Info "  2. Reducir -Xmx en Dockerfile y reconstruir"
    Write-Info "  3. Usar herramienta de stress testing (Apache JMeter, wrk)"
    
    Write-Step "Método recomendado: Apache JMeter o similar para simular carga"
    Write-Info "Ejemplo de comando con Apache Bench:"
    Write-Host "  ab -n 10000 -c 100 http://localhost:8590/user-service/api/users" -ForegroundColor Cyan
    
    Write-Step "Monitoreando uso actual de memoria..."
    try {
        $memoryQuery = "jvm_memory_used_bytes{area='heap'} / jvm_memory_max_bytes{area='heap'} * 100"
        $encodedQuery = [System.Web.HttpUtility]::UrlEncode($memoryQuery)
        $memory = Invoke-RestMethod -Uri "http://localhost:9090/api/v1/query?query=$encodedQuery"
        
        if ($memory.data.result.Count -gt 0) {
            Write-Info "Uso de memoria por servicio:"
            foreach ($result in $memory.data.result) {
                $service = $result.metric.job
                $usage = [math]::Round([double]$result.value[1], 2)
                $color = if ($usage -gt 90) { "Red" } elseif ($usage -gt 70) { "Yellow" } else { "Green" }
                Write-Host "    $service : $usage%" -ForegroundColor $color
            }
        }
    } catch {
        Write-Error-Custom "Error al consultar memoria: $($_.Exception.Message)"
    }
    
    Write-Info "Para disparar alerta HighMemoryUsage, el heap debe superar 90% por 2 minutos"
}

# =============================================================================
# TEST 3: HIGH LATENCY - Simular latencia alta
# =============================================================================
function Test-HighLatency {
    Write-Header "TEST 3: ALERTA DE LATENCIA ALTA"
    
    Write-Info "Para disparar alerta de latencia, el p95 debe superar 1 segundo por 3 minutos"
    Write-Step "Opciones para generar latencia:"
    Write-Info "  1. Agregar Thread.sleep() en un endpoint"
    Write-Info "  2. Ejecutar queries pesadas en la BD"
    Write-Info "  3. Simular carga con herramienta de stress testing"
    
    Write-Step "Monitoreando latencia actual (p95)..."
    try {
        $latencyQuery = "histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{uri!~'/actuator.*'}[5m])) by (job, uri, le))"
        $encodedQuery = [System.Web.HttpUtility]::UrlEncode($latencyQuery)
        $latency = Invoke-RestMethod -Uri "http://localhost:9090/api/v1/query?query=$encodedQuery"
        
        if ($latency.data.result.Count -gt 0) {
            Write-Info "Latencia P95 por endpoint:"
            foreach ($result in $latency.data.result) {
                $service = $result.metric.job
                $uri = $result.metric.uri
                $lat = [math]::Round([double]$result.value[1], 3)
                $color = if ($lat -gt 1) { "Red" } elseif ($lat -gt 0.5) { "Yellow" } else { "Green" }
                Write-Host "    $service $uri : ${lat}s" -ForegroundColor $color
            }
        } else {
            Write-Warning "No hay datos de latencia disponibles (puede necesitar tráfico)"
        }
    } catch {
        Write-Error-Custom "Error al consultar latencia: $($_.Exception.Message)"
    }
}

# =============================================================================
# TEST 4: HIGH ERROR RATE - Simular errores
# =============================================================================
function Test-HighErrorRate {
    Write-Header "TEST 4: ALERTA DE TASA ALTA DE ERRORES"
    
    Write-Info "Para disparar alerta de errores 5xx, debe haber >10 errores/minuto por 2 minutos"
    Write-Step "Opciones para generar errores:"
    Write-Info "  1. Detener base de datos (genera 500 en servicios)"
    Write-Info "  2. Hacer requests a endpoints inexistentes (genera 404)"
    Write-Info "  3. Enviar datos inválidos (genera 400/422)"
    
    Write-Step "Monitoreando tasa actual de errores..."
    try {
        $errorQuery = "sum(rate(http_server_requests_seconds_count{status=~'5..'}[1m])) by (job)"
        $encodedQuery = [System.Web.HttpUtility]::UrlEncode($errorQuery)
        $errors = Invoke-RestMethod -Uri "http://localhost:9090/api/v1/query?query=$encodedQuery"
        
        if ($errors.data.result.Count -gt 0) {
            Write-Info "Tasa de errores 5xx por minuto:"
            foreach ($result in $errors.data.result) {
                $service = $result.metric.job
                $rate = [math]::Round([double]$result.value[1], 2)
                $color = if ($rate -gt 10) { "Red" } elseif ($rate -gt 5) { "Yellow" } else { "Green" }
                Write-Host "    $service : $rate errores/min" -ForegroundColor $color
            }
        } else {
            Write-Success "No hay errores 5xx actualmente"
        }
    } catch {
        Write-Error-Custom "Error al consultar errores: $($_.Exception.Message)"
    }
    
    Write-Step "Generando errores de prueba (deteniendo PostgreSQL temporalmente)..."
    Write-Warning "Esto generará errores 500 en servicios que usan BD"
    $generate = Read-Host "¿Proceder? (S/N)"
    
    if ($generate -eq 'S' -or $generate -eq 's') {
        Write-Step "Deteniendo PostgreSQL..."
        docker-compose stop postgres-authentication postgres-exercise
        Write-Success "PostgreSQL detenido"
        
        Write-Info "Generando requests que fallarán..."
        Write-Host "  curl http://localhost:8590/user-service/api/users (este fallará)" -ForegroundColor Cyan
        
        for ($i = 1; $i -le 20; $i++) {
            try {
                Invoke-WebRequest -Uri "http://localhost:8590/user-service/api/users" -UseBasicParsing -ErrorAction SilentlyContinue | Out-Null
            } catch {
                # Esperamos errores
            }
            Start-Sleep -Milliseconds 500
        }
        
        Write-Success "Generados 20 requests (algunos habrán fallado)"
        
        Write-Step "Restaurando PostgreSQL..."
        docker-compose start postgres-authentication postgres-exercise
        Write-Success "PostgreSQL restaurado"
        
        Write-Info "Espera 2-3 minutos y verifica alertas en Prometheus"
    }
}

# =============================================================================
# MENÚ PRINCIPAL
# =============================================================================
function Show-Menu {
    Write-Header "MENÚ DE PRUEBAS DE ALERTAS"
    Write-Host ""
    Write-Host "  1. Verificar Estado Inicial" -ForegroundColor White
    Write-Host "  2. Test: Servicio Caído (ServiceDown)" -ForegroundColor White
    Write-Host "  3. Test: Memoria Alta (HighMemoryUsage)" -ForegroundColor White
    Write-Host "  4. Test: Latencia Alta (HighLatencyP95)" -ForegroundColor White
    Write-Host "  5. Test: Tasa de Errores Alta (HighErrorRate5xx)" -ForegroundColor White
    Write-Host "  6. Ejecutar Todos los Tests" -ForegroundColor White
    Write-Host "  7. Consultar Alertas Activas" -ForegroundColor White
    Write-Host "  0. Salir" -ForegroundColor Gray
    Write-Host ""
}

function Get-ActiveAlerts {
    Write-Header "ALERTAS ACTIVAS"
    
    Write-Step "Consultando Prometheus..."
    try {
        $alerts = Invoke-RestMethod -Uri "http://localhost:9090/api/v1/alerts"
        $firingAlerts = $alerts.data.alerts | Where-Object { $_.state -eq "firing" }
        $pendingAlerts = $alerts.data.alerts | Where-Object { $_.state -eq "pending" }
        
        if ($firingAlerts) {
            Write-Host "`n  🔥 FIRING ($($firingAlerts.Count)):" -ForegroundColor Red
            foreach ($alert in $firingAlerts) {
                Write-Host "    • $($alert.labels.alertname) - $($alert.labels.job)" -ForegroundColor Red
                Write-Host "      $($alert.annotations.description)" -ForegroundColor Gray
            }
        } else {
            Write-Success "No hay alertas FIRING"
        }
        
        if ($pendingAlerts) {
            Write-Host "`n  ⏳ PENDING ($($pendingAlerts.Count)):" -ForegroundColor Yellow
            foreach ($alert in $pendingAlerts) {
                Write-Host "    • $($alert.labels.alertname) - $($alert.labels.job)" -ForegroundColor Yellow
            }
        }
    } catch {
        Write-Error-Custom "Error: $($_.Exception.Message)"
    }
    
    Write-Step "Consultando Alertmanager..."
    try {
        $amAlerts = Invoke-RestMethod -Uri "http://localhost:9093/api/v2/alerts"
        if ($amAlerts) {
            Write-Host "`n  📬 En Alertmanager ($($amAlerts.Count)):" -ForegroundColor Cyan
            foreach ($alert in $amAlerts) {
                Write-Host "    • $($alert.labels.alertname) - $($alert.labels.job)" -ForegroundColor Cyan
                Write-Host "      Receivers: $($alert.receivers -join ', ')" -ForegroundColor Gray
            }
        } else {
            Write-Info "No hay alertas en Alertmanager"
        }
    } catch {
        Write-Error-Custom "Error: $($_.Exception.Message)"
    }
}

# =============================================================================
# EJECUCIÓN PRINCIPAL
# =============================================================================

# Si se especificó un tipo de test en parámetros, ejecutarlo directamente
if ($TestType -ne 'all') {
    Test-InitialState
    
    switch ($TestType) {
        'service-down' { Test-ServiceDown }
        'memory' { Test-HighMemory }
        'latency' { Test-HighLatency }
        'errors' { Test-HighErrorRate }
    }
    exit 0
}

# Menú interactivo
while ($true) {
    Show-Menu
    $choice = Read-Host "Selecciona una opción"
    
    switch ($choice) {
        '1' { Test-InitialState }
        '2' { Test-ServiceDown }
        '3' { Test-HighMemory }
        '4' { Test-HighLatency }
        '5' { Test-HighErrorRate }
        '6' { 
            Test-InitialState
            Test-ServiceDown
            Test-HighMemory
            Test-HighLatency
            Test-HighErrorRate
        }
        '7' { Get-ActiveAlerts }
        '0' { 
            Write-Host "`n👋 Saliendo..." -ForegroundColor Cyan
            exit 0
        }
        default { Write-Warning "Opción inválida" }
    }
    
    Write-Host "`nPresiona Enter para continuar..." -ForegroundColor Gray
    Read-Host
}
