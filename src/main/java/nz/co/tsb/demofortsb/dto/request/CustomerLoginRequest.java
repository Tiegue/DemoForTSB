package nz.co.tsb.demofortsb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

/**
 * DTO for customer login (using nationalId as identifier)
 */
public class CustomerLoginRequest {
    @NotBlank(message = "National ID is required")
    @Size(max = 20, message = "National ID must not exceed 20 characters")
    private String nationalId;

    @NotBlank(message = "Password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private boolean rememberMe = false;

    // Constructors
    public CustomerLoginRequest() {}

    public CustomerLoginRequest(String nationalId, String password) {
        this.nationalId = nationalId;
        this.password = password;
    }

    // Getters and Setters
    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
