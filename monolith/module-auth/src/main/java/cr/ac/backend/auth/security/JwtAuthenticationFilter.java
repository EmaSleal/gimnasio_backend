package cr.ac.backend.auth.security;

import cr.ac.backend.shared.security.JwtProvider;
import cr.ac.backend.shared.security.TokenType;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = authHeader.substring(7);
        try {
            jwtProvider.validateToken(token);

            var tokenType = jwtProvider.extractTokenType(token);
            if (tokenType != TokenType.ACCESS) {
                writeUnauthorized(response, "Invalid token type");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var userId = jwtProvider.extractUserId(token);
                var role = jwtProvider.extractRole(token);
                var auth = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority(role.name()))
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);

        } catch (JwtException ex) {
            log.debug("JWT validation failed: {}", ex.getMessage());
            writeUnauthorized(response, "Invalid or expired token");
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/problem+json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {"type":"/errors/unauthorized","title":"Unauthorized","status":401,"detail":"%s"}
                """.formatted(message));
    }
}
