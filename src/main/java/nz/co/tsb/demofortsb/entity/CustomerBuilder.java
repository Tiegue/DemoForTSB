package nz.co.tsb.demofortsb.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerBuilder {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nationalId;
    private Long id;
    private LocalDate dateOfBirth;
    private Customer.CustomerStatus status = Customer.CustomerStatus.ACTIVE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String passwordHash;
    private String password;

    private CustomerBuilder() {}

    public static CustomerBuilder create() {
        return new CustomerBuilder();
    }

    public CustomerBuilder firstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        this.firstName = firstName;
        return this;
    }

    public CustomerBuilder lastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.lastName = lastName;
        return this;
    }

    public CustomerBuilder email(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        this.email = email;
        return this;
    }

    public CustomerBuilder phoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        this.phoneNumber = phoneNumber;
        return this;
    }

    public CustomerBuilder nationalId(String nationalId) {
        if (nationalId == null || nationalId.trim().isEmpty()) {
            throw new IllegalArgumentException("National ID cannot be null or empty");
        }
        this.nationalId = nationalId;
        return this;
    }

    public CustomerBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public CustomerBuilder dateOfBirth(int year, int month, int day) {
        this.dateOfBirth = LocalDate.of(year, month, day);
        return this;
    }

    public CustomerBuilder status(Customer.CustomerStatus status) {
        this.status = status != null ? status : Customer.CustomerStatus.ACTIVE;
        return this;
    }

    public CustomerBuilder active() {
        this.status = Customer.CustomerStatus.ACTIVE;
        return this;
    }

    public CustomerBuilder inactive() {
        this.status = Customer.CustomerStatus.INACTIVE;
        return this;
    }

    public CustomerBuilder suspended() {
        this.status = Customer.CustomerStatus.SUSPENDED;
        return this;
    }

    public CustomerBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public CustomerBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public CustomerBuilder passwordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public CustomerBuilder password(String password) {
        this.password = password;
        return this;
    }

    public Customer build() {
        validateRequiredFields();

        Customer customer = new Customer();

        if (id != null) {
            customer.setId(id);
        }
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setNationalId(nationalId);
        customer.setDateOfBirth(dateOfBirth);
        customer.setStatus(status);

        if (createdAt != null) {
            customer.setCreatedAt(createdAt);
        }
        if (updatedAt != null) {
            customer.setUpdatedAt(updatedAt);
        }

        if (passwordHash != null) {
            customer.setPasswordHash(passwordHash);
        }
        if (password != null) {
            customer.setPassword(password);
        }

        return customer;
    }

    public Customer buildWithConstructor() {
        validateRequiredFields();

        Customer customer = new Customer(firstName, lastName, phoneNumber, email, nationalId);

        if (id != null) {
            customer.setId(id);
        }
        if (dateOfBirth != null) {
            customer.setDateOfBirth(dateOfBirth);
        }
        customer.setStatus(status);

        if (createdAt != null) {
            customer.setCreatedAt(createdAt);
        }
        if (updatedAt != null) {
            customer.setUpdatedAt(updatedAt);
        }
        if (passwordHash != null) {
            customer.setPasswordHash(passwordHash);
        }
        if (password != null) {
            customer.setPassword(password);
        }

        return customer;
    }

    private void validateRequiredFields() {
        if (firstName == null) {
            throw new IllegalStateException("First name is required");
        }
        if (lastName == null) {
            throw new IllegalStateException("Last name is required");
        }
        if (email == null) {
            throw new IllegalStateException("Email is required");
        }
        if (phoneNumber == null) {
            throw new IllegalStateException("Phone number is required");
        }
        if (nationalId == null) {
            throw new IllegalStateException("National ID is required");
        }
    }
}