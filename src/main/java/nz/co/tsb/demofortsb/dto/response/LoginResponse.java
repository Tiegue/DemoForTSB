package nz.co.tsb.demofortsb.dto.response;

/**
 * DTO for login response
 */
public class LoginResponse {

    private String message;
    private CustomerResponse customer;
    private String token; // JWT token if using JWT authentication
    private Long expiresIn; // Token expiration time in seconds

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String message, CustomerResponse customer) {
        this.message = message;
        this.customer = customer;
    }

    public LoginResponse(String message, CustomerResponse customer, String token, Long expiresIn) {
        this.message = message;
        this.customer = customer;
        this.token = token;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CustomerResponse getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerResponse customer) {
        this.customer = customer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
