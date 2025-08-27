package nz.co.tsb.demofortsb.exception.Account;

import nz.co.tsb.demofortsb.exception.ResourceNotFoundException;

public class AccountNotFoundException extends ResourceNotFoundException {
    public AccountNotFoundException(Long accountId) {
        super("Account", accountId.toString());
    }

    public AccountNotFoundException(String accountNumber) {
        super("Account not found with number: " + accountNumber, "Account", accountNumber);
    }
}
