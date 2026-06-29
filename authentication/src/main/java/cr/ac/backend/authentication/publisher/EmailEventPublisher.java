package cr.ac.backend.authentication.publisher;

import cr.ac.backend.authentication.event.PasswordResetEmailEvent;
import cr.ac.backend.authentication.event.WelcomeEmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Publisher para eventos de email asíncronos
 * Publica mensajes a RabbitMQ para procesamiento asíncrono
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    
    private static final String EMAIL_EXCHANGE = "email.exchange";
    private static final String WELCOME_ROUTING_KEY = "email.welcome";
    private static final String PASSWORD_RESET_ROUTING_KEY = "email.password-reset";

    /**
     * Publica evento de email de bienvenida
     * @param email Email del destinatario
     * @param userName Nombre de usuario
     * @param userId ID del usuario
     */
    public void publishWelcomeEmail(String email, String userName, Long userId) {
        WelcomeEmailEvent event = WelcomeEmailEvent.builder()
                .email(email)
                .userName(userName)
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .build();
        
        log.info("📤 Publicando WelcomeEmailEvent para user: {} (email: {})", userName, email);
        
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, WELCOME_ROUTING_KEY, event);
        
        log.debug("✅ WelcomeEmailEvent publicado exitosamente");
    }

    /**
     * Publica evento de email de recuperación de contraseña
     * @param email Email del destinatario
     * @param resetToken Token JWT para reset
     * @param expiresAt Timestamp de expiración
     */
    public void publishPasswordResetEmail(String email, String resetToken, Long expiresAt) {
        PasswordResetEmailEvent event = PasswordResetEmailEvent.builder()
                .email(email)
                .resetToken(resetToken)
                .timestamp(System.currentTimeMillis())
                .expiresAt(expiresAt)
                .build();
        
        log.info("📤 Publicando PasswordResetEmailEvent para email: {}", email);
        
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, PASSWORD_RESET_ROUTING_KEY, event);
        
        log.debug("✅ PasswordResetEmailEvent publicado exitosamente");
    }
}
