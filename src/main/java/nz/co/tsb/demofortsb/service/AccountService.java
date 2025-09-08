package nz.co.tsb.demofortsb.service;

import nz.co.tsb.demofortsb.dto.request.TransferRequest;
import nz.co.tsb.demofortsb.dto.response.AccountResponse;
import nz.co.tsb.demofortsb.dto.response.TransactionResponse;
import nz.co.tsb.demofortsb.entity.Account;
import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.entity.Transaction;
import nz.co.tsb.demofortsb.entity.TransactionBuilder;
import nz.co.tsb.demofortsb.exception.Customer.CustomerNotFoundException;
import nz.co.tsb.demofortsb.exception.ResourceNotFoundException;
import nz.co.tsb.demofortsb.exception.ValidationException;
import nz.co.tsb.demofortsb.repository.AccountRepository;
import nz.co.tsb.demofortsb.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class  AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerService customerService;

    /**
     * Get all accounts for a customer
     */
    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {
        log.info("Fetching accounts for customer ID: {}", customerId);

        // Verify customer exists
        if (!customerService.isCustomerExisted(customerId)) {
            throw new CustomerNotFoundException(customerId.toString());
        }

        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream()
                .map(AccountResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all accounts for a customer by national ID
     */
    public List<AccountResponse> getAccountsByNationalId(String nationalId) {
        log.info("Fetching accounts for customer with national ID: {}", nationalId);

        Customer customer = customerService.getCustomerByNationalId(nationalId);
        return getAccountsByCustomerId(customer.getId());
    }

    /**
     * Get all transactions for an account
     */
    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
        log.info("Fetching transactions for account ID: {}", accountId);

        // Verify account exists
        if (!accountRepository.existsByAccountId(accountId)) {
            throw new ResourceNotFoundException("Account", accountId.toString());
        }

        List<Transaction> transactions = transactionRepository.findAllTransactionsForAccount(accountId);
        return transactions.stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all transactions for an account by account number
     */
    public List<TransactionResponse> getTransactionsByAccountNumber(String accountNumber) {
        log.info("Fetching transactions for account number: {}", accountNumber);

        // Verify account exists
        if (!accountRepository.existsByAccountNumber(accountNumber)) {
            throw new ResourceNotFoundException("Account", accountNumber);
        } else {
            Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
            return getTransactionsByAccountId(account.get().getAccountId());
        }

    }

    /**
     * Transfer money between two accounts belonging to the same customer
     */
    @Transactional
    public TransactionResponse transferBetweenAccounts(Long customerId, TransferRequest request) {
        log.debug("Processing transfer for customer ID: {} from {} to {} amount: {}",
                customerId, request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount());

        // Validate transfer request
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new ValidationException("Cannot transfer to the same account");
        }

        // Get and validate accounts
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source account", request.getFromAccountNumber()));

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account", request.getToAccountNumber()));

        // Verify both accounts belong to the same customer
        if (!fromAccount.getCustomerId().equals(customerId) || !toAccount.getCustomerId().equals(customerId)) {
            throw new ValidationException("Both accounts must belong to the same customer");
        }

        // Check sufficient balance
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new ValidationException("Insufficient balance in source account");
        }


        // Perform the transfer
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        // Save updated accounts
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Create transaction records for debit
        Transaction debitTransaction =  new TransactionBuilder()
                .accountId(fromAccount.getAccountId())
                .transactionType(Transaction.TransactionType.TRANSFER_OUT)
                .amount(request.getAmount())
                .fromAccountId(fromAccount.getAccountId())
                .toAccountId(toAccount.getAccountId())
                .status(Transaction.TransactionStatus.COMPLETED)
                .currencyCode("NZD")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        transactionRepository.save(debitTransaction);

        // Create transaction records for credit
        Transaction creditTransaction =  new TransactionBuilder()
                .accountId(toAccount.getAccountId())
                .transactionType(Transaction.TransactionType.TRANSFER_IN)
                .amount(request.getAmount())
                .toAccountId(toAccount.getAccountId())
                .status(Transaction.TransactionStatus.COMPLETED)
                .currencyCode("NZD")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Transaction savedCreditTransaction = transactionRepository.save(creditTransaction);

        log.info("Transfer completed successfully. Transaction ID: {}", savedCreditTransaction.getTransactionId());

        return new TransactionResponse(savedCreditTransaction);
    }

    /**
     * Transfer by national ID
     */
    @Transactional
    public TransactionResponse transferByNationalId(String nationalId, TransferRequest request) {
        log.info("Processing transfer for customer with national ID: {}", nationalId);

        Customer customer = customerService.getCustomerByNationalId(nationalId);
        return transferBetweenAccounts(customer.getId(), request);
    }

    /**
     * Create an account for a customer (helper method for testing)
     */
    @Transactional
    public AccountResponse createAccount(Long customerId, String accountNumber, BigDecimal initialBalance) {
        log.info("Creating account for customer ID: {} with account number: {}", customerId, accountNumber);

        // Verify customer exists
        customerService.getCustomerById(customerId);

        // Check if account number already exists
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new ValidationException("Account number already exists: " + accountNumber);
        }

        Account account = new Account(customerId);
        account.setBalance(initialBalance != null ? initialBalance : BigDecimal.ZERO);

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with ID: {}", savedAccount.getAccountId());

        return new AccountResponse(savedAccount);
    }

    public List<Account> getAllAccountsForDebugging() {

        return accountRepository.findAll();
    }

//    private Transaction createTransaction(Long accountId, Transaction.TransactionType type, BigDecimal amount,
//                                          Long fromAccountId, Long toAccountId, Transaction.TransactionStatus status) {
//        Transaction transaction = new Transaction();
//        transaction.setAccountId(accountId);
//        transaction.setTransactionType(type);
//        transaction.setAmount(amount);
//        transaction.setTransactionDate(LocalDateTime.now());
//        transaction.setFromAccountId(fromAccountId);
//        transaction.setToAccountId(toAccountId);
//        transaction.setTransactionStatus(status);
//        transaction.setCurrencyCode("NZD");
//        return transaction;
//    }
}