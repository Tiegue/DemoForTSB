package nz.co.tsb.demofortsb.repository;

import nz.co.tsb.demofortsb.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find by email
    Optional<Customer> findByEmail(String email);

    // Find by national ID
    Optional<Customer> findByNationalId(String nationalId);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if national ID exists
    boolean existsByNationalId(String nationalId);

    // Find all active customers
    List<Customer> findByStatus(Customer.CustomerStatus status);

    // Custom query example
    @Query("SELECT c FROM Customer c WHERE c.firstName LIKE %?1% OR c.lastName LIKE %?1%")
    List<Customer> searchByName(String name);
}
