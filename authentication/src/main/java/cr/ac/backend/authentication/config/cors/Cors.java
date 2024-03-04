package cr.ac.backend.authentication.config.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class Cors {


    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*"); // Permitir cualquier origen, debes ajustarlo según tus necesidades
        config.addAllowedHeader("*"); // Permitir cualquier encabezado, debes ajustarlo según tus necesidades
        config.addAllowedMethod("*"); // Permitir cualquier método HTTP, debes ajustarlo según tus necesidades
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}