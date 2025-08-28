package nz.co.tsb.demofortsb.service;

import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.exception.Customer.CustomerAlreadyExistsException;
import nz.co.tsb.demofortsb.exception.Customer.CustomerNotFoundException;
import nz.co.tsb.demofortsb.exception.Customer.InvalidCustomerDataException;
import nz.co.tsb.demofortsb.repository.CustomerRepository;
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

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private final Long CUSTOMER_ID = 1L;
    private final String EMAIL = "test@example.com";
    private final String NATIONAL_ID = "ABC123";

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(CUSTOMER_ID)
                .firstName("John")
                .lastName("Doe")
                .email(EMAIL)
                .nationalId(NATIONAL_ID)
                .phoneNumber("+640987654321")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    // Create Customer Tests
    @Test
    void createCustomer_Success() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsByNationalId(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        Customer created = customerService.createCustomer(testCustomer);

        assertNotNull(created);
        assertEquals(CUSTOMER_ID, created.getId());
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void createCustomer_EmailExists_ThrowsException() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        CustomerAlreadyExistsException exception = assertThrows(CustomerAlreadyExistsException.class,
            () -> customerService.createCustomer(testCustomer)
        );
        
        assertEquals("Customer already exists with email: " + EMAIL, exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void createCustomer_NationalIdExists_ThrowsException() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsByNationalId(anyString())).thenReturn(true);

        CustomerAlreadyExistsException exception = assertThrows(CustomerAlreadyExistsException.class,
            () -> customerService.createCustomer(testCustomer)
        );
        
        assertEquals("Customer already exists with nationalId: " + NATIONAL_ID, exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // Get Customer Tests
    @Test
    void getCustomerById_Found() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(testCustomer));
        
        Optional<Customer> found = customerService.getCustomerById(CUSTOMER_ID);
        
        assertTrue(found.isPresent());
        assertEquals(CUSTOMER_ID, found.get().getId());
    }

    @Test
    void getCustomerById_NotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        Optional<Customer> found = customerService.getCustomerById(999L);
        
        assertTrue(found.isEmpty());
    }

    @Test
    void getCustomerById_InvalidId_ThrowsException() {
        assertThrows(InvalidCustomerDataException.class,
            () -> customerService.getCustomerById(-1L)
        );
        
        verify(customerRepository, never()).findById(anyLong());
    }

    // Get All Customers Tests
    @Test
    void getAllCustomers_Success() {
        List<Customer> customers = Arrays.asList(testCustomer, testCustomer);
        when(customerRepository.findAll()).thenReturn(customers);
        
        List<Customer> result = customerService.getAllCustomers();
        
        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    // Update Customer Tests
    @Test
    void updateCustomer_Success() {
        Customer updatedInfo = Customer.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("new@example.com")
                .phoneNumber("+6498765432")
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .build();

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        Customer updated = customerService.updateCustomer(CUSTOMER_ID, updatedInfo);

        assertNotNull(updated);
        assertEquals("Updated", testCustomer.getFirstName());
        assertEquals("Name", testCustomer.getLastName());
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void updateCustomer_NotFound_ThrowsException() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
            () -> customerService.updateCustomer(999L, new Customer())
        );
        
        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_DuplicateEmail_ThrowsException() {
        Customer existingCustomer = Customer.builder()
                .id(2L)
                .email("existing@example.com")
                .build();
                
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);
        
        Customer updateRequest = new Customer();
        updateRequest.setEmail("existing@example.com");
        
        assertThrows(CustomerAlreadyExistsException.class,
            () -> customerService.updateCustomer(CUSTOMER_ID, updateRequest)
        );
        
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // Delete Customer Tests
    @Test
    void deleteCustomer_Success() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(CUSTOMER_ID);
        
        assertDoesNotThrow(() -> customerService.deleteCustomer(CUSTOMER_ID));
        verify(customerRepository, times(1)).deleteById(CUSTOMER_ID);
    }

    @Test
    void deleteCustomer_NotFound_ThrowsException() {
        when(customerRepository.existsById(anyLong())).thenReturn(false);
        
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
            () -> customerService.deleteCustomer(999L)
        );
        
        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteCustomer_InvalidId_ThrowsException() {
        assertThrows(InvalidCustomerDataException.class,
            () -> customerService.deleteCustomer(null)
        );
        
        verify(customerRepository, never()).deleteById(anyLong());
    }

    // Search Customers Tests
    @Test
    void searchCustomers_Success() {
        List<Customer> expected = Arrays.asList(testCustomer);
        when(customerRepository.searchByName("John")).thenReturn(expected);
        
        List<Customer> result = customerService.searchCustomers("John");
        
        assertEquals(1, result.size());
        verify(customerRepository, times(1)).searchByName("John");
    }
    
    @Test
    void searchCustomers_InvalidInput_ThrowsException() {
        assertThrows(InvalidCustomerDataException.class,
            () -> customerService.searchCustomers("")
        );
        
        assertThrows(InvalidCustomerDataException.class,
            () -> customerService.searchCustomers("a")
        );
        
        verify(customerRepository, never()).searchByName(anyString());
    }
}
