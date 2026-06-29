package cr.ac.backend.user.controller;

import cr.ac.backend.shared.dto.ApiResponse;
import cr.ac.backend.user.dto.CreateUserRequest;
import cr.ac.backend.user.dto.UpdateUserRequest;
import cr.ac.backend.user.dto.UserResponse;
import cr.ac.backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.of(service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(service.findById(id)));
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.of(service.findByUsername(username)));
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.of(service.findByEmail(email)));
    }

    @GetMapping("/trainer/{trainerId}/clients")
    public ResponseEntity<ApiResponse<List<UserResponse>>> findByTrainer(@PathVariable Long trainerId) {
        return ResponseEntity.ok(ApiResponse.of(service.findByCreatedBy(trainerId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(service.createUser(request), "User created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.of(service.updateUser(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
