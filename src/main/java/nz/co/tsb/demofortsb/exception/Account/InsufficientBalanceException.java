package nz.co.tsb.demofortsb.exception.Account;

import nz.co.tsb.demofortsb.exception.BusinessException;

public class InsufficientBalanceException extends BusinessException {
    private final String accountNumber;
    private final Double requestedAmount;
    private final Double availableBalance;

    public InsufficientBalanceException(String accountNumber, Double requestedAmount, Double availableBalance) {
        super(String.format("Insufficient balance in account %s. Requested: %.2f, Available: %.2f",
                accountNumber, requestedAmount, availableBalance), "INSUFFICIENT_BALANCE");
        this.accountNumber = accountNumber;
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public Double getRequestedAmount() { return requestedAmount; }
    public Double getAvailableBalance() { return availableBalance; }
}