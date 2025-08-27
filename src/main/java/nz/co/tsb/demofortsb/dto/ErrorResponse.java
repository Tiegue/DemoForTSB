package nz.co.tsb.demofortsb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standardized error response structure")
public class ErrorResponse {

    @Schema(description = "Timestamp when error occurred", example = "2025-08-26 10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type/category", example = "VALIDATION_ERROR")
    private String error;

    @Schema(description = "Human-readable error message", example = "Customer not found with id: 123")
    private String message;

    @Schema(description = "API path where error occurred", example = "/api/customers/123")
    private String path;

    @Schema(description = "Additional error details")
    private Map<String, String> details;

    @Schema(description = "Trace ID for debugging", example = "abc123-def456")
    private String traceId;

    // Private constructor for builder pattern
    private ErrorResponse() {
        this.timestamp = LocalDateTime.now();
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

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    // Builder Pattern Implementation
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ErrorResponse errorResponse;

        public Builder() {
            this.errorResponse = new ErrorResponse();
        }

        public Builder timestamp(LocalDateTime timestamp) {
            errorResponse.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            errorResponse.status = status;
            return this;
        }

        public Builder error(String error) {
            errorResponse.error = error;
            return this;
        }

        public Builder message(String message) {
            errorResponse.message = message;
            return this;
        }

        public Builder path(String path) {
            errorResponse.path = path;
            return this;
        }

        public Builder details(Map<String, String> details) {
            errorResponse.details = details;
            return this;
        }

        public Builder traceId(String traceId) {
            errorResponse.traceId = traceId;
            return this;
        }

        public ErrorResponse build() {
            return errorResponse;
        }
    }

    // Convenience static factory methods (HappyGigs style)
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

    public static ErrorResponse withDetails(int status, String error, String message, String path, Map<String, String> details) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .details(details)
                .build();
    }
}
