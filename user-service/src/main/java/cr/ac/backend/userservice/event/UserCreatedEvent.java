package cr.ac.backend.userservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Evento que representa la creación de un nuevo usuario
 * Se publica cuando un usuario se registra exitosamente en el sistema
 * Permite a otros servicios reaccionar a la creación del usuario de forma asíncrona
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID del usuario creado
     */
    private Long userId;
    
    /**
     * Nombre de usuario
     */
    private String userName;
    
    /**
     * Email del usuario
     */
    private String email;
    
    /**
     * Rol del usuario en el sistema (ADMIN, USER, TRAINER)
     */
    private String role;
    
    /**
     * Timestamp de cuando se creó el usuario
     */
    private Long timestamp;
    
    /**
     * ID del usuario que creó este usuario (para trainers creando clientes)
     * Puede ser null si es auto-registro
     */
    private Long createdBy;
    
    /**
     * Flag que indica si el usuario está habilitado
     */
    private boolean enabled;
}
