package nz.co.tsb.demofortsb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;


@Schema(description = "Transfer request between accounts")
public class TransferRequest {

    @NotBlank(message = "From account number is required")
    @Schema(description = "Source account number", example = "ACC123456789", required = true)
    private String fromAccountNumber;

    @NotBlank(message = "To account number is required")
    @Schema(description = "Destination account number", example = "ACC987654321", required = true)
    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @DecimalMax(value = "999999.99", message = "Amount cannot exceed 999999.99")
    @Schema(description = "Transfer amount", example = "100.00", required = true)
    private BigDecimal amount;

    @Schema(description = "Transfer description", example = "Payment for services")
    private String description;

    // Getters and Setters
    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}