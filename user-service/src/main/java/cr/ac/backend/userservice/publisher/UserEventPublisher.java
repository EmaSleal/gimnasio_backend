package cr.ac.backend.userservice.publisher;

import cr.ac.backend.userservice.event.UserCreatedEvent;
import cr.ac.backend.userservice.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Publisher para eventos de usuario
 * Publica eventos relacionados con el ciclo de vida del usuario
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    
    private static final String USER_EXCHANGE = "user.exchange";
    private static final String USER_CREATED_ROUTING_KEY = "user.created";

    /**
     * Publica evento cuando se crea un nuevo usuario
     * @param user Usuario creado
     */
    public void publishUserCreated(User user) {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(user.getId())
                .userName(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().toString() : "USER")
                .timestamp(System.currentTimeMillis())
                .createdBy(user.getCreatedBy())
                .enabled(user.isEnabled())
                .build();
        
        log.info("📤 Publicando UserCreatedEvent para user: {} (id: {}, email: {})", 
                user.getUsername(), user.getId(), user.getEmail());
        
        rabbitTemplate.convertAndSend(USER_EXCHANGE, USER_CREATED_ROUTING_KEY, event);
        
        log.debug("✅ UserCreatedEvent publicado exitosamente para userId: {}", user.getId());
    }
}
