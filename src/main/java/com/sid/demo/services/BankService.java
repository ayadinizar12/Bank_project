package com.sid.demo.services;

import java.util.List;

import com.sid.demo.dtos.AccountHistoryDTO;
import com.sid.demo.dtos.AccountOperationDTO;
import com.sid.demo.dtos.BankAcountDTO;
import com.sid.demo.dtos.CurrentAcountDTO;
import com.sid.demo.dtos.CustomerDTO;
import com.sid.demo.dtos.SavingAccountDTO;
import com.sid.demo.exceptions.BalanceNotSufficient;
import com.sid.demo.exceptions.BankAccountNotFound;
import com.sid.demo.exceptions.CustomerNotFound;
import com.sid.demo.models.AccountOperation;
import com.sid.demo.models.BankAcount;
import com.sid.demo.models.CurrentAccount;
import com.sid.demo.models.Customer;
import com.sid.demo.models.SavingAccount;

public interface BankService {

	CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentAcountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)throws CustomerNotFound;
    SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFound;
    List<CustomerDTO> listCustomers();
    BankAcountDTO getBankAccount(String accountId) throws BankAccountNotFound ;
    void debit(String accountId, double amount, String description) throws BalanceNotSufficient, BankAccountNotFound ;
    void credit(String accountId, double amount, String description) throws BankAccountNotFound ;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFound, BalanceNotSufficient;

    List<BankAcountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFound;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFound;

    List<CustomerDTO> searchCustomers(String keyword);
    
   
}
