package nz.co.tsb.demofortsb.config;

import nz.co.tsb.demofortsb.security.CustomerUserDetailsService;
import nz.co.tsb.demofortsb.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        // Create ProviderManager with our DaoAuthenticationProvider
        return new ProviderManager(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customerUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder()); // Uses same BCrypt(12) in PasswordEncoder of
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Disable Security
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll()  // Allow all requests without authentication
                )
                .csrf(csrf -> csrf.disable())  // Disable CSRF for REST APIs
                .httpBasic(httpBasic -> httpBasic.disable())  // Disable HTTP Basic Auth
                .formLogin(formLogin -> formLogin.disable());  // Disable form login


        // Enable Security
//        http
//                // Configure authentication provider
//                .authenticationProvider(daoAuthenticationProvider())
//
//                // Session management for JWT
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//
//                // *** MODIFIED: Updated authorization rules
//                .authorizeHttpRequests(authz -> authz
//                        // Public endpoints
//                        .requestMatchers("/h2-console/**").permitAll()
//                        .requestMatchers("/api/auth/login").permitAll()
//                        .requestMatchers("/api/auth/register").permitAll()
//                        .requestMatchers("/api/public/**").permitAll()
//                        .requestMatchers("/swagger-ui/**").permitAll()
//                        .requestMatchers("/v3/api-docs/**").permitAll()
//                        .requestMatchers("/actuator/**").permitAll()
//                        // Admin endpoints
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        // User endpoints
//                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
//                        // All other endpoints require authentication
//                        .anyRequest().authenticated() // From permitAll() to authenticated()
//                )
//                .csrf(csrf -> csrf.disable())
//                .httpBasic(httpBasic -> httpBasic.disable())
//                .formLogin(formLogin -> formLogin.disable())
//
//                // *** ADDED: JWT filter
//                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }


}