package nz.co.tsb.demofortsb.controller;

import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nz.co.tsb.demofortsb.dto.request.CustomerLoginRequest;
import nz.co.tsb.demofortsb.dto.request.CustomerRegistrationRequest;
import nz.co.tsb.demofortsb.dto.response.CustomerReponse;
import nz.co.tsb.demofortsb.dto.response.LoginResponse;
import nz.co.tsb.demofortsb.dto.response.SuccessResponse;
import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.exception.Customer.CustomerNotFoundException;
import nz.co.tsb.demofortsb.exception.ValidationException;
import nz.co.tsb.demofortsb.security.CustomerUserDetailsService;
import nz.co.tsb.demofortsb.security.JwtUtil;
import nz.co.tsb.demofortsb.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Optional;

/**
 * Authentication Controller for handling login, registration, and logout operations
 */
@Tag(name = "Authentication", description = "APIs for user authentication, registration, and session management")
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {


    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // REMOVED: private static final long REMEMBER_ME_TTL_MINUTES = 7 * 24 * 60; // ADDED: Constant for remember-me TTL

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    /**
     * Login endpoint - authenticates user and issues JWT
     */
    @Operation(summary = "User Login", description = "Authenticate user with email and password to receive a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "403", description = "Account is not active", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody CustomerLoginRequest request) {
        try {
            // Normalize email to lowercase
            String email = request.getEmail().toLowerCase();

            // Find customer by email
            Optional<Customer> customerOpt = customerService.findByEmail(email);

            if (customerOpt.isEmpty()) {
                logger.warn("Login attempt with non-existent email: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new SuccessResponse("Invalid email or password"));
            }

            Customer customer = customerOpt.get();


            // Check if customer is active
            if (!customer.isActive()) {
                logger.warn("Login attempt for inactive account: {}", email);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new SuccessResponse("Account is not active. Please contact support."));
            }

            // Authenticate using email and password
            Authentication authentication;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email, request.getPassword()));
            } catch (AuthenticationException e) {
                logger.warn("Failed login attempt for email: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new SuccessResponse("Invalid email or password"));
            }

            // Set authentication in context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token // CHANGED: Removed rememberMe TTL logic
            String role = userDetailsService.getUserRole(email);
            String token = jwtUtil.generateToken(email, role); // CHANGED: Use default TTL

            // Calculate token expiration in seconds // CHANGED: Use JwtUtil's TTL
            long expiresIn = jwtUtil.getTokenTtlMinutes() * 60;

            // Create response
            CustomerReponse customerResponse = new CustomerReponse(customer);
            LoginResponse loginResponse = new LoginResponse("Login successful", customerResponse, token, expiresIn);

            logger.info("Successful login for customer: {}", email);
            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            logger.error("Unexpected error during login for email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SuccessResponse("An error occurred during login. Please try again."));
        }
    }

    /**
     * Registration endpoint - creates a new customer account
     */
    @Operation(summary = "User Registration", description = "Register a new customer account with personal details and credentials")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registration successful", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate email/nationalId", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CustomerRegistrationRequest request) {
        try {
            // Validate password match
            if (!request.isPasswordMatching()) {
                logger.warn("Registration attempt with mismatched passwords for email: {}", request.getEmail());
                return ResponseEntity.badRequest()
                        .body(new SuccessResponse("Passwords do not match"));
            }

            // Normalize email to lowercase // ADDED
            request.setEmail(request.getEmail().toLowerCase());

            // Use CustomerService to create customer (it handles all validations and password encoding)
            try {
                CustomerReponse customerResponse = customerService.createCustomer(request);

                logger.info("New customer registered: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new SuccessResponse("Registration successful", customerResponse));

            } catch (ValidationException e) {
                // CHANGED: CustomerService throws ValidationException for duplicate email/nationalId
                logger.warn("Registration validation failed: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(new SuccessResponse(e.getMessage()));
            }

        } catch (Exception e) {
            logger.error("Unexpected error during registration for email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SuccessResponse("An error occurred during registration. Please try again."));
        }
    }

    /**
     * Logout endpoint - invalidates the JWT token and deactivates account
     */
    @Operation(summary = "User Logout", description = "Log out user, invalidate JWT token, and deactivate account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "No valid token provided", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Parameter(description = "JWT Bearer token", required = true) // CHANGED: Mark as required
            @RequestHeader(value = "Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Logout attempt with invalid or missing Authorization header");
                return ResponseEntity.badRequest()
                        .body(new SuccessResponse("No valid token provided"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            String jti = jwtUtil.extractTokenId(token); // Extract jti for logging

            // Invalidate the token by adding to Redis blacklist
            jwtUtil.invalidateToken(token);

            // Deactivate customer account
            try {
                customerService.deactivateCustomerByEmail(email);
                logger.info("Deactivated account for email: {}", email);
            } catch (CustomerNotFoundException e) {
                logger.warn("Customer not found during logout for email: {}", email);
                // Continue with logout even if customer not found
            }

            // Clear security context
            SecurityContextHolder.clearContext();

            logger.info("User logged out successfully, token jti: {}, email: {}", jti, email);
            return ResponseEntity.ok(new SuccessResponse("Logout successful"));

        } catch (JwtException e) {
            logger.error("Error during logout for token: {}", authHeader, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SuccessResponse("Invalid token provided"));
        } catch (Exception e) {
            logger.error("Unexpected error during logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SuccessResponse("An error occurred during logout"));
        }
    }

    /**
     * Verify token endpoint - checks if current token is valid
     */
    @Operation(summary = "Verify Token", description = "Verify if the provided JWT token is valid and return user information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token is valid", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or missing token", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "403", description = "Account is not active", content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(
            @Parameter(description = "JWT Bearer token", required = true)
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) { // CHANGED: Add null check
                logger.warn("Token verification attempt with invalid or missing Authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new SuccessResponse("Invalid or missing token"));
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                logger.warn("Invalid token during verification: {}", token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new SuccessResponse("Invalid token"));
            }

            String email = jwtUtil.extractEmail(token);

            // Use CustomerService to get customer
            Optional<Customer> customerOpt = customerService.findByEmail(email);
            if (customerOpt.isEmpty()) {
                logger.warn("User not found during token verification: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new SuccessResponse("User not found"));
            }
            Customer customer = customerOpt.get();

            if (!customer.isActive()) {
                logger.warn("Token verification for inactive account: {}", email);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new SuccessResponse("Account is not active"));
            }

            // Calculate remaining token validity
            long remainingTime = jwtUtil.getRemainingTokenTimeInSeconds(token) ; // in seconds

            CustomerReponse customerResponse = new CustomerReponse(customer);
            LoginResponse response = new LoginResponse("Token is valid", customerResponse, token, remainingTime);

            logger.info("Token verified successfully for email: {}", email);
            return ResponseEntity.ok(response);

        } catch (JwtException e) {
            logger.error("Token validation failed: {}", authHeader, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse("Token validation failed"));
        } catch (Exception e) {
            logger.error("Unexpected error during token verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SuccessResponse("An error occurred during token verification"));
        }
    }

    /**
     * Get current user info
     */
    @Operation(summary = "Get Current User", description = "Retrieve the currently authenticated user's information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User information retrieved successfully", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@Parameter(hidden = true) Principal principal) {
        try {
            if (principal == null) {
                logger.warn("Attempt to access /me without authentication");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new SuccessResponse("Not authenticated"));
            }

            String email = principal.getName().toLowerCase(); // Normalize email

            // CHANGED: Use CustomerService to get customer by email
            try {
                CustomerReponse customerResponse = customerService.findCustomerResponseByEmail(email);

                logger.info("User information retrieved for email: {}", email);
                return ResponseEntity.ok(new SuccessResponse("User retrieved successfully", customerResponse));

            } catch (CustomerNotFoundException e) {
                logger.error("User not found for email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new SuccessResponse("User not found"));
            }
        } catch (Exception e) {
            logger.error("Error retrieving current user for principal: {}", principal != null ? principal.getName() : "null", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SuccessResponse("Error retrieving user information"));
        }
    }
}
