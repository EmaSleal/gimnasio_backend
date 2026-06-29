package cr.ac.backend.auth.event;

public record PasswordResetRequestedEvent(String email, String token) {}
