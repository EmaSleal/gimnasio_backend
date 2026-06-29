package cr.ac.backend.apigateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Fallback Controller para Circuit Breakers.
 * 
 * Proporciona respuestas amigables cuando los servicios no están disponibles
 * debido a:
 * - Circuit Breaker OPEN (demasiados fallos)
 * - Timeout (>3s)
 * - Servicio caído
 * 
 * Endpoints:
 * - /fallback/user-service
 * - /fallback/workout-service
 * - /fallback/authentication
 * 
 * Retorna HTTP 503 (Service Unavailable) con mensaje descriptivo.
 */
@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    /**
     * Fallback para User Service.
     * 
     * Causas posibles:
     * - Circuit Breaker abierto (>50% fallos en últimas 10 llamadas)
     * - Timeout (>3s de respuesta)
     * - Servicio no disponible
     */
    @GetMapping("/user-service")
    @PostMapping("/user-service")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        log.warn("⚠️ Circuit Breaker activado para user-service - Servicio temporalmente no disponible");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "success", false,
                        "error", "SERVICE_UNAVAILABLE",
                        "message", "User Service está temporalmente no disponible. Por favor, intenta nuevamente en unos momentos.",
                        "service", "user-service",
                        "timestamp", LocalDateTime.now().toString(),
                        "estimatedRecovery", "2-5 minutos",
                        "retryAfter", "30 segundos",
                        "status", 503,
                        "details", "El servicio está experimentando problemas. Circuit Breaker activado."
                ));
    }
    
    /**
     * Fallback para Workout Service.
     * 
     * Causas posibles:
     * - Circuit Breaker abierto (>50% fallos en últimas 10 llamadas)
     * - Timeout (>3s de respuesta)
     * - Servicio no disponible
     */
    @GetMapping("/workout-service")
    @PostMapping("/workout-service")
    public ResponseEntity<Map<String, Object>> workoutServiceFallback() {
        log.warn("⚠️ Circuit Breaker activado para workout-service - Servicio temporalmente no disponible");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "success", false,
                        "error", "SERVICE_UNAVAILABLE",
                        "message", "Workout Service está temporalmente no disponible. Por favor, intenta nuevamente en unos momentos.",
                        "service", "workout-service",
                        "timestamp", LocalDateTime.now().toString(),
                        "estimatedRecovery", "2-5 minutos",
                        "retryAfter", "30 segundos",
                        "status", 503,
                        "details", "El servicio está experimentando problemas. Circuit Breaker activado."
                ));
    }
    
    /**
     * Fallback para Authentication Service.
     * 
     * Causas posibles:
     * - Circuit Breaker abierto (>50% fallos en últimas 10 llamadas)
     * - Timeout (>3s de respuesta)
     * - Servicio no disponible
     */
    @GetMapping("/authentication")
    @PostMapping("/authentication")
    public ResponseEntity<Map<String, Object>> authenticationServiceFallback() {
        log.warn("⚠️ Circuit Breaker activado para authentication - Servicio temporalmente no disponible");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "success", false,
                        "error", "SERVICE_UNAVAILABLE",
                        "message", "Authentication Service está temporalmente no disponible. Por favor, intenta nuevamente en unos momentos.",
                        "service", "authentication",
                        "timestamp", LocalDateTime.now().toString(),
                        "estimatedRecovery", "2-5 minutos",
                        "retryAfter", "30 segundos",
                        "status", 503,
                        "details", "El servicio está experimentando problemas. Circuit Breaker activado."
                ));
    }
    
    /**
     * Fallback genérico para servicios no especificados.
     */
    @GetMapping("/generic")
    @PostMapping("/generic")
    public ResponseEntity<Map<String, Object>> genericFallback() {
        log.warn("⚠️ Circuit Breaker activado para servicio desconocido");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "success", false,
                        "error", "SERVICE_UNAVAILABLE",
                        "message", "El servicio solicitado está temporalmente no disponible. Por favor, intenta nuevamente en unos momentos.",
                        "service", "unknown",
                        "timestamp", LocalDateTime.now().toString(),
                        "estimatedRecovery", "2-5 minutos",
                        "retryAfter", "30 segundos",
                        "status", 503,
                        "details", "El servicio está experimentando problemas. Circuit Breaker activado."
                ));
    }
}
