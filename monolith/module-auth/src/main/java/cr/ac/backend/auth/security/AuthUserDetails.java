package cr.ac.backend.auth.security;

import cr.ac.backend.user.dto.UserCredentials;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthUserDetails implements UserDetails {

    private final UserCredentials credentials;

    public AuthUserDetails(UserCredentials credentials) {
        this.credentials = credentials;
    }

    public UserCredentials getCredentials() {
        return credentials;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(credentials.role().name()));
    }

    @Override
    public String getPassword() {
        return credentials.password();
    }

    @Override
    public String getUsername() {
        return credentials.email();
    }

    @Override
    public boolean isAccountNonExpired() {
        return credentials.accountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return credentials.accountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentials.credentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return credentials.enabled();
    }
}
