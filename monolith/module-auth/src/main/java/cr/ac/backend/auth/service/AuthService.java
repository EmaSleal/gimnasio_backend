package cr.ac.backend.auth.service;

import cr.ac.backend.auth.dto.AuthResponse;
import cr.ac.backend.auth.dto.ForgotPasswordRequest;
import cr.ac.backend.auth.dto.LoginRequest;
import cr.ac.backend.auth.dto.RefreshTokenRequest;
import cr.ac.backend.auth.dto.RegisterRequest;
import cr.ac.backend.auth.dto.ResetPasswordRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
