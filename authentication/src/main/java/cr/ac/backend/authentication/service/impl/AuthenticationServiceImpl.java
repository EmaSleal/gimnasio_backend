package cr.ac.backend.authentication.service.impl;

import cr.ac.backend.authentication.audit.Auditable;
import cr.ac.backend.authentication.config.JwtService;
import cr.ac.backend.authentication.model.AuthenticationResponse;
import cr.ac.backend.authentication.model.User;
import cr.ac.backend.authentication.model.UserAuth;
import cr.ac.backend.authentication.model.UserCredentialsDto;
import cr.ac.backend.authentication.model.UserDto;

import cr.ac.backend.authentication.publisher.EmailEventPublisher;
import cr.ac.backend.authentication.service.AuthenticationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;
    private final EmailEventPublisher emailEventPublisher;



    /**
     * @deprecated Este método está obsoleto. El registro debe hacerse directamente en user-service.
     * Se mantiene temporalmente para backward compatibility.
     * 
     * Flujo actual (DEPRECATED):
     * Cliente → Authentication → User Service (proxy) → DB
     * 
     * Flujo nuevo (RECOMENDADO):
     * Cliente → User Service → DB → Evento UserCreated → Authentication (async)
     */
    @Override
    @Deprecated
    @Auditable(action = "REGISTER", resource = "User", details = "New user registration (deprecated endpoint)")
    public UserDto register(User request) {
        log.warn("⚠️  DEPRECATED: AuthenticationService.register() está obsoleto");
        log.info("Registering user {} (via deprecated proxy)", request.getUsername());
        
        var userSecurity = User.builder()
                .userName(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole())
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .build();
        
        // Proxy a user-service (temporal, para backward compatibility)
        var UserDto = restTemplate.postForObject("http://user-service/user/register", userSecurity, UserDto.class);

        // Publicar evento de email de bienvenida de forma asíncrona
        emailEventPublisher.publishWelcomeEmail(
            UserDto.email(), 
            UserDto.userName(), 
            UserDto.id()
        );
        log.info("✅ WelcomeEmailEvent publicado para user: {}", UserDto.userName());

        // NOTA: NO generamos JWT aquí. El cliente debe hacer login después del registro.
        // El JWT se genera en authenticate()
        return UserDto;
    }

    /**
     * Autenticar usuario con flujo optimizado y desacoplado.
     * 
     * Flujo NUEVO (optimizado):
     * 1. GET /user/credentials/{email} → Obtener credenciales (id, email, hash, role, enabled)
     * 2. Validar password localmente con PasswordEncoder
     * 3. Generar JWT (access + refresh tokens)
     * 4. Devolver UserDto con tokens
     * 
     * Flujo ANTERIOR (deprecated):
     * 1. POST /user/authenticate (enviar email + password)
     * 2. User Service valida password
     * 3. Devuelve UserDto completo
     * 4. Generar JWT
     * 
     * Beneficios del flujo nuevo:
     * - Menos datos transferidos (solo credenciales necesarias)
     * - Validación de password en Authentication (más seguro)
     * - Separación de responsabilidades
     * - Más fácil de cachear si es necesario
     * 
     * @param request UserAuth con email y password
     * @return Optional<UserDto> con tokens JWT si autenticación exitosa
     */
    @Override
    @Auditable(action = "LOGIN", resource = "Authentication", details = "User authentication")
    public Optional<UserDto> authenticate(UserAuth request) {
        log.info("🔐 Iniciando login optimizado - Email: {}", request.email());
        
        try {
            // Paso 1: Obtener credenciales desde user-service (solo datos necesarios)
            log.debug("📡 Obteniendo credenciales desde user-service");
            UserCredentialsDto credentials = restTemplate.getForObject(
                "http://user-service/user/credentials/" + request.email(), 
                UserCredentialsDto.class
            );
            
            if (credentials == null) {
                log.warn("⚠️ Usuario no encontrado: {}", request.email());
                return Optional.empty();
            }
            
            log.info("✅ Credenciales obtenidas - User ID: {}, Role: {}, Enabled: {}", 
                     credentials.id(), credentials.role(), credentials.enabled());
            
            // Paso 2: Validar password localmente
            log.debug("🔑 Validando password");
            if (!passwordEncoder.matches(request.password(), credentials.passwordHash())) {
                log.warn("❌ Password incorrecto para usuario: {}", request.email());
                return Optional.empty();
            }
            
            log.info("✅ Password validado correctamente");
            
            // Paso 3: Verificar cuenta habilitada
            if (!credentials.enabled()) {
                log.warn("⚠️ Cuenta deshabilitada: {}", request.email());
                return Optional.empty();
            }
            
            // Paso 4: Generar tokens JWT
            log.debug("🎫 Generando tokens JWT");
            var access = jwtService.generateToken(
                credentials.id().toString(), 
                credentials.role(), 
                "AUTHORIZATION"
            );
            var refresh = jwtService.generateToken(
                credentials.id().toString(), 
                credentials.role(), 
                "REFRESH"
            );
            
            var token = AuthenticationResponse.builder()
                    .token(access)
                    .refreshToken(refresh)
                    .build();
            
            long timeSession = LocalDateTime.now().plusHours(5).getNano();
            
            log.info("✅ Login exitoso - User ID: {}, Role: {}", credentials.id(), credentials.role());
            
            // Paso 5: Construir UserDto con tokens
            // Convertir role de String a Enum
            User.Rol roleEnum = User.Rol.valueOf(credentials.role());
            
            return Optional.of(new UserDto(
                credentials.id(), 
                request.email(),  // userName (usamos email)
                credentials.email(), 
                roleEnum,  // role como Enum User.Rol
                credentials.enabled(), 
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                token, 
                timeSession
            ));
            
        } catch (Exception e) {
            log.error("❌ Error durante autenticación: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Auditable(action = "FORGOT_PASSWORD", resource = "Authentication", details = "Password reset request")
    public Optional<AuthenticationResponse> TokenforgotPassword(String email) {
        //var user = authenticationRepository.findByEmail(email).orElse(null);

        try {
            var UserDto = restTemplate.getForObject("http://user-service/user/findByEmail/"+email, UserDto.class);
            assert UserDto != null;
            var token = jwtService.generateTokenFP(UserDto.id().toString(), "AUTHORIZATION");
            log.info("token: {}", token);
            return Optional.of(AuthenticationResponse.builder()
                    .token(token)
                    .build());

        } catch (Exception e) {
            return Optional.empty();
        }


    }

    @Override
    public Optional<AuthenticationResponse> resetPassword(UserAuth request) {
        //var user = authenticationRepository.findByEmail(request.email()).orElse(null);
        var UserDto = restTemplate.getForObject("http://user-service/user/findByEmail/"+request.email(), UserDto.class);

        if (UserDto != null) {

            var user = UserAuth.builder()
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .build();

            restTemplate.postForObject("http://user-service/user/resetPassword", user, UserDto.class);

            return Optional.of(AuthenticationResponse.builder()
                    .token(jwtService.generateToken(UserDto.id().toString(), UserDto.role().toString(), "AUTHORIZATION"))
                    .build());
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<UserDto>> getUsers() {
        //var list = authenticationRepository.findAll();
        List list = restTemplate.getForObject("http://user-service/user/all", List.class);
        assert list != null;
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        var user = restTemplate.getForObject("http://user-service/user/id/"+id, UserDto.class);
        assert user != null;
        return Optional.of(user);
    }

    @Override
    public Optional<UserDto> findByUserName(String username) {
        var user = restTemplate.getForObject("http://user-service/user/findByUserName/"+username, UserDto.class);
        assert user != null;
        return Optional.of(user);
    }
}