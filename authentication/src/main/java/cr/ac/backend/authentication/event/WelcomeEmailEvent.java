package cr.ac.backend.authentication.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Evento que representa la solicitud de envío de email de bienvenida
 * Se publica cuando un usuario se registra exitosamente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WelcomeEmailEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Email del destinatario
     */
    private String email;
    
    /**
     * Nombre de usuario registrado
     */
    private String userName;
    
    /**
     * ID del usuario para tracking
     */
    private Long userId;
    
    /**
     * Timestamp de cuando se creó el evento
     */
    private Long timestamp;
}
