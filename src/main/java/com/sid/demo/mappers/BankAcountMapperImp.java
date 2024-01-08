package com.sid.demo.mappers;


import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.sid.demo.dtos.AccountOperationDTO;
import com.sid.demo.dtos.CurrentAcountDTO;
import com.sid.demo.dtos.CustomerDTO;
import com.sid.demo.dtos.SavingAccountDTO;
import com.sid.demo.models.AccountOperation;
import com.sid.demo.models.CurrentAccount;
import com.sid.demo.models.Customer;
import com.sid.demo.models.SavingAccount;

@Service
public class BankAcountMapperImp {

	public CustomerDTO fromCustomer(Customer customer){
		CustomerDTO customerDTO=new CustomerDTO();
		BeanUtils.copyProperties(customer, customerDTO);
		//customerDTO.setId(customer.getId());
		//customerDTO.setName(customer.getName());
		//customerDTO.setEmail(customer.getEmail());
		return customerDTO;
	}

	public Customer fromCustomerDTO(CustomerDTO customerDTO){
		Customer customer=new Customer();
		BeanUtils.copyProperties(customerDTO, customer);
		return customer;
	}

	public SavingAccountDTO fromSavingBankAccount(SavingAccount savingAccount){
		SavingAccountDTO savingAccountDTO=new SavingAccountDTO();
		BeanUtils.copyProperties(savingAccount, savingAccountDTO);
		savingAccountDTO.setCustomerDTO(fromCustomer(savingAccount.getCustomer()));
		savingAccountDTO.setType(savingAccount.getClass().getSimpleName());
		return savingAccountDTO;
	}
	
	public SavingAccount fromSavingBankAccountDTO(SavingAccountDTO savingAccountDTO){
        SavingAccount savingAccount=new SavingAccount();
        BeanUtils.copyProperties(savingAccountDTO,savingAccount);
        savingAccount.setCustomer(fromCustomerDTO(savingAccountDTO.getCustomerDTO()));
        return savingAccount;
    }
	
	public CurrentAcountDTO fromCurrentBankAccount(CurrentAccount currentAccount){
		CurrentAcountDTO currentAcountDTO=new CurrentAcountDTO();
		BeanUtils.copyProperties(currentAccount, currentAcountDTO);
		currentAcountDTO.setCustomerDTO(fromCustomer(currentAccount.getCustomer()));
		currentAcountDTO.setType(currentAccount.getClass().getSimpleName());
		return currentAcountDTO;
	}
	
	public CurrentAccount fromCurrentBankAccountDTO(CurrentAcountDTO currentAcountDTO){
        CurrentAccount currentAccount=new CurrentAccount();
        BeanUtils.copyProperties(currentAcountDTO,currentAccount);
        currentAccount.setCustomer(fromCustomerDTO(currentAcountDTO.getCustomerDTO()));
        return currentAccount;
    }
	
	public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation){
        AccountOperationDTO accountOperationDTO=new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation,accountOperationDTO);
        return accountOperationDTO;
    }

	public AccountOperation fromAccountOperationDTO(AccountOperationDTO accountOperationDTO){
        AccountOperation accountOperation=new AccountOperation();
        BeanUtils.copyProperties(accountOperationDTO,accountOperation);
        return accountOperation;
    }
}
