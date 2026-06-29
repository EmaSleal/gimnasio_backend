package cr.ac.backend.authentication.listener;

import cr.ac.backend.authentication.event.PasswordResetEmailEvent;
import cr.ac.backend.authentication.event.WelcomeEmailEvent;
import cr.ac.backend.authentication.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * Listener para eventos de email asíncronos
 * Consume mensajes de RabbitMQ y delega el envío al EmailService
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailService emailService;

    /**
     * Escucha eventos de email de bienvenida
     * Queue: email.welcome.queue
     * 
     * @param event Evento con datos del usuario registrado
     */
    @RabbitListener(queues = "email.welcome.queue")
    @Retryable(
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2.0),
        retryFor = {Exception.class}
    )
    public void handleWelcomeEmailEvent(WelcomeEmailEvent event) {
        log.info("📧 Procesando WelcomeEmailEvent para user: {} (email: {})", 
                event.getUserName(), event.getEmail());
        
        try {
            // Enviar email de bienvenida
            emailService.sendWelcomeEmail(event.getEmail(), event.getUserName());
            
            log.info("✅ Email de bienvenida enviado exitosamente a: {}", event.getEmail());
            
        } catch (Exception e) {
            log.error("❌ Error enviando email de bienvenida a: {}. Error: {}", 
                    event.getEmail(), e.getMessage(), e);
            throw e; // Re-throw para que Retry maneje el reintento
        }
    }

    /**
     * Escucha eventos de email de recuperación de contraseña
     * Queue: email.password-reset.queue
     * 
     * @param event Evento con datos del token de reset
     */
    @RabbitListener(queues = "email.password-reset.queue")
    @Retryable(
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2.0),
        retryFor = {Exception.class}
    )
    public void handlePasswordResetEmailEvent(PasswordResetEmailEvent event) {
        log.info("📧 Procesando PasswordResetEmailEvent para email: {}", event.getEmail());
        
        try {
            // Enviar email de recuperación de contraseña
            emailService.sendForgotPasswordEmail(event.getEmail(), event.getResetToken());
            
            log.info("✅ Email de recuperación enviado exitosamente a: {}", event.getEmail());
            
        } catch (Exception e) {
            log.error("❌ Error enviando email de recuperación a: {}. Error: {}", 
                    event.getEmail(), e.getMessage(), e);
            throw e; // Re-throw para que Retry maneje el reintento
        }
    }
}
