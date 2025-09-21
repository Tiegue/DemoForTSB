package nz.co.tsb.demofortsb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.security.DataMaskingService;

import java.time.LocalDateTime;

/**
 * Immutable record for user profile responses with masked sensitive data
 * Uses your existing DataMaskingService
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String maskedPhoneNumber,
        String maskedNationalId,
        String birthYear, // Only year, not full date
        Customer.CustomerStatus status,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt)
{
    /**
     * Factory method that uses your existing DataMaskingService
     */
    public static UserProfileResponse from(Customer customer, DataMaskingService maskingService) {
        return new UserProfileResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                maskingService.maskPhone(customer.getPhoneNumber()),
                maskingService.maskNationalId(customer.getNationalId()),
                maskingService.maskDateOfBirth(customer.getDateOfBirth()), // Uses your existing method
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

}