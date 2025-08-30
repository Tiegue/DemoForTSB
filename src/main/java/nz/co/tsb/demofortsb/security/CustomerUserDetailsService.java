package nz.co.tsb.demofortsb.security;

import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.springframework.security.core.userdetails.User.*;

/**
 * Custom UserDetailsService implementation for loading user details from the database.
 * <p>
 * In order to simplify the demo, we use the national id as the role,
 * the email as the username, and set customer with national id "123456789" as admin at dev time.
 * </p>
 */
@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public CustomerUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    //
    @Value("${WT_ADMIN_NATIONAL_ID:123456789}")
    private String ADMIN_NATIONAL_ID;

    @Override
    public UserDetails loadUserByUsername(String emailAsUsername) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(emailAsUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!customer.isActive()) {
            throw new UsernameNotFoundException("User account is disabled: " + emailAsUsername);
        }

        String role = ADMIN_NATIONAL_ID.equals(customer.getNationalId()) ? "ROLE_ADMIN" : "ROLE_USER";

        return builder()
                .username(customer.getEmail())
                .password(customer.getPasswordHash())
                .authorities(role)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    public String getUserRole(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ADMIN_NATIONAL_ID.equals(customer.getNationalId()) ? "ROLE_ADMIN" : "ROLE_USER";
    }

}