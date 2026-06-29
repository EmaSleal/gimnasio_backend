package cr.ac.backend.apigateway.config;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gateway Configuration con Circuit Breakers y Time Limiters.
 * 
 * Configuración de Resilience4j:
 * - Circuit Breaker: Protege servicios de fallos en cascada
 * - Time Limiter: Evita timeouts largos (3s máximo)
 * - Fallback: Respuestas amigables cuando servicios caen
 * 
 * Rutas protegidas:
 * - /Login → authentication service
 * - /user/** → user-service
 * - /workout/**, /workoutPlan/**, etc. → workout-service
 * 
 * Parámetros Circuit Breaker:
 * - sliding-window-size: 10 llamadas
 * - failure-rate-threshold: 50% fallos
 * - wait-duration-in-open-state: 10 segundos
 * - timeout-duration: 3 segundos
 */
@Configuration
@RestController
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter filter;

    /**
     * Define las rutas del Gateway con Circuit Breakers aplicados.
     * 
     * Cada ruta incluye:
     * 1. AuthenticationFilter para validación JWT
     * 2. CircuitBreaker con nombre del servicio
     * 3. Fallback URI en caso de fallo
     * 4. Load Balancing con Eureka (lb://)
     */
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Ruta: Authentication Service (Login)
                .route("authentication", r -> r.path("/Login")
                        .filters(f -> f
                                .filter(filter)
                                .circuitBreaker(config -> config
                                        .setName("authentication")
                                        .setFallbackUri("forward:/fallback/authentication")
                                )
                        )
                        .uri("lb://authentication"))
                
                // Ruta: User Service
                .route("user-service", r -> r.path("/user/**")
                        .filters(f -> f
                                .filter(filter)
                                .circuitBreaker(config -> config
                                        .setName("user-service")
                                        .setFallbackUri("forward:/fallback/user-service")
                                )
                        )
                        .uri("lb://user-service"))
                
                // Ruta: Workout Service
                .route("workout-service", r -> r.path("/workout/**", "/workoutSpecification/**", "/workoutPlan/**", "/dailyRoutine/**", "/muscularGroup/**")
                        .filters(f -> f
                                .filter(filter)
                                .circuitBreaker(config -> config
                                        .setName("workout-service")
                                        .setFallbackUri("forward:/fallback/workout-service")
                                )
                        )
                        .uri("lb://workout-service"))
                .build();
    }
}
