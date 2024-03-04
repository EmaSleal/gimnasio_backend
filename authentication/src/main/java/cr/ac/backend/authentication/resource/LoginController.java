package cr.ac.backend.authentication.resource;

import cr.ac.backend.authentication.model.AuthenticationResponse;
import cr.ac.backend.authentication.model.User;
import cr.ac.backend.authentication.model.UserAuth;
import cr.ac.backend.authentication.model.UserDto;
import cr.ac.backend.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Login")
@RequiredArgsConstructor

public class LoginController {

    private final AuthenticationService service;

    @PostMapping
    public ResponseEntity<UserDto> authenticate(
            @RequestBody UserAuth request
    ) {
        var user = service.authenticate(request);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}