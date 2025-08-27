package nz.co.tsb.demofortsb.exception.Customer;

import nz.co.tsb.demofortsb.exception.BusinessException;

public class CustomerHasActiveAccountsException extends BusinessException {
    private final Long customerId;
    private final Integer activeAccountCount;

    public CustomerHasActiveAccountsException(Long customerId, Integer activeAccountCount) {
        super(String.format("Cannot delete customer %d - has %d active accounts", customerId, activeAccountCount),
                "CUSTOMER_HAS_ACTIVE_ACCOUNTS");
        this.customerId = customerId;
        this.activeAccountCount = activeAccountCount;
    }

    // Getters
    public Long getCustomerId() { return customerId; }
    public Integer getActiveAccountCount() { return activeAccountCount; }
}