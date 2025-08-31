package nz.co.tsb.demofortsb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import nz.co.tsb.demofortsb.dto.response.SuccessResponse;
import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.exception.Customer.CustomerNotFoundException;
import nz.co.tsb.demofortsb.exception.ResourceNotFoundException;
import nz.co.tsb.demofortsb.repository.CustomerRepository;
import nz.co.tsb.demofortsb.security.DataMaskingService;
import nz.co.tsb.demofortsb.security.JwtUtil;
import nz.co.tsb.demofortsb.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nz.co.tsb.demofortsb.security.DataMaskingService;

import java.util.HashMap;
import java.util.Map;

/**
 * Setup controller for initial configuration
 * Only active in dev/demo profiles for security
 */
@Tag(name = "Setup", description = "Initial setup and configuration endpoints for development environment")
@RestController
@RequestMapping("/api/setup")
@Profile({"dev", "demo", "local", "docker"}) // Only available in non-production environments
public class SetupController {

    private static final Logger logger = LoggerFactory.getLogger(SetupController.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private DataMaskingService dataMaskingService;


    @Value("${WT_ADMIN_NATIONAL_ID:123456789}")
    private String adminNationalId;

    /**
     * Set or update admin password
     * Example: POST /api/setup/admin-password?password=Admin123!
     */
    @PostMapping("/admin-password")
    @Operation(summary = "Set or update admin password", description = "Set or update admin password only for dev/demo profiles")
    public ResponseEntity<?> setupAdminPassword(@RequestParam String password) {
        String businessId = "setup-admin-password";
        MDC.put("businessId", businessId);
        try {
            // Validate password
            if (password == null || password.length() < 8) {
                return ResponseEntity.badRequest()
                        .body("Password must be at least 8 characters");
            }

            // Find admin user by national ID
            Customer admin = customerRepository.findByNationalId(adminNationalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", adminNationalId) );

            // Hash password with BCrypt strength 12
            String hashedPassword = passwordService.hashPassword(password);
            admin.setPasswordHash(hashedPassword);

            // Ensure admin is active
            if (!admin.isActive()) {
                admin.setStatus(Customer.CustomerStatus.ACTIVE);
            }

            customerRepository.save(admin);

            logger.info("Admin password updated for user: {}", admin.getEmail());

            Map<String, String> response = new HashMap<>();
            String maskNationalId = dataMaskingService.maskNationalId(admin.getNationalId());
            response.put("message", "Admin password updated successfully");
            response.put("email", admin.getEmail());
            response.put("nationalId", maskNationalId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to update admin password", e);
            return ResponseEntity.internalServerError()
                    .body("Failed to update admin password: " + e.getMessage());
        }
    }

    /**
     * Check if admin password is set
     */
    @Operation(summary = "Check if admin password is set", description = "Check if admin password is set only for dev/demo profiles")
    @GetMapping("/admin-status")
    public ResponseEntity<?> checkAdminStatus() {
        String businessId = "check-admin-status";
        MDC.put("businessId", businessId);
        try {
            Customer admin = customerRepository.findByNationalId(adminNationalId)
                    .orElse(null);

            Map<String, Object> status = new HashMap<>();

            if (admin == null) {
                status.put("exists", false);
                status.put("message", "Admin user not found");
            } else {
                String maskNationalId = dataMaskingService.maskNationalId(admin.getNationalId());
                status.put("exists", true);
                status.put("email", admin.getEmail());
                status.put("hasPassword", admin.getPasswordHash() != null && !admin.getPasswordHash().isEmpty());
                status.put("isActive", admin.isActive());
                status.put("nationalId", maskNationalId);
            }

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            logger.error("Failed to check admin status", e);
            return ResponseEntity.internalServerError()
                    .body("Failed to check admin status");
        }
    }
}
