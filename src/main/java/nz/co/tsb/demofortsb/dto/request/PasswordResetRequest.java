package nz.co.tsb.demofortsb.dto.request;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
/**
 * DTO for password reset request (forgot password using national ID)
 */
public class PasswordResetRequest {

    @NotBlank(message = "National ID is required")
    @Size(max = 20, message = "National ID must not exceed 20 characters")
    private String nationalId;

    @NotBlank(message = "Phone number is required") // Added for SMS
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{10,15}$", message = "Please provide a valid phone number")
    private String phoneNumber;

    // Constructors, getters, setters...
    public PasswordResetRequest() {}

    public PasswordResetRequest(String nationalId, String phoneNumber) {
        this.nationalId = nationalId;
        this.phoneNumber = phoneNumber;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
