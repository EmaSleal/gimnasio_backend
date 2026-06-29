package cr.ac.backend.user.dto;

import cr.ac.backend.shared.security.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        UserRole role,
        boolean enabled,
        LocalDateTime createdAt
) {}
