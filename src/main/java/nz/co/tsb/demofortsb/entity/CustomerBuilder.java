package nz.co.tsb.demofortsb.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class CustomerBuilder {
    private static final Logger logger = LoggerFactory.getLogger(CustomerBuilder.class);

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nationalId;
    private LocalDate dateOfBirth;
    private Customer.CustomerStatus status = Customer.CustomerStatus.ACTIVE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String passwordHash;
    private String password;


    public CustomerBuilder () {}

    public CustomerBuilder id(Long id) {
        this.id = id;
        return this;
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


    public Customer build() {

        Customer customer = new Customer();
        customer.setId(this.id);// Will be null unless explicitly set
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setNationalId(nationalId);
        customer.setDateOfBirth(dateOfBirth);
        customer.setStatus(status);
        customer.setCreatedAt(createdAt);
        customer.setUpdatedAt(updatedAt);
        customer.setPasswordHash(passwordHash);

        return customer;
    }


}