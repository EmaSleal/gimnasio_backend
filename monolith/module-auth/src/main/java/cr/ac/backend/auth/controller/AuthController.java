package cr.ac.backend.auth.controller;

import cr.ac.backend.auth.config.AuthProperties;
import cr.ac.backend.auth.dto.AuthResponse;
import cr.ac.backend.auth.dto.ForgotPasswordRequest;
import cr.ac.backend.auth.dto.LoginRequest;
import cr.ac.backend.auth.dto.RefreshTokenRequest;
import cr.ac.backend.auth.dto.RegisterRequest;
import cr.ac.backend.auth.dto.ResetPasswordRequest;
import cr.ac.backend.auth.service.AuthService;
import cr.ac.backend.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthProperties authProperties;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.of(authService.login(request)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(authService.register(request), "Registration successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.of(authService.refreshToken(request)));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.of("If your email is registered, you will receive a reset link shortly."));
    }

    @GetMapping("/forgot-password/redirect")
    public RedirectView redirectForgotPassword(@RequestParam String token) {
        return new RedirectView(authProperties.getFrontendUrl() + "/reset-password?token=" + token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}
