package cr.ac.backend.authentication.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {



    @Value("${jwt.expiration}")
    private String expiration;

    private Key key;

    @Autowired
    public JwtService(@Value("${jwt.secret}") String SECRET) {

        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String userId, String rol, String tokenType) {
        Map<String,String> claims = Map.of(
                "userId",userId,
                "rol", rol,
                "tokenType", tokenType
        );
        return buidToken(claims, tokenType);
    }

    public String buidToken(Map<String,String> claims, String tokenType) {
        long expirationTimeLong =
        "ACCESS".equalsIgnoreCase(tokenType) ?
             1000 * 60 * 5 :
              1000 * 60 * 60 * 5;
        final Date now = new Date(System.currentTimeMillis());
        final Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeLong);

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(claims.get("userId"))
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
    }



/*    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        extraClaims.put("rol", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 180))  // 60 minutes
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }*/

    public String generateTokenFP(String userId, String tokenType) {
        log.info("claims: {}", userId);
        Map<String,String> claims = Map.of(
                "userId",userId,
                "tokenType", tokenType,
                "rol", "PASSWORD_RESET"
        );
        log.info("claims: {}", claims);
        var token = buidTokenFP(claims);
        log.info("token: {}", token);
        return token;
    }

    public String buidTokenFP(
            Map<String, String> extraClaims
    ) {
        log.info("extraClaims: {}", extraClaims);

        log.info("extraClaims: {}", extraClaims);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(extraClaims.get("userId"))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // 24 hours
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(key)
                .compact();
    }


}