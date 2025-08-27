package nz.co.tsb.demofortsb.exception.Account;

import nz.co.tsb.demofortsb.exception.BusinessException;

public class AccountAlreadyExistsException extends BusinessException {
    public AccountAlreadyExistsException(String accountNumber) {
        super("Account already exists with number: " + accountNumber, "ACCOUNT_ALREADY_EXISTS");
    }
}