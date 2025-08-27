package nz.co.tsb.demofortsb.exception.Customer;

import nz.co.tsb.demofortsb.exception.BusinessException;

public class CustomerAlreadyExistsException extends BusinessException {
    public CustomerAlreadyExistsException(String email) {
        super("Customer already exists with email: " + email, "CUSTOMER_ALREADY_EXISTS");
    }

    public CustomerAlreadyExistsException(String field, String value) {
        super(String.format("Customer already exists with %s: %s", field, value), "CUSTOMER_ALREADY_EXISTS");
    }
}