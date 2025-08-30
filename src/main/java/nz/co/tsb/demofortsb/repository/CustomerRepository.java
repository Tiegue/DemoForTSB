package nz.co.tsb.demofortsb.repository;

import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.entity.Customer.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Customer entity operations
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // ========== CORE LOOKUP METHODS ==========

    Optional<Customer> findByNationalId(String nationalId);

    @Query("SELECT c FROM Customer c WHERE c.nationalId = :nationalId AND c.status = 'ACTIVE'")
    Optional<Customer> findActiveByNationalId(@Param("nationalId") String nationalId);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    // ========== SMS OTP SUPPORT ==========

    @Query("SELECT c FROM Customer c WHERE c.nationalId = :nationalId AND c.phoneNumber = :phoneNumber")
    Optional<Customer> findByNationalIdAndPhoneNumber(
            @Param("nationalId") String nationalId,
            @Param("phoneNumber") String phoneNumber);

    @Query("SELECT c FROM Customer c WHERE c.nationalId = :nationalId AND c.phoneNumber = :phoneNumber AND c.status = 'ACTIVE'")
    Optional<Customer> findActiveByNationalIdAndPhoneNumber(
            @Param("nationalId") String nationalId,
            @Param("phoneNumber") String phoneNumber);

    // ========== EXISTENCE CHECKS ==========

    boolean existsByNationalId(String nationalId);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    // ========== UPDATE VALIDATION ==========

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.email = :email AND c.id != :customerId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("customerId") Long customerId);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.phoneNumber = :phoneNumber AND c.id != :customerId")
    boolean existsByPhoneNumberAndIdNot(@Param("phoneNumber") String phoneNumber, @Param("customerId") Long customerId);

    // ========== SEARCH ==========

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Customer> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    // ========== STATUS QUERIES ==========

    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);
}