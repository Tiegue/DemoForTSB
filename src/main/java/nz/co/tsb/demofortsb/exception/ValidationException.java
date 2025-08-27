package nz.co.tsb.demofortsb.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception for data validation errors
 */
public class ValidationException extends RuntimeException {
    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}