package nz.co.tsb.demofortsb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import nz.co.tsb.demofortsb.entity.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Account response with details")
public class AccountResponse {

    @Schema(description = "Account ID", example = "1")
    private Long accountId;

    @Schema(description = "Customer ID", example = "1")
    private Long customerId;

    @Schema(description = "Account number", example = "ACC123456789")
    private String accountNumber;

    @Schema(description = "Account balance", example = "1000.00")
    private BigDecimal balance;

    @Schema(description = "Currency code", example = "NZD")
    private String currencyCode;

    @Schema(description = "Account creation date", example = "2025-01-01 10:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update date", example = "2025-01-01 10:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public AccountResponse() {}

    public AccountResponse(Account account) {
        this.accountId = account.getAccountId();
        this.customerId = account.getCustomerId();
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
        this.currencyCode = account.getCurrencyCode();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getUpdatedAt();
    }

    // Getters and Setters
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
