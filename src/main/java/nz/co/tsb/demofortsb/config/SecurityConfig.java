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

import java.time.Duration;

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
    // Call by authenticationManager()
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
//        http
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/h2-console/**").permitAll()
//                        .anyRequest().permitAll()  // Allow all requests without authentication
//                )
//                .csrf(csrf -> csrf.disable())  // Disable CSRF for REST APIs
//                .httpBasic(httpBasic -> httpBasic.disable())  // Disable HTTP Basic Auth
//                .formLogin(formLogin -> formLogin.disable());  // Disable form login


        // Enable Security
        http
                // Force HTTPS
                .requiresChannel(channel ->
                        channel.requestMatchers(r ->
                                        r.getHeader("X-Forwarded-Proto") != null &&
                                                r.getHeader("X-Forwarded-Proto").equals("http"))
                                .requiresSecure()
                )

                // Configure authentication provider
                .authenticationProvider(daoAuthenticationProvider())

                // Session management for JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Set authorization rules
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/accounts/**").permitAll()

                        // Only for dev stage, and then remove them
                        .requestMatchers("/api/setup/**").permitAll()
                        .requestMatchers("/api/customers/allinfo").permitAll()


                        // OpenAPI/Swagger endpoints
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        .requestMatchers("/actuator/**").permitAll()
                        // Only admin can get all customers
                        .requestMatchers("/api/customers/admin").hasRole("ADMIN")
                        // Customer endpoints
                        .requestMatchers("/api/customers/**").hasAnyRole("USER", "ADMIN")
                        // All other endpoints require authentication
                        .anyRequest().authenticated() // From permitAll() to authenticated()
                )

                // add headers for HTTPS
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true)
                                .preload(true))
                )

                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())

                // Add JWT filter
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }


}