package nz.co.tsb.demofortsb.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * JWT Utility class for generating and validating JWT tokens.
 *<br>With force to invalidate tokens by adding them to a blacklist.</br>
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${JWT_SECRET:$2a$12$Rn0.OZsB.WWH0YzNrsyHAuw6.8T5yhed10EF14Q2JHCV2FI.l1EMm}") // default
    private String SECRET;

    @Value("${JWT_TTL_MINUTES:60}") // default 60 minutes
    private long TOKEN_TTL_MINUTES;

    @Value("${JWT_RESET_TOKEN_TTL_MINUTES:15}") // default 15 minutes
    private long RESET_TOKEN_TTL_MINUTES;

    private static final long MINUTES_TO_MILLIS = 60 * 1000; // Conversion factor

    private final RedisTemplate<String, String> redisTemplate;

    public JwtUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long getTokenTtlMinutes() {
        return TOKEN_TTL_MINUTES;
    }

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
                .setId(jti)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate a password reset token with shorter expiration
     * @param email User's email
     * @return JWT token for password reset
     */
    public String generatePasswordResetToken(String email, String nationalId) {
        String jti = UUID.randomUUID().toString();
        long resetTokenExpiration = RESET_TOKEN_TTL_MINUTES * MINUTES_TO_MILLIS; // 15 minutes for password reset
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "PASSWORD_RESET")
                .claim("nationalId", nationalId)
                .setId(jti)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + resetTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public void invalidateToken(String token) {
        try {
            String jti = getClaims(token).getId();
            redisTemplate.opsForValue().set("jwt:blacklist:" + jti, "true");
            redisTemplate.expire("jwt:blacklist:" + jti, TOKEN_TTL_MINUTES, TimeUnit.MINUTES);
            logger.info("Token invalidated with jti: {}", jti);
        } catch (Exception e) {
            logger.error("Failed to invalidate token: {}", e.getMessage());
        }
    }

//    public void invalidateAllTokens() {
//        redisTemplate.delete("jwt:blacklist");
//    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Extract token ID (jti)
     * @param token JWT token
     * @return Token ID
     */
    public String extractTokenId(String token) {
        return getClaims(token).getId();
    }

    /**
     * Extract token type (for password reset tokens)
     * @param token JWT token
     * @return Token type or null if not present
     */
    public String extractTokenType(String token) {
        return getClaims(token).get("type", String.class);
    }

    /**
     * Get token expiration date
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    /**
     * Check if token is expired
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Check if token is a password reset token
     * @param token JWT token
     * @return true if it's a password reset token
     */
    public boolean isPasswordResetToken(String token) {
        try {
            String type = extractTokenType(token);
            return "PASSWORD_RESET".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            String jti = claims.getId();

            // Check if token is blacklisted
            String blacklistKey = "jwt:blacklist:" + jti;
            Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);

            if (Boolean.TRUE.equals(isBlacklisted)) {
                logger.debug("Token is blacklisted with jti: {}", jti);
                return false;
            }

            // Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                logger.debug("Token is expired");
                return false;
            }

            return true;
        } catch (ExpiredJwtException e) {
            logger.debug("Token validation failed: expired");
            return false;
        } catch (Exception e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Parse and get claims from token - PRIVATE for internal use only
     * All external access should use specific extraction methods above
     * @param token JWT token
     * @return Claims object containing all token data
     * @throws JwtException if token is invalid
     */
    private Claims getClaims(String token) throws JwtException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Error parsing JWT token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get specific claim from token
     * @param token JWT token
     * @param claimName Name of the claim
     * @param claimType Type of the claim
     * @return Claim value
     */
    public <T> T getClaim(String token, String claimName, Class<T> claimType) {
        return getClaims(token).get(claimName, claimType);
    }

    /**
     * Get remaining time until token expiration in seconds
     * @param token JWT token
     * @return Remaining time in seconds, or 0 if expired
     */
    public long getRemainingTokenTimeInSeconds(String token) {
        try {
            Date expiration = extractExpiration(token);
            long remainingMillis = expiration.getTime() - System.currentTimeMillis();
            return remainingMillis > 0 ? remainingMillis / 1000 : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}