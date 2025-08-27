package nz.co.tsb.demofortsb.service;

import nz.co.tsb.demofortsb.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nz.co.tsb.demofortsb.entity.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        log.info("Creating new customer: {} {}", customer.getFirstName(), customer.getLastName());

        // Check if email already exists
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email already exists: " + customer.getEmail());
        }

        // Check if national ID already exists
        if (customer.getNationalId() != null &&
                customerRepository.existsByNationalId(customer.getNationalId())) {
            throw new RuntimeException("National ID already exists: " + customer.getNationalId());
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());

        return savedCustomer;
    }

    public Optional<Customer> getCustomerById(Long id) {
        log.debug("Fetching customer with ID: {}", id);
        return customerRepository.findById(id);
    }

    public List<Customer> getAllCustomers() {
        log.debug("Fetching all customers");
        return customerRepository.findAll();
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        log.info("Updating customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        customer.setFirstName(updatedCustomer.getFirstName());
        customer.setLastName(updatedCustomer.getLastName());
        customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
        customer.setDateOfBirth(updatedCustomer.getDateOfBirth());

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully");

        return savedCustomer;
    }

    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);

        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with ID: " + id);
        }

        customerRepository.deleteById(id);
        log.info("Customer deleted successfully");
    }

    public List<Customer> searchCustomers(String name) {
        log.debug("Searching customers by name: {}", name);
        return customerRepository.searchByName(name);
    }
}
