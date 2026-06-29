package cr.ac.backend.auth.service;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import cr.ac.backend.auth.config.AuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final AuthProperties properties;

    @Async
    public void sendWelcomeEmail(String to, String username) {
        try {
            var resend = new Resend(properties.getEmail().getResendApiKey());
            var html = buildWelcomeHtml(username);
            var req = SendEmailRequest.builder()
                    .from(properties.getEmail().getFrom())
                    .to(to)
                    .subject("Welcome to the Gym!")
                    .html(html)
                    .build();
            resend.emails().send(req);
        } catch (Exception ex) {
            log.error("Failed to send welcome email to {}: {}", to, ex.getMessage());
        }
    }

    @Async
    public void sendPasswordResetEmail(String to, String token) {
        try {
            var resend = new Resend(properties.getEmail().getResendApiKey());
            var resetLink = properties.getFrontendUrl() + "/reset-password?token=" + token;
            var html = buildPasswordResetHtml(resetLink);
            var req = SendEmailRequest.builder()
                    .from(properties.getEmail().getFrom())
                    .to(to)
                    .subject("Password Reset Request")
                    .html(html)
                    .build();
            resend.emails().send(req);
        } catch (Exception ex) {
            log.error("Failed to send password reset email to {}: {}", to, ex.getMessage());
        }
    }

    private String buildWelcomeHtml(String username) {
        return "<h1>Welcome, " + username + "!</h1><p>Your gym account is ready.</p>";
    }

    private String buildPasswordResetHtml(String resetLink) {
        return "<p>Click <a href=\"" + resetLink + "\">here</a> to reset your password. Link expires in 24 hours.</p>";
    }
}
