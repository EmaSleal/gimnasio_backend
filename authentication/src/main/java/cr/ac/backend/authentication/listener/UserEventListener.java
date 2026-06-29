package cr.ac.backend.authentication.listener;

import cr.ac.backend.authentication.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener para eventos de usuario desde user-service
 * Escucha eventos UserCreated para sincronización y procesamiento
 * 
 * IMPORTANTE: Este listener es parte del flujo desacoplado de registro.
 * Flujo: Cliente → User Service → DB → Evento UserCreated → Este Listener (async)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    /**
     * Escucha eventos de usuario creado
     * Queue: user.created.auth.queue
     * 
     * Casos de uso:
     * - Auditoría y logging centralizado
     * - Sincronizar caché de usuarios (futuro)
     * - Preparar datos de sesión (futuro)
     * - Notificaciones internas (futuro)
     * 
     * NOTA: NO generamos JWT aquí. El JWT se genera cuando el usuario hace login.
     * Este listener es solo para procesamiento asíncrono post-registro.
     * 
     * @param event Evento de usuario creado
     */
    @RabbitListener(queues = "user.created.auth.queue")
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("👤 Recibido UserCreatedEvent para user: {} (id: {}, email: {})", 
                event.getUserName(), event.getUserId(), event.getEmail());
        
        try {
            // Logging de auditoría
            log.info("📊 Usuario registrado vía flujo desacoplado - Role: {}, Enabled: {}, CreatedBy: {}", 
                    event.getRole(), 
                    event.isEnabled(), 
                    event.getCreatedBy() != null ? event.getCreatedBy() : "self-registration");
            
            // TODO: Implementar lógica adicional si es necesario
            // Ejemplos futuros:
            // 1. Actualizar caché local de usuarios
            // 2. Pre-cargar datos de sesión
            // 3. Notificaciones internas al equipo
            // 4. Sincronizar con sistemas externos
            
            log.info("✅ UserCreatedEvent procesado exitosamente para userId: {} - Flujo desacoplado activo", 
                    event.getUserId());
            
        } catch (Exception e) {
            log.error("❌ Error procesando UserCreatedEvent para userId: {}. Error: {}", 
                    event.getUserId(), e.getMessage(), e);
            throw e; // Re-throw para que vaya a DLQ si falla
        }
    }
}