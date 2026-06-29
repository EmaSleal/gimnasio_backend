package cr.ac.backend.user.event;

import cr.ac.backend.shared.security.UserRole;

public record UserCreatedEvent(Long userId, String email, UserRole role) {}
