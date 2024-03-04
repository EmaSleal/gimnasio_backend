package cr.ac.backend.authentication.service;

import cr.ac.backend.authentication.model.AuthenticationResponse;
import cr.ac.backend.authentication.model.User;
import cr.ac.backend.authentication.model.UserAuth;
import cr.ac.backend.authentication.model.UserDto;

import java.util.List;
import java.util.Optional;

public interface AuthenticationService {
    UserDto register(User request);

    Optional<UserDto> authenticate(UserAuth request);

    Optional<AuthenticationResponse> TokenforgotPassword(String email);

    Optional<AuthenticationResponse> resetPassword(UserAuth request);

    Optional<List<UserDto>> getUsers();

    Optional<UserDto> getUserById(Long id);

    Optional<UserDto> findByUserName(String username);
}