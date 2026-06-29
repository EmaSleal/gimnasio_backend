package cr.ac.backend.auth.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth")
@Getter
@Setter
public class AuthProperties {

    private Email email = new Email();
    private String frontendUrl = "http://localhost:4200";

    @PostConstruct
    public void validate() {
        if (email.resendApiKey == null || email.resendApiKey.isBlank()) {
            throw new IllegalStateException("app.auth.email.resend-api-key must not be blank");
        }
        if (email.from == null || email.from.isBlank()) {
            throw new IllegalStateException("app.auth.email.from must not be blank");
        }
    }

    @Getter
    @Setter
    public static class Email {
        private String resendApiKey;
        private String from;
    }
}
