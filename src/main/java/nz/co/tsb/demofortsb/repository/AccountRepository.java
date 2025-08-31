package nz.co.tsb.demofortsb.repository;

import nz.co.tsb.demofortsb.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByCustomerId(Long customerId);

    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.customerId = :customerId AND a.accountNumber = :accountNumber")
    Optional<Account> findByCustomerIdAndAccountNumber(@Param("customerId") Long customerId,
                                                       @Param("accountNumber") String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}