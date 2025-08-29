package nz.co.tsb.demofortsb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Validation error response for form validation errors
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Validation error response with field-specific errors")
public class ValidationErrorResponse {

    @Schema(description = "Timestamp when error occurred", example = "2025-08-26 10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type/category", example = "VALIDATION_ERROR")
    private String error;

    @Schema(description = "Human-readable error message", example = "Validation failed for request")
    private String message;

    @Schema(description = "API path where error occurred", example = "/api/customers")
    private String path;

    @Schema(description = "Trace ID for debugging", example = "abc123-def456")
    private String traceId;

    @Schema(description = "Field-specific validation errors")
    private Map<String, String> fieldErrors;

    // Constructors
    public ValidationErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.status = 400;
        this.error = "VALIDATION_ERROR";
        this.fieldErrors = new HashMap<>();
    }

    public ValidationErrorResponse(String message, String path) {
        this();
        this.message = message;
        this.path = path;
    }

    public ValidationErrorResponse(String message, String path, Map<String, String> fieldErrors) {
        this(message, path);
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public void addFieldError(String field, String error) {
        if (this.fieldErrors == null) {
            this.fieldErrors = new HashMap<>();
        }
        this.fieldErrors.put(field, error);
    }

    // Factory methods for convenience
    public static ValidationErrorResponse of(String message, String path) {
        return new ValidationErrorResponse(message, path);
    }

    public static ValidationErrorResponse withFieldErrors(String message, String path, Map<String, String> fieldErrors) {
        return new ValidationErrorResponse(message, path, fieldErrors);
    }
}