package nz.co.tsb.demofortsb.dto;

import nz.co.tsb.demofortsb.entity.Account;

// DTO for account details with customer name
public class AccountDetailsDTO {
    Account account;
    String customerName;

    public AccountDetailsDTO() {
    }

    public AccountDetailsDTO(Account account, String customerName) {
        this.account = account;
        this.customerName = customerName;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public static class Builder {
        Account account;
        String customerName;

        public Builder account(Account account) {
            this.account = account;
            return this;
        }

        public Builder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public AccountDetailsDTO build() {
            AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO();
            accountDetailsDTO.setAccount(account);
            accountDetailsDTO.setCustomerName(customerName);

            return accountDetailsDTO;
        }
    }


}
