package nz.co.tsb.demofortsb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nz.co.tsb.demofortsb.dto.ErrorResponse;
import nz.co.tsb.demofortsb.exception.Customer.CustomerNotFoundException;
import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "APIs for managing customer data")
public class CustomerControllerOld {

//    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
//
//    private final CustomerService customerService;
//
//    public CustomerControllerOLd(CustomerService customerService) {
//        this.customerService = customerService;
//    }
//
//    @PostMapping
//    @Operation(
//            summary = "Create a new customer",
//            description = "Creates a new customer with the provided information"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(
//                    responseCode = "201",
//                    description = "Customer created successfully",
//                    content = @Content(schema = @Schema(implementation = Customer.class))
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid customer data provided or validation failed",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "409",
//                    description = "Customer already exists (duplicate email or national ID)",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "500",
//                    description = "Internal server error",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            )
//    })
//    public ResponseEntity<Customer> createCustomer(
//            @Valid @RequestBody
//            @Parameter(description = "Customer data to be created", required = true)
//            Customer customer) {
//        MDC.put("businessId", "create-customer");
//        logger.info("Creating a new customer.");
//
//        Customer createdCustomer = customerService.createCustomer(customer);
//
//        if (createdCustomer != null && createdCustomer.getId() != null) {
//            String businessId = "create-cust-" + createdCustomer.getId();
//            MDC.put("businessId", businessId);
//            logger.info("Customer created successfully");
//        }
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
//    }
//
//    @GetMapping("/{id}")
//    @Operation(
//            summary = "Get customer by ID",
//            description = "Retrieves a customer by their unique identifier"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "Customer found",
//                    content = @Content(schema = @Schema(implementation = Customer.class))
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid ID format (must be positive number)",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "404",
//                    description = "Customer not found",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "500",
//                    description = "Internal server error",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            )
//    })
//    public ResponseEntity<Customer> getCustomer(
//            @PathVariable
//            @Parameter(description = "Customer ID", required = true, example = "1")
//            Long id) {
//        String businessId = "get-cust-" + id;
//        MDC.put("businessId", businessId);
//        logger.info("Fetching customer details.");
//
//        return customerService.getCustomerById(id)
//                .map(ResponseEntity::ok)
//                .orElseThrow(() -> new CustomerNotFoundException(id));
//    }
//
//    @GetMapping
//    @Operation(
//            summary = "Get all customers",
//            description = "Retrieves a list of all customers in the system"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "List of customers retrieved successfully",
//                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))
//            ),
//            @ApiResponse(
//                    responseCode = "500",
//                    description = "Internal server error",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            )
//    })
//
//    public ResponseEntity<List<Customer>> getAllCustomers() {
//        String businessId = "get-all-customers";
//        MDC.put("businessId", businessId);
//        logger.info("Fetching all customers.");
//
//        List<Customer> customers = customerService.getAllCustomers();
//        return ResponseEntity.ok(customers);
//    }
//
//    @PutMapping("/{id}")
//    @Operation(
//            summary = "Update customer",
//            description = "Updates an existing customer with the provided information"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "Customer updated successfully",
//                    content = @Content(schema = @Schema(implementation = Customer.class))
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid customer data provided or validation failed",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "404",
//                    description = "Customer not found",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "409",
//                    description = "Email or National ID already exists",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "500",
//                    description = "Internal server error",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            )
//    })
//    public ResponseEntity<Customer> updateCustomer(
//            @PathVariable
//            @Parameter(description = "Customer ID", required = true, example = "1")
//            Long id,
//            @Valid @RequestBody
//            @Parameter(description = "Updated customer data", required = true)
//            Customer customer) {
//        String businessId = "update-cust-" + id;
//        MDC.put("businessId", businessId);
//        logger.info("Updating customer details.");
//
//        Customer updatedCustomer = customerService.updateCustomer(id, customer);
//        return ResponseEntity.ok(updatedCustomer);
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(
//            summary = "Delete customer",
//            description = "Deletes a customer by their unique identifier"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(
//                    responseCode = "204",
//                    description = "Customer deleted successfully"
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid ID format (must be positive number)",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "404",
//                    description = "Customer not found",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "409",
//                    description = "Customer has active accounts and cannot be deleted",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "500",
//                    description = "Internal server error",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            )
//    })
//    public ResponseEntity<Void> deleteCustomer(
//            @PathVariable
//            @Parameter(description = "Customer ID", required = true, example = "1")
//            Long id) {
//        String businessId = "del-cust-" + id;
//        MDC.put("businessId", businessId);
//        logger.info("Deleting customer.");
//
//        customerService.deleteCustomer(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/search")
//    @Operation(
//            summary = "Search customers by name",
//            description = "Searches for customers whose names contain the specified search term"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "Search completed successfully",
//                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid search parameter (empty or too short)",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "500",
//                    description = "Internal server error",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//            )
//    })
//    public ResponseEntity<List<Customer>> searchCustomers(
//            @RequestParam
//            @Parameter(description = "Name to search for", required = true, example = "John")
//            String name) {
//        List<Customer> customers = customerService.searchCustomers(name);
//        return ResponseEntity.ok(customers);
//    }
}