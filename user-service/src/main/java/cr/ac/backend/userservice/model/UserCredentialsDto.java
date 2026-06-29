package cr.ac.backend.userservice.model;

import lombok.Builder;

import java.io.Serializable;

/**
 * DTO optimizado para login que contiene solo los datos necesarios para autenticación.
 * 
 * Se utiliza en flujo de login desacoplado:
 * Authentication Service → GET /user/credentials/{email} → User Service
 * 
 * Contiene:
 * - Credenciales: email, passwordHash
 * - Datos de autenticación: role, enabled
 * - Metadata: id
 * 
 * IMPORTANTE:
 * - passwordHash nunca debe exponerse en logs
 * - Solo para uso interno entre servicios (no exponer públicamente)
 * - Minimiza datos transferidos vs UserDto completo
 * 
 * @param id ID del usuario en base de datos
 * @param email Email del usuario (único)
 * @param passwordHash Hash bcrypt del password (NUNCA el password plano)
 * @param role Rol del usuario (ADMIN, TRAINER, CLIENT)
 * @param enabled Si la cuenta está activa
 */
@Builder
public record UserCredentialsDto(
    Long id,
    String email,
    String passwordHash,  // IMPORTANTE: Es el HASH, no password plano
    User.Rol role,
    boolean enabled
) implements Serializable {
    
    @Override
    public String toString() {
        // Sobrescribir toString para NUNCA loguear el passwordHash
        return "UserCredentialsDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", passwordHash='***HIDDEN***'" +  // Ocultar hash
                ", role=" + role +
                ", enabled=" + enabled +
                '}';
    }
}
