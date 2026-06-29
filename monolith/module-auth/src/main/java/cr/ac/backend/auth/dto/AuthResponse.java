package cr.ac.backend.auth.dto;

import cr.ac.backend.shared.security.UserRole;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        UserRole role
) {}
