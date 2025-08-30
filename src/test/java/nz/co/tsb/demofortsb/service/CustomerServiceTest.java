package nz.co.tsb.demofortsb.service;

import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.exception.Customer.CustomerAlreadyExistsException;
import nz.co.tsb.demofortsb.exception.Customer.CustomerNotFoundException;
import nz.co.tsb.demofortsb.exception.Customer.InvalidCustomerDataException;
import nz.co.tsb.demofortsb.exception.ResourceNotFoundException;
import nz.co.tsb.demofortsb.repository.CustomerRepository;
import nz.co.tsb.demofortsb.security.DataMaskingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {


    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DataMaskingService maskingService;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private final Long CUSTOMER_ID = 1L;

    @BeforeEach
    void setUp() {
        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(CUSTOMER_ID);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setPhoneNumber("+640987654321");
        testCustomer.setDateOfBirth(LocalDate.of(1990, 1, 1));

    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenCustomerExists() {
        // Arrange
        when(customerRepository.findById(CUSTOMER_ID))
                .thenReturn(Optional.of(testCustomer));

        // Act
        Customer result = customerService.getCustomerById(CUSTOMER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void getCustomerById_ShouldThrowResourceNotFoundException_WhenCustomerDoesNotExist() {
        // Arrange
        Long nonExistentId = 999L;
        when(customerRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerById(nonExistentId)
        );
        System.out.println("Actual message: '" + exception.getMessage() + "'");

        assertEquals("Customer not found with id: " + nonExistentId, exception.getMessage());
    }

    @Test
    void getCustomerById_ShouldThrowException_WhenIdIsNull() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> customerService.getCustomerById(null)
        );
    }

    @Test
    void getCustomerByNationalId_ShouldReturnCustomer_WhenCustomerExists() {
        // Arrange
        String nationalId = "ABC123456";
        when(customerRepository.findByNationalId(nationalId))
                .thenReturn(Optional.of(testCustomer));

        // Act
        Customer result = customerService.getCustomerByNationalId(nationalId);

        // Assert
        assertNotNull(result);
        assertEquals(testCustomer, result);
        verify(customerRepository, times(1)).findByNationalId(nationalId);
    }

    @Test
    void getCustomerByNationalId_ShouldThrowResourceNotFoundException_WhenCustomerDoesNotExist() {
        // Arrange
        String nationalId = "NONEXISTENT123";
        when(customerRepository.findByNationalId(nationalId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerByNationalId(nationalId)
        );

        assertEquals("Customer not found with id: NONEXISTENT123", exception.getMessage());
        verify(customerRepository, times(1)).findByNationalId(nationalId);
    }

    @Test
    void getCustomerByNationalId_ShouldThrowException_WhenNationalIdIsNull() {
        // Arrange
        when(customerRepository.findByNationalId(null))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerByNationalId(null)
        );

        assertEquals("Customer not found with id: null", exception.getMessage());
    }
}
