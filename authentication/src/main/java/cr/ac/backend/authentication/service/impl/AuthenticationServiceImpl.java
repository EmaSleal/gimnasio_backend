package cr.ac.backend.authentication.service.impl;

import cr.ac.backend.authentication.config.JwtService;
import cr.ac.backend.authentication.model.AuthenticationResponse;
import cr.ac.backend.authentication.model.User;
import cr.ac.backend.authentication.model.UserAuth;
import cr.ac.backend.authentication.model.UserDto;

import cr.ac.backend.authentication.service.AuthenticationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;



    @Override
    public UserDto register(User request) {
        log.info("Registering user {}", request);
        var userSecurity = User.builder()
                .userName(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole())
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .build();
        var UserDto = restTemplate.postForObject("http://user-service/user/register", userSecurity, UserDto.class);

        var jwtToken = jwtService.generateToken(userSecurity.getId().toString(),userSecurity.getRole().toString(),"AUTHORIZATION");
        var jwtRefreshToken = jwtService.generateToken(userSecurity.getId().toString(),userSecurity.getRole().toString(),"REFRESH");
        var token = AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(jwtRefreshToken)
                .build();
        return new UserDto(UserDto.id(), UserDto.userName(), UserDto.email(), UserDto.role(), UserDto.enabled(), UserDto.accountNonExpired(), UserDto.credentialsNonExpired(), UserDto.accountNonLocked(), token);
    }

    @Override
    public Optional<UserDto> authenticate(UserAuth request) {
        UserDto UserDto;
        try {
             UserDto = restTemplate.postForObject("http://user-service/user/authenticate", request, UserDto.class);
            var access = jwtService.generateToken(UserDto.id().toString(), UserDto.role().toString(), "AUTHORIZATION");
            var refresh = jwtService.generateToken(UserDto.id().toString(), UserDto.role().toString(), "REFRESH");
            var token = AuthenticationResponse.builder()
                    .token(access)
                    .refreshToken(refresh)
                    .build();
            return Optional.of(new UserDto(UserDto.id(), UserDto.userName(), UserDto.email(), UserDto.role(), UserDto.enabled(), UserDto.accountNonExpired(), UserDto.credentialsNonExpired(), UserDto.accountNonLocked(), token));
        } catch (Exception e) {
            return Optional.empty();
        }


    }

    @Override
    public Optional<AuthenticationResponse> TokenforgotPassword(String email) {
        //var user = authenticationRepository.findByEmail(email).orElse(null);

        try {
            var UserDto = restTemplate.getForObject("http://user-service/user/findByEmail/"+email, UserDto.class);
            assert UserDto != null;
            var token = jwtService.generateTokenFP(UserDto.id().toString(), "AUTHORIZATION");
            log.info("token: {}", token);
            return Optional.of(AuthenticationResponse.builder()
                    .token(token)
                    .build());

        } catch (Exception e) {
            return Optional.empty();
        }


    }

    @Override
    public Optional<AuthenticationResponse> resetPassword(UserAuth request) {
        //var user = authenticationRepository.findByEmail(request.email()).orElse(null);
        var UserDto = restTemplate.getForObject("http://user-service/user/findByEmail/"+request.email(), UserDto.class);

        if (UserDto != null) {

            var user = UserAuth.builder()
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .build();

            restTemplate.postForObject("http://user-service/user/resetPassword", user, UserDto.class);

            return Optional.of(AuthenticationResponse.builder()
                    .token(jwtService.generateToken(UserDto.id().toString(), UserDto.role().toString(), "AUTHORIZATION"))
                    .build());
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<UserDto>> getUsers() {
        //var list = authenticationRepository.findAll();
        List list = restTemplate.getForObject("http://user-service/user/all", List.class);
        assert list != null;
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        var user = restTemplate.getForObject("http://user-service/user/id/"+id, UserDto.class);
        assert user != null;
        return Optional.of(user);
    }

    @Override
    public Optional<UserDto> findByUserName(String username) {
        var user = restTemplate.getForObject("http://user-service/user/findByUserName/"+username, UserDto.class);
        assert user != null;
        return Optional.of(user);
    }
}