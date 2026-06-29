package cr.ac.backend.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final JwtProperties properties;

    public JwtProvider(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, UserRole role, TokenType type) {
        var now = new Date();
        var expiration = new Date(now.getTime() + resolveDuration(type).toMillis());

        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role.name())
                .claim("tokenType", type.name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(validateToken(token).getSubject());
    }

    public UserRole extractRole(String token) {
        return UserRole.valueOf(validateToken(token).get("role", String.class));
    }

    public TokenType extractTokenType(String token) {
        return TokenType.valueOf(validateToken(token).get("tokenType", String.class));
    }

    public boolean isExpired(String token) {
        try {
            validateToken(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Duration resolveDuration(TokenType type) {
        return switch (type) {
            case ACCESS -> properties.getAccessTokenExpiration();
            case REFRESH -> properties.getRefreshTokenExpiration();
            case PASSWORD_RESET -> properties.getPasswordResetTokenExpiration();
        };
    }
}
