package nz.co.tsb.demofortsb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Immutable record for logout responses
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LogoutResponse(
        String message,
        boolean success,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime loggedOutAt)
{
    /**
     * Factory method for successful logout
     */
    public static LogoutResponse logoutSuccess() {
        return new LogoutResponse(
                "Logout successful",
                true,
                LocalDateTime.now()
        );
    }

    /**
     * Factory method for logout with custom message
     */
    public static LogoutResponse logoutCreate(String message, boolean success) {
        return new LogoutResponse(
                message,
                success,
                LocalDateTime.now()
        );
    }
}