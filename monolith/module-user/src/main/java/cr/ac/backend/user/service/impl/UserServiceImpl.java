package cr.ac.backend.user.service.impl;

import cr.ac.backend.shared.exception.BusinessRuleViolationException;
import cr.ac.backend.shared.exception.DuplicateResourceException;
import cr.ac.backend.shared.exception.ResourceNotFoundException;
import cr.ac.backend.user.domain.User;
import cr.ac.backend.user.dto.CreateUserRequest;
import cr.ac.backend.user.dto.UpdateUserRequest;
import cr.ac.backend.user.dto.UserCredentials;
import cr.ac.backend.user.dto.UserResponse;
import cr.ac.backend.user.event.UserCreatedEvent;
import cr.ac.backend.user.mapper.UserMapper;
import cr.ac.backend.user.repository.UserRepository;
import cr.ac.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (repository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("username already taken", "username");
        }
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("email already registered", "email");
        }

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .build();

        var saved = repository.save(user);
        eventPublisher.publishEvent(new UserCreatedEvent(saved.getId(), saved.getEmail(), saved.getRole()));
        return mapper.toResponse(saved);
    }

    @Override
    public UserResponse findById(Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapper.toResponse(user);
    }

    @Override
    public UserResponse findByUsername(String username) {
        var user = repository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return mapper.toResponse(user);
    }

    @Override
    public UserResponse findByEmail(String email) {
        var user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapper.toResponse(user);
    }

    @Override
    public List<UserResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public List<UserResponse> findByCreatedBy(Long trainerId) {
        return mapper.toResponseList(repository.findByCreatedBy(trainerId));
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.username() != null
                && !request.username().equals(user.getUsername())
                && repository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("username already taken", "username");
        }

        if (request.email() != null
                && !request.email().equals(user.getEmail())
                && repository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("email already registered", "email");
        }

        mapper.updateEntity(user, request);
        return mapper.toResponse(repository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        var callerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (callerId.equals(user.getId())) {
            throw new BusinessRuleViolationException("cannot delete own account");
        }

        repository.deleteById(id);
    }

    @Override
    public UserCredentials loadCredentialsByEmail(String email) {
        var user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapper.toCredentials(user);
    }

    @Override
    public void changePassword(Long userId, String rawPassword) {
        var user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setPassword(passwordEncoder.encode(rawPassword));
        repository.save(user);
    }
}
