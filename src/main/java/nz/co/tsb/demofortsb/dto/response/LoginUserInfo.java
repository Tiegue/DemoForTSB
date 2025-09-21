package nz.co.tsb.demofortsb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import nz.co.tsb.demofortsb.entity.Customer;

import java.time.LocalDateTime;

/**
 * Immutable record for minimal customer info in login responses
 * Only contains non-sensitive data that's safe to expose
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginUserInfo(
        Long id,
        String firstName,
        String lastName,
        String email,
        Customer.CustomerStatus status,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastLoginAt)
{
    /**
     * Factory method to create a LoginUserInfo from a Customer entity.
     */
    public static LoginUserInfo fromCustomer(Customer customer) {
        return new LoginUserInfo(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getStatus(),
                LocalDateTime.now()
        );
    }

}
