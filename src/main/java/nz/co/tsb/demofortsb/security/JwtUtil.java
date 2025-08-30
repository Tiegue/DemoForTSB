package nz.co.tsb.demofortsb.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * JWT Utility class for generating and validating JWT tokens.
 *<br>With force to invalidate tokens by adding them to a blacklist.</br>
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${JWT_SECRET:DemoForTsBSecretKeyMustBeAtLeast256BitsLongForHS256Algorithm2024}") // default
    private String SECRET;

    @Value("${JWT_TTL_MINUTES:60}") // default 60 minutes
    private long TOKEN_TTL_MINUTES;

    private static final long MINUTES_TO_MILLIS = 60 * 1000; // Conversion factor

    // In-memory blacklist (replace with Redis/database in production)
    private final Set<String> tokenBlacklist = new HashSet<>();

    @PostConstruct
    public void init() {
        if (SECRET == null) {
            throw new IllegalStateException("JWT_SECRET is not configured. Please set the 'jwt.secret' property or 'JWT_SECRET' environment variable.");
        }
        if (TOKEN_TTL_MINUTES <= 0 || TOKEN_TTL_MINUTES > 10080) { // Max 7 days
            throw new IllegalStateException("JWT_TTL_MINUTES must be between 1 and 10080 minutes.");
        }
    }

    private Key getSigningKey() {
        if (SECRET.getBytes().length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits long");
        }
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String email, String role) {
        String jti = UUID.randomUUID().toString(); // Unique token ID
        long expirationMillis = TOKEN_TTL_MINUTES * MINUTES_TO_MILLIS;
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setId(jti) // Add jti claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public void invalidateToken(String token) {
        try {
            String jti = getClaims(token).getId(); // Extract jti
            tokenBlacklist.add(jti); // Add to blacklist
        } catch (Exception e) {
            // Log error if token is invalid, but no need to throw
            logger.error("Failed to invalidate token: " + e.getMessage());
        }
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !tokenBlacklist.contains(claims.getId()); // Token is valid if not in blacklist
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}