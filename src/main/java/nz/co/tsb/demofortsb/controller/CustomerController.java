package nz.co.tsb.demofortsb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nz.co.tsb.demofortsb.dto.request.*;
import nz.co.tsb.demofortsb.dto.response.CustomerReponse;
import nz.co.tsb.demofortsb.dto.response.SuccessResponse;
import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.logging.BusinessOperation;
import nz.co.tsb.demofortsb.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@SecurityRequirement(name = "bearerAuth") // Apply JWT requirement to all methods in this controller
@Tag(name = "Customers", description = "TSB Customer management APIs")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "Get all customers with all info for debugging")
    @GetMapping("/allinfo")
    @BusinessOperation("get-all-customers-allinfo-debugging")
    public ResponseEntity<List<Customer>> getAllCustomersForDebugging() {

        logger.info("Get all customers with all info for debugging");

        List<Customer> customers = customerService.getAllCustomersForDebugging();

        logger.info("Get all customers completed successfully, count: {}", customers.size());
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Get customer by internal ID")
    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CustomerReponse> getCustomerById(@PathVariable Long id) {

        logger.info("Get customer by ID request: {}", id);

        CustomerReponse response = customerService.getCustomerResponseById(id);

        logger.info("Get customer by ID completed successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all customers for debugging")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerReponse>> getAllCustomers() {
        String businessId = "get-all-customers";
        MDC.put("businessId", businessId);

        List<CustomerReponse> customersResponse = customerService.getAllCustomersResponse();

        logger.info("Get all customers completed successfully, count: {}", customersResponse.size());
        return ResponseEntity.ok(customersResponse);
    }


    @Operation(summary = "Get customer by national ID")
    @GetMapping("/{nationalId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CustomerReponse> getCustomerByNationalId(@PathVariable String nationalId) {
        String businessId = "get-customer-by-national-id";
        MDC.put("businessId", businessId);
        logger.info("Get customer by national ID request: {}", nationalId);

        CustomerReponse response = customerService.getCustomerResponseByNationalId(nationalId);

        logger.info("Get customer by national ID completed successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update customer profile")
    @PutMapping("/{nationalId}")
    public ResponseEntity<CustomerReponse> updateCustomer(
            @PathVariable String nationalId,
            @Valid @RequestBody CustomerUpdateRequest request) {
        String businessId = "update-customer";
        MDC.put("businessId", businessId);
        logger.info("Update customer request for nationalId: {}", nationalId);

        CustomerReponse response = customerService.updateCustomer(nationalId, request);

        logger.info("Update customer completed successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete customer by national ID")
    @DeleteMapping("/{nationalId}")
    public ResponseEntity<SuccessResponse> deleteCustomer(@PathVariable String nationalId) {
        String businessId = "delete-customer";
        MDC.put("businessId", businessId);
        logger.info("Delete customer request for nationalId: {}", nationalId);

        customerService.deleteCustomer(nationalId);

        logger.info("Delete customer completed successfully");
        return ResponseEntity.ok(new SuccessResponse("Customer deleted successfully"));
    }

    @Operation(summary = "Search customers by name")
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<CustomerReponse>> searchCustomers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String businessId = "search-customers";
        MDC.put("businessId", businessId);
        logger.info("Search customers request for name: {}, page: {}, size: {}", name, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerReponse> customers = customerService.searchCustomers(name, pageable);

        logger.info("Search customers completed successfully, found: {} results", customers.getTotalElements());
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Update customer password")
    @PutMapping("/id/{id}/password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateRequest request) {
        String businessId = "update-password";
        MDC.put("businessId", businessId);
        logger.info("Update password request for customer ID: {}", id);

        customerService.updatePassword(id, request);

        logger.info("Update password completed successfully");
        return ResponseEntity.ok(new SuccessResponse("Password updated successfully"));
    }

    @Operation(summary = "Admin reset customer password")
    @PostMapping("/admin/reset-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> adminResetPassword(
            @Valid @RequestBody AdminPasswordResetRequest request) {
        String businessId = "admin-reset-password";
        MDC.put("businessId", businessId);
        logger.info("Admin reset password request for customer ID: {}", request.getCustomerId());

        customerService.adminResetPassword(request);

        logger.info("Admin reset password completed successfully");
        return ResponseEntity.ok(new SuccessResponse("Password reset successfully"));
    }

    @Operation(summary = "Request password reset via SMS OTP")
    @PostMapping("/password-reset/request")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest request) {
        String businessId = "request-password-reset";
        MDC.put("businessId", businessId);
        logger.info("Password reset request for nationalId: {}", request.getNationalId());

        customerService.validateCustomerForPasswordReset(request);

        // TODO: Send OTP via SMS service
        // smsService.sendOtp(request.getPhoneNumber(), otpCode);

        logger.info("Password reset request completed successfully");
        return ResponseEntity.ok(new SuccessResponse("OTP sent to registered phone number"));
    }

    @Operation(summary = "Verify OTP for password reset")
    @PostMapping("/password-reset/verify-otp")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request) {
        String businessId = "verify-otp";
        MDC.put("businessId", businessId);
        logger.info("OTP verification request for nationalId: {}", request.getNationalId());

        // TODO: Verify OTP with OTP service
        // boolean isValid = otpService.verifyOtp(request.getNationalId(), request.getOtpCode());
        // if (!isValid) throw new ValidationException("Invalid or expired OTP");

        logger.info("OTP verification completed successfully");
        return ResponseEntity.ok(new SuccessResponse("OTP verified successfully"));
    }

    @Operation(summary = "Confirm password reset with verified OTP")
    @PostMapping("/password-reset/confirm")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmRequest request) {
        String businessId = "confirm-password-reset";
        MDC.put("businessId", businessId);
        logger.info("Confirm password reset request for nationalId: {}", request.getNationalId());

        // TODO: Verify OTP before password reset
        // boolean isValid = otpService.verifyOtp(request.getNationalId(), request.getOtpCode());
        // if (!isValid) throw new ValidationException("Invalid or expired OTP");

        customerService.resetPasswordWithOtp(request);

        logger.info("Confirm password reset completed successfully");
        return ResponseEntity.ok(new SuccessResponse("Password reset successfully"));
    }
}