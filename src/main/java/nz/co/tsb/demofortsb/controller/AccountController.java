package nz.co.tsb.demofortsb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nz.co.tsb.demofortsb.dto.request.TransferRequest;
import nz.co.tsb.demofortsb.dto.response.AccountResponse;
import nz.co.tsb.demofortsb.dto.response.TransactionResponse;
import nz.co.tsb.demofortsb.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Account Management", description = "Account and transaction operations")
@RestController
@RequestMapping("/api/accounts")
@SecurityRequirement(name = "bearer-jwt")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Get all accounts for a customer",
            description = "Retrieve all accounts belonging to a specific customer by customer ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.principal.id")
    public ResponseEntity<List<AccountResponse>> getCustomerAccounts(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId) {

        log.info("Fetching accounts for customer ID: {}", customerId);
        List<AccountResponse> accounts = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Get all accounts by national ID",
            description = "Retrieve all accounts belonging to a customer by their national ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/customer/national-id/{nationalId}")
    @PreAuthorize("hasRole('ADMIN') or #nationalId == authentication.principal.nationalId")
    public ResponseEntity<List<AccountResponse>> getAccountsByNationalId(
            @Parameter(description = "National ID", required = true)
            @PathVariable String nationalId) {

        log.info("Fetching accounts for national ID: {}", nationalId);
        List<AccountResponse> accounts = accountService.getAccountsByNationalId(nationalId);
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Get all transactions for an account",
            description = "Retrieve all transactions for a specific account by account ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{accountId}/transactions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long accountId) {

        log.info("Fetching transactions for account ID: {}", accountId);
        List<TransactionResponse> transactions = accountService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Get transactions by account number",
            description = "Retrieve all transactions for an account by account number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/number/{accountNumber}/transactions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountNumber(
            @Parameter(description = "Account number", required = true)
            @PathVariable String accountNumber) {

        log.info("Fetching transactions for account number: {}", accountNumber);
        List<TransactionResponse> transactions = accountService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Transfer between accounts",
            description = "Transfer money between two accounts belonging to the same customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer request"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/customer/{customerId}/transfer")
    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.principal.id")
    public ResponseEntity<TransactionResponse> transferBetweenAccounts(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId,
            @Valid @RequestBody TransferRequest request) {

        log.info("Processing transfer for customer ID: {}", customerId);
        TransactionResponse transaction = accountService.transferBetweenAccounts(customerId, request);
        return ResponseEntity.ok(transaction);
    }

    @Operation(summary = "Transfer by national ID",
            description = "Transfer money between accounts using customer's national ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer request"),
            @ApiResponse(responseCode = "404", description = "Customer or account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/customer/national-id/{nationalId}/transfer")
    @PreAuthorize("hasRole('ADMIN') or #nationalId == authentication.principal.nationalId")
    public ResponseEntity<TransactionResponse> transferByNationalId(
            @Parameter(description = "National ID", required = true)
            @PathVariable String nationalId,
            @Valid @RequestBody TransferRequest request) {

        log.info("Processing transfer for national ID: {}", nationalId);
        TransactionResponse transaction = accountService.transferByNationalId(nationalId, request);
        return ResponseEntity.ok(transaction);
    }
}