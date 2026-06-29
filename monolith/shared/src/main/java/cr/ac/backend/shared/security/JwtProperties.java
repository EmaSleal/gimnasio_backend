package cr.ac.backend.shared.security;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret;
    private Duration accessTokenExpiration = Duration.ofMinutes(15);
    private Duration refreshTokenExpiration = Duration.ofDays(7);
    private Duration passwordResetTokenExpiration = Duration.ofHours(24);

    @PostConstruct
    public void validate() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("app.jwt.secret must not be blank");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 characters (256 bits)");
        }
    }
}
