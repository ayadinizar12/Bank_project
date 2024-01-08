package com.sid.demo;




import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.sid.demo.dtos.BankAcountDTO;
import com.sid.demo.dtos.CurrentAcountDTO;
import com.sid.demo.dtos.CustomerDTO;
import com.sid.demo.dtos.SavingAccountDTO;
import com.sid.demo.enums.AccountStatus;
import com.sid.demo.enums.OperationType;
import com.sid.demo.exceptions.CustomerNotFound;
import com.sid.demo.models.AccountOperation;
import com.sid.demo.models.BankAcount;
import com.sid.demo.models.CurrentAccount;
import com.sid.demo.models.Customer;
import com.sid.demo.models.SavingAccount;
import com.sid.demo.repositories.AccountOperationRepo;
import com.sid.demo.repositories.BankAccountRepo;
import com.sid.demo.repositories.CustomerRepo;
import com.sid.demo.services.BankService;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class BankProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankProjectApplication.class, args);
	}
	
	@Bean
    CommandLineRunner commandLineRunner(BankService bankService){
        return args -> {
           Stream.of("Ayed","Nizar","Arij").forEach(name->{
               CustomerDTO customerDTO=new CustomerDTO();
               customerDTO.setName(name);
               customerDTO.setEmail(name+"@gmail.com");
               bankService.saveCustomer(customerDTO);
            });
           bankService.listCustomers().forEach(customer->{
               try {
                   bankService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
                   bankService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());

               } catch (CustomerNotFound e) {
                   e.printStackTrace();
               }
           });
            List<BankAcountDTO> bankAccounts = bankService.bankAccountList();
            for (BankAcountDTO bankAccount:bankAccounts){
                for (int i = 0; i <10 ; i++) {
                    String accountId;
                    if(bankAccount instanceof SavingAccountDTO){
                        accountId=((SavingAccountDTO) bankAccount).getId();
                    } else{
                        accountId=((CurrentAcountDTO) bankAccount).getId();
                    }
                    bankService.credit(accountId,10000+Math.random()*120000,"Credit");
                    bankService.debit(accountId,1000+Math.random()*9000,"Debit");
                }
            }
        };
    }
	//@Bean
	CommandLineRunner start( CustomerRepo customerRepo, BankAccountRepo bankAccountRepo,AccountOperationRepo accountOperationRepo){
		return args -> { 
			Stream.of("Ayed","Nizar","Arij").forEach(name->{
			Customer customer=new Customer();
			customer.setName(name);
			customer.setEmail(name+"@gmail.com");
			customerRepo.save(customer);
			
			});
			
			customerRepo.findAll().forEach(c->{
				CurrentAccount currentAccount=new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance( Math.random()*1900);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.ACTIVATED);
				currentAccount.setCustomer(c);
				currentAccount.setOverDraft(1000);
				bankAccountRepo.save(currentAccount);
				
				SavingAccount savingAccount=new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(c);
                savingAccount.setInterestRate(5.5);
                bankAccountRepo.save(savingAccount);
			});
			
			bankAccountRepo.findAll().forEach(a->{
				for (int i = 0; i <10 ; i++) {
                    AccountOperation accountOperation=new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setType(Math.random()>0.5? OperationType.DEBIT: OperationType.CREDIT);
                    accountOperation.setBankAcount(a);
                    accountOperationRepo.save(accountOperation);
				    }
			   });
		};

	}
}
