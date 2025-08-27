package nz.co.tsb.demofortsb.exception.Customer;

import nz.co.tsb.demofortsb.exception.ResourceNotFoundException;

public class CustomerNotFoundException extends ResourceNotFoundException {
    public CustomerNotFoundException(Long id) {
        super("Customer", id.toString());
    }

    public CustomerNotFoundException(String email) {
        super("Customer not found with email: " + email, "Customer", email);
    }
}
