package cr.ac.backend.user.service;

import cr.ac.backend.user.dto.CreateUserRequest;
import cr.ac.backend.user.dto.UpdateUserRequest;
import cr.ac.backend.user.dto.UserCredentials;
import cr.ac.backend.user.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse findById(Long id);
    UserResponse findByUsername(String username);
    UserResponse findByEmail(String email);
    List<UserResponse> findAll();
    List<UserResponse> findByCreatedBy(Long trainerId);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    UserCredentials loadCredentialsByEmail(String email);
    void changePassword(Long userId, String rawPassword);
}
