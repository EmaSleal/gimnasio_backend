package cr.ac.backend.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableConfigurationProperties(AuthProperties.class)
@EnableAsync
public class AuthModuleConfiguration {
}
