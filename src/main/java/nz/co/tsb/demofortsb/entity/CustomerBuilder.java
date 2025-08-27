package nz.co.tsb.demofortsb.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CustomerBuilder.class);
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String nationalId;
    private Customer.CustomerStatus status = Customer.CustomerStatus.ACTIVE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomerBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public CustomerBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public CustomerBuilder email(String email) {
        this.email = email;
        return this;
    }

    public CustomerBuilder phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public CustomerBuilder dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public CustomerBuilder nationalId(String nationalId) {
        this.nationalId = nationalId;
        return this;
    }

    public CustomerBuilder status(Customer.CustomerStatus status) {
        this.status = status;
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

    public Customer build() {
        return new Customer(id, firstName, lastName, email, phoneNumber, dateOfBirth,
                nationalId, status, createdAt, updatedAt);
    }
}
