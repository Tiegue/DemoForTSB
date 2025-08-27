package nz.co.tsb.demofortsb.exception.Customer;

import nz.co.tsb.demofortsb.exception.ValidationException;

import java.util.Map;

public class InvalidCustomerDataException extends ValidationException {
    public InvalidCustomerDataException(String message) {
        super(message);
    }

    public InvalidCustomerDataException(String message, Map<String, String> fieldErrors) {
        super(message, fieldErrors);
    }
}
