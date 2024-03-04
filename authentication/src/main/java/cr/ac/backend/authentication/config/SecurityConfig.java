package cr.ac.backend.authentication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration

@RequiredArgsConstructor
public class SecurityConfig {
    /*private final FiltroAutorizacionJWT jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final FiltroAutenticacionJWT jwtAuthenFilter;*/

   /* @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests( (authorizeRequests) -> authorizeRequests
                        .requestMatchers("/Login/**", "/forgot-password/**","/actuator/**").permitAll()
                ).authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                        .requestMatchers("user/**").hasAnyAuthority("ADMIN", "TRAINER")
                )
                .exceptionHandling( (exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenFilter)
                ).sessionManagement( (sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider)
                .cors((cors) -> {

                })
                .csrf(AbstractHttpConfigurer::disable);


        return http.build();
    }*/

}