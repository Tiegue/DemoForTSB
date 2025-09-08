package nz.co.tsb.demofortsb.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionBuilder {
    private Long transactionId;
    private Long accountId;
    private Transaction.TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private Long fromAccountId;
    private Long toAccountId;
    private Transaction.TransactionStatus transactionStatus = Transaction.TransactionStatus.PENDING;
    private String currencyCode = "NZD";
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Transaction.TransactionStatus status;

    public TransactionBuilder transactionId(Long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public TransactionBuilder accountId(Long accountId) {
        this.accountId = accountId;
        return this;
    }

    public TransactionBuilder transactionType(Transaction.TransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public TransactionBuilder amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TransactionBuilder transactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    public TransactionBuilder fromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
        return this;
    }

    public TransactionBuilder toAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
        return this;
    }

    public TransactionBuilder transactionStatus(Transaction.TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
        return this;
    }

    public TransactionBuilder currencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public TransactionBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public TransactionBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public TransactionBuilder status(Transaction.TransactionStatus status) {
        this.transactionStatus = status;
        return this;
    }

    public Transaction build() {
        if (transactionType == Transaction.TransactionType.TRANSFER_IN ||
                transactionType == Transaction.TransactionType.TRANSFER_OUT) {
            if (fromAccountId == null || toAccountId == null) {
                throw new IllegalStateException("Transfer requires both accounts");
            }
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionId(this.transactionId);
        transaction.setAccountId(this.accountId);
        transaction.setTransactionType(this.transactionType);
        transaction.setAmount(this.amount);
        transaction.setTransactionDate(this.transactionDate != null ? this.transactionDate : LocalDateTime.now());
        transaction.setFromAccountId(this.fromAccountId);
        transaction.setToAccountId(this.toAccountId);
        transaction.setTransactionStatus(this.transactionStatus);
        transaction.setCurrencyCode(this.currencyCode);
        transaction.setCreatedAt(this.createdAt != null ? this.createdAt : LocalDateTime.now());
        transaction.setUpdatedAt(this.updatedAt != null ? this.updatedAt : LocalDateTime.now());
        transaction.setTransactionStatus(this.transactionStatus);
        return transaction;
    }
}
