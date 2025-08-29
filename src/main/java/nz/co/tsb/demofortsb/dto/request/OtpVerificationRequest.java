package nz.co.tsb.demofortsb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
/**
 * DTO for OTP verification
 */
public class OtpVerificationRequest {

    @NotBlank(message = "National ID is required")
    @Size(max = 20, message = "National ID must not exceed 20 characters")
    private String nationalId;

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be exactly 6 digits")
    private String otpCode;

    // Constructors
    public OtpVerificationRequest() {}

    public OtpVerificationRequest(String nationalId, String otpCode) {
        this.nationalId = nationalId;
        this.otpCode = otpCode;
    }

    // Getters and Setters
    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
