package nz.co.tsb.demofortsb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Immutable record for secure login response
 * Contains only essential authentication information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SecureLoginResponse(
        String message,
        LoginUserInfo user,
        String token,
        String tokenType,
        Long expiresIn,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime issuedAt)
{
    /**
     * Factory method with default token type
     */
    public static SecureLoginResponse loginSuccess(
            LoginUserInfo user,
            String token,
            Long expiresIn
    ) {
        return new SecureLoginResponse(
                "Login successful",
                user,
                token,
                "Bearer",
                expiresIn,
                LocalDateTime.now()
        );
    }

    /**
     *  Factory method for custom message
     */
    public static SecureLoginResponse loginCreate(
            String message,
            LoginUserInfo user,
            String token,
            Long expiresIn
    ) {
        return new SecureLoginResponse(
                message,
                user,
                token,
                "Bearer",
                expiresIn,
                LocalDateTime.now()
        );
    }
}
