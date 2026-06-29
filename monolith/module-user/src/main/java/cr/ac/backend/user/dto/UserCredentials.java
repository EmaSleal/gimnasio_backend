package cr.ac.backend.user.dto;

import cr.ac.backend.shared.security.UserRole;

public record UserCredentials(
        Long id,
        String email,
        String password,
        UserRole role,
        boolean enabled,
        boolean accountNonExpired,
        boolean credentialsNonExpired,
        boolean accountNonLocked
) {
    @Override
    public String toString() {
        return "UserCredentials{id=" + id + ", email='" + email + "', password='***', role=" + role + ", enabled=" + enabled + "}";
    }
}
