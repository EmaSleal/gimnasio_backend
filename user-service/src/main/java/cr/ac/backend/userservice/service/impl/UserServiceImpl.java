package cr.ac.backend.userservice.service.impl;

import cr.ac.backend.userservice.model.User;
import cr.ac.backend.userservice.model.UserAuth;
import cr.ac.backend.userservice.model.UserDto;
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

    @Override
    public User register(User request) {
        log.info("Registering user {}", request);
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
        userRepository.save(userSecurity);
        return userSecurity;
    }

    @Override
    public Optional<UserDto> authenticate(UserAuth request) {
        log.info("Authenticating user {}", request);
        var auth = userRepository.findByUserName(request.userName());
        log.info("Authenticating user {}", auth);
        if (auth.isEmpty()) {
            return Optional.empty();
        }
        log.info("password: {}",passwordEncoder.matches(auth.get().getPassword(), auth.get().getPassword()));
        if (!passwordEncoder.matches(request.password(), auth.get().getPassword())) {
            return Optional.empty();
        }
        var user = userRepository.findByUserName(request.userName())
                .orElseThrow();

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
    public Optional<List<UserDto>> getUsersByTrainer(Long idTrainer) {
        Optional<List<User>> list = userRepository.findByIdTrainer(idTrainer);
        return list.map(users -> users.stream().map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), null)).toList());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserDto> findByUserName(String username) {
        var user = userRepository.findByUserName(username);
        return user.map(value -> new UserDto(value.getId(), value.getUsername(), value.getEmail(), value.getRole(), value.isEnabled(), value.isAccountNonExpired(), value.isCredentialsNonExpired(), value.isAccountNonLocked(), null));
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        var user = userRepository.findByEmail(email);
        return user.map(value -> new UserDto(value.getId(), value.getUsername(), value.getEmail(), value.getRole(), value.isEnabled(), value.isAccountNonExpired(), value.isCredentialsNonExpired(), value.isAccountNonLocked(), null));
    }
}