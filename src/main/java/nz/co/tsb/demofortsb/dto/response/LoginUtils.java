package nz.co.tsb.demofortsb.dto.response;

import java.time.LocalDateTime;

/**
 * Utility class for computed operations on login data
 * Keeps records pure while providing helper methods
 */
public final class LoginUtils {

    private LoginUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Get display name from user info
     */
    public static String getDisplayName(LoginUserInfo userInfo) {
        return userInfo.firstName() + " " + userInfo.lastName();
    }

    /**
     * Check if token expires soon (within 5 minutes)
     */
    public static boolean expiresSoon(SecureLoginResponse response) {
        return response.expiresIn() != null && response.expiresIn() < 300;
    }

    /**
     * Calculate token expiration time
     */
    public static LocalDateTime getExpiresAt(SecureLoginResponse response) {
        return response.issuedAt().plusSeconds(response.expiresIn());
    }

    /**
     * Check if token is expired
     */
    public static boolean isExpired(SecureLoginResponse response) {
        return LocalDateTime.now().isAfter(getExpiresAt(response));
    }
}
