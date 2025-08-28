package nz.co.tsb.demofortsb.service;

import nz.co.tsb.demofortsb.repository.CustomerRepository;
import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.exception.Customer.CustomerNotFoundException;
import nz.co.tsb.demofortsb.exception.Customer.CustomerAlreadyExistsException;
import nz.co.tsb.demofortsb.exception.Customer.InvalidCustomerDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer) {
        log.info("Creating new customer: {} {}", customer.getFirstName(), customer.getLastName());

        // Business logic validation only (Bean Validation handles @NotBlank, @Email, etc.)
        validateBusinessRules(customer, null);

        try {
            Customer savedCustomer = customerRepository.save(customer);
            log.info("Customer created successfully with ID: {}", savedCustomer.getId());
            return savedCustomer;
        } catch (Exception ex) {
            log.error("Failed to save customer: {}", ex.getMessage(), ex);
            throw new InvalidCustomerDataException("Failed to save customer: " + ex.getMessage());
        }
    }

    /**
     * Returns Optional - let controller decide how to handle not found
     */
    public Optional<Customer> getCustomerById(Long id) {
        log.debug("Fetching customer with ID: {}", id);

        if (id == null || id <= 0) {
            throw new InvalidCustomerDataException("Customer ID must be a positive number");
        }

        return customerRepository.findById(id);
    }

    public List<Customer> getAllCustomers() {
        log.debug("Fetching all customers");
        try {
            return customerRepository.findAll();
        } catch (Exception ex) {
            log.error("Failed to fetch all customers: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to retrieve customers", ex);
        }
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        log.info("Updating customer with ID: {}", id);

        // Find existing customer - throw exception for update operations
        Customer existingCustomer = getCustomerById(id)
                .orElseThrow(() -> {
                    log.warn("Attempted to update non-existent customer with ID: {}", id);
                    return new CustomerNotFoundException(id);
                });

        // Business logic validation (uniqueness checks)
        validateBusinessRules(updatedCustomer, existingCustomer);

        try {
            // Update fields - @Valid annotation already validated the input format
            existingCustomer.setFirstName(updatedCustomer.getFirstName());
            existingCustomer.setLastName(updatedCustomer.getLastName());
            existingCustomer.setEmail(updatedCustomer.getEmail());
            existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());
            existingCustomer.setDateOfBirth(updatedCustomer.getDateOfBirth());
            if (updatedCustomer.getNationalId() != null) {
                existingCustomer.setNationalId(updatedCustomer.getNationalId());
            }

            Customer savedCustomer = customerRepository.save(existingCustomer);
            log.info("Customer updated successfully with ID: {}", savedCustomer.getId());
            return savedCustomer;
        } catch (Exception ex) {
            log.error("Failed to update customer {}: {}", id, ex.getMessage(), ex);
            throw new InvalidCustomerDataException("Failed to update customer: " + ex.getMessage());
        }
    }

    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);

        if (id == null || id <= 0) {
            throw new InvalidCustomerDataException("Customer ID must be a positive number");
        }

        // Check if customer exists - throw exception for delete operations
        if (!customerRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent customer with ID: {}", id);
            throw new CustomerNotFoundException(id);
        }

        // TODO: Future enhancement - Check for active accounts
        // if (hasActiveAccounts(id)) {
        //     int activeCount = getActiveAccountCount(id);
        //     log.warn("Cannot delete customer {} - has {} active accounts", id, activeCount);
        //     throw new CustomerHasActiveAccountsException(id, activeCount);
        // }

        try {
            customerRepository.deleteById(id);
            log.info("Customer deleted successfully with ID: {}", id);
        } catch (Exception ex) {
            log.error("Failed to delete customer {}: {}", id, ex.getMessage(), ex);
            throw new RuntimeException("Failed to delete customer", ex);
        }
    }

    public List<Customer> searchCustomers(String name) {
        log.debug("Searching customers by name: {}", name);

        // Input validation for search parameter (not covered by Bean Validation)
        if (!StringUtils.hasText(name)) {
            throw new InvalidCustomerDataException("Search name cannot be empty");
        }

        if (name.trim().length() < 2) {
            throw new InvalidCustomerDataException("Search name must be at least 2 characters long");
        }

        try {
            return customerRepository.searchByName(name.trim());
        } catch (Exception ex) {
            log.error("Failed to search customers by name '{}': {}", name, ex.getMessage(), ex);
            throw new RuntimeException("Failed to search customers", ex);
        }
    }

    /**
     * Validates business rules that Bean Validation (@Valid) cannot handle
     * Focus on: uniqueness constraints, database integrity, business logic
     */
    private void validateBusinessRules(Customer customer, Customer existingCustomer) {
        // Email uniqueness check (Bean Validation can't check database)
        if (existingCustomer == null || !existingCustomer.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(customer.getEmail())) {
                log.warn("Email already exists: {}", customer.getEmail());
                throw new CustomerAlreadyExistsException(customer.getEmail());
            }
        }

        // National ID uniqueness check (if provided)
        if (customer.getNationalId() != null) {
            if (existingCustomer == null || !customer.getNationalId().equals(existingCustomer.getNationalId())) {
                if (customerRepository.existsByNationalId(customer.getNationalId())) {
                    log.warn("National ID already exists: {}", customer.getNationalId());
                    throw new CustomerAlreadyExistsException("nationalId", customer.getNationalId());
                }
            }
        }

        // Future business rules can be added here:
        // - Age restrictions for certain services
        // - Regional compliance checks
        // - Account type eligibility validation
        // - Complex cross-field validation rules
    }

    /**
     * Future methods for account relationship validation
     * TODO: Implement when Account entity is added
     */
    // private boolean hasActiveAccounts(Long customerId) {
    //     return accountRepository.countActiveAccountsByCustomerId(customerId) > 0;
    // }

    // private int getActiveAccountCount(Long customerId) {
    //     return accountRepository.countActiveAccountsByCustomerId(customerId);
    // }
}