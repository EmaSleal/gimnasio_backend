package cr.ac.backend.adminservice.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * Security configuration for Spring Boot Admin Server
 * 
 * Configures basic authentication for accessing the admin dashboard while
 * allowing unauthenticated access to actuator endpoints (required for monitoring).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AdminServerProperties adminServerProperties;

    public SecurityConfig(AdminServerProperties adminServerProperties) {
        this.adminServerProperties = adminServerProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = 
            new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminServerProperties.path("/"));

        http
            .authorizeHttpRequests(auth -> auth
                // Allow public access to assets and actuator endpoints
                .requestMatchers(
                    adminServerProperties.path("/assets/**"),
                    adminServerProperties.path("/actuator/**"),
                    "/actuator/**"
                ).permitAll()
                // Require authentication for everything else
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage(adminServerProperties.path("/login"))
                .successHandler(successHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl(adminServerProperties.path("/logout"))
                .permitAll()
            )
            .httpBasic(basic -> {})
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    adminServerProperties.path("/instances"),
                    adminServerProperties.path("/actuator/**"),
                    "/actuator/**"
                )
            );

        return http.build();
    }
}
