package cr.ac.backend.authentication.resource;

import cr.ac.backend.authentication.model.AuthenticationResponse;
import cr.ac.backend.authentication.model.UserAuth;
import cr.ac.backend.authentication.publisher.EmailEventPublisher;
import cr.ac.backend.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("forgot-password")
public class ForgotPasswordController {

    private final AuthenticationService service;
    private final EmailEventPublisher emailEventPublisher;

    @GetMapping("/email")
    public RedirectView RedirecionarConToken(@RequestParam("token") String token, Model model) {
        // Validar el token y redirigir al usuario a la página de restablecimiento de contraseña

        //envia el token a la direccion localhost:4200/reset-password?token=tokenDecode
        model.addAttribute("token", token);//envia el token a la vista
        return new RedirectView("http://localhost:4200/Login?token=" + token);
    }

    @PostMapping
    public ResponseEntity<String> forgotPassword(@RequestBody UserAuth request) {
        var response = service.TokenforgotPassword(request.email());
        log.info("UserDto: {}", response);
        if (response.isPresent()) {
            // Calcular expiración del token (24 horas)
            long expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
            
            // Publicar evento de forma asíncrona
            emailEventPublisher.publishPasswordResetEmail(
                request.email(), 
                response.get().getToken(),
                expiresAt
            );
            log.info("✅ PasswordResetEmailEvent publicado para email: {} (latencia reducida)", request.email());
            
            return ResponseEntity.ok("Password reset email sent successfully");
        }
        return ResponseEntity.notFound().build();

    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthenticationResponse> resetPassword(@RequestBody UserAuth request) {
        Optional<AuthenticationResponse> response = service.resetPassword(request);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

}
