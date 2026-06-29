package cr.ac.backend.user.dto;

import cr.ac.backend.shared.security.UserRole;

public record UpdateUserRequest(
        String username,
        String email,
        UserRole role,
        Boolean enabled
) {}
