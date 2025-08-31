package nz.co.tsb.demofortsb.service;

import nz.co.tsb.demofortsb.dto.response.CustomerReponse;
import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.dto.request.*;
import nz.co.tsb.demofortsb.entity.CustomerBuilder;
import nz.co.tsb.demofortsb.exception.Customer.CustomerNotFoundException;
import nz.co.tsb.demofortsb.exception.ResourceNotFoundException;
import nz.co.tsb.demofortsb.exception.ValidationException;
import nz.co.tsb.demofortsb.repository.CustomerRepository;
import nz.co.tsb.demofortsb.security.DataMaskingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for customer operations including authentication and password management
 */
@Service
@Transactional
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private DataMaskingService maskingService;

    // ========== CUSTOMER REGISTRATION ==========

    public CustomerReponse createCustomer(CustomerRegistrationRequest request) {
        log.info("Creating new customer with nationalId: {}", request.getNationalId());

        validatePasswordMatching(request.getPassword(), request.getConfirmPassword(), "Passwords do not match");
        validateCustomerUniqueness(request.getNationalId(), request.getEmail(), request.getPhoneNumber());

        Customer customer = buildCustomerFromRequest(request);
        Customer savedCustomer = customerRepository.save(customer);

        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return new CustomerReponse(savedCustomer);
    }

    // ========== AUTHENTICATION ==========

// MOVED TO AUTHCONTROLLER

    // ========== PASSWORD MANAGEMENT ==========

    public void updatePassword(Long customerId, PasswordUpdateRequest request) {
        log.info("Password update request for customer ID: {}", customerId);

        validatePasswordMatching(request.getNewPassword(), request.getConfirmNewPassword(), "New passwords do not match");

        Customer customer = getCustomerById(customerId);
        validateCurrentPassword(request.getCurrentPassword(), customer, customerId);

        updateCustomerPassword(customer, request.getNewPassword());
        log.info("Password updated successfully for customer ID: {}", customerId);
    }

    public void adminResetPassword(AdminPasswordResetRequest request) {
        log.info("Admin password reset for customer ID: {}, reason: {}",
                request.getCustomerId(), request.getReason());

        Customer customer = getCustomerById(request.getCustomerId());
        updateCustomerPassword(customer, request.getNewPassword());

        log.info("Admin password reset completed for customer ID: {}", request.getCustomerId());
    }

    // ========== SMS OTP SUPPORT ==========

    public Customer validateCustomerForPasswordReset(PasswordResetRequest request) {
        log.info("Validating customer for password reset: nationalId={}, phone={}",
                request.getNationalId(), request.getPhoneNumber());

        Optional<Customer> customerOpt = customerRepository
                .findActiveByNationalIdAndPhoneNumber(request.getNationalId(), request.getPhoneNumber());

        if (!customerOpt.isPresent()) {
            log.warn("Password reset validation failed - customer not found or phone mismatch");
            throw new ValidationException("National ID and phone number combination not found");
        }

        Customer customer = customerOpt.get();
        log.info("Customer validated for password reset: ID={}", customer.getId());
        return customer;
    }

    public void resetPasswordWithOtp(PasswordResetConfirmRequest request) {
        log.info("Password reset with OTP for nationalId: {}", request.getNationalId());

        validatePasswordMatching(request.getNewPassword(), request.getConfirmPassword(), "Passwords do not match");

        Customer customer = findActiveCustomerByNationalId(request.getNationalId());
        updateCustomerPassword(customer, request.getNewPassword());

        log.info("Password reset completed for customer ID: {}", customer.getId());
    }

    // ========== CUSTOMER LOOKUP ==========

    public List<Customer> getAllCustomers() {
        log.debug("Fetching all customers");
        try {
            return customerRepository.findAll();
        } catch (Exception ex) {
            log.error("Failed to fetch all customers: {}", ex.getMessage(), ex);
            throw new CustomerNotFoundException("Failed to retrieve customers");
        }
    }
    public List<CustomerReponse> getAllCustomersResponse() {
        log.debug("Fetching all customers");
        try {
            return customerRepository.findAll().stream()
                    .map(CustomerReponse::new)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Failed to fetch all customers: {}", ex.getMessage(), ex);
            throw new CustomerNotFoundException("Failed to retrieve customers");
        }
    }

    public List<Customer> getAllCustomersForDebugging() {
        log.debug("Fetching all customers info for debugging");
        try {
            return customerRepository.findAll();
        } catch (Exception ex) {
            log.error("Failed to fetch all customers: {}", ex.getMessage(), ex);
            throw new CustomerNotFoundException("Failed to retrieve customers");
        }
    }

    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId.toString()));
    }

    public Customer getCustomerByNationalId(String nationalId) {

        return customerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", nationalId));//
    }

    public CustomerReponse getCustomerResponseById(Long customerId) {
        Customer customer = getCustomerById(customerId);
        return new CustomerReponse(customer);
    }

    public CustomerReponse getCustomerResponseByNationalId(String nationalId) {
        Customer customer = getCustomerByNationalId(nationalId);
        return new CustomerReponse(customer);
    }

    public Page<CustomerReponse> searchCustomers(String name, Pageable pageable) {
        log.debug("Searching customers by name: {}", name);

        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Name must not be empty");
        }

        Page<Customer> customers = customerRepository.searchByName(name.trim(), pageable);
        return customers.map(CustomerReponse::new);
    }

    public void deleteCustomer(String nationalId) {
        log.info("Deleting customer with nationalId: {}", nationalId);

        Customer customer = getCustomerByNationalId(nationalId);
        customerRepository.delete(customer);

        log.info("Customer deleted successfully with nationalId: {}", nationalId);
    }

    public CustomerReponse updateCustomer(String nationalId, CustomerUpdateRequest request) {
        log.info("Updating customer with nationalId: {}", nationalId);

        Customer customer = getCustomerByNationalId(nationalId);

        // Update customer fields
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setDateOfBirth(request.getDateOfBirth());

        Customer updatedCustomer = customerRepository.save(customer);

        log.info("Customer updated successfully with nationalId: {}", nationalId);
        return new CustomerReponse(updatedCustomer);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email.toLowerCase());
    }

    public CustomerReponse findCustomerResponseByEmail(String email) {
        Customer customer = findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException(email));
        return new CustomerReponse(customer);
    }

    public void deactivateCustomerByEmail(String email) {
        Customer customer = findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException(email));
        customer.setStatus(Customer.CustomerStatus.INACTIVE);
        customerRepository.save(customer);
    }

    public boolean isCustomerActive(String email) {
        return findByEmail(email)
                .map(Customer::isActive)
                .orElse(false);
    }

    // ========== PRIVATE HELPER METHODS ==========

    private Customer buildCustomerFromRequest(CustomerRegistrationRequest request) {
        CustomerBuilder builder = new CustomerBuilder();

        return builder
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .nationalId(request.getNationalId())
                .passwordHash(passwordService.hashPassword(request.getPassword()))
                .build();
    }

    private Customer findActiveCustomerByNationalId(String nationalId) {
        return customerRepository.findActiveByNationalId(nationalId)
                .orElseThrow(() -> new ValidationException("Invalid national ID or password"));
    }

    private void validateCustomerUniqueness(String nationalId, String email, String phoneNumber) {
        if (customerRepository.existsByNationalId(nationalId)) {
            throw new ValidationException("National ID already exists");
        }
        if (customerRepository.existsByEmail(email)) {
            throw new ValidationException("Email address already exists");
        }
        if (customerRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ValidationException("Phone number already exists");
        }
    }

    private void validateUpdateUniqueness(Long customerId, String phoneNumber) {
        if (customerRepository.existsByPhoneNumberAndIdNot(phoneNumber, customerId)) {
            throw new ValidationException("Phone number already exists for another customer");
        }
    }

    private void validatePasswordMatching(String password, String confirmPassword, String errorMessage) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new ValidationException(errorMessage);
        }
    }

    private void validateCustomerHasPassword(Customer customer) {
        if (customer.getPasswordHash() == null) {
            log.warn("Authentication failed - no password set for customer: {}", customer.getNationalId());
            throw new ValidationException("Account requires password setup");
        }
    }

    private void validatePassword(String password, String hashedPassword, String nationalId) {
        if (!passwordService.verifyPassword(password, hashedPassword)) {
            log.warn("Authentication failed - invalid password for customer: {}", nationalId);
            throw new ValidationException("Invalid national ID or password");
        }
    }

    private void validateCurrentPassword(String currentPassword, Customer customer, Long customerId) {
        if (customer.getPasswordHash() == null) {
            throw new ValidationException("No current password set");
        }
        if (!passwordService.verifyPassword(currentPassword, customer.getPasswordHash())) {
            log.warn("Password update failed - incorrect current password for customer ID: {}", customerId);
            throw new ValidationException("Current password is incorrect");
        }
    }

    private void updateCustomerPassword(Customer customer, String newPassword) {
        String hashedPassword = passwordService.hashPassword(newPassword);
        customer.setPasswordHash(hashedPassword);
        customerRepository.save(customer);
    }

    private void checkAndLogPasswordRehashNeeded(Customer customer) {
        if (passwordService.needsRehash(customer.getPasswordHash())) {
            log.info("Password needs rehash for customer ID: {}", customer.getId());
        }
    }
}