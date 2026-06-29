package cr.ac.backend.authentication.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Evento que representa la solicitud de envío de email de recuperación de contraseña
 * Se publica cuando un usuario solicita resetear su contraseña
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetEmailEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Email del destinatario
     */
    private String email;
    
    /**
     * Token JWT para resetear contraseña
     */
    private String resetToken;
    
    /**
     * Timestamp de cuando se creó el evento
     */
    private Long timestamp;
    
    /**
     * Tiempo de expiración del token (en milisegundos)
     */
    private Long expiresAt;
}
