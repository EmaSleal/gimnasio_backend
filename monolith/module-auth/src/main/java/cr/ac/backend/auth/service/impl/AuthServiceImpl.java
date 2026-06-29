package cr.ac.backend.auth.service.impl;

import cr.ac.backend.auth.dto.AuthResponse;
import cr.ac.backend.auth.dto.ForgotPasswordRequest;
import cr.ac.backend.auth.dto.LoginRequest;
import cr.ac.backend.auth.dto.RefreshTokenRequest;
import cr.ac.backend.auth.dto.RegisterRequest;
import cr.ac.backend.auth.dto.ResetPasswordRequest;
import cr.ac.backend.auth.event.PasswordResetRequestedEvent;
import cr.ac.backend.auth.event.WelcomeEmailRequestedEvent;
import cr.ac.backend.auth.service.AuthService;
import cr.ac.backend.shared.exception.ResourceNotFoundException;
import cr.ac.backend.shared.exception.UnauthorizedException;
import cr.ac.backend.shared.security.JwtProvider;
import cr.ac.backend.shared.security.TokenType;
import cr.ac.backend.shared.security.UserRole;
import cr.ac.backend.user.dto.CreateUserRequest;
import cr.ac.backend.user.dto.UserCredentials;
import cr.ac.backend.user.dto.UserResponse;
import cr.ac.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public AuthResponse login(LoginRequest request) {
        UserCredentials credentials;
        try {
            credentials = userService.loadCredentialsByEmail(request.email());
        } catch (ResourceNotFoundException ex) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!passwordEncoder.matches(request.password(), credentials.password())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        if (!credentials.enabled()) {
            throw new UnauthorizedException("Account is disabled");
        }
        if (!credentials.accountNonLocked()) {
            throw new UnauthorizedException("Account is locked");
        }

        var accessToken = jwtProvider.generateToken(credentials.id(), credentials.role(), TokenType.ACCESS);
        var refreshToken = jwtProvider.generateToken(credentials.id(), credentials.role(), TokenType.REFRESH);

        return new AuthResponse(accessToken, refreshToken, credentials.id(), credentials.role());
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        userService.createUser(new CreateUserRequest(
                request.username(), request.email(), request.password(), UserRole.CLIENT));
        eventPublisher.publishEvent(new WelcomeEmailRequestedEvent(request.email(), request.username()));
        return login(new LoginRequest(request.email(), request.password()));
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        jwtProvider.validateToken(request.refreshToken());
        if (jwtProvider.extractTokenType(request.refreshToken()) != TokenType.REFRESH) {
            throw new UnauthorizedException("Not a refresh token");
        }
        var userId = jwtProvider.extractUserId(request.refreshToken());
        var role = jwtProvider.extractRole(request.refreshToken());
        var newAccessToken = jwtProvider.generateToken(userId, role, TokenType.ACCESS);
        return new AuthResponse(newAccessToken, request.refreshToken(), userId, role);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        UserResponse user;
        try {
            user = userService.findByEmail(request.email());
        } catch (ResourceNotFoundException ex) {
            return;
        }
        var token = jwtProvider.generateToken(user.id(), user.role(), TokenType.PASSWORD_RESET);
        eventPublisher.publishEvent(new PasswordResetRequestedEvent(request.email(), token));
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        jwtProvider.validateToken(request.token());
        if (jwtProvider.extractTokenType(request.token()) != TokenType.PASSWORD_RESET) {
            throw new UnauthorizedException("Invalid token type");
        }
        var userId = jwtProvider.extractUserId(request.token());
        userService.changePassword(userId, request.newPassword());
    }
}
