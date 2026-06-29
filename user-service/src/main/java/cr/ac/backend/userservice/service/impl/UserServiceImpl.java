package cr.ac.backend.userservice.service.impl;

import cr.ac.backend.userservice.model.User;
import cr.ac.backend.userservice.model.UserAuth;
import cr.ac.backend.userservice.model.UserCredentialsDto;
import cr.ac.backend.userservice.model.UserDto;
import cr.ac.backend.userservice.publisher.UserEventPublisher;
import cr.ac.backend.userservice.repo.UserRepository;
import cr.ac.backend.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher userEventPublisher;

    @Override
    public User register(User request) {
        log.info("📝 Registrando usuario: {}", request.getUsername());
        //get the current date of the system
        var currentDate = java.time.LocalDateTime.now();
        var userSecurity = User.builder()
                .userName(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole())
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .createdBy(request.getCreatedBy())
                .createdAt(currentDate)
                .updatedAt(currentDate)
                .build();
        
        // Persistir usuario en base de datos
        User savedUser = userRepository.save(userSecurity);
        log.info("✅ Usuario persistido con ID: {}", savedUser.getId());
        
        // Publicar evento UserCreated de forma asíncrona
        userEventPublisher.publishUserCreated(savedUser);
        
        return savedUser;
    }

    @Override
    public Optional<UserDto> authenticate(UserAuth request) {
        //log.info("Authenticating user {}", request);
        Optional<User> auth;
        if(request.userName() != null){
            auth = userRepository.findByUserName(request.userName());
        }else if (request.email() != null){
            auth = userRepository.findByEmail(request.email());
        }else{
            return Optional.empty();
        }
        //log.info("Authenticating user {}", auth);
        if (auth.isEmpty()) {
            return Optional.empty();
        }
        //log.info("password: {}",passwordEncoder.matches(auth.get().getPassword(), auth.get().getPassword()));
        if (!passwordEncoder.matches(request.password(), auth.get().getPassword())) {
            return Optional.empty();
        }

        User user;
        if(request.userName() != null){
            user = userRepository.findByUserName(request.userName()).get();
        }else {
            user = userRepository.findByEmail(request.email()).get();
        }

        return Optional.of(new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), null));
    }

    @Override
    public Optional<List<UserDto>> getUsers() {
        var list = userRepository.findAll();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.stream().map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), null)).toList());
    }

    @Override
    public Optional<List<UserDto>> getUsersByCreatedBy(Long idTrainer) {
        Optional<List<User>> list = userRepository.findByCreatedBy(idTrainer);
        return list.map(users -> users.stream().map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), null)).toList());
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        var user = userRepository.findById(id);
        return user.map(value -> new UserDto(value.getId(), value.getUsername(), value.getEmail(), value.getRole(), value.isEnabled(), value.isAccountNonExpired(), value.isCredentialsNonExpired(), value.isAccountNonLocked(), null));
    }

    @Override
    public Optional<UserDto> findByUserName(String username) {
        var user = userRepository.findByUserName(username);
        return user.map(value -> new UserDto(value.getId(), value.getUsername(), value.getEmail(), value.getRole(), value.isEnabled(), value.isAccountNonExpired(), value.isCredentialsNonExpired(), value.isAccountNonLocked(), null));
    }

    /**
     * Obtiene credenciales optimizadas para login (flujo desacoplado).
     * 
     * Devuelve solo datos necesarios para autenticación:
     * - id, email, passwordHash, role, enabled
     * 
     * IMPORTANTE:
     * - passwordHash es bcrypt hash, no password plano
     * - Solo para uso interno entre servicios
     * - Logging NO debe incluir passwordHash
     * 
     * @param email Email del usuario
     * @return UserCredentialsDto con credenciales para login
     */
    @Override
    public Optional<UserCredentialsDto> getCredentialsByEmail(String email) {
        log.info("🔍 Obteniendo credenciales para login - Email: {}", email);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            log.warn("⚠️ Usuario no encontrado: {}", email);
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        UserCredentialsDto credentials = UserCredentialsDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(user.getPassword())  // Hash bcrypt
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
        
        log.info("✅ Credenciales obtenidas - User ID: {}, Role: {}, Enabled: {}", 
                 user.getId(), user.getRole(), user.isEnabled());
        // NOTA: NO loguear passwordHash (toString está sobrescrito para ocultarlo)
        
        return Optional.of(credentials);
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        var user = userRepository.findByEmail(email);
        return user.map(value -> new UserDto(value.getId(), value.getUsername(), value.getEmail(), value.getRole(), value.isEnabled(), value.isAccountNonExpired(), value.isCredentialsNonExpired(), value.isAccountNonLocked(), null));
    }

    @Override
    public Optional<UserDto> deleteUser(Long id) {
        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        userRepository.deleteById(id);
        return user.map(value -> new UserDto(value.getId(), value.getUsername(), value.getEmail(), value.getRole(), value.isEnabled(), value.isAccountNonExpired(), value.isCredentialsNonExpired(), value.isAccountNonLocked(), null));
    }

    @Override
    public Optional<UserDto> updateUser(User userDto) {
        var user = userRepository.findById(userDto.getId());
        if (user.isEmpty()) {
            return Optional.empty();
        }
        //get the current date of the system
        var currentDate = java.time.LocalDateTime.now();
        var userSecurity = User.builder()
                .id(userDto.getId())
                .userName(userDto.getUsername())
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .enabled(userDto.isEnabled())
                .accountNonExpired(userDto.isAccountNonExpired())
                .credentialsNonExpired(userDto.isCredentialsNonExpired())
                .accountNonLocked(userDto.isAccountNonLocked())
                .createdBy(userDto.getCreatedBy())
                .createdAt(userDto.getCreatedAt())
                .updatedAt(currentDate)
                .build();
        userRepository.save(userSecurity);
        return Optional.of(new UserDto(userSecurity.getId(), userSecurity.getUsername(), userSecurity.getEmail(), userSecurity.getRole(), userSecurity.isEnabled(), userSecurity.isAccountNonExpired(), userSecurity.isCredentialsNonExpired(), userSecurity.isAccountNonLocked(), null));
    }
}