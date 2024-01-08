package com.sid.demo.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sid.demo.dtos.AccountHistoryDTO;
import com.sid.demo.dtos.AccountOperationDTO;
import com.sid.demo.dtos.BankAcountDTO;
import com.sid.demo.dtos.CurrentAcountDTO;
import com.sid.demo.dtos.CustomerDTO;
import com.sid.demo.dtos.SavingAccountDTO;
import com.sid.demo.enums.OperationType;
import com.sid.demo.exceptions.BalanceNotSufficient;
import com.sid.demo.exceptions.BankAccountNotFound;
import com.sid.demo.exceptions.CustomerNotFound;
import com.sid.demo.mappers.BankAcountMapperImp;
import com.sid.demo.models.AccountOperation;
import com.sid.demo.models.BankAcount;
import com.sid.demo.models.CurrentAccount;
import com.sid.demo.models.Customer;
import com.sid.demo.models.SavingAccount;
import com.sid.demo.repositories.AccountOperationRepo;
import com.sid.demo.repositories.BankAccountRepo;
import com.sid.demo.repositories.CustomerRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankServiceImp implements BankService {

	private AccountOperationRepo accountOperationRepo;
	private BankAccountRepo accountRepo;
	private CustomerRepo customerRepo;
	private BankAcountMapperImp mapperImp;
	
	Logger log=LoggerFactory.getLogger(this.getClass().getName());
	
	
	@Override
	public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
		log.info("Save Customer");
		Customer customer=mapperImp.fromCustomerDTO(customerDTO); 
		Customer saveC=customerRepo.save(customer);
		return mapperImp.fromCustomer(saveC);
	}
	

	@Override
	public CurrentAcountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFound {
		Customer customer=customerRepo.findById(customerId).orElse(null);
        if(customer==null) throw new CustomerNotFound("Customer not found");
        CurrentAccount currentAccount=new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        CurrentAccount saveAC = accountRepo.save(currentAccount);
        return mapperImp.fromCurrentBankAccount(saveAC);
	}

	@Override
	public SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFound {
		Customer customer=customerRepo.findById(customerId).orElse(null);
		if (customer==null) throw new CustomerNotFound("not found");	
		SavingAccount currentAccount=new SavingAccount();
		currentAccount.setId(UUID.randomUUID().toString());
		currentAccount.setCreatedAt(new Date());
		currentAccount.setBalance(initialBalance);
		currentAccount.setCustomer(customer);
		currentAccount.setInterestRate(interestRate);
		SavingAccount saveAS=accountRepo.save(currentAccount);
		return mapperImp.fromSavingBankAccount(saveAS);
	}
	

	@Override
	public List<CustomerDTO> listCustomers() {
		List<Customer> customers=customerRepo.findAll();
		/*
		 * List<CustomerDTO>customerDTOs=new ArrayList<>(); for (Customer
		 * customer:customers) { CustomerDTO
		 * customerDTO=mapperImp.fromCustomer(customer); customerDTOs.add(customerDTO);
		 * }
		 */
		List<CustomerDTO> customerDTOs = customers.stream()
				.map(customer->mapperImp.fromCustomer(customer))
				.collect(Collectors.toList());
		return customerDTOs;
	}
	

	@Override
	public BankAcountDTO getBankAccount(String accountId) throws BankAccountNotFound {
		BankAcount bankAcount =accountRepo.findById(accountId).orElseThrow(() -> new BankAccountNotFound("not found"));	
	
		if(bankAcount instanceof SavingAccount){
            SavingAccount savingAccount= (SavingAccount) bankAcount;
            return mapperImp.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount= (CurrentAccount) bankAcount;
            return mapperImp.fromCurrentBankAccount(currentAccount);
        }
		
	}

	@Override
	public void debit(String accountId, double amount, String description) throws BankAccountNotFound, BalanceNotSufficient {
		BankAcount bankAcount=accountRepo.findById(accountId).orElseThrow(()->new BankAccountNotFound("Account not found"));
		 if(bankAcount.getBalance()<amount) throw new BalanceNotSufficient("Not Sufficient");
	        AccountOperation accountOperation=new AccountOperation();
	        accountOperation.setType(OperationType.DEBIT);
	        accountOperation.setAmount(amount);
	        accountOperation.setDescription(description);
	        accountOperation.setOperationDate(new Date());
	        accountOperation.setBankAcount(bankAcount);
	        accountOperationRepo.save(accountOperation);
	        bankAcount.setBalance(bankAcount.getBalance()-amount);
	        accountRepo.save(bankAcount);
	}

	@Override
	public void credit(String accountId, double amount, String description) throws BankAccountNotFound {
		BankAcount bankAcount=accountRepo.findById(accountId).orElseThrow(()->new BankAccountNotFound("Account not found"));
	        AccountOperation accountOperation=new AccountOperation();
	        accountOperation.setType(OperationType.CREDIT);
	        accountOperation.setAmount(amount);
	        accountOperation.setDescription(description);
	        accountOperation.setOperationDate(new Date());
	        accountOperation.setBankAcount(bankAcount);
	        accountOperationRepo.save(accountOperation);
	        bankAcount.setBalance(bankAcount.getBalance()+ amount);
	        accountRepo.save(bankAcount);
	}
	@Override
	public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFound, BalanceNotSufficient {
		debit(accountIdSource, amount,"transfer to"+accountIdDestination);
		credit(accountIdDestination, amount, "transfer from"+accountIdSource);
		 
		
	}

	@Override
	public List<BankAcountDTO> bankAccountList() {
		List<BankAcount> bankAccounts = accountRepo.findAll();
        List<BankAcountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount->{
        if (bankAccount instanceof SavingAccount) {
        	SavingAccount savingAccount=(SavingAccount)bankAccount;
        	return mapperImp.fromSavingBankAccount(savingAccount);
        }else {
        	CurrentAccount currentAccount=(CurrentAccount)bankAccount;
        	return mapperImp.fromCurrentBankAccount(currentAccount);
        }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
	}

	@Override
	public CustomerDTO getCustomer(Long customerId) throws CustomerNotFound {
		Customer customer =customerRepo.findById(customerId).orElseThrow(()->new CustomerNotFound("Not Found"));
		return mapperImp.fromCustomer(customer);
	}

	@Override
	public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
		        log.info("Save new Customer");
		        Customer customer=mapperImp.fromCustomerDTO(customerDTO);
		        Customer savedCustomer = customerRepo.save(customer);
		        return mapperImp.fromCustomer(savedCustomer);
	}

	 @Override
	 public void deleteCustomer(Long customerId){
	      customerRepo.deleteById(customerId);
	 }

	@Override
	public List<AccountOperationDTO> accountHistory(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepo.findByBankAccountId(accountId);
        return accountOperations.stream().map(op->mapperImp.fromAccountOperation(op)).collect(Collectors.toList());
    }


	@Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers=customerRepo.searchCustomer(keyword);
        List<CustomerDTO> customerDTOS = customers.stream().map(cust -> mapperImp.fromCustomer(cust)).collect(Collectors.toList());
        return customerDTOS;
    }

	@Override
	public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFound {
		BankAcount bankAcount=accountRepo.findById(accountId).orElse(null);
		if(bankAcount==null)throw new BankAccountNotFound("Not Found");
		Page<AccountOperation> accountOperations=accountOperationRepo.findByBankAccountIdOrderByOperationDateDesc(accountId,PageRequest.of(page, size));
		AccountHistoryDTO accountHistoryDTO= new AccountHistoryDTO();
		List<AccountOperationDTO> accountOperatioDtos= accountOperations.getContent().stream().map(oop-> mapperImp.fromAccountOperation(oop)).collect(Collectors.toList());
		accountHistoryDTO.setAccountId(bankAcount.getId());
		accountHistoryDTO.setAccountOperationDTOS(accountOperatioDtos);
		accountHistoryDTO.setBalance(bankAcount.getBalance());
		accountHistoryDTO.setCurrentPage(page);
		accountHistoryDTO.setPageSize(size);
		accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
		return accountHistoryDTO;
	}
}
