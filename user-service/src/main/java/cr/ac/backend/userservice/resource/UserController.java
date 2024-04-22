package cr.ac.backend.userservice.resource;


import cr.ac.backend.userservice.model.User;
import cr.ac.backend.userservice.model.UserAuth;
import cr.ac.backend.userservice.model.UserDto;
import cr.ac.backend.userservice.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("user")
@Slf4j
public class UserController {

    private final UserService service;

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getUsers() {
        Optional<List<UserDto>> users = service.getUsers();
        return users.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/createdBy/{idTrainer}")
    public ResponseEntity<List<UserDto>> getUsersByIdTrainer(@PathVariable Long idTrainer) {
        Optional<List<UserDto>> users = service.getUsersByCreatedBy(idTrainer);
        return users.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<UserDto> register(@RequestBody User request) {
        var userLogin = service.register(request);
        UserDto userDto = UserDto.builder()
                .userName(userLogin.getUsername())
                .email(userLogin.getEmail())
                .role(userLogin.getRole())
                .build();
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        Optional<UserDto> user = service.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserDto> authenticate(@RequestBody UserAuth userAuth) {
        Optional<UserDto> user = service.authenticate(userAuth);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/findByUserName/{username}")
    public ResponseEntity<UserDto> findByUserName(@PathVariable String username) {
        Optional<UserDto> user = service.findByUserName(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //find by email
    @GetMapping("/findByEmail/{email}")
    public ResponseEntity<UserDto> findByEmail(@PathVariable String email) {
        Optional<UserDto> user = service.findByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody User userDto) {
        Optional<UserDto> user = service.updateUser(userDto);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}