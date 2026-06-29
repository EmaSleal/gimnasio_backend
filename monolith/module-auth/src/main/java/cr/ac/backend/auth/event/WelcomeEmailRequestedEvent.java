package cr.ac.backend.auth.event;

public record WelcomeEmailRequestedEvent(String email, String username) {}
