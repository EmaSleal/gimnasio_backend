package cr.ac.backend.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
    scanBasePackages = "cr.ac.backend",
    exclude = {UserDetailsServiceAutoConfiguration.class}
)
public class GimnasioBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GimnasioBackendApplication.class, args);
    }
}
