package nz.co.tsb.demofortsb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String path = request.getRequestURI();
        // Skip JWT validation for health endpoint
        if (path.startsWith("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }
        // JWT validation logic
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (jwtUtil.validateToken(token)) {
                    // Check if it's not a password reset token
                    if (!jwtUtil.isPasswordResetToken(token)) {
                        String email = jwtUtil.extractEmail(token);
                        String role = jwtUtil.extractRole(token);

                        if (role != null) {
                            UsernamePasswordAuthenticationToken authToken =
                                    new UsernamePasswordAuthenticationToken(
                                            email,          // Principal
                                            null,           // null because already authenticated
                                            List.of(new SimpleGrantedAuthority(role)) // Authorities
                                    );

                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            logger.debug("JWT authentication successful for user: {}", email);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("JWT authentication failed: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
