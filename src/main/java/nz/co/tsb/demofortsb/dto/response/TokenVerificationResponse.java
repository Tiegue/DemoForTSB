package nz.co.tsb.demofortsb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Immutable record for token verification responses
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenVerificationResponse(
        String message,
        boolean valid,
        LoginUserInfo user,
        Long remainingSeconds,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime expiresAt)
{
    /**
     * Factory method for valid token
     */
    public static TokenVerificationResponse valid(
            LoginUserInfo user,
            Long remainingSeconds,
            LocalDateTime expiresAt
    ) {
        return new TokenVerificationResponse(
                "Token is valid",
                true,
                user,
                remainingSeconds,
                expiresAt
        );
    }

    /**
     * Factory method for invalid token
     */
    public static TokenVerificationResponse invalid(String reason) {
        return new TokenVerificationResponse(
                reason,
                false,
                null,
                null,
                null
        );
    }

}
