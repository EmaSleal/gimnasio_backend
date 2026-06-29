package cr.ac.backend.authentication.resource;

import cr.ac.backend.authentication.model.User;
import cr.ac.backend.authentication.model.UserDto;
import cr.ac.backend.authentication.service.AuthenticationService;
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
public class AuthenticationController {

    private final AuthenticationService service;

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getUsers() {
        Optional<List<UserDto>> users = service.getUsers();
        return users.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * @deprecated Este endpoint está obsoleto. Use POST /user/register directamente en user-service.
     * Este endpoint se mantiene temporalmente para backward compatibility pero será eliminado en futuras versiones.
     * Redirige la solicitud a user-service sin procesamiento adicional.
     * 
     * Flujo recomendado:
     * Cliente → Gateway → User Service (directo)
     * User Service publica evento UserCreated → Authentication lo procesa (async)
     */
    @Deprecated
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody User request) {
        log.warn("⚠️  DEPRECATED: /auth/register está obsoleto. Use /user/register directamente.");
        
        // Redirigir a user-service (backward compatibility temporal)
        var userLogin = service.register(request);
        return ResponseEntity.ok(userLogin);
    }

    @GetMapping("/id")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        Optional<UserDto> user = service.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/findByUserName/{username}")
    public ResponseEntity<UserDto> findByUserName(@PathVariable String username) {
        Optional<UserDto> user = service.findByUserName(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}