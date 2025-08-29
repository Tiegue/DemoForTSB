package nz.co.tsb.demofortsb.dto.response;

/**
 * DTO for OTP verification response (NEW)
 */
public class OtpVerificationResponse {

    private String message;
    private boolean verified;
    private String verificationToken; // Optional: temporary token for next step
    private Long expiresIn; // Token expiration time in seconds

    // Constructors
    public OtpVerificationResponse() {}

    public OtpVerificationResponse(String message, boolean verified) {
        this.message = message;
        this.verified = verified;
    }

    public OtpVerificationResponse(String message, boolean verified, String verificationToken, Long expiresIn) {
        this.message = message;
        this.verified = verified;
        this.verificationToken = verificationToken;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}