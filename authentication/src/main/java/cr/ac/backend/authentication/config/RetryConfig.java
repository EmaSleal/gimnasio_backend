package cr.ac.backend.authentication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Configuración para habilitar Spring Retry
 * Permite reintentos automáticos en métodos anotados con @Retryable
 */
@Configuration
@EnableRetry
public class RetryConfig {
    // Spring Retry habilitado para reintentos en EmailEventListener
}
