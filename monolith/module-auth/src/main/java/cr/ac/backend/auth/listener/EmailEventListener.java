package cr.ac.backend.auth.listener;

import cr.ac.backend.auth.event.PasswordResetRequestedEvent;
import cr.ac.backend.auth.event.WelcomeEmailRequestedEvent;
import cr.ac.backend.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService emailService;

    @EventListener
    @Async
    public void onWelcomeEmail(WelcomeEmailRequestedEvent event) {
        emailService.sendWelcomeEmail(event.email(), event.username());
    }

    @EventListener
    @Async
    public void onPasswordReset(PasswordResetRequestedEvent event) {
        emailService.sendPasswordResetEmail(event.email(), event.token());
    }
}
