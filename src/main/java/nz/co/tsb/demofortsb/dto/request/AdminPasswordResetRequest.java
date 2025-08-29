package nz.co.tsb.demofortsb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
/**
 * DTO for admin password reset (admin changes another user's password,no current password required)
 */
public class AdminPasswordResetRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    private String reason; // Optional reason for audit

    // Constructors
    public AdminPasswordResetRequest() {}

    public AdminPasswordResetRequest(Long customerId, String newPassword, String reason) {
        this.customerId = customerId;
        this.newPassword = newPassword;
        this.reason = reason;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
