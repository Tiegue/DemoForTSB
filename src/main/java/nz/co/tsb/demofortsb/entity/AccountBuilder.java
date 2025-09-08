package nz.co.tsb.demofortsb.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountBuilder {
    private Long accountId;
    private Long customerId;
    private String accountNumber;
    private BigDecimal balance = BigDecimal.ZERO;
    private String currencyCode = "NZD";
    private Account.AccountStatus status = Account.AccountStatus.ACTIVE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AccountBuilder accountId(Long accountId) {
        this.accountId = accountId;
        return this;
    }

    public AccountBuilder customerId(Long customerId) {
        this.customerId = customerId;
        return this;
    }

    public AccountBuilder accountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public AccountBuilder balance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public AccountBuilder currencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public AccountBuilder status(Account.AccountStatus status) {
        this.status = status;
        return this;
    }

    public AccountBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public AccountBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }


    public Account build() {
        if (this.customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        Account account = new Account();
        account.setAccountId(this.accountId);
        account.setCustomerId(this.customerId);
        account.setAccountNumber(Account.generateAccountNumber());
        account.setBalance(this.balance);
        account.setCurrencyCode(this.currencyCode);
        account.setCreatedAt(this.createdAt);
        account.setUpdatedAt(this.updatedAt);

       return account;
    }
}