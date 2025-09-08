package nz.co.tsb.demofortsb.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import nz.co.tsb.demofortsb.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Transaction response with details")
public class TransactionResponse {

    @Schema(description = "Transaction ID", example = "1")
    private Long transactionId;

    @Schema(description = "Account ID", example = "1")
    private Long accountId;

    @Schema(description = "Transaction type", example = "TRANSFER")
    private Transaction.TransactionType transactionType;

    @Schema(description = "Transaction amount", example = "100.00")
    private BigDecimal amount;

    @Schema(description = "Transaction date", example = "2025-01-01 10:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionDate;

    @Schema(description = "Source account ID", example = "1")
    private Long fromAccountId;

    @Schema(description = "Destination account ID", example = "2")
    private Long toAccountId;

    @Schema(description = "Transaction status", example = "COMPLETED")
    private Transaction.TransactionStatus transactionStatus;

    @Schema(description = "Currency code", example = "NZD")
    private String currencyCode;

    public TransactionResponse() {}

    public TransactionResponse(Transaction transaction) {
        this.transactionId = transaction.getTransactionId();
        this.accountId = transaction.getAccountId();
        this.transactionType = transaction.getTransactionType();
        this.amount = transaction.getAmount();
        this.transactionDate = transaction.getTransactionDate();
        this.fromAccountId = transaction.getFromAccountId();
        this.toAccountId = transaction.getToAccountId();
        this.transactionStatus = transaction.getTransactionStatus();
        this.currencyCode = transaction.getCurrencyCode();
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Transaction.TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Transaction.TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public Transaction.TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(Transaction.TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
