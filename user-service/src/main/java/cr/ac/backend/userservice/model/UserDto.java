package cr.ac.backend.userservice.model;

import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Builder
public record UserDto(Long id, String userName, String email, User.Rol role, boolean enabled, boolean accountNonExpired,
                      boolean credentialsNonExpired, boolean accountNonLocked,
                      AuthenticationResponse authenticationResponse) implements Serializable {
}