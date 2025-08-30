package nz.co.tsb.demofortsb.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for password hashing and verification using BCrypt
 */
@Service
public class PasswordService {

    private static final Logger log = LoggerFactory.getLogger(PasswordService.class);

    private final BCryptPasswordEncoder passwordEncoder;

    int hashStrength = 12;//Strength 12 provides good security vs performance balance

    /**
     * Constructor with BCrypt strength configuration
     */
    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder(hashStrength);
        log.info("PasswordService initialized with BCrypt strength: {}", hashStrength);
    }

    /**
     * Hash a plain text password using BCrypt
     */
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (plainPassword.length() > 50) {
            throw new IllegalArgumentException("Password cannot exceed 50 characters");
        }

        String hashedPassword = passwordEncoder.encode(plainPassword);
        log.debug("Password hashed successfully, length: {}", hashedPassword.length());
        return hashedPassword;
    }

    /**
     * Verify a plain text password against a BCrypt hash
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            log.debug("Password verification failed: null input(s)");
            return false;
        }

        if (plainPassword.trim().isEmpty()) {
            log.debug("Password verification failed: empty plain password");
            return false;
        }

        if (!isValidBCryptHash(hashedPassword)) {
            log.debug("Password verification failed: invalid BCrypt hash format");
            return false;
        }

        try {
            boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
            log.debug("Password verification completed");
            return matches;
        } catch (Exception e) {
            log.error("Error during password verification", e);
            return false;
        }
    }

    /**
     * Check if a password hash needs to be rehashed (for security upgrades)
     * This is useful when you want to upgrade password security over time
     *
     * @param hashedPassword the current hash to check
     * @return true if password should be rehashed with current settings
     */
    public boolean needsRehash(String hashedPassword) {
        if (hashedPassword == null || !isValidBCryptHash(hashedPassword)) {
            return true;
        }

        try {
            // Extract the cost/rounds from the hash
            // BCrypt hash format: $2a$rounds$salthash
            String[] parts = hashedPassword.split("\\$");
            if (parts.length < 4) {
                return true;
            }

            int currentRounds = Integer.parseInt(parts[2]);
            int desiredRounds = hashStrength; // Current configured strength

            boolean needsRehash = currentRounds < desiredRounds;
            log.debug("Hash rounds check - current: {}, desired: {}, needs rehash: {}",
                    currentRounds, desiredRounds, needsRehash);

            return needsRehash;
        } catch (Exception e) {
            log.debug("Error checking if hash needs rehash, defaulting to rehash", e);
            return true; // When in doubt, rehash for security
        }
    }

    /**
     * Generate a secure random password for temporary use
     * Useful for admin-generated temporary passwords
     *
     * @return a randomly generated password meeting security requirements (12 chars)
     */
    public String generateTemporaryPassword() {
        return generateTemporaryPassword(12);
    }

    /**
     * Generate a secure random password with specified length
     *
     * @param length desired password length (minimum 8)
     * @return a randomly generated password meeting security requirements
     * @throws IllegalArgumentException if length is less than 8
     */
    public String generateTemporaryPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "@$!%*?&";
        String allChars = upperCase + lowerCase + digits + special;

        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each required category
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Fill remaining length with random characters
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password to randomize positions
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        String generatedPassword = new String(passwordArray);
        log.debug("Generated temporary password of length: {}", length);

        return generatedPassword;
    }

    /**
     * Validate if a string is a valid BCrypt hash
     *
     * @param hash the hash to validate
     * @return true if valid BCrypt hash format
     */
    private boolean isValidBCryptHash(String hash) {
        if (hash == null || hash.length() != 60) {
            return false;
        }

        // BCrypt hash format: $2a$rounds$salt+hash (22 chars salt + 31 chars hash)
        return hash.matches("^\\$2[aaby]?\\$\\d{2}\\$[A-Za-z0-9./]{53}$");
    }

}
